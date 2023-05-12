package com.yzzer.source;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

/**
 * @author yzzer
 */
public class KafkaTest {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "10.112.195.22:9092");
        props.setProperty("group.id", "yzzer");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("auto.offset.reset", "latest");

        FlinkKafkaConsumer<String> kafkaSource = new FlinkKafkaConsumer<>("test", new SimpleStringSchema(), props);

    }
}
