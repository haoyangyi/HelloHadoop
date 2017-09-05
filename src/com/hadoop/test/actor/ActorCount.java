package com.hadoop.test.actor;

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

public class ActorCount extends Configured implements Tool{

	public static class ActorMapper extends Mapper<Object, Text, Text, Text>{

		@Override
		protected void map(Object key, Text value,
				Mapper<Object, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			String[] tokens=value.toString().split("\t");
			String gender=tokens[1];
			String nameHot=tokens[0]+"\t"+tokens[2];
			context.write(new Text(gender), new Text(nameHot));
		}
		
		
	}
	
	public static class ActorCombiner extends Reducer<Text, Text, Text, Text>{

		@Override
		protected void reduce(Text key, Iterable<Text> values,
				Context context)
				throws IOException, InterruptedException {
			int maxHot=Integer.MIN_VALUE;
			int hot=0;
			String name="";
			for(Text value:values){
				String[] tokens=value.toString().split("\t");
				hot=Integer.parseInt(tokens[1]);
				if(hot>maxHot){
					maxHot=hot;
					name=tokens[0];
				}
			}
			context.write(key, new Text(name+"\t"+maxHot));
		}
		
	}
	public static class ActorPartitioner extends Partitioner<Text, Text>{

		@Override
		public int getPartition(Text key, Text value, int numReduceTasks) {
			String gender=key.toString();
			if(numReduceTasks==0) return 0;
			if(gender.equals("male")) return 0;
			if(gender.equals("female")) return 1%numReduceTasks;
			else return 2%numReduceTasks;
		}
		
	}
	public static class ActorReducer extends Reducer<Text, Text, Text, Text>{
		
		@Override
		protected void reduce(Text key, Iterable<Text> values,
				Context context)
				throws IOException, InterruptedException {
			int maxHot=Integer.MIN_VALUE;
			int hot=0;
			String name="";
			for(Text value:values){
				String[] tokens=value.toString().split("\t");
				hot=Integer.parseInt(tokens[1]);
				if(hot>maxHot){
					maxHot=hot;
					name=tokens[0];
				}
			}
			context.write(new Text(name), new Text( key + "\t"+ maxHot));
		}
	} 
	
	@Override
	public int run(String[] args) throws Exception {
		Configuration conf=new Configuration();
		Path mypath=new Path(args[1]);
		FileSystem hdfs=mypath.getFileSystem(conf);
		if(hdfs.isDirectory(mypath)){
			hdfs.delete(mypath,true);
		}
		Job job=Job.getInstance(conf, "actor");
		job.setJarByClass(ActorCount.class);
		job.setNumReduceTasks(2);
		job.setMapperClass(ActorMapper.class);
		job.setCombinerClass(ActorCombiner.class);
		job.setPartitionerClass(ActorPartitioner.class);
		job.setReducerClass(ActorReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));// 输入路径
		FileOutputFormat.setOutputPath(job, new Path(args[1]));// 输出路径
		job.waitForCompletion(true);//提交任务
		return 0;
	}

	public static void main(String[] args) throws Exception {
		String[] args0 = { "hdfs://djt:9000/middle/actor/actor.txt",
				"hdfs://djt:9000/middle/actor/out/" };
		int ec = ToolRunner.run(new Configuration(), new ActorCount(),
				args0);
		System.exit(ec);
	}
}
