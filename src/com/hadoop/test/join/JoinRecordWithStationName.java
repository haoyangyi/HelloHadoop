package com.hadoop.test.join;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class JoinRecordWithStationName extends Configured implements Tool{

	public static class KeyPartitioner extends Partitioner<TextPair,Text>{

		@Override
		public int getPartition(TextPair key, Text value, int numPartitions) {
			return (key.getFirst().hashCode()&Integer.MAX_VALUE)%numPartitions;
		}
		
	}
	
	public static class GroupingComparator extends WritableComparator{
		protected GroupingComparator(){
			super(TextPair.class,true);
		}

		@Override
		public int compare(WritableComparable a, WritableComparable b) {
			TextPair tp1=(TextPair) a;
			TextPair tp2=(TextPair) b;
			Text text1=tp1.getFirst();
			Text text2=tp2.getFirst();
			return text1.compareTo(text2);
		}
		
		
	}
	@Override
	public int run(String[] args) throws Exception {
		Configuration conf=new Configuration();
		
		FileSystem hdfs = FileSystem.get(new URI("hdfs://djt:9000"), conf);
		Path out = new Path(args[2]);
		if (hdfs.isDirectory(out)) {
			hdfs.delete(out, true);
		}
		
		Job job=Job.getInstance(conf,"join");
		job.setJarByClass(JoinRecordWithStationName.class);
		
		Path recordInputPath=new Path(args[0]);
		Path stationInputPath=new Path(args[1]);
		Path outputPath=new Path(args[2]);
		
		MultipleInputs.addInputPath(job, recordInputPath, TextInputFormat.class, JoinRecordMapper.class);
		MultipleInputs.addInputPath(job, stationInputPath, TextInputFormat.class, JoinStationMapper.class);
		
		FileOutputFormat.setOutputPath(job, outputPath);
		

		job.setReducerClass(JoinReducer.class);
		job.setNumReduceTasks(2);
		
		job.setPartitionerClass(KeyPartitioner.class);
		job.setGroupingComparatorClass(GroupingComparator.class);
		
		job.setMapOutputKeyClass(TextPair.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		return job.waitForCompletion(true)?0:1;
	}
	
	public static void main(String[] args) throws Exception {
		String[] args1={
				"hdfs://djt:9000/middle/temperature/records.txt",
				"hdfs://djt:9000/middle/temperature/station.txt",
				"hdfs://djt:9000/middle/temperature/out"
				};
		int exitCode = ToolRunner.run(new JoinRecordWithStationName(),args1);
		System.exit(exitCode);
	}

}
