package com.company;

import com.company.concurrency.BlockingQueue;
import com.company.net.CrawlerUtils;
import com.company.net.Page;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by art71_000 on 16.03.2015.
 */
public class Analyser implements Runnable{
    private BlockingQueue<URI> pagesToDownload;
    private BlockingQueue<Page> pagesToAnalyse;
    final private ConcurrentSkipListSet<URI> visitedPages;
    public Analyser(BlockingQueue<Page> pagesToAnalyse,
                    BlockingQueue<URI> pagesToDownload,
                    final ConcurrentSkipListSet<URI> visitedPages) {
        this.pagesToAnalyse = pagesToAnalyse;
        this.pagesToDownload = pagesToDownload;
        this.visitedPages = visitedPages;
    }

    @Override
    public void run() {
        while (pagesToAnalyse
                .producersAmount.get() != 0
                || !pagesToAnalyse.isEmpty()) {
            try {
                if (!pagesToAnalyse.isEmpty()) {
                    Page pageToAnalyse =
                            pagesToAnalyse.take();
                    if (pageToAnalyse != null) {
                        Set<URI> links = CrawlerUtils.getLinks(pageToAnalyse.address.toURL(), pageToAnalyse.content);
                        for (URI link : links) {
                            if (!visitedPages.contains(link)) {
                                pagesToDownload.put(link);
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }


}
