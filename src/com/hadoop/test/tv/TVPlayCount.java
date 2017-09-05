package com.hadoop.test.tv;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
public class TVPlayCount extends Configured implements Tool {

	public static class TVPlayMapper extends
			Mapper<Text, TVPlayData, Text, TVPlayData> {

		@Override
		protected void map(Text key, TVPlayData value, Context context)
				throws IOException, InterruptedException {
			context.write(key, value);
		}

	}

	public static class TVPlayReducer extends
			Reducer<Text, TVPlayData, Text, Text> {
		private Text m_key = new Text();
		private Text m_value = new Text();
		private MultipleOutputs<Text, Text> mos;

		@Override
		protected void cleanup(
				Reducer<Text, TVPlayData, Text, Text>.Context context)
				throws IOException, InterruptedException {
			if (mos != null) {
				mos.close();
			}
		}

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			mos = new MultipleOutputs<Text, Text>(context);
		}

		@Override
		protected void reduce(Text key, Iterable<TVPlayData> values,
				Context context) throws IOException, InterruptedException {
			int daynumber=0;
			int collectnumber=0;
			int commentnumber=0;
			int againstnumber=0;
			int supportnumber=0;
			for(TVPlayData value:values){
				daynumber+=value.getDaynumber();
				collectnumber+=value.getCollectnumber();
				commentnumber+=value.getCommentnumber();
				againstnumber+=value.getAgainstnumber();
				supportnumber+=value.getSupportnumber();
			}
			String[] records=key.toString().split("\t");
			String source=records[1];
			m_key.set(records[0]);
			m_value.set(daynumber+"\t"+collectnumber+"\t"+commentnumber+"\t"+againstnumber+"\t"+supportnumber);
			if(source.equals("1")){
				mos.write("youku", m_key, m_value);
			}else if(source.equals("2")){
				mos.write("souhu", m_key, m_value);
			}else if(source.equals("3")){
				mos.write("tudou", m_key, m_value);
			}else if(source.equals("4")){
				mos.write("aiqiyi", m_key, m_value);
			}else if(source.equals("5")){
				mos.write("xunlei", m_key, m_value);
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

		Job job = Job.getInstance(conf,  "tvplay");
		//Job job = new Job(conf, "weibo");// 构造任务
		job.setJarByClass(TVPlayCount.class);// 主类

		job.setMapperClass(TVPlayMapper.class);// Mapper
		job.setMapOutputKeyClass(Text.class);// Mapper key输出类型
		job.setMapOutputValueClass(TVPlayData.class);// Mapper value输出类型
        
		job.setReducerClass(TVPlayReducer.class);// Reducer
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));// 输入路径
		FileOutputFormat.setOutputPath(job, new Path(args[1]));// 输出路径
		job.setInputFormatClass(TVPlayInputFormat.class) ;// 自定义输入格式
		//自定义文件输出类别
		MultipleOutputs.addNamedOutput(job, "youku", TextOutputFormat.class,
						Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "tudou", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "souhu", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "aiqiyi", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "xunlei", TextOutputFormat.class,
				Text.class, Text.class);
		job.waitForCompletion(true);
		return 0;
	}
	
	public static void main(String[] args) throws Exception {
		String[] args0 = { "hdfs://djt:9000/junior/tvplay.txt",
		"hdfs://djt:9000/junior/tvplay-out/"};
		int ec = ToolRunner.run(new Configuration(), new TVPlayCount(), args0);
		System.exit(ec);
	}

}
