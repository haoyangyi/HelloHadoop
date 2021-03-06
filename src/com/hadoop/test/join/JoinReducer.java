package com.hadoop.test.join;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class JoinReducer extends Reducer<TextPair, Text, Text, Text>{
//	public static int num=0;
	@Override
	protected void reduce(TextPair key, Iterable<Text> values,
			Context context)
			throws IOException, InterruptedException {
		Iterator<Text> iter=values.iterator();
		Text stationName=new Text(iter.next());
		while(iter.hasNext()){
			Text record=iter.next();
			Text outValue=new Text(stationName.toString()+"\t"+record.toString());
			context.write(key.getFirst(), outValue);
		}
//		num++;
//		System.out.println(num+":::"+key.getFirst());
//		for(Text value:values){
//			System.out.println(key+":"+value);
//		}
	}
	
}
