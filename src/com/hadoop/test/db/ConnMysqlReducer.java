package com.hadoop.test.db;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ConnMysqlReducer extends Reducer<Text, Text, Text, Text> {
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		// 将数据输出到HDFS中
		for (Iterator<Text> itr = values.iterator(); itr.hasNext();) {
			context.write(key, itr.next());
		}
	}
}
