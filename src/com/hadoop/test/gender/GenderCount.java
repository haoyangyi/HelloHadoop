package com.hadoop.test.gender;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class GenderCount extends Configured implements Tool {

	public static class PCMapper extends Mapper<Object, Text, Text, Text> {

		@Override
		protected void map(Object key, Text value,
				Mapper<Object, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			String[] tokens = value.toString().split("<tab>");
			String gender = tokens[2].toString();
			String nameAgeScore = tokens[0] + "\t" + tokens[1] + "\t"
					+ tokens[3];
			context.write(new Text(gender), new Text(nameAgeScore));
		}

	}

	public static class PCReducer extends Reducer<Text, Text, Text, Text> {

		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			int maxScore = Integer.MIN_VALUE;
			String name = " ";
			String age = " ";
			String gender = " ";
			int score = 0;
			for (Text value : values) {
				String[] valTokens = value.toString().split("\\t");
				score = Integer.parseInt(valTokens[2]);
				if (score > maxScore) {
					maxScore=score;
					name = valTokens[0];
					age = valTokens[1];
					gender = key.toString();
				}
			}
			context.write(new Text(name), new Text("age- " + age + "\t"+ gender + "\tscore-" + maxScore));
		}

	}

	public static class PCPartitioner extends Partitioner<Text, Text> {

		@Override
		public int getPartition(Text key, Text value, int numReduceTasks) {
			String[] nameAgeScore = value.toString().split("\\t");
			String age = nameAgeScore[1];
			int ageInt = Integer.parseInt(age);
			if (numReduceTasks == 0)
				return 0;
			if (ageInt <= 20)
				return 0;
			if (ageInt > 20 && ageInt <= 50) {
				return 1 % numReduceTasks;
			} else {
				return 2 % numReduceTasks;
			}
		}

	}

	@Override
	public int run(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();// 读取配置文件

		Path mypath = new Path(args[1]);
		FileSystem hdfs = mypath.getFileSystem(conf);
		if (hdfs.isDirectory(mypath)) {
			hdfs.delete(mypath, true);
		}

		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "gender");// 新建一个任务
		job.setJarByClass(GenderCount.class);// 主类
		job.setMapperClass(PCMapper.class);// Mapper
		job.setReducerClass(PCReducer.class);// Reducer

		job.setPartitionerClass(PCPartitioner.class);// 设置Partitioner类
		job.setNumReduceTasks(3);// reduce个数设置为3

		job.setMapOutputKeyClass(Text.class);// map 输出key类型
		job.setMapOutputValueClass(Text.class);// map 输出value类型

		job.setOutputKeyClass(Text.class);// 输出结果 key类型
		job.setOutputValueClass(Text.class);// 输出结果 value 类型

		FileInputFormat.addInputPath(job, new Path(args[0]));// 输入路径
		FileOutputFormat.setOutputPath(job, new Path(args[1]));// 输出路径

		return job.waitForCompletion(true) ? 0 : 1;// 提交任务
	}

	public static void main(String[] args) throws Exception {
		String[] args0 = { "hdfs://djt:9000/junior/gender.txt",
				"hdfs://djt:9000/junior/gender-out/" };
		int ec = ToolRunner.run(new Configuration(), new GenderCount(),
				args0);
		System.exit(ec);
	}

}
