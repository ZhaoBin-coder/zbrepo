package baobiao

import java.util.Properties

import config.ConfigHelper
import org.apache.ivy.core.module.descriptor.ConfigurationGroup
import org.apache.spark.sql.{SaveMode, SparkSession}
import utils.Kpi

object Net2Mysql {
  def main(args: Array[String]): Unit = {
   val session=SparkSession.builder()
     .master("local[*]")
     .appName(this.getClass.getSimpleName)
     .config("serializer",ConfigHelper.SparkSerializer)
     .getOrCreate()
    val source = session.read.parquet("hdfs://10.10.0.12:8020/flume/test")
    import session.implicits._
    val prop=new Properties()
    prop.setProperty("driver",ConfigHelper.driver)
    prop.setProperty("user",ConfigHelper.user)
    prop.setProperty("password",ConfigHelper.password)
    val result = source.map(row => {
      val networkmannername = row.getAs[String]("networkmannername")
      val list = Kpi.getKpi(row)
      (networkmannername, list)
    }).rdd.reduceByKey((list1, list2) => {
      list1.zip(list2).map(x => x._1 + x._2)
    })
       result.map(line=>{(line._1,line._2(0),line._2(1),line._2(2),line._2(3),line._2(4),line._2(7),line._2(8),line._2(5),line._2(6))}).toDF("连网方式","总请求","有效请求","广告请求","参与竞价数","竞价成功数","展示量","点击量","广告成本","广告消费").write.mode(SaveMode.Overwrite).jdbc(ConfigHelper.url,"net",prop)
  }
}
