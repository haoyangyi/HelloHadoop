package com.hadoop.test.join;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class JoinStationMapper extends Mapper< LongWritable,Text,TextPair,Text>{

	@Override
	protected void map(LongWritable key, Text value,
			Mapper<LongWritable, Text, TextPair, Text>.Context context)
			throws IOException, InterruptedException {
		String line=value.toString();
		String[] arr=line.split("\\s+");
		int length=arr.length;
		if(length==2){
			context.write(new TextPair(arr[0], "0"), new Text(arr[1]));
		}
	}
	
}
