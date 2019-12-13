package Report


import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.commons.lang3.StringUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import utils.{Kpis, ManagerOffset, TimeCut, TurnType}
import java.util

import Config.ConfigHelper

object Report {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getName)
      .set("spark.serializer", ConfigHelper.serializer)
      .set("spark.streaming.stopGracefullyOnShutdown", "true")
    //streamingcontext
    val ssc = new StreamingContext(conf, Seconds(5))
    //流
    val dstream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream(ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](
        ConfigHelper.topics,
        ConfigHelper.kafkaParams,
        ManagerOffset.readOffsetFromMysql()
      )
    )
    //分析dstream，一系列的rdd
    dstream.foreachRDD(rdd => {
      if (!rdd.isEmpty()) {
        //获取偏移量
        val ranges: Array[OffsetRange] = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        //获取数据
        val sources = rdd.map(_.value())
          .filter(
            line => line.contains("bussinessRst") && line.contains("startReqTime")
              && line.contains("chargefee") && line.contains("endReqTime"))
          .map(line => line.substring(line.indexOf("{")))

        val result: RDD[(String, String, String, String, List[Double])] = sources.map(line => {
          //字符串转json
          val nObject: JSONObject = JSON.parseObject(line)
          //充值金额
          val bussinessRst = nObject.getString("bussinessRst")
          //设定金额
          var cost = 0.0
          //设定成功值
          var succ = 0
          if ("0000".equals(bussinessRst)) {
            cost += TurnType.toDouble(nObject.getString("chargefee"))
            succ = 1
          }
          //获取开始时间，结束时间
          var startTime = nObject.getString("startReqTime").substring(0, 17)
          var endTime = nObject.getString("endReqTime").substring(0, 17)
          if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
            startTime = "20190711111100000"
            endTime = "20190711111100001"
          }

          //计算时差
          val timeCut = TimeCut.cmccfeeTimeCut(startTime, endTime)
          //创建一个容器存放指标
          val list = List[Double](1, cost, succ, timeCut)
          //设置天
          val day = startTime.substring(0, 8)
          //设置小时
          val hour = startTime.substring(0, 10)
          //设置分钟
          val mountes = startTime.substring(0, 12)
          //省市
          val pCode = nObject.getString("provinceCode")
          (day, hour, mountes,pCode, list)
        })
        //分析数据
        //按照天统计
          //      Kpis.day(result)
        //        //按照省市统计
   //   Kpis.province(result)
        //按照分钟来
           Kpis.minute(result)
        //按照省市
       // Kpis.pNameKpis(result)
        ManagerOffset.saveOffset(ranges)
      }
    })

    //启动
    ssc.start()
    ssc.awaitTermination()
  }
}
