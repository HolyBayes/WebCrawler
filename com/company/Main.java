package com.company;

import org.omg.CORBA.INTERNAL;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class Main {
    public static class MasterConfigs {
        public URL rootPage;
        public int depth = 2;
        public int downloadsLimit = 500;
        public String directory = "";
        public final int WRITERS_AMOUNT = 1;
        public final int ANALYSERS_AMOUNT = 2;
        public final int DOWNLOADERS_AMOUNT = 2;
        public final int QUEUE_SIZE = 1000;
    }
    public static void main(String[] args) {
        MasterConfigs configs = new MasterConfigs();
        try {
            if (args.length > 0) {
                if (args[0] != null) {
                    configs.rootPage = new URL(args[0]);
                }
            } else {
                configs.rootPage = new URL("http://www.yandex.ru");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();;
        }
        if (args.length > 1) {
            if (args[1] != null) {
                configs.depth = Integer.parseInt(args[1]);
            }
        }
        if (args.length > 2) {
            if (args[2] != null) {
                configs.downloadsLimit = Integer.parseInt(args[2]);
            }
        }
        if (args.length > 3) {
            if (args[3] != null) {
                configs.directory = args[3];
            }
        }
        Thread masterThread = new Thread(new Master(configs));
        masterThread.start();
        try {
            masterThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
