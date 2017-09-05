package com.hadoop.test.weibo;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.LineReader;

public class WeiboInputFormat extends FileInputFormat<Text, WeiBo> {

	
	@Override
	public RecordReader<Text, WeiBo> createRecordReader(InputSplit arg0,
			TaskAttemptContext arg1) throws IOException, InterruptedException {
		return new WeiboRecordReader();
	}
	
	public class WeiboRecordReader extends RecordReader<Text,WeiBo>{
		public LineReader in;
		public Text lineKey;
		public WeiBo lineValue;
		public Text line;
		@Override
		public void close() throws IOException {
			if(in!=null){
				in.close();
			}
		}

		@Override
		public Text getCurrentKey() throws IOException, InterruptedException {
			return lineKey;
		}

		@Override
		public WeiBo getCurrentValue() throws IOException, InterruptedException {
			return lineValue;
		}

		@Override
		public float getProgress() throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void initialize(InputSplit input, TaskAttemptContext context)
				throws IOException, InterruptedException {
			FileSplit split=(FileSplit) input;
			Configuration job=context.getConfiguration();
			Path file=split.getPath();
			FileSystem fs=file.getFileSystem(job);
			
			FSDataInputStream filein=fs.open(file);
			in=new LineReader(filein, job);
			line=new Text();
			lineKey=new Text();
			lineValue=new WeiBo();
		}

		@Override
		public boolean nextKeyValue() throws IOException, InterruptedException {
			int linesize=in.readLine(line);
			if(linesize==0) return false;
			String[] pieces=line.toString().split("\t");
			if(pieces.length!=5){
				throw new IOException("Invalid record received");
			}
			
			int a,b,c;
			
			try {
				a=Integer.parseInt(pieces[2].trim());
				b=Integer.parseInt(pieces[3].trim());
				c=Integer.parseInt(pieces[4].trim());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new IOException("Error parsing floating poing value in record");
			}
			
			lineKey.set(pieces[0]);
			lineValue.set(a,b,c);
			return true;
		}
		
	}

}
