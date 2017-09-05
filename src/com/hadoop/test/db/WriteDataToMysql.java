package com.hadoop.test.db;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.lib.db.DBConfiguration;
import org.apache.hadoop.mapred.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

public class WriteDataToMysql {
	public static void main(String args[]) throws IOException,
			InterruptedException, ClassNotFoundException {
		Configuration conf = new Configuration();
		// 配置 JDBC 驱动、数据源和数据库访问的用户名和密码
		DBConfiguration.configureDB(conf, "com.mysql.jdbc.Driver",
				"jdbc:mysql://192.168.1.100:3306/djtdb",
				"root", "root");
		Job job = new Job(conf, "test mysql connection");// 新建一个任务
		job.setJarByClass(WriteDataToMysql.class);// 主类

		job.setMapperClass(ConnMysqlMapper1.class); // Mapper
		job.setReducerClass(ConnMysqlReducer1.class); // Reducer

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(DBOutputFormat.class);// 向数据库写数据
		// 输入路径
		FileInputFormat
				.addInputPath(
						job,
						new Path(
								"hdfs://djt:9000/db/mysql/data/data.txt"));
		// 设置输出到数据库 表名：test 字段：uid、email、name
		DBOutputFormat.setOutput(job, "user", "uid", "email", "name");
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
