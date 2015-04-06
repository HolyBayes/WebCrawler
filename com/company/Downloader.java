package com.company;

import com.company.concurrency.BlockingQueue;
import com.company.net.CrawlerUtils;
import com.company.net.Page;
import sun.security.krb5.internal.PAForUserEnc;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by art71_000 on 17.03.2015.
 */
public class Downloader implements Runnable{
    private BlockingQueue<URI> pagesToDownload;
    private BlockingQueue<Page> pagesToAnalyse;
    private ConcurrentSkipListSet<URI> visitedPages;
    private BlockingQueue<Page> pagesToWrite;
    public static AtomicInteger downloadsRest = new AtomicInteger(0);
    public Downloader(BlockingQueue<URI> pagesToDownload,
                    BlockingQueue<Page> pagesToAnalyse,
                    ConcurrentSkipListSet<URI> visitedPages,
                    BlockingQueue<Page> pagesToWrite) {
        this.pagesToAnalyse = pagesToAnalyse;
        this.pagesToDownload = pagesToDownload;
        this.visitedPages = visitedPages;
        this.pagesToWrite = pagesToWrite;
        pagesToAnalyse.producersAmount.incrementAndGet();
        pagesToWrite.producersAmount.incrementAndGet();
    }

    @Override
    public void run() {
        while (!pagesToDownload.isEmpty() && downloadsRest.get() > 0) {
            try {
                if (!pagesToDownload.isEmpty()) {
                    URI pageToDownload = pagesToDownload.take();
                    visitedPages.add(pageToDownload);
                    Page page = new Page();
                    page.address = pageToDownload;
                    downloadsRest.decrementAndGet();
                    page.content = CrawlerUtils.getContent(pageToDownload.toURL());
                    pagesToWrite.put(page);
                    pagesToAnalyse.put(page);
                }
            } catch(MalformedURLException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stop();
    }

    private void stop() {
        pagesToAnalyse.producersAmount.decrementAndGet();
        pagesToWrite.producersAmount.decrementAndGet();
    }

}

