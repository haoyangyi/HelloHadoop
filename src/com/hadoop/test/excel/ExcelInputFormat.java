package com.hadoop.test.excel;

import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class ExcelInputFormat extends FileInputFormat {

	@Override
	public RecordReader createRecordReader(InputSplit arg0,
			TaskAttemptContext arg1) throws IOException, InterruptedException {
		return new ExcelRecordReader();
	}
	
	public class ExcelRecordReader extends RecordReader<LongWritable,Text>{
		private LongWritable key;
		private Text value;
		private InputStream is;
		private String[] strArrayofLines;
		
		@Override
		public void initialize(InputSplit genericSplit, TaskAttemptContext context)
				throws IOException, InterruptedException {
			FileSplit split=(FileSplit) genericSplit;
			Configuration job=context.getConfiguration();
			Path file=split.getPath();
			
			FileSystem fs=file.getFileSystem(job);
			FSDataInputStream fileIn=fs.open(file);
			
			is=fileIn;
			String line=new ExcelParser().parseExcelData(is);
			this.strArrayofLines=line.split("\n");//将返回数据解析为数组strArrayofLines
		}
		
		@Override
		public boolean nextKeyValue() throws IOException, InterruptedException {
			if(key==null){
				key=new LongWritable(0);
				value=new Text(strArrayofLines[0]);
			}else{
				if(key.get()<(this.strArrayofLines.length-1)){
					long pos=(int) key.get();
					key.set(pos+1);
					value.set(this.strArrayofLines[(int)(pos+1)]);
				}else{
					return false;
				}
				
			}
			
			if(key==null||value==null){
				return false;
			}else{
				return true;
			}
		}
		

		@Override
		public LongWritable getCurrentKey() throws IOException,
				InterruptedException {
			
			return key;
		}
		
		@Override
		public Text getCurrentValue() throws IOException, InterruptedException {
			return value;
		}
		
		@Override
		public float getProgress() throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public void close() throws IOException {
			if(is!=null){
				is.close();
			}
		}







		
	}

}
