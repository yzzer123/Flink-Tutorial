package com.yzzer.wc;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * 基于DataStream API 实现的 WordCount
 * @author yzzer
 */
public class StreamWordCount {


    public static void main(String[] args) throws Exception {

        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 从参数中提取主机名和端口号
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String host = parameterTool.get("host");
        int port = parameterTool.getInt("port");

        // 读取文本流 使用 nc -lk 7899 发送数据
        DataStreamSource<String> sourceStream = env.socketTextStream(host, port );

        // 分词
        SingleOutputStreamOperator<Tuple2<String, Long>> wordAndOneStream =
                sourceStream.flatMap((String line, Collector<Tuple2<String, Long>> collector) -> {
                    String[] words = line.split("\\s+");

                    for (String word : words) {
                        collector.collect(Tuple2.of(word, 1L));
                    }
                }).returns(Types.TUPLE(Types.STRING, Types.LONG));

        // 分组聚合
        SingleOutputStreamOperator<Tuple2<String, Long>> wordCountStream = wordAndOneStream.keyBy(data -> data.f0)
                .sum(1);

        wordCountStream.print();

        env.execute("word count");

    }
}
