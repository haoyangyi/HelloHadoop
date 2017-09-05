package com.hyy.wc;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * wordcount 统计单词数目
 * @author root
 *
 */
public class WCJob extends Configured implements Tool {
	
	/**
	 * mapper
	 * @author root
	 *
	 */
	public static class WCJobMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
	    private final static IntWritable one = new IntWritable(1);
	    private Text word = new Text();
		@Override
		protected void map(LongWritable key, Text value,
				Context context)
				throws IOException, InterruptedException {
			
			String line=value.toString();
			String[] records = line.split("\\s+");// 使用空格正则解析数据
			for(String record:records){
				word.set(record.trim());
				context.write(word, one);
			}
		}
	}
	
	/**
	 * reducer
	 * @author root
	 *
	 */
	public static class WCJobReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		private IntWritable reslut=new IntWritable();
		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Context context)
				throws IOException, InterruptedException {
			int sum=0;
			for(IntWritable value:values){
				sum+=value.get();
			}
			reslut.set(sum);
			context.write(key, reslut);
		}
		
	}


	
	@Override
	public int run(String[] args) throws Exception {
		//1.创建配置文件
		Configuration conf=new Configuration();
		
		//2.创建job
		Job job=Job.getInstance(conf, WCJob.class.getSimpleName());
		//设置job
		job.setJarByClass(WCJob.class);
		//输入路径
		FileInputFormat.addInputPath(job, new Path(args[0]));
		
		//map
		job.setMapperClass(WCJobMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
//		
//	
//		job.setPartitionerClass(HashPartitioner.class);
//		job.setCombinerClass(null);
		
		//reduce
		job.setReducerClass(WCJobReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
//		job.setSortComparatorClass(TestComparator.class);
		//job.setGroupingComparatorClass(cls);
		//输出路径
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		//提交任务
		job.waitForCompletion(true);
		
		return 0;
	}

	public static void main(String[] args) throws Exception {
		String[] args0 = { "hdfs://vmm01:8020//wc/input/wc",
		"hdfs://vmm01:8020//wc/output/" };
		int ec = ToolRunner.run(new Configuration(), new WCJob(),
				args0);
		
		System.out.println("KO!!!!");
		System.exit(ec);
	}

}
