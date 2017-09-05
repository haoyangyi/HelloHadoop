package com.hadoop.test.join;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

public class TextPair implements WritableComparable<TextPair>{
	private Text first;
	private Text second;
	
	public TextPair(){
		set(new Text(),new Text());
	}
	
	public TextPair(String first,String second){
		set(new Text(first),new Text(second));
	}
	
	public void set(Text first, Text second) {
		this.first=first;
		this.second=second;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		first.readFields(in);
		second.readFields(in);
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		first.write(out);
		second.write(out);
	}
	
	@Override
	public int compareTo(TextPair o) {
		if(!first.equals(o.first)){
			return first.compareTo(o.first);
		}else if(!second.equals(o.second)){
			return second.compareTo(o.second);
		}else{
			return 0; 
		}
	}

	@Override
	public int hashCode() {
		return first.hashCode()*163+second.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TextPair){
			TextPair tp=(TextPair) obj;
			return first.equals(tp.first)&&second.equals(tp.second);
		}
		return false;
	}

	@Override
	public String toString() {
 		return first+"\t"+second;
	}

	public Text getFirst() {
		return first;
	}

	public void setFirst(Text first) {
		this.first = first;
	}

	public Text getSecond() {
		return second;
	}

	public void setSecond(Text second) {
		this.second = second;
	}
	
	
}
