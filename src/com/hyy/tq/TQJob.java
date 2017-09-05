package com.hyy.tq;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.hyy.tq.TQGroup;
import com.hyy.tq.TQMapper;
import com.hyy.tq.TQPartition;
import com.hyy.tq.TQReducer;
import com.hyy.tq.TQSort;

/**
 * 天气文件二次排序
 * @author root
 *
 */
public class TQJob extends Configured implements Tool {

//	public static class TQMapper extends Mapper<LongWritable, Text, Weather, IntWritable> {
//		
//	}
//	
//	public class TQReducer extends Reducer<Weather, IntWritable, Text, NullWritable> {
//		
//	}
//	
//	public class TQPartition extends HashPartitioner<Weather, IntWritable> {
//		
//	}
//	
//	public class TQGroup extends WritableComparator {
//		
//	}
//	
//	public class TQSort extends WritableComparator {}
	
	@Override
	public int run(String[] args) throws Exception {
		//1.创建配置文件
		Configuration conf=new Configuration();
		
		//2.创建job
		Job job=Job.getInstance(conf, TQJob.class.getSimpleName());
		//设置job
		job.setJarByClass(TQJob.class);
		//输入路径
		FileInputFormat.addInputPath(job, new Path(args[0]));
		
		//map
		job.setMapperClass(TQMapper.class);
		job.setMapOutputKeyClass(Weather.class);
		job.setMapOutputValueClass(IntWritable.class);
//		
//	
		job.setPartitionerClass(TQPartition.class);
//		job.setCombinerClass(null);
		job.setSortComparatorClass(TQSort.class);
		job.setGroupingComparatorClass(TQGroup.class);
		
		//reduce
		job.setReducerClass(TQReducer.class);
//		job.setOutputKeyClass(Text.class);
//		job.setOutputValueClass(IntWritable.class);
		job.setNumReduceTasks(3);
		
        //输出目录重复删除输出目录
		Path outPath=new Path(args[1]);
		FileSystem hdfs=outPath.getFileSystem(conf);
		if(hdfs.isDirectory(outPath)){
			hdfs.delete(outPath,true);
		}
		//输出路径
		FileOutputFormat.setOutputPath(job, outPath);
		//提交任务
		job.waitForCompletion(true);
		
		return 0;
	}

	public static void main(String[] args) throws Exception {
		String[] args0 = { "hdfs://vmm01:8020//tq/input/tq",
		"hdfs://vmm01:8020//tq/output/" };
		int ec = ToolRunner.run(new Configuration(), new TQJob(),
				args0);
		
		System.out.println("KO!!!!");
		System.exit(ec);
	}
}
