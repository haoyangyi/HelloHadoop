package com.hadoop.test.db;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ConnMysqlMapper1 extends Mapper< LongWritable,Text,Text,Text>
{  
     public void map(LongWritable key,Text value,Context context)throws IOException,InterruptedException
     {  
			//读取 hdfs 中的数据
     	String email = value.toString().split("\\s")[0];
     	String name = value.toString().split("\\s")[1];
         context.write(new Text(email),new Text(name));  
     }  
}  
