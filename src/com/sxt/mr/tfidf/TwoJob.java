package com.sxt.mr.tfidf;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TwoJob {

	public static void main(String[] args) {
		Configuration config = new Configuration();
		config.set("fs.defaultFS", "hdfs://vmm01:8020");
		config.set("yarn.resourcemanager.hostname", "node3");
		try {
			Job job = Job.getInstance(config);
			job.setJarByClass(TwoJob.class);
			job.setJobName("weibo2");
			// 设置map任务的输出key类型、value类型
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);

			job.setMapperClass(TwoMapper.class);
			job.setCombinerClass(TwoReduce.class);
			job.setReducerClass(TwoReduce.class);

			// mr运行时的输入数据从hdfs的哪个目录中获取
			FileInputFormat.addInputPath(job, new Path("/user/tfidf/output/weibo1"));
			FileOutputFormat.setOutputPath(job, new Path("/user/tfidf/output/weibo2"));

			boolean f = job.waitForCompletion(true);
			if (f) {
				System.out.println("执行job成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
