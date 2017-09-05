package com.hadoop.test.crime;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import au.com.bytecode.opencsv.CSVReader;

/**
 * 
 * @function 从 map/reduce的输出结果中读取并提取数据
 * @author 小讲
 * 
 */
public abstract class DataFile {

	/**
	 * @function 从 map/reduce job 的输出结果，提取key值集合
	 * @param fn HDFS上的文件路径
	 * @return list  key值的集合
	 * @throws IOException
	 */
    public static List<String> extractKeys(String fn,FileSystem fs) throws IOException {
    	FSDataInputStream in = fs.open(new Path(fn));//打开文件
    	List<String> retVal = new ArrayList<String>();//新建存储key值的集合list
    	BufferedReader br = new BufferedReader(new InputStreamReader(in));
    	String line = br.readLine();//按行读取数据
    	while  (line != null) {
    		String[] lp = line.split("\t");
    		if (lp.length > 0) {
    			retVal.add(lp[0]);//提取每行的第一个字段key
    		}
    		line = br.readLine();
    	}
    	br.close();
    	Collections.sort(retVal);//对key值进行排序
    	return retVal;
    }
    
    /**
     * @function 将 csv文件格式的每行内容转换为数组返回
     * @param 读取的一行数据
     * @return array 数组
     * @throws IOException
     */
    public static String[] getColumns(String line) throws IOException {
		CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(line.getBytes())));
		String[] retVal = reader.readNext();
		reader.close();
		return retVal;
	}
    	
}
