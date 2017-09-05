package com.hyy.fof;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class FofJobTwo extends Configured implements Tool{

	@Override
	public int run(String[] args) throws Exception {
		//1.创建配置文件
		Configuration conf=new Configuration();
		
		//2.创建job
		Job job=Job.getInstance(conf, FofJobTwo.class.getSimpleName());
		//设置job
		job.setJarByClass(FofJobTwo.class);
		
		//输入路径
		FileInputFormat.addInputPath(job, new Path(args[0]));
        //输出目录重复删除输出目录
		Path outPath=new Path(args[1]);
		FileSystem hdfs=outPath.getFileSystem(conf);
		if(hdfs.isDirectory(outPath)){
			hdfs.delete(outPath,true);
		}
		//输出路径
		FileOutputFormat.setOutputPath(job, outPath);
		
		//map
		job.setMapperClass(FofMapperTwo.class);
		job.setMapOutputKeyClass(Friend.class);
		job.setMapOutputValueClass(IntWritable.class);

		//shuffle
//		job.setPartitionerClass(TQPartition.class);
		//不设置默认为map输出key类的排序方式，例如本次如果不配置则使用Friend类的compareTo的方法进行比较
		job.setSortComparatorClass(FofSort.class);
//		job.setCombinerClass(null);
		//不设置默认与分组排序方式一样！！！！！
		job.setGroupingComparatorClass(FofGroup.class);
		
		//reduce
		job.setReducerClass(FofReducerTwo.class);
//		job.setOutputKeyClass(Text.class);
//		job.setOutputValueClass(IntWritable.class);
//		job.setNumReduceTasks(3);
		
		//提交任务
		job.waitForCompletion(true);
		
		return 0;
	}

	public static void main(String[] args) throws Exception {
		String[] args0 = { "hdfs://vmm01:8020/fof/output1/",
		"hdfs://vmm01:8020/fof/output2/" };
		int ec = ToolRunner.run(new Configuration(), new FofJobTwo(),
				args0);
		
		System.out.println("KO!!!!");
		System.exit(ec);
	}

}
