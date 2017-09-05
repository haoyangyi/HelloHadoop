package com.hadoop.test;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * 统计美国各个气象站30年来的平均气温
 * @author hyy
 *
 */
public class Temperature extends Configured implements Tool{
	
	
	public static class TemperatureMapper extends Mapper<LongWritable,Text,Text,IntWritable>{
		public void map(LongWritable key,Text value,Context context) throws IOException, InterruptedException{
			//数据示例：1985 07 31 02  200 94 10137 220 26 1 0 -9999
			String line=value.toString();//读取每行的数据
			int temperature=Integer.parseInt(line.substring(14,19).trim());
			if(temperature!=-9999){//过滤无效数据
				FileSplit fileSplit=(FileSplit) context.getInputSplit();
				//通过文件名称获取气象站id
				String weatherStationId=fileSplit.getPath().getName().substring(5, 10);
				//map输出
				context.write(new Text(weatherStationId), new IntWritable(temperature));
			}
		}
	}
	
	public static class TemperatureReducer extends Reducer<Text,IntWritable,Text,IntWritable>{
		private IntWritable result=new IntWritable();
		
		public void reduce(Text key,Iterable<IntWritable> values,Context context) throws IOException, InterruptedException{
			int sum=0;
			int count=0;
			//循环values，对统一气象站的所有温度值求和
			for(IntWritable val:values){
				sum+=val.get();
				count++;
			}
			//求每个气象站的平均值
			result.set(sum/count);
			//reduce输出
			context.write(key, result);
		}
	}
	
	/**
	 * 任务驱动方法
	 */
	@Override
	public int run(String[] args) throws Exception {
		Configuration conf=new Configuration();//读取配置
		
		Path mypath=new Path(args[1]);
		FileSystem hdfs=mypath.getFileSystem(conf);
		if(hdfs.isDirectory(mypath)){//已经存在的输出删除目录
			hdfs.delete(mypath, true);
		}
		
		Job job=new Job(conf, "temperature");//新建一个任务
		job.setJarByClass(Temperature.class);//主类
		
		FileInputFormat.addInputPath(job, new Path(args[0]));// 输入路径
		FileOutputFormat.setOutputPath(job, new Path(args[1]));// 输出路径

		job.setMapperClass(TemperatureMapper.class);// Mapper
		job.setReducerClass(TemperatureReducer.class);// Reducer

		job.setOutputKeyClass(Text.class);//输出结果的key类型
		job.setOutputValueClass(IntWritable.class);//输出结果的value类型

		
		return job.waitForCompletion(true) ? 0 : 1;//提交任务
	}
	
	public static void main(String[] args) throws Exception {
		//数据输入路径和输出路径
		String[] args0={"hdfs://djt:9000/weather","hdfs://djt:9000/weather/out"};
		int ec=ToolRunner.run(new Configuration(), new Temperature(), args);
		System.exit(ec);
	}

}
