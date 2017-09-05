package com.hadoop.test.sort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class IntPair implements WritableComparable<IntPair>{
	int first;
	int second;
	
	public void set(int first,int second){
		this.first=first;
		this.second=second;
	}
	
	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		first=in.readInt();
		second=in.readInt();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(first);
		out.writeInt(second);
	}

	@Override
	public int compareTo(IntPair o) {
		if(first!=o.first){
			return first<o.first?-1:1;
		}else if(second!=o.second){
			return second<o.second?-1:1;
		}else{
			return 0;
		}
	}
	
	@Override
	public int hashCode(){
		return first * 157 + second;
	}
	@Override
	public boolean equals(Object right){
		if (right == null)
			return false;
		if (this == right)
			return true;
		if (right instanceof IntPair){
			IntPair r = (IntPair) right;
			return r.first == first && r.second == second;
		}else{
			return false;
		}
	}
	
}
