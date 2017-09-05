package com.hadoop.test.weibo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class WeiBo implements WritableComparable<Object> {
	private int friends;
	private int followers;
	private int statuses;
	
	public WeiBo(){}
	
	public WeiBo(int friends, int followers, int statuses) {
		this.friends = friends;
		this.followers = followers;
		this.statuses = statuses;
	}

	
	
	public int getFriends() {
		return friends;
	}
	public void setFriends(int friends) {
		this.friends = friends;
	}
	public int getFollowers() {
		return followers;
	}
	public void setFollowers(int followers) {
		this.followers = followers;
	}
	public int getStatuses() {
		return statuses;
	}
	public void setStatuses(int statuses) {
		this.statuses = statuses;
	}

	// 实现WritableComparable的readFields()方法，以便该数据能被序列化后完成网络传输或文件输入
	@Override
	public void readFields(DataInput in) throws IOException {
		friends=in.readInt();
		followers=in.readInt();
		statuses=in.readInt();
	}

	// 实现WritableComparable的write()方法，以便该数据能被序列化后完成网络传输或文件输出 
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(friends);
		out.writeInt(followers);
		out.writeInt(statuses);
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void set(int followers, int friends, int statuses) {
		this.followers=followers;
		this.friends=friends;
		this.statuses=statuses;
	}
	
}
