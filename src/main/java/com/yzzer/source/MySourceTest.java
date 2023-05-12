package com.yzzer.source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author yzzer
 */
public class MySourceTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(3);

//        DataStreamSource<Event> eventStream = env.addSource(new MySource());
        DataStreamSource<Event> eventStream = env.addSource(new MyParallelSource());

        eventStream.print("from my source");

        env.execute();

    }
}
