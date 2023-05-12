package com.yzzer.source;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.ArrayList;
import java.util.Properties;

/**
 * @author yzzer
 */
public class SourceTest {
    public static void main(String[] args) throws Exception {
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 读取数据源
        DataStreamSource<String> clickOriginStream = env.readTextFile("data/clicks.txt");

        // 从集合中读取数据
        ArrayList<Event> events = new ArrayList<>();
        events.add(new Event("Mary", "./home", 1000L));
        events.add(new Event("Bob", "./cart", 2000L));
        DataStreamSource<Event> eventsStream = env.fromCollection(events);


        // 从元素读取数据
        env.fromElements(
                new Event("Mary", "./home", 1000L),
                new Event("Bob", "./cart", 2000L)
        ).print("from elements");


        // 从socket文本流读取
        //  DataStreamSource<String> socketStream = env.socketTextStream("localhost", 7777);

        // 从Kafka中读取数据
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "10.112.195.22:9092");
        props.setProperty("group.id", "yzzer");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("auto.offset.reset", "latest");

        FlinkKafkaConsumer<String> kafkaSource = new FlinkKafkaConsumer<>("test", new SimpleStringSchema(), props);

        DataStreamSource<String> kafkaStream = env.addSource(kafkaSource).setParallelism(1);




        kafkaStream.print("from kafka");
        // socketStream.print("from socket");
        // eventsStream.print("from collections");
        // clickOriginStream.print("from file");

        // 触发惰性提交
        env.execute("Source Test");
    }
}
