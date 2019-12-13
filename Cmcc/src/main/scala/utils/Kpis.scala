package utils



import Config.ConfigHelper

import org.apache.spark.rdd.RDD

import scalikejdbc.config.DBs
import scalikejdbc.{DB, SQL}

object Kpis {

  def day(value:RDD[(String, String, String, String, List[Double])]) ={
    //按天统计
value.map(tp=>(tp._1,tp._5)).reduceByKey{(list1,list2)=>list1.zip(list2).map(x=>x._1+x._2)}.foreachPartition(partion=>{
val jedis = RedisConn.getJedis()
  partion.map(tp=>{
    jedis.hincrBy("cmcc",tp._1+"all",tp._2(0).toLong)
    jedis.hincrByFloat("cmcc",tp._1+"cost",tp._2(1))
    jedis.hincrBy("cmcc",tp._1+"succ",tp._2(2).toLong)
    jedis.hincrBy("cmcc",tp._1+"timecut",tp._2(3).toLong)
  })
      jedis.close()
})
  }
  def hour(value:RDD[(String, String, String, String, List[Double])]) = {


    //javaJDBC存入数据库
    val result: RDD[(String, List[Double])] = value.map(line => {
      (line._2, line._5)
    })

    val value2: RDD[(String, String, String, String, String)] = result.map(tp => (tp._1, tp._2)).reduceByKey { (list1, list2) =>
      (list1.zip(list2).map(x => x._1 + x._2))
    }.map(line=>{
      (line._1,line._2(0).toString,line._2(1).toString,line._2(2).toString,line._2(3).toString)
    })
    value2.saveAsTextFile("F:/logs")

  }
  def minute(value:RDD[(String, String, String, String, List[Double])]) = {
    //按分钟统计
    val res = value.map(line => {
      (line._3, line._5)
    }).reduceByKey { (list1, list2) => list1.zip(list2).map(x => x._1 + x._2) }


    DBs.setup()
    res.foreachPartition(partition=> {
      DB.localTx { implicit session =>
        partition.foreach(line => {
          SQL("insert into minutes(cmcc,all2,cost,succ,timecut) values(?,?,?,?,?)")
            .bind(line._1, line._2(0).toString, line._2(1).toString, line._2(2).toString, line._2(3).toString)
            .update().apply()})
      }
    })
  }

      def province(value: RDD[(String, String, String, String, List[Double])]): Unit = {
        val value1: RDD[(AnyRef, List[Double])] = value.map(tp => (ConfigHelper.map.get(tp._4), tp._5)).reduceByKey {
          (list1, list2) => list1.zip(list2).map(x => x._1 + x._2)
        }
        val value2 = value1.map(line => {
          (line._1, line._2(0).toString, line._2(1).toString, line._2(2).toString, line._2(3).toString)
        })

        value2.saveAsTextFile("F:/logs")

      }

}
