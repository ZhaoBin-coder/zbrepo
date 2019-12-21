package baobiao

import java.util.Properties

import config.ConfigHelper
import org.apache.spark.sql.{SaveMode, SparkSession}
import utils.{Kpi, TurnType}

object App2Mysql {
  def main(args: Array[String]): Unit = {


    val session = SparkSession.builder()
      .master("local[*]")
      .appName(this.getClass.getSimpleName)
      .config("serializer", ConfigHelper.SparkSerializer)
      .getOrCreate()

    val source = session.read.parquet("hdfs://10.10.0.12/flume/test")
    import session.implicits._
    val pro = new Properties()
    pro.setProperty("driver", ConfigHelper.driver)
    pro.setProperty("user", ConfigHelper.user)
    pro.setProperty("password", ConfigHelper.password)
    source.map(row => {
      val appname = row.getAs[String]("appname")
      val list = Kpi.getKpi(row)
      (appname, list)
    }).rdd.reduceByKey((list1, list2) => {
      list1.zip(list2).map(x => x._1 + x._2)
    }).map(tp => {
      (tp._1, tp._2(0), tp._2(1), tp._2(2), tp._2(3), tp._2(4),TurnType.result(tp._2(4),tp._2(3)), tp._2(7), tp._2(8), TurnType.result(tp._2(6),tp._2(5)), tp._2(5), tp._2(6))
    }).toDF("app名称", "总请求", "有效请求", "广告请求", "参与竞价数", "竞价成功数", "竞价成功率", "展示量", "点击量", "点击率", "广告成本", "广告消费")
      .write.mode(SaveMode.Overwrite).jdbc(ConfigHelper.url, "app", pro)

  }
}
