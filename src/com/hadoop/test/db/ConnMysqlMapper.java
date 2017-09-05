package com.hadoop.test.db;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ConnMysqlMapper extends
		Mapper<LongWritable, UserRecord, Text, Text> {
	public void map(LongWritable key, UserRecord values, Context context)
			throws IOException, InterruptedException {
		// 从 mysql 数据库读取需要的数据字段
		context.write(new Text(values.uid + ""), new Text(values.name + " "
				+ values.email));
	}
}
