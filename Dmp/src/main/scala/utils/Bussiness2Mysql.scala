package utils

import java.util.Properties

import ch.hsr.geohash.GeoHash
import config.ConfigHelper
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.{SaveMode, SparkSession}

object Bussiness2Mysql {
  def main(args: Array[String]): Unit = {
   val session=SparkSession.builder()
     .master("local[*]")
     .appName(this.getClass.getSimpleName)
     .config("serializer",ConfigHelper.SparkSerializer)
     .getOrCreate()

    val pro=new Properties()
    pro.setProperty("driver",ConfigHelper.driver)
    pro.setProperty("user",ConfigHelper.user)
    pro.setProperty("password",ConfigHelper.password)

    val source = session.read.parquet("hdfs://10.10.0.12:8020/flume/test")
    import session.implicits._
    source.select("lng","lat").filter(
      """
        lat < 54 and lat > 4 and lng > 73 and lng <135
      """.stripMargin).map(row=> {
      val lng = row.getAs[String]("lng")
      val lat = row.getAs[String]("lat")
      val geoHash: String = GeoHash.withCharacterPrecision(lat.toDouble, lng.toDouble, 5).toBase32

      val str: String = BussinessInfo.getBusiness(lng,lat)
      (geoHash,str)
    }).rdd.filter(line=>StringUtils.isNotEmpty(line._2)).reduceByKey((x1,x2)=>x1.concat(x2)).toDF("geohash","bussiness").write.mode(SaveMode.Overwrite).jdbc(ConfigHelper.url,"bussiness",pro)

session.stop()
  }
}
