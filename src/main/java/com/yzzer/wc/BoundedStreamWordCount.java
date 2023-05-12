package com.yzzer.wc;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * 基于DataStream API 实现的 WordCount
 * @author yzzer
 */
public class BoundedStreamWordCount {


    public static void main(String[] args) throws Exception {

        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置批处理模式 默认为   RuntimeExecutionMode.STREAMING
        env.setRuntimeMode(RuntimeExecutionMode.BATCH);

        // 批处理可以在运行时指定  flink run -Dexecution.runtime-mode=BATCH

        env.setParallelism(1);

        // 读取数据流
        DataStreamSource<String> textStream = env.readTextFile("data/words.txt");

        // 分词
        SingleOutputStreamOperator<Tuple2<String, Long>> wordAndOneStream =
                textStream.flatMap((String line, Collector<Tuple2<String, Long>> collector) -> {
                    String[] words = line.split("\\s+");
                    for (String word : words) {
                        collector.collect(Tuple2.of(word, 1L));
                    }
                }).returns(Types.TUPLE(Types.STRING, Types.LONG));

        // group by and aggregate
        SingleOutputStreamOperator<Tuple2<String, Long>> resultStream = wordAndOneStream.keyBy( data -> data.f0).sum(1);

        resultStream.print().setParallelism(1);

        // 提交执行
        env.execute("word count");
    }
}
