package model;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 04/03/2013
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */
public class MetricQueue<E> extends LinkedList<E> {
    private int windowSize;

    public MetricQueue(int windowSize) {
        this.windowSize = windowSize;
    }

    @Override
    public boolean add(E element) {
        super.add(element);
        while (super.size() > this.windowSize) {
            super.remove();
        }
        return true;
    }
}
