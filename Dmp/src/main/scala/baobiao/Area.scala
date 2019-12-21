package baobiao

import config.ConfigHelper
import org.apache.spark.sql.SparkSession
import scalikejdbc.{DB, SQL}
import scalikejdbc.config.DBs
import utils.Kpi

import scala.collection.immutable

object Area {
  def main(args: Array[String]): Unit = {
    val session= SparkSession.builder()
      .appName(this.getClass.getSimpleName)
      .master("local[*]")
      .config("serializer",ConfigHelper.SparkSerializer)
      .getOrCreate()
    //读取数据
    val source=session.read.parquet("F:/Test")
    import session.implicits._
    val res= source.map(
      row=>{
        val ispname=row.getAs[String]("ispname")
        val list: List[Double] = Kpi.getKpi(row)
        (ispname,list)
      }
    ).rdd.reduceByKey{(list1,list2)=>(list1.zip(list2)).map(x=>x._1+x._2)}

    DBs.setup()
    res.foreachPartition(partition=>{
      DB.localTx{implicit session=>
        partition.foreach(tp=>{
          SQL("insert into area values (?,?,?,?,?,?,?,?,?,?)")
            .bind(tp._1,tp._2(0),tp._2(1),tp._2(2),tp._2(3),tp._2(4),tp._2(7),tp._2(8),tp._2(5),tp._2(6))
            .update().apply()
        })
      }
    })



   /* res.map(line=>{
      (line._1,line._2(0),line._2(1),line._2(2),line._2(3),line._2(4),line._2(7),line._2(8),line._2(5),line._2(6))
    }).toDF("运营商","总请求","有效请求","广告请求","参与竞价数","竞价成功数","展示量","点击量","广告成本","广告消费").show(2000)*/

    session.stop()
  }

}
