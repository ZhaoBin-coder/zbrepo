package report

import java.util.Properties

import config.ConfigHelper
import org.apache.spark.rdd.RDD
import org.apache.spark.sql
import org.apache.spark.sql.SaveMode


object Area2Mysql {
  def main(args: Array[String]): Unit = {
      //读取parquet文件
      val session=new sql.SparkSession.Builder().
        appName(this.getClass.getSimpleName)
        .master("local[*]")
        .config("serializer",ConfigHelper.SparkSerializer)
        .getOrCreate()

      val soruce = session.read.parquet("F:/Test")
      import session.implicits._
      val res: RDD[((String, String), Int)] = soruce.map(line => {
        val provincename = line.getAs[String]("provincename")
        val cityname = line.getAs[String]("cityname")
        ((provincename, cityname), 1)
      }).rdd.reduceByKey(_+_)
   /* val prop=new Properties()
    prop.setProperty("user",ConfigHelper.user)
    prop.setProperty("password",ConfigHelper.password)
    prop.setProperty("driver",ConfigHelper.driver)
      res.map(line=>{
        (line._2,line._1._1,line._1._2)
      }).toDF("ct","provincename","cityname").write.mode(SaveMode.Overwrite).jdbc(ConfigHelper.url,"dmparea",prop)*/
    //转换成json输出到本地磁盘
    res.map(row=>{
      (row._2,row._1._1,row._1._2)
    }).toDF("ct","provincename","cityname").write.mode(SaveMode.Overwrite).json("F:/Test1")



      session.stop()
    }
}
