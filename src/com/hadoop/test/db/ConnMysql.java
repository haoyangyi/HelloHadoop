package com.hadoop.test.db;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.lib.db.DBConfiguration;
import org.apache.hadoop.mapred.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ConnMysql {
	public static void main(String[] args) throws Exception {
		 Configuration conf = new Configuration();
		 //输出路径
        Path output = new Path("hdfs://djt:9000/db/mysql/out");
        
        FileSystem fs = FileSystem.get(URI.create(output.toString()), conf);
        if (fs.exists(output)) {
                fs.delete(output,true);
        }
        
        //mysql的jdbc驱动
        DistributedCache.addFileToClassPath(new Path("hdfs://djt:9000/db/jar/mysql-connector-java-5.1.20-bin.jar"), conf);  
        //设置mysql配置信息   4个参数分别为： Configuration对象、mysql数据库地址、用户名、密码
        DBConfiguration.configureDB(conf, "com.mysql.jdbc.Driver", "jdbc:mysql://192.168.1.100:3306/djtdb", "root", "root");  
        
        Job job =Job.getInstance(conf, "test mysql connection"); 
        job.setJarByClass(ConnMysql.class);//主类
        
        job.setMapperClass(ConnMysqlMapper.class);//Mapper
        job.setReducerClass(ConnMysqlReducer.class);//Reducer
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        
        job.setInputFormatClass(DBInputFormat.class);//从数据库中读取数据
        FileOutputFormat.setOutputPath(job, output);
        
        //列名
        String[] fields = { "uid", "email","name" }; 
        //六个参数分别为：
        //1.Job;2.Class< extends DBWritable> 3.表名;4.where条件 5.order by语句;6.列名
		DBInputFormat.setInput(job, UserRecord.class,"user", null, null, fields);           
        System.exit(job.waitForCompletion(true) ? 0 : 1);
        }
}
