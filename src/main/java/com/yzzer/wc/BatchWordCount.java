package com.yzzer.wc;


import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;


/**
 * 实现基于DataSet API 的 WordCount
 * @author yzzer
 */
public class BatchWordCount {

    public static void main(String[] args) throws Exception {
        // 创建执行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        // 从文件中读取数据
        DataSource<String> dataSource = env.readTextFile("data/words.txt");

        // 由于lambda表达式的类型擦除，需要对算子调用returns来配置返回类型
        FlatMapOperator<String, Tuple2<String, Long>> wordAndOneTuple =
                dataSource.flatMap((String s, Collector<Tuple2<String, Long>> collector) -> {
                    // 分词
                    String[] words = s.split("\\s+");

                    // 转换二元组
                    for (String word : words) {
                        collector.collect(Tuple2.of(word, 1L));
                    }
                }).returns(Types.TUPLE(Types.STRING, Types.LONG));


        // 分组
        UnsortedGrouping<Tuple2<String, Long>> wordAndOneGroup = wordAndOneTuple.groupBy(0);

        // 聚合
        AggregateOperator<Tuple2<String, Long>> wordAndCount = wordAndOneGroup.sum(1);

        // 打印输出
        wordAndCount.print();
    }

}
