package com.yzzer.source;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;

/**
 * @author yzzer
 */
public class MySource implements SourceFunction<Event> {
    private Boolean isRunning = true;

    @Override
    public void run(SourceContext<Event> ctx) throws Exception {

        Random random = new Random();

        String[] users = {"Mary", "Bob", "Cary", "yzzer"};
        String[] urls = {"./home", "./cart", "./fav", "./prod?id=100", "./prod?id=10"};


        // 循环生成数据
        while (isRunning){
            ctx.collect(new Event(
                    users[random.nextInt(users.length)],
                    urls[random.nextInt(urls.length)],
                    System.currentTimeMillis()
            ));

            // 每隔一秒生成一次数据
            Thread.sleep(1000L);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
