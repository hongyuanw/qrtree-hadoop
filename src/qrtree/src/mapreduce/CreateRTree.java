/**
 * 并行创建R树
 * by hongyuan wang
 */

package mapreduce;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import tree.RTree;
import common.FileRecursiveTool;
import common.Record;
import common.Rectangle;

public class CreateRTree {

	/**
	 * 一个map针对一个文件，把文件中的记录一条一条地插入到对应的R树中去
	 * 
	 * @author hongyuan wang
	 * 
	 */
	public static class CreateRTreeMapper extends
			Mapper<Object, Text, NullWritable, NullWritable> {

		private RTree rtree;

		// map task执行之前初始化R树的相关信息,包括设置根节点mbr
		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			rtree = new RTree();

			// 输入数据集的文件名(0,0,500,500)-r-00000,提取矩形即为R树的mbr
			FileSplit fileSplit = (FileSplit) context.getInputSplit();
			String fileName = fileSplit.getPath().getName();
			fileName = fileName.substring(0, fileName.indexOf(")") + 1);

			rtree.setQuadTreeMbr(Rectangle.getRectFromString(fileName));
		}

		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			// value:(0,0,500,500)-12.2
			int index = value.toString().indexOf("-");
			Rectangle mbr = Rectangle.getRectFromString(value.toString()
					.substring(0, index));
			double obj = Double.parseDouble(value.toString().substring(
					index + 1));
			rtree.insert(new Record(mbr, obj));
		}

		// map task执行结束后保存R树
		@Override
		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			FileSystem fs = FileSystem.get(context.getConfiguration());
			String basePath = FileOutputFormat.getOutputPath(context).toString();

			//System.out.println(basePath);
			OutputStream out = fs.create(new Path(basePath + "/" + rtree.getQuadTreeMbr().toString() + ".rt"));
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(rtree);
			oos.close();
		}
	}

	/*
	public static class CreateRTreeReducer extends
			Reducer<Text, Text, NullWritable, Text> {

		private MultipleOutputs<NullWritable, Text> multipleOutputs;

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			multipleOutputs = new MultipleOutputs<NullWritable, Text>(context);
		}

		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			for (Text value : values) {
				multipleOutputs
						.write(NullWritable.get(), value, key.toString());
			}
		}

		@Override
		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			multipleOutputs.close();
		}
	}
	*/
	
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
	    if (otherArgs.length != 2) {
	      System.err.println("Usage: CreateRTree <in> <out>");
	      System.exit(2);
	    }
		String inputpath = otherArgs[0];
		String outputpath = otherArgs[1];
	    
	    
		Job job = new Job(conf, "Create RTree");
		job.setJarByClass(CreateRTree.class);
		job.setMapperClass(CreateRTreeMapper.class);
		job.setMapOutputKeyClass(NullWritable.class);
		job.setMapOutputValueClass(NullWritable.class);

		job.setNumReduceTasks(0);		//不需要reduce过程

	
		
		// 递归地获得指定目录下的所有文件，作为建立R树的输入数据集
		Path[] inputPaths = FileRecursiveTool.getRecursivePaths(
				FileSystem.get(conf), inputpath);
		System.out.println(FileSystem.get(conf));

		FileInputFormat.setInputPaths(job, inputPaths);
		FileOutputFormat.setOutputPath(job, new Path(outputpath));
		LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
