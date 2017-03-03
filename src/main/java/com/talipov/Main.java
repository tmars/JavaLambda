package com.talipov;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    /**
     * Логгер
     */
    protected static final Logger logger = Logger.getLogger(Main.class);

    static {
        PropertyConfigurator.configure("src/main/resources/log4j.xml");
    }

    public static void main(String[] args) {

        String[] resources = {
                "src/main/resources/1.txt",
                "src/main/resources/2.txt",
                "src/main/resources/3.txt",
        };

        ExecutorService pool = Executors.newFixedThreadPool(resources.length);
        ArrayList<Future<Long>> futures = new ArrayList<>();

        for (final String resource: resources) {
            Future future = pool.submit(() ->
                ResourceReader.read(resource)
                        .stream()
                        .filter(Filter::positiveEven)
                        .reduce(0L, Reduce::sum)
            );
            futures.add(future);
        }

        long count = 0L;
        for (Future future: futures) {
            try {
                Long val = (Long) future.get();
                count += val;
                logger.trace("result = " + count + ", val = " + val);
            } catch (InterruptedException e) {
                logger.error("Прерывание на одном из потоков");
            } catch (ExecutionException e) {
                logger.error("Ошибка в работе одного из потоков");
            }
        }

        pool.shutdown();

    }
}
