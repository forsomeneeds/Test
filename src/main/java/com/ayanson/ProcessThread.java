package com.ayanson;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

public class ProcessThread implements Runnable {

    private ArrayList<Item> taskList;
    private Random random = new Random();
    private  CountDownLatch latch;
    private Sql sql;

    public ProcessThread(ArrayList<Item> taskList, CountDownLatch latch){
        this.taskList = taskList;
        this.latch = latch;
        this.sql = new Sql();
    }

    public void run() {
        for(Item item : taskList) {
            Main.logger.info(Thread.currentThread().getName() + " ItemId=" + item.getItemId() + ". GroupId=" + item.getGroupId());
            processCommand(item.getItemId());
        }

        latch.countDown();
    }

    private void processCommand(int itemId) {
        try {
            Thread.sleep(random.nextInt(300)+301);
            sql.deleteItem(itemId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}