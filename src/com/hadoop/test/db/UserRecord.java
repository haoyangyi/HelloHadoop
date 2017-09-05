package com.hadoop.test.db;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.lib.db.DBWritable;

public class UserRecord implements Writable,DBWritable{
	int uid;
	String email;
	String name;
	
	public UserRecord(){}
	
	public UserRecord(String email, String name) {
		this.email = email;
		this.name = name;
	}

	@Override
	public void readFields(ResultSet resultSet) throws SQLException {
		// TODO Auto-generated method stub
		this.uid=resultSet.getInt(1);
		this.email=resultSet.getString(2);
		this.name=resultSet.getString(3);
	}

	@Override
	public void write(PreparedStatement statement) throws SQLException {
		// TODO Auto-generated method stub
		statement.setInt(1, this.uid);
		statement.setString(2, this.email);
		statement.setString(3, this.name);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.uid=in.readInt();
		this.email=in.readUTF();
		this.name=in.readUTF();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(uid);
		out.writeUTF(email);
		out.writeUTF(name);
	}

	@Override
	public String toString() {
		return "UserRecord [uid=" + uid + ", email=" + email + ", name=" + name
				+ "]";
	}
	

}
