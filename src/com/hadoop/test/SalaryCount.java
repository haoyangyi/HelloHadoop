package com.hadoop.test;

import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * 基于样本数据做Hadoop工程师薪资统计：计算各工作年限段的薪水范围
 */
public class SalaryCount extends Configured implements Tool {

	public static class SalaryMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			// 示例数据：美团 3-5年经验 15-30k 北京 hadoop高级工程
			String line = value.toString();// 读取每行数据

			String[] record = line.split("\\s+");// 使用空格正则解析数据
			// key=record[1]：输出3-5年经验
			// value=record[2]：15-30k
			// 作为Mapper输出，发给 Reduce 端

			if (record.length >= 3) {

				context.write(new Text(record[1]), new Text(record[2]));

			}
		}
	}

	public static class SalaryReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text Key, Iterable<Text> Values, Context context)
				throws IOException, InterruptedException {

			int low = 0;// 记录最低工资
			int high = 0;// 记录最高工资
			int count = 1;
			// 针对同一个工作年限（key），循环薪资集合（values），并拆分value值，统计出最低工资low和最高工资high
			for (Text value : Values) {
				String[] arr = value.toString().split("-");
				int l = filterSalary(arr[0]);
				int h = filterSalary(arr[1]);
				if (count == 1 || l < low) {
					low = l;
				}
				if (count == 1 || h > high) {
					high = h;
				}
				count++;
			}
			context.write(Key, new Text(low + "-" + high + "k"));

		}
	}

	// 正则表达式提取工资值
	public static int filterSalary(String salary) {
		String sal = Pattern.compile("[^0-9]").matcher(salary).replaceAll("");
		return Integer.parseInt(sal);
	}

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = new Configuration();// 读取配置文件
		Path out = new Path(args[1]);
		FileSystem hdfs = out.getFileSystem(conf);
		if (hdfs.isDirectory(out)) {// 删除已经存在的输出目录
			hdfs.delete(out, true);
		}

		Job job = new Job(conf, "SalaryCount");// 新建一个任务
		job.setJarByClass(SalaryCount.class);// 主类

		FileInputFormat.addInputPath(job, new Path(args[0]));// 文件输入路径
		FileOutputFormat.setOutputPath(job, new Path(args[1]));// 文件输出路径

		job.setMapperClass(SalaryMapper.class);// Mapper
		job.setReducerClass(SalaryReducer.class);// Reducer

		job.setOutputKeyClass(Text.class);// 输出结果key类型
		job.setOutputValueClass(Text.class);// 输出结果的value类型

		return job.waitForCompletion(true) ? 0 : 1;// 等待完成退出作业
	}

	/**
	 * @param args
	 *            输入文件、输出路径，可在Eclipse中Run Configurations中配Arguments，如：
	 *            hdfs://djt:9000/junior/salary.txt
	 *            hdfs://djt:9000/junior/salary-out/
	 */
	public static void main(String[] args) throws Exception {
		try {
			// 数据输入路径和输出路径
//			String[] args0 = { "hdfs://djt:9000/junior/salary.txt",
//					"hdfs://djt:9000/junior/salary-out/" };
			String[] args0 = { "hdfs://djt:9000/salary/salary.txt",
			"hdfs://djt:9000/salary/salary-out11/" };
			int res = ToolRunner.run(new Configuration(), new SalaryCount(),
					args0);
			System.exit(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}