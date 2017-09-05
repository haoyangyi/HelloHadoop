package com.hadoop.test.score;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class ScoreWritable implements WritableComparable<Object>{

	private float Chinese;
	private float Math;
	private float English;
	private float Physics;
	private float Chemistry;
	
	public ScoreWritable(){}
	
	public ScoreWritable(float chinese, float math, float english,
			float physics, float chemistry) {
		Chinese = chinese;
		Math = math;
		English = english;
		Physics = physics;
		Chemistry = chemistry;
	}

	public void set(float Chinese,float Math,float English,float Physics,float Chemistry){
	    this.Chinese = Chinese;
	    this.Math = Math;
	    this.English = English;
	    this.Physics = Physics;
	    this.Chemistry = Chemistry;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		Chinese=in.readFloat();
		Math=in.readFloat();
		English=in.readFloat();
		Physics=in.readFloat();
		Chemistry=in.readFloat();
	}
	

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeFloat(Chinese);
		out.writeFloat(Math);
		out.writeFloat(English);
		out.writeFloat(Physics);
		out.writeFloat(Chemistry);
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getChinese() {
		return Chinese;
	}

	public void setChinese(float chinese) {
		Chinese = chinese;
	}

	public float getMath() {
		return Math;
	}

	public void setMath(float math) {
		Math = math;
	}

	public float getEnglish() {
		return English;
	}

	public void setEnglish(float english) {
		English = english;
	}

	public float getPhysics() {
		return Physics;
	}

	public void setPhysics(float physics) {
		Physics = physics;
	}

	public float getChemistry() {
		return Chemistry;
	}

	public void setChemistry(float chemistry) {
		Chemistry = chemistry;
	}

	
}
