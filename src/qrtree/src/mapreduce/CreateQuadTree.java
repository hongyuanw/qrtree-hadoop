/**
 * by hongyuan wang
 * 将空间数据进行划分，依据四叉树算法
 */

package mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import common.Rectangle;

public class CreateQuadTree {
	public static final long MAXNODES = 20000;

	// 定义四个计数器，分别记录四个子空间的节点个数
	enum SUBSPACESNODES {
		FIRST, SECONDE, THIRD, FOURTH
	}

	/**
	 * 当前欲划分的空间的mbr信息，此空间信息由map和reduce同时使用 1、2、3、4分别对应1、2、3、4象限
	 */
	public static Rectangle curSpaceMbr;
	public static Rectangle subSpaceMbr1;
	public static Rectangle subSpaceMbr2;
	public static Rectangle subSpaceMbr3;
	public static Rectangle subSpaceMbr4;

	public static class CreateQuadTreeMapper extends
			Mapper<Object, Text, Text, Text> {

		private Text spaceKey = new Text(); // 用子空间的mbr作为key

		@Override
		protected void setup(Context context)
				throws IOException, InterruptedException {
			//从job中获取全局空间信息
			curSpaceMbr = Rectangle.getRectFromString(context.getConfiguration().get("curSpaceMbr"));
			subSpaceMbr1 = Rectangle.getRectFromString(context.getConfiguration().get("subSpaceMbr1"));
			subSpaceMbr2 = Rectangle.getRectFromString(context.getConfiguration().get("subSpaceMbr2"));
			subSpaceMbr3 = Rectangle.getRectFromString(context.getConfiguration().get("subSpaceMbr3"));
			subSpaceMbr4 = Rectangle.getRectFromString(context.getConfiguration().get("subSpaceMbr4"));
		}
		
		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			// 先获得一条记录的mbr
			int splitIndex = value.toString().indexOf("-");
			String nodeMbrStr = value.toString().substring(0, splitIndex);
			Rectangle nodeMbr = Rectangle.getRectFromString(nodeMbrStr);
			
			// 判断该记录所属的子空间，设置子空间mbr为key
			if (subSpaceMbr1.contain(nodeMbr)) {
				spaceKey.set(subSpaceMbr1.toString());
			} else if (subSpaceMbr2.contain(nodeMbr)) {
				spaceKey.set(subSpaceMbr2.toString());
			} else if (subSpaceMbr3.contain(nodeMbr)) {
				spaceKey.set(subSpaceMbr3.toString());
			} else if (subSpaceMbr4.contain(nodeMbr)) {
				spaceKey.set(subSpaceMbr4.toString());
			} else {
				// 当矩形不被任何子空间完全包围时，将其置于顶层空间
				spaceKey.set(curSpaceMbr.toString());
			}

			context.write(spaceKey, value);
		}
	}

	public static class CreateQuadTreeReducer extends
			Reducer<Text, Text, NullWritable, Text> {
		private MultipleOutputs<NullWritable, Text> multipleOutputs;

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			multipleOutputs = new MultipleOutputs<NullWritable, Text>(context);
			
			//从job中获取全局空间信息
			curSpaceMbr = Rectangle.getRectFromString(context.getConfiguration().get("curSpaceMbr"));
			subSpaceMbr1 = Rectangle.getRectFromString(context.getConfiguration().get("subSpaceMbr1"));
			subSpaceMbr2 = Rectangle.getRectFromString(context.getConfiguration().get("subSpaceMbr2"));
			subSpaceMbr3 = Rectangle.getRectFromString(context.getConfiguration().get("subSpaceMbr3"));
			subSpaceMbr4 = Rectangle.getRectFromString(context.getConfiguration().get("subSpaceMbr4"));
		}

		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			//将空间数据放入对应的空间容器中，并记录容器的数据量
			for (Text value : values) {
				multipleOutputs
						.write(NullWritable.get(), value, key.toString());
				if (key.toString().equals(subSpaceMbr1.toString())) {
					context.getCounter("SUBSPACESNODES", "FIRST").increment(1);
				} else if (key.toString().equals(subSpaceMbr2.toString())) {
					context.getCounter("SUBSPACESNODES", "SECONDE")
							.increment(1);
				} else if (key.toString().equals(subSpaceMbr3.toString())) {
					context.getCounter("SUBSPACESNODES", "THIRD").increment(1);
				} else if (key.toString().equals(subSpaceMbr4.toString())) {
					context.getCounter("SUBSPACESNODES", "FOURTH").increment(1);
				}
			}
		}

		@Override
		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			multipleOutputs.close();
		}
	}

	// 递归地对给定数据集进行空间划分，直到所划分的子空间的记录数小于最大值
	private static void partition(Rectangle mbr, String inpath, String outpath)
			throws Exception {
		// 为mapreduce作业设置数据集的空间范围
		double midX = (mbr.getxMin() + mbr.getxMax()) / 2;
		double midY = (mbr.getyMin() + mbr.getyMax()) / 2;
		Rectangle mbr1 = new Rectangle(midX, midY, mbr.getxMax(), mbr.getyMax());
		Rectangle mbr2 = new Rectangle(mbr.getxMin(), midY, midX, mbr.getyMax());
		Rectangle mbr3 = new Rectangle(mbr.getxMin(), mbr.getyMin(), midX, midY);
		Rectangle mbr4 = new Rectangle(midX, mbr.getyMin(), mbr.getxMax(), midY);

		// 配置job,添加job中的空间数据属性，供task共享使用	
		Configuration conf = new Configuration();
		conf.set("curSpaceMbr", mbr.toString());
		conf.set("subSpaceMbr1", mbr1.toString());
		conf.set("subSpaceMbr2", mbr2.toString());
		conf.set("subSpaceMbr3", mbr3.toString());
		conf.set("subSpaceMbr4", mbr4.toString());
		
		Job job = new Job(conf, "Create QuadTree for " + mbr.toString());
		job.setJarByClass(CreateQuadTree.class);
		job.setMapperClass(CreateQuadTreeMapper.class);
		job.setMapOutputKeyClass(Text.class);

		job.setReducerClass(CreateQuadTreeReducer.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job, new Path(inpath));
		FileOutputFormat.setOutputPath(job, new Path(outpath));
		LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);
		job.waitForCompletion(true);

		// 继续对四个子空间进行必要的分区
		Counters counters = job.getCounters();
		long first = counters.findCounter("SUBSPACESNODES", "FIRST").getValue();
		long second = counters.findCounter("SUBSPACESNODES", "SECONDE")
				.getValue();
		long third = counters.findCounter("SUBSPACESNODES", "THIRD").getValue();
		long fourth = counters.findCounter("SUBSPACESNODES", "FOURTH")
				.getValue();

		if (first > MAXNODES) {
			partition(mbr1, outpath + "/" + mbr1.toString() + "-r-00000",
					outpath + "/" + mbr1.toString());
			FileSystem fs = FileSystem.get(new Configuration());
			fs.delete(new Path(outpath + "/" + mbr1.toString() + "-r-00000"), true);
		}
		if (second > MAXNODES) {
			partition(mbr2, outpath + "/" + mbr2.toString() + "-r-00000",
					outpath + "/" + mbr2.toString());
			FileSystem fs = FileSystem.get(new Configuration());
			fs.delete(new Path(outpath + "/" + mbr2.toString() + "-r-00000"), true);
		}
		if (third > MAXNODES) {
			partition(mbr3, outpath + "/" + mbr3.toString() + "-r-00000",
					outpath + "/" + mbr3.toString());
			FileSystem fs = FileSystem.get(new Configuration());
			fs.delete(new Path(outpath + "/" + mbr3.toString() + "-r-00000"), true);
		}
		if (fourth > MAXNODES) {
			partition(mbr4, outpath + "/" + mbr4.toString() + "-r-00000",
					outpath + "/" + mbr4.toString());
			FileSystem fs = FileSystem.get(new Configuration());
			fs.delete(new Path(outpath + "/" + mbr4.toString() + "-r-00000"), true);
		}
		
	}

	public static void main(String[] args) throws Exception {
	    Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
	    if (otherArgs.length != 2) {
	      System.err.println("Usage: CreateQuadTree <in> <out>");
	      System.exit(2);
	    }
		
	    //从命令行中获取data路径和四叉树保存路径
		String datapath = otherArgs[0];
		String spacepath = otherArgs[1];

		partition(new Rectangle(0, 0, 1000, 1000), datapath, spacepath);
	}
}
