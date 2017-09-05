package com.hyy.fof;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class Friend implements WritableComparable<Friend> {
	private String friend1;
	private String friend2;
	// 亲密度
	private int hot;

	public String getFriend1() {
		return friend1;
	}

	public void setFriend1(String friend1) {
		this.friend1 = friend1;
	}

	public String getFriend2() {
		return friend2;
	}

	public void setFriend2(String friend2) {
		this.friend2 = friend2;
	}

	public int getHot() {
		return hot;
	}

	public void setHot(int hot) {
		this.hot = hot;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		this.friend1 = in.readUTF();
		this.friend2 = in.readUTF();
		this.hot = in.readInt();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeUTF(this.friend1);
		out.writeUTF(this.friend2);
		out.writeInt(this.hot);
	}

	@Override
	public int compareTo(Friend f) {
		int c = this.friend1.compareTo(f.getFriend1());

		if (c == 0) {
			c = Integer.compare(this.hot, f.getHot());
		}

		if (c == 0) {
			c = this.friend2.compareTo(f.getFriend2());
		}
		return c;
	}

}
