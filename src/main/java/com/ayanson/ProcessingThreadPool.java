package com.ayanson;

import java.util.ArrayList;
import java.util.concurrent.*;

public class ProcessingThreadPool {

    private ArrayList<Item> unprepared = new ArrayList<Item>();
    private ExecutorService executor;
    private MonitorThread monitor;
    private Thread monitorThread;

    public ProcessingThreadPool(int threadCount) {
        executor= Executors.newFixedThreadPool(threadCount);
        monitor = new MonitorThread(executor, threadCount, this.unprepared);
        monitorThread = new Thread(monitor);
        monitorThread.start();
    }

    public void add(int itemId, int groupId) {
        unprepared.add(new Item(itemId, groupId));
    }

    public  void  shutdown() {
        monitor.shutdown();
    }

}
