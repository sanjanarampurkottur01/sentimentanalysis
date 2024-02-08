package dal.csci5408;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.Arrays;

public class Main {
    private static final String REUTER_FILEPATH = "/home/sanjanarampurkottur01/reut2-009.sgm";

    /**
     * Entry method to process the reuter file and perform word count on the dataset
     */
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("WordFrequencyCalculator").setMaster("local[*]");
        SparkSession spark = SparkSession.builder().config(conf).getOrCreate();
        JavaRDD<String> xmlLines = spark.read().textFile(REUTER_FILEPATH).javaRDD();
        JavaRDD<String> cleanText = xmlLines.map(StringOperations::cleanString);
        JavaRDD<String> words = cleanText.flatMap(line -> Arrays.asList(line.trim().split("\\s+")).iterator());
        JavaRDD<String> lowercaseWords = words.map(String::toLowerCase);
        JavaRDD<Row> wordRows = lowercaseWords.map(RowFactory::create);
        StructType schema = DataTypes.createStructType(Arrays.asList(DataTypes.createStructField("word",
                DataTypes.StringType, true)));
        Dataset<Row> rows = spark.createDataFrame(wordRows, schema);
        rows = rows.withColumn("word", functions.trim(rows.col("word")));
        rows = rows.filter(rows.col("word").isNotNull().and(rows.col("word").notEqual("")));
        Dataset<Row> wordCountsDescDF = rows.groupBy("word").count().orderBy(functions.desc("count"));
        Dataset<Row> wordCountsAscDF = rows.groupBy("word").count().orderBy("count");
        wordCountsAscDF.show(10, false);
        wordCountsDescDF.show(10, false);
        wordCountsDescDF.write().csv("count_output");
        spark.stop();
    }
}