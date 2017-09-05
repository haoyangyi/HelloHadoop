package com.hadoop.test.excel;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.lib.MultipleOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelContactCount extends Configured implements Tool {
	private static Logger logger = LoggerFactory
			.getLogger(ExcelContactCount.class);

	public static class ExcelMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		private static Logger LOG = LoggerFactory.getLogger(ExcelMapper.class);
		private Text pkey = new Text();
		private Text pvalue = new Text();

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			// 1.0 老爸 13999123786 2014-12-20
			String line = value.toString();
			String[] records = line.split("\\s+");
			String[] months = records[3].split("-");
			pkey.set(records[1] + "\t" + months[1]);// 昵称+月份
			pvalue.set(records[2]);// 手机号
			context.write(pkey, pvalue);
			LOG.info("Map processing finished");
		}
	}

	public static class PhoneReducer extends Reducer<Text, Text, Text, Text> {
		private Text pvalue = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			Text outKey = values.iterator().next();
			for (Text value : values) {
				sum++;
			}
			pvalue.set(outKey + "\t" + sum);
			context.write(key, pvalue);
		}
	}

	// 自定义多文件输出格式类
	public static class PhoneOutputFormat extends
			MailMultipleOutputFormat<Text, Text> {
		@Override
		protected String generateFileNameForKeyValue(Text key, Text value,
				Configuration conf) {
			// name+month
			String[] records = key.toString().split("\t");
			return records[1] + ".txt";
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
		logger.info("Driver started");

		Job job = new Job();
		job.setJarByClass(ExcelContactCount.class);
		job.setJobName("Excel Record Reader");
		job.setMapperClass(ExcelMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setInputFormatClass(ExcelInputFormat.class) ;//自定义输入格式
		
		job.setReducerClass(PhoneReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setOutputFormatClass(PhoneOutputFormat.class);//自定义输出格式

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.waitForCompletion(true);
		return 0;
	}
	
	public static void main(String[] args) throws Exception {
		String[] args0 = { "hdfs://djt:9000/junior/phone.xls",
				"hdfs://djt:9000/junior/phone-out/" };
		int ec = ToolRunner.run(new Configuration(), new ExcelContactCount(), args0);
		System.exit(ec);
	}

}
