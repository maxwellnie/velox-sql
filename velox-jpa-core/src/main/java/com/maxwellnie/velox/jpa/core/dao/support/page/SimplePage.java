package com.maxwellnie.velox.jpa.core.dao.support.page;

import java.util.List;

/**
 * @author Maxwell Nie
 */
public class SimplePage<T> implements DataPage<T> {
    @Override
    public long getCapacity() {
        return 0;
    }

    @Override
    public void setCapacity(long capacity) {

    }

    @Override
    public boolean getNavigation() {
        return false;
    }

    @Override
    public void setNavigation(boolean enable) {

    }

    @Override
    public long getStart() {
        return 0;
    }

    @Override
    public long getEnd() {
        return 0;
    }

    @Override
    public long getCurrent() {
        return 0;
    }

    @Override
    public void setCurrent(long current) {

    }

    @Override
    public long getOffset() {
        return 0;
    }

    @Override
    public void setOffset(long offset) {

    }

    @Override
    public long getPrevious() {
        return 0;
    }

    @Override
    public long getNext() {
        return 0;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public long getPages() {
        return 0;
    }

    @Override
    public List<T> getData() {
        return null;
    }
}
