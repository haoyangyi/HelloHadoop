package com.hadoop.test.count;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MyCounter extends Configured implements Tool {
	public static class MyCounterMap extends
			Mapper<LongWritable, Text, Text, Text> {
		// 定义枚举对象
		public static enum LOG_PROCESSOR_COUNTER {
			BAD_RECORDS_LONG, BAD_RECORDS_SHORT
		};

		protected void map(LongWritable key, Text value, Context context)
				throws java.io.IOException, InterruptedException {
			//按空格分割
			String arr_value[] = value.toString().split("\\s+");
			if (arr_value.length > 3) {
				/* 动态自定义计数器 */
				context.getCounter("ErrorCounter", "toolong").increment(1);
				/* 枚举声明计数器 */
				context.getCounter(LOG_PROCESSOR_COUNTER.BAD_RECORDS_LONG)
						.increment(1);
			} else if (arr_value.length < 3) {
				// 动态自定义计数器
				context.getCounter("ErrorCounter", "tooshort").increment(1);
				// 枚举声明计数器
				context.getCounter(LOG_PROCESSOR_COUNTER.BAD_RECORDS_SHORT)
						.increment(1);
			}
		}
	}

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = new Configuration();// 配置文件对象
		Path mypath = new Path(args[1]);
		FileSystem hdfs = mypath.getFileSystem(conf);// 创建输出路径
		if (hdfs.isDirectory(mypath)) {
			hdfs.delete(mypath, true);
		}
		Job job = new Job(conf, "MyCounter");
		job.setJarByClass(MyCounter.class);
		//只有mapper
		job.setMapperClass(MyCounterMap.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.waitForCompletion(true);
		return 0;
	}

	public static void main(String[] args) throws Exception {
		String[] args0={"hdfs://djt:9000/counter/","hdfs://djt:9000/counter/out"};
		int ec=ToolRunner.run(new Configuration(), new MyCounter(), args0);
		System.exit(ec);
	}
}
