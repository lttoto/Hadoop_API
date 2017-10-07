package com.lt.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by taoshiliu on 2017/10/7.
 */
public class WordCount {

    public static class WordCountMapper extends Mapper<LongWritable,Text,Text,IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(LongWritable key,Text value,Context context) throws IOException,InterruptedException {

            String line = value.toString();
            StringTokenizer token = new StringTokenizer(line);

            while (token.hasMoreTokens()) {
                word.set(token.nextToken());
                context.write(word,one);
            }
        }
    }

    public static class WordCountReducer extends Reducer<Text,IntWritable,Text,IntWritable> {


        public void reduce(Text key,Iterable<IntWritable> values,Context context) throws IOException,InterruptedException{

            int sum = 0;

            for(IntWritable val:values) {
                sum += val.get();
            }

            context.write(key,new IntWritable(sum));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        /*String[] otherArgs = new GenericOptionsParser(conf,args).getRemainingArgs();
        if(otherArgs.length != 3) {
            System.err.println("Usage: wordcount <in> <out>");
            System.exit(2);
        }*/

        Job job = new Job(conf);
        job.setJarByClass(WordCount.class);
        job.setJobName("wordcount");
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        job.waitForCompletion(true);
    }

}
