package com.hyy.pagerank;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;


public class PageRankJob extends Configured implements Tool{



	static class PageRankMapper extends Mapper<Text, Text, Text, Text> {
		@Override
		protected void map(Text key, Text value, Context context)
				throws IOException, InterruptedException {
			int runCount = context.getConfiguration().getInt("runCount", 1);
			// A	B	D
			String page = key.toString();
			Node node = null;
			if (runCount == 1) { //第一次计算 初始化PR值为1.0
				node = Node.fromMR("1.0" + "\t" + value.toString());
			} else {
				node = Node.fromMR(value.toString());
			}
			// A:1.0 B D
			// 将计算前的数据输出  reduce计算做差值
			context.write(new Text(page), new Text(node.toString()));

			if (node.containsAdjacentNodes()) {
				double outValue = node.getPageRank() / node.getAdjacentNodeNames().length;
				for (int i = 0; i < node.getAdjacentNodeNames().length; i++) {
					String outPage = node.getAdjacentNodeNames()[i];
					// B:0.5
					// D:0.5
					context.write(new Text(outPage), new Text(outValue + ""));
				}
			}
		}
	}

	static class PageRankReducer extends Reducer<Text, Text, Text, Text> {
		@Override
		protected void reduce(Text key, Iterable<Text> iterable, Context context)
				throws IOException, InterruptedException {
			double sum = 0.0;
			Node sourceNode = null;
			for (Text i : iterable) {
				Node node = Node.fromMR(i.toString());
				//A:1.0 B D
				if (node.containsAdjacentNodes()) {
					// 计算前的数据 // A:1.0 B D
					sourceNode = node;
					System.out.println(key+"1:"+node.getPageRank());
				} else {
					// B:0.5 // D:0.5
					sum = sum + node.getPageRank();
					System.out.println(key+"2:"+node.getPageRank());
				}
			}

			// 计算新的PR值  4为页面总数
			double newPR = (0.15 / 4.0) + (0.85 * sum);
			System.out.println("*********** new pageRank value is " + newPR);

			// 把新的pr值和计算之前的pr比较
			double d = newPR - sourceNode.getPageRank();

			int j = (int) (d * 1000.0);
			j = Math.abs(j);
			System.out.println(j + "___________");
			// 累加
			context.getCounter(Mycounter.my).increment(j);

			sourceNode.setPageRank(newPR);
			context.write(key, new Text(sourceNode.toString()));
		}
	}
	
	public static enum Mycounter {
		//枚举
		my
	}

	@Override
	public int run(String[] arg0) throws Exception {
		
		return 0;
	}
	
	public static void main(String[] args) {
		Configuration config = new Configuration();
		config.set("fs.defaultFS", "hdfs://vmm01:8020");
		config.set("yarn.resourcemanager.hostname", "vmm03");
		
//		config.set("mapred.jar", "D://MR/pagerank.jar");
		
		// 收敛值
		double d = 0.001;
		int i = 0;
		while (true) {
			i++;
			try {
				// 记录计算的次数
				config.setInt("runCount", i);

				FileSystem fs = FileSystem.get(config);
				Job job = Job.getInstance(config);

				job.setJarByClass(PageRankJob.class);
				job.setJobName("pr" + i);
				job.setMapperClass(PageRankMapper.class);
				job.setReducerClass(PageRankReducer.class);
				job.setMapOutputKeyClass(Text.class);
				job.setMapOutputValueClass(Text.class);
				job.setInputFormatClass(KeyValueTextInputFormat.class);

				Path inputPath = new Path("/pagerank/input/pagerank.txt");

				if (i > 1) {
					inputPath = new Path("/pagerank/output/pr" + (i - 1));
				}
				FileInputFormat.addInputPath(job, inputPath);

				Path outpath = new Path("/pagerank/output/pr" + i);
				if (fs.exists(outpath)) {
					fs.delete(outpath, true);
				}
				FileOutputFormat.setOutputPath(job, outpath);

				boolean f = job.waitForCompletion(true);
				if (f) {
					System.out.println("success.");
					// 获取 计数器 中的差值
					long sum = job.getCounters().findCounter(Mycounter.my).getValue();

					System.out.println("SUM:  " + sum);
					double avgd = sum / 4000.0;
					if (avgd < d) {
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		
	
	}
}
