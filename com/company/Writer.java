package com.company;

import com.company.concurrency.BlockingQueue;
import com.company.net.Page;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by art71_000 on 17.03.2015.
 */
public class Writer implements Runnable{
    static AtomicInteger counter = new AtomicInteger(0);
    private BlockingQueue<Page> pagesToWrite;
    private String directory;
    public Writer(BlockingQueue<Page> pagesToWrite, final String directory) {
        this.pagesToWrite = pagesToWrite;
        this.directory = directory;
    }

    private void save(final Page pageToSave, final String directory) {
        BufferedWriter output = null;
        try {
            File dataFile = new File(directory + String.valueOf(counter.incrementAndGet()) + ".html");
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataFile), "UTF-8"));
            if (pageToSave.content != null) { // HTML content found!
                output.write(pageToSave.address + "\t" + pageToSave.content + "\n");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        while (pagesToWrite.size() != 0 || pagesToWrite.producersAmount.get() != 0) {
            try {
                if (!pagesToWrite.isEmpty()) {
                    Page pageToSave = pagesToWrite.take();
                    save(pageToSave, directory);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
