package com.hadoop.mould;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MapReduceMould extends Configured implements Tool{

	@Override
	public int run(String[] args) throws Exception {
		
		//1.创建配置文件
		Configuration conf=new Configuration();
		
		//2.创建job
		Job job=Job.getInstance(conf, MapReduceMould.class.getSimpleName());
		//设置job
		job.setJarByClass(MapReduceMould.class);
		//输入路径
		FileInputFormat.addInputPath(job, new Path(args[0]));
		
		//map
		job.setMapperClass(Mapper.class);
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(Text.class);
		
	
		job.setPartitionerClass( HashPartitioner.class);
		job.setCombinerClass(null);
		
		//reduce
		job.setReducerClass(Reducer.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(Text.class);
		

		
		//输出路径
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		//提交任务
		job.waitForCompletion(true);
		
		return 0;
	}
	
	public static void main(String[] args) throws Exception {
		String[] args0 = { "hdfs://djt:9000/mould/in/test.txt",
		"hdfs://djt:9000/mould/out/" };
		int ec = ToolRunner.run(new Configuration(), new MapReduceMould(),
				args0);
		System.exit(ec);
	}

}
