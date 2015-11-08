package com.ayanson;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class MonitorThread implements Runnable {

    ExecutorService executor;
    private  boolean run=true;
    private  int threadCount;
    private final int groupLimit = 2;
    private  ArrayList<Item> unprepared;
    private int preparedCount = 0;
    private HashMap<Integer, PriorityQueue<Integer>> prepared = new HashMap<Integer, PriorityQueue<Integer>>();
    private int[] threadLoad;

    public MonitorThread(ExecutorService executor, int threadCount, ArrayList<Item> unprepared){
        this.executor = executor;
        this.threadCount = threadCount;
        this.unprepared = unprepared;
    }

    public void run() {
        Main.logger.info(Thread.currentThread().getName() + " Monitor on "+ Calendar.getInstance().getTime());

        while (run) {
            try {
                synchronized (unprepared) {

                    if (!unprepared.isEmpty()) {
                        //Сортировка данных из очереди по группам
                        sortToGroups();
                    }
                }

                if (preparedCount > 0) {

                    //Распределяем загрузку по потокам
                    ArrayList<ArrayList<Item>> threadsData = balancingData();

                    //Запускаем
                    Main.logger.info("Start");
                    CountDownLatch latch = new CountDownLatch(threadsData.size());
                    for (ArrayList<Item> data : threadsData)
                        executor.execute(new ProcessThread(data, latch));

                    //Ждем окончания обработки
                    latch.await();
                    Main.logger.info("Finish");
                }

                Thread.sleep(1000);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Main.logger.info(Thread.currentThread().getName() + " Monitor off " + Calendar.getInstance().getTime());
        executor.shutdown();
    }

    public void shutdown() {
        this.run=false;
    }

    private  void  sortToGroups() {
        for(Item item : unprepared) {
            preparedCount++;

            PriorityQueue<Integer> q = prepared.get(item.getGroupId());

            if (q!=null)
                q.add(item.getItemId());
            else {
                PriorityQueue<Integer> queue = new PriorityQueue<Integer>();
                queue.add(item.getItemId());
                prepared.put(item.getGroupId(), queue);
            }
        }
        unprepared.clear();
    }

    private ArrayList<ArrayList<Item>> balancingData() {
        ArrayList<ArrayList<Item>> threadsData = new ArrayList<ArrayList<Item>>();

        for (Integer key : prepared.keySet()) {
            PriorityQueue<Integer> queue = prepared.get(key);
            if (queue.size() > 0) {
                int index = minThreadIndex(threadsData);
                int takeCount = queue.size() < groupLimit ? queue.size() : groupLimit;

                for (int i = 0; i < takeCount; i++) {
                    int itemId = queue.poll();
                    preparedCount--;
                    threadsData.get(index).add(new Item(itemId, key));
                }

            }
        }

        return  threadsData;
    }

    private int minThreadIndex(ArrayList<ArrayList<Item>> list) {
        if (list.size() < threadCount) {
            list.add(new ArrayList<Item>());
            return list.size()-1;
        }
        else {
            int index = -1;
            int count = Integer.MAX_VALUE;


            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).size()<count)
                {
                    index = i;
                    count = list.get(i).size();
                }
            }

            return  index;
        }
    }

}
