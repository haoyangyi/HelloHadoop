package com.hyy.fof;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.StringUtils;

public class FofMapperTwo extends Mapper<LongWritable, Text, Friend, IntWritable>{

	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		String[] strs=StringUtils.split(value.toString(), '-');
		
		//hadoop-hello
		Friend friend1=new Friend();
		friend1.setFriend1(strs[0]);
		friend1.setFriend2(strs[1]);
		friend1.setHot(Integer.parseInt(strs[2]));
		context.write(friend1, new IntWritable(friend1.getHot()));
		
		//hello-hadoop
		Friend friend2=new Friend();
		friend2.setFriend1(strs[1]);
		friend2.setFriend2(strs[0]);
		friend2.setHot(Integer.parseInt(strs[2]));
		context.write(friend2, new IntWritable(friend2.getHot()));
	}
	
	
}
