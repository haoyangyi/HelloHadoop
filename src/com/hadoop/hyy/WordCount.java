package com.hadoop.hyy;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class WordCount extends Configured implements Tool{
	
	
	
	public static class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
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
	
	public static class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
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

	public static class TestComparator extends WritableComparator{
		protected TestComparator() {
			super(Text.class, true);
		}
		@Override
		public int compare(WritableComparable a, WritableComparable b) {
			Text text1 = (Text) a;
			Text text2 = (Text) b;
			int num=text2.toString().compareTo(text1.toString());
			System.out.println(text1+" 比 "+text1+"  "+num);
			return num;
		}
		
	}
	
	
	@Override
	public int run(String[] args) throws Exception {
		//1.创建配置文件
		Configuration conf=new Configuration();
		
		//2.创建job
		Job job=Job.getInstance(conf, WordCount.class.getSimpleName());
		//设置job
		job.setJarByClass(WordCount.class);
		//输入路径
		FileInputFormat.addInputPath(job, new Path(args[0]));
		
		//map
		job.setMapperClass(WordCountMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
//		
//	
//		job.setPartitionerClass(HashPartitioner.class);
//		job.setCombinerClass(null);
		
		//reduce
		job.setReducerClass(WordCountReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setSortComparatorClass(TestComparator.class);
		//job.setGroupingComparatorClass(cls);
		//输出路径
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		//提交任务
		job.waitForCompletion(true);
		
		return 0;
	}

	public static void main(String[] args) throws Exception {
//		String[] args0 = { "hdfs://hyy1:8020/hyy/wordcount/in/data.txt",
//		"hdfs://hyy1:8020/hyy/wordcount/out22" };
		String[] args0 = { "hdfs://dajiangtai:9000/dajiangtai/wordcount/in/data.txt",
		"hdfs://dajiangtai:9000/dajiangtai/wordcount/out" };
		int ec = ToolRunner.run(new Configuration(), new WordCount(),
				args0);
		System.exit(ec);
	}
}
