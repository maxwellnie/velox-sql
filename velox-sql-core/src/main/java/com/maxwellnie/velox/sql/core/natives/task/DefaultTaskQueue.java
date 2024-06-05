package com.maxwellnie.velox.sql.core.natives.task;

import com.maxwellnie.velox.sql.core.cache.key.CacheKey;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.DefaultJdbcSession;
import com.maxwellnie.velox.sql.core.utils.reflect.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import static java.lang.Thread.sleep;


/**
 * @author Maxwell Nie
 */
public class DefaultTaskQueue implements TaskQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTaskQueue.class);
    private ConcurrentHashMap<String, ConcurrentTaskQueue> queueMap = new ConcurrentHashMap<>();

    public DefaultTaskQueue() {
    }

    @Override
    public void require(String group, CacheKey cacheKey, Runnable task) {
        ConcurrentTaskQueue queue = getQueue(group);
        queue.insertToHead(task);
        LOGGER.info("Task {} finished", cacheKey);
    }

    private ConcurrentTaskQueue getQueue(String group) {
        return queueMap.computeIfAbsent(group, k -> new ConcurrentTaskQueue());
    }

    static class ConcurrentTaskQueue extends Thread {
        private static final Unsafe UNSAFE;
        private static final long HEAD_OFFSET;
        private static final long TAIL_OFFSET;

        static {
            try {
                UNSAFE = ReflectionUtils.getUnsafe();
                assert UNSAFE != null : "Unsafe";
                HEAD_OFFSET = UNSAFE.objectFieldOffset(ConcurrentTaskQueue.class.getDeclaredField("head"));
                TAIL_OFFSET = UNSAFE.objectFieldOffset(ConcurrentTaskQueue.class.getDeclaredField("tail"));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        private volatile Node head;
        private volatile Node tail;

        public ConcurrentTaskQueue() {
            head = tail = new Node(null);
            this.setDaemon(true);
            start();
        }

        /**
         * powered by ConcurrentLinkedDeque
         * @author Doug Lea
         * @param runnable
         * @see ConcurrentLinkedDeque#linkFirst(Object)
         * @return new Node
         */
        private Node insertToHead(Runnable runnable) {
            runnable = Objects.requireNonNull(runnable);
            Task task = new Task(runnable);
            Node newNode = new Node(task.getLock());

            restartFromHead:
            for (; ; )
                for (Node h = head, p = h, q; ; ) {
                    if ((q = p.prev) != null &&
                            (q = (p = q).prev) != null)
                        // Check for head updates every other hop.
                        // If p == q, we are sure to follow head instead.
                        p = (h != (h = head)) ? h : q;
                    else if (p.next == p) // PREV_TERMINATOR
                        continue restartFromHead;
                    else {
                        // p is first node
                        newNode.lazySetNext(p); // CAS piggyback
                        if (p.casPrev(null, newNode)) {
                            // Successful CAS is the linearization point
                            // for e to become an element of this deque,
                            // and for newNode to become "live".
                            if (p != h) {// hop two nodes at a time
                                casHead(h, newNode);  // Failure is OK.
                            }
                            break restartFromHead;
                        }
                        // Lost CAS race to another thread; re-read prev
                    }
                }
            Thread thread = new Thread(task);
            newNode.task = thread;
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return newNode;
        }

        private boolean casHead(Node cmp, Node val) {
            return UNSAFE.compareAndSwapObject(this, HEAD_OFFSET, cmp, val);
        }

        private boolean casTail(Node cmp, Node val) {
            return UNSAFE.compareAndSwapObject(this, TAIL_OFFSET, cmp, val);
        }

        private void remove() {
            for (; ; ) {
                if (head != tail) {
                    Node pt = tail;
                    Node p = tail.prev;
                    Node q = p.prev;
                    Node r = p.next;
                    Thread t = p.task;
                    if (p.casNext(r, null) && p.casPrev(q, null)) {
                        if (q != null)
                            q.casNext(p, tail);
                        else
                            casHead(p, tail);
                        p.casItem(t, null);
                        pt.casPrev(p, q);
                        return;
                    }
                } else {
                    if (tail.prev == null)
                        break;
                }
            }
        }

        @Override
        public void run() {
            reStart:
            for (; ; ) {
                Node node = tail.prev;
                if (node != null && node.task != null) {
                    Thread task = node.task;
                    boolean isLocked = node.isLocked;
                    if (task.getState().equals(State.TERMINATED))
                        continue;
                    while (!task.getState().equals(State.WAITING)) {
                        if (isLocked) {
                            continue reStart;
                        }
                    }
                    synchronized (node.lock) {
                        node.lock.notify();
                    }
                    try {
                        task.join(node.timeout);
                        node.casLock(isLocked, true);
                        remove();
                    } catch (InterruptedException e) {
                        LOGGER.error("Thread join error");
                    }
                }
            }
        }

        static class Node {
            private static final long itemOffset;
            private static final long nextOffset;
            private static final long prevOffset;
            private static final long lockOffset;

            static {
                try {
                    itemOffset = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("task"));
                    nextOffset = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("next"));
                    lockOffset = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("isLocked"));
                    prevOffset = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("prev"));
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }

            final Object lock;
            volatile Node prev;
            volatile Node next;
            volatile long timeout;
            volatile Thread task;
            boolean isLocked;

            public Node(Object lock) {
                this.lock = lock;
            }

            boolean casItem(Thread cmp, Thread val) {
                return UNSAFE.compareAndSwapObject(this, itemOffset, cmp, val);
            }

            void lazySetNext(Node val) {
                UNSAFE.putOrderedObject(this, nextOffset, val);
            }

            boolean casNext(Node cmp, Node val) {
                return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
            }

            void lazySetPrev(Node val) {
                UNSAFE.putOrderedObject(this, prevOffset, val);
            }

            boolean casPrev(Node cmp, Node val) {
                return UNSAFE.compareAndSwapObject(this, prevOffset, cmp, val);
            }

            boolean casLock(boolean cmp, boolean val) {
                return UNSAFE.compareAndSwapObject(this, lockOffset, cmp, val);
            }
        }
    }

}
