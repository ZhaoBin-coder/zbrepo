package utils

import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row

import scala.collection.mutable.ListBuffer

object getAllUser {
  def getUUID(row:Row) ={
    val list: ListBuffer[String] = new ListBuffer[String]

    if (StringUtils.isNotEmpty(row.getAs[String]("imei"))) list += ("imei:" + row.getAs[String]("imei"))
    if (StringUtils.isNotEmpty(row.getAs[String]("mac"))) list += ("mac:" + row.getAs[String]("mac"))
    if (StringUtils.isNotEmpty(row.getAs[String]("idfa"))) list += ("idfa:" + row.getAs[String]("idfa"))
    if (StringUtils.isNotEmpty(row.getAs[String]("openudid"))) list += ("openudid:" + row.getAs[String]("openudid"))
    if (StringUtils.isNotEmpty(row.getAs[String]("androidid"))) list += ("androidid:" + row.getAs[String]("androidid"))

    if (StringUtils.isNotEmpty(row.getAs[String]("imeimd5"))) list += ("imeimd5:" + row.getAs[String]("imeimd5"))
    if (StringUtils.isNotEmpty(row.getAs[String]("macmd5"))) list += ("macmd5:" + row.getAs[String]("macmd5"))
    if (StringUtils.isNotEmpty(row.getAs[String]("idfamd5"))) list += ("idfamd5:" + row.getAs[String]("idfamd5"))
    if (StringUtils.isNotEmpty(row.getAs[String]("openudidmd5"))) list += ("openudidmd5:" + row.getAs[String]("openudidmd5"))
    if (StringUtils.isNotEmpty(row.getAs[String]("androididmd5"))) list += ("androididmd5:" + row.getAs[String]("androididmd5"))

    if (StringUtils.isNotEmpty(row.getAs[String]("imeisha1"))) list += ("imeisha1:" + row.getAs[String]("imeisha1"))
    if (StringUtils.isNotEmpty(row.getAs[String]("macsha1"))) list += ("macsha1:" + row.getAs[String]("macsha1"))
    if (StringUtils.isNotEmpty(row.getAs[String]("idfasha1"))) list += ("idfasha1:" + row.getAs[String]("idfasha1"))
    if (StringUtils.isNotEmpty(row.getAs[String]("openudidsha1"))) list += ("openudidsha1:" + row.getAs[String]("openudidsha1"))
    if (StringUtils.isNotEmpty(row.getAs[String]("androididsha1"))) list += ("androididsha1:" + row.getAs[String]("androididsha1"))
    list
  }
}
