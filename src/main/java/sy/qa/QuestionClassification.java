package sy.qa;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.NaiveBayes;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.IDF;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class QuestionClassification implements Serializable {

    SparkSession session = null;
    public QuestionClassification(SparkSession session) {
        this.session = session;
    }

    /**
     * 训练
     * @param trainFile
     * @param modelFile
     */
    public PipelineModel train(String trainFile, String modelFile) {

        StructType schema = new StructType(new StructField[] {
                new StructField("label", DataTypes.IntegerType, false, Metadata.empty()),
                new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
        });

        JavaRDD<Row> rowJavaRDD = session.read().textFile(trainFile).repartition(14).toJavaRDD().mapPartitions(new FlatMapFunction<Iterator<String>, Row>() {
            @Override
            public Iterator<Row> call(Iterator<String> input) throws Exception {
                List<Row> listRow = new ArrayList<>();
                while(input.hasNext()) {
                    String[] line = input.next().split(",");
                    Integer label = Integer.valueOf(line[0]);
                    String feature = line[1];
                    listRow.add(RowFactory.create(label, feature));
                }
                return listRow.iterator();
            }
        });

        System.out.println("total samples： " + rowJavaRDD.collect().size());
        Dataset<Row> dataset = session.createDataFrame(rowJavaRDD, schema);

        Tokenizer tokenizer = new Tokenizer()
                .setInputCol("sentence")
                .setOutputCol("words");

        HashingTF hashingTF = new HashingTF()
                .setNumFeatures(1000)
                .setInputCol(tokenizer.getOutputCol())
                .setOutputCol("rowfeatures");

        IDF idf = new IDF()
                .setInputCol(hashingTF.getOutputCol())
                .setOutputCol("features");

        NaiveBayes nb = new NaiveBayes()
                .setSmoothing(0.001);

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[] {tokenizer, hashingTF, idf, nb});

        PipelineModel model = pipeline.fit(dataset);

//        try {
//            model.save(modelFile);
//            System.out.println("model save to : " + modelFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return model;
    }

    /**
     * 预测
     * @param modelFile
     * @param text
     * @return
     */
    public double predict(String modelFile, String text) {
        StructType schema = new StructType(new StructField[] {
                new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
        });
        List<Row> predictRow = new ArrayList<>();
        predictRow.add(RowFactory.create(text));
        Dataset<Row> prediction = session.createDataFrame(predictRow, schema);
        PipelineModel model = PipelineModel.load(modelFile);
        Dataset<Row> predictions = model.transform(prediction);
        return predictions.select("prediction").collectAsList().get(0).getDouble(0);
    }

    /**
     * 预测
     * @param model
     * @param text
     * @return
     */
    public double predict(PipelineModel model, String text) {
        StructType schema = new StructType(new StructField[] {
                new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
        });
        List<Row> predictRow = new ArrayList<>();
        predictRow.add(RowFactory.create(text));
        Dataset<Row> prediction = session.createDataFrame(predictRow, schema);
        Dataset<Row> predictions = model.transform(prediction);
        return predictions.select("prediction").collectAsList().get(0).getDouble(0);
    }

}
