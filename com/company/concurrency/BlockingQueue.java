package com.company.concurrency;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by art71_000 on 17.03.2015.
 */
public class BlockingQueue<T> extends ArrayBlockingQueue<T> {
    public AtomicInteger producersAmount = new AtomicInteger(0);
    public BlockingQueue(final int size) {
        super(size);
    }
}
