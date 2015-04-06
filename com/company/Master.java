package com.company;

import com.company.concurrency.BlockingQueue;
import com.company.net.Page;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by art71_000 on 17.03.2015.
 */
public class Master implements Runnable {
    public int WRITERS_AMOUNT;
    public int ANALYSERS_AMOUNT;
    public int DOWNLOADERS_AMOUNT;
    public int QUEUE_SIZE;
    private int depth;
    private URL rootPage;
    private String directory;
    private int downloadsLimit;

    public Master (Main.MasterConfigs configs) {
        this.WRITERS_AMOUNT = configs.WRITERS_AMOUNT;
        this.ANALYSERS_AMOUNT = configs.ANALYSERS_AMOUNT;
        this.DOWNLOADERS_AMOUNT = configs.DOWNLOADERS_AMOUNT;
        this.QUEUE_SIZE = configs.QUEUE_SIZE;
        this.depth = configs.depth;
        this.rootPage = configs.rootPage;
        this.directory = configs.directory;
        this.downloadsLimit = configs.downloadsLimit;
    }

    @Override
    public void run() {
        BlockingQueue<Page> pagesToAnalyse = new BlockingQueue<Page>(QUEUE_SIZE);
        BlockingQueue<URI> pagesToDownload = new BlockingQueue<URI>(QUEUE_SIZE);
        BlockingQueue<URI> pagesForNextDownload = new BlockingQueue<URI>(QUEUE_SIZE);
        BlockingQueue<Page> pagesToWrite = new BlockingQueue<Page>(QUEUE_SIZE);
        ConcurrentSkipListSet<URI> visitedPages = new ConcurrentSkipListSet<URI>();
        Thread[] downloaders = new Thread[DOWNLOADERS_AMOUNT];
        try {
            pagesForNextDownload.add(rootPage.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Downloader.downloadsRest.set(downloadsLimit);

        Thread[] analysers = new Thread[ANALYSERS_AMOUNT];

        Thread[] writers = new Thread[WRITERS_AMOUNT];

        for (int i = 0; i < depth; ++i) {
            try {
                pagesToDownload.addAll(pagesForNextDownload);
                pagesForNextDownload.clear();
                for (Thread downloader : downloaders) {
                    downloader = new Thread(new Downloader(pagesToDownload, pagesToAnalyse, visitedPages, pagesToWrite));
                    downloader.start();
                }
                for (Thread analyser : analysers) {
                    analyser = new Thread(new Analyser(pagesToAnalyse, pagesForNextDownload, visitedPages));
                    analyser.start();
                }
                for (Thread writer : writers) {
                    writer = new Thread(new Writer(pagesToWrite, directory));
                    writer.start();
                }
                for (Thread downloader : downloaders) {
                    if (downloader != null) {
                        downloader.join();
                    }
                }
                for (Thread analyser : analysers) {
                    if (analyser != null) {
                        analyser.join();
                    }
                }
                for (Thread writer : writers) {
                    if (writer != null) {
                        writer.join();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
