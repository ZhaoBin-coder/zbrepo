package utils

import Config.ConfigHelper
import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.kafka010.OffsetRange
import scalikejdbc.{DB, SQL}
import scalikejdbc.config.DBs

object ManagerOffset {
def saveOffset(ranges:Array[OffsetRange] ): Unit = {
  DBs.setup()
  DB.localTx { implicit session =>
    ranges.foreach(osr => {
      SQL("replace into streaming_offset values (?,?,?,?)")
        .bind(osr.fromOffset, osr.topic, ConfigHelper.groupId, osr.partition)
        .update().apply()
    })
  }
}
  def readOffsetFromMysql() = {
    DBs.setup()
    val map: Map[TopicPartition, Long] = DB.readOnly { implicit session =>
      SQL("select * from cmcc where topic=? and groupid=?")
        .bind(ConfigHelper.topics, ConfigHelper.groupId)
        .map(rs => (
          new TopicPartition(rs.string("topic"), rs.int("partition")),
          rs.long("offset")
        )).list().apply()
    }.toMap
    map
  }
}
