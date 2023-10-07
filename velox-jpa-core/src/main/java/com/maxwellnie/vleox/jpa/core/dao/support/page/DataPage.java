package com.maxwellnie.vleox.jpa.core.dao.support.page;

import java.util.List;

/**
 * 分页接口
 *
 * @author Maxwell Nie
 */
public interface DataPage<T> {
    /**
     * 导航栏页码容量
     *
     * @return
     */
    long getCapacity();

    void setCapacity(long capacity);

    boolean getNavigation();

    /**
     * 设置开启导航栏模式
     *
     * @param enable
     */
    void setNavigation(boolean enable);

    /**
     * 底部导航栏页码起始下标
     *
     * @return
     */
    long getStart();

    /**
     * 底部导航栏页码结束下标
     *
     * @return
     */
    long getEnd();

    /**
     * 当前页
     *
     * @return
     */
    long getCurrent();

    void setCurrent(long current);

    /**
     * 偏移量
     *
     * @return
     */
    long getOffset();

    void setOffset(long offset);

    /**
     * 上一页页码
     *
     * @return
     */
    long getPrevious();

    /**
     * 下一页页码
     *
     * @return
     */
    long getNext();

    long getSize();

    long getPages();

    List<T> getData();
}