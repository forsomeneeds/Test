package com.ayanson;

import java.io.IOException;
import org.apache.log4j.Logger;

public class Main {

    public static final org.apache.log4j.Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException, IOException {

        if (args.length>0 && args[0].equals("-h")) {
            System.out.println("java -jar test-1.0-SNAPSHOT.one-jar.jar X Y");
            System.out.println("X - имитировать обращение к БД. 1- да, 0 - нет");
            System.out.println("Y - число потоков");
            System.out.println("Без параметров - имитация обращений, 4 потока");
            return;
        }

        int threadCount = 4;
        if (args.length == 2) {
            Sql.fakeMode =  args[0].equals("1");

            try {
                threadCount = Integer.parseInt(args[1]);
            } catch (Exception ex) {
                logger.error("Incorrect threadCount. Using defaults", ex);
            }
        }
        else {
            logger.info("Using default parameters");
        }

        logger.info("fakeSqlMode " + (Sql.fakeMode ? "on" : "off") + ", threadCount=" + threadCount);

        Sql sql= new Sql();

        ProcessingThreadPool pool = new ProcessingThreadPool(threadCount);

        for(Item item : sql.getItems()) {
            //можно и сразу item передавать, но раз сказано метод с 2 параметрами, то пускай
            pool.add(item.getItemId(), item.getGroupId());
        }

        //Ожидание нажатия клавиши, чтобы можно было рассмотреть результат
        int i = System.in.read();
        pool.shutdown();

    }
}
