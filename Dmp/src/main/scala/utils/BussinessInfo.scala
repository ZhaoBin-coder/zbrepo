package utils

import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.security.NoSuchAlgorithmException

import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.lang.StringUtils

import scala.collection.mutable
import scala.collection.JavaConversions._
object BussinessInfo {
  def getBusiness(lng:String,lat:String) = {
    //获取url
    val urlstr = geturl(lng,lat)
    //创建httpclient
    val httpClient = new HttpClient()
    //创建一个请求方法
    val method = new GetMethod(urlstr)
    //开始请求
    val status: Int = httpClient.executeMethod(method)
    if (status == 200) {
      //获得数据
      val response = method.getResponseBodyAsString
      //解析json
      val res = parseJson(response)
      res
    }else{
      ""
    }
  }

  private def parseJson(response: String) = {
    //创建一个容器
    val businessInfo = new StringBuffer()
    //判断response是否为空
    if (StringUtils.isNotEmpty(response)) {
      //转json
      val nObject: JSONObject = JSON.parseObject(response)
      //获取状态码
      val status = nObject.getInteger("status")
      //判断status是否为0
      if (status == 0) {
        //获取result
        val resultObject = nObject.getJSONObject("result")
        //获取business
        val business = resultObject.getString("business")
        if (StringUtils.isNotEmpty(business) && !businessInfo.toString.contains(business)) {
          businessInfo.append(business)
        }
        //获取pois
        val array = resultObject.getJSONArray("pois")
        //遍历循环array
        for (i <- 0 until array.size()) {
          //获取元素
          val poiJson = array.getJSONObject(i)
          //获取tag
          val tag = poiJson.getString("tag")
          if (StringUtils.isNotEmpty(tag)) {
            val tags = tag.split(";", -1)
            for (t <- tags) {
              if (!businessInfo.toString.contains(t)) {
                businessInfo.append(t).append(";")
              }
            }
          }
        }
      }
    }
    businessInfo.toString
  }

  private def geturl(lng: String, lat: String) = {
    val paramsMap = new mutable.LinkedHashMap[String, String]()
    paramsMap.put("location", lat+","+lng)
    paramsMap.put("output", "json")
    paramsMap.put("pois", "1")
    paramsMap.put("ak", "yv3qSdkEAwvvEvSpGs5mbpG89mUXBunk")

    val paramsStr = toQueryString(paramsMap)
    val wholeStr = new String("/geocoder/v2/?" + paramsStr + "bHL5XQgqfSGzhDgSZY2px8iXChKKfgFs")
    val tempStr = URLEncoder.encode(wholeStr, "UTF-8")
    val sn = MD5(tempStr)
    "http://api.map.baidu.com/geocoder/v2/?" + paramsStr + "&sn=" + sn
  }

  @throws[UnsupportedEncodingException]
  private def toQueryString(data: mutable.LinkedHashMap[String, String]): String = {
    val queryString = new StringBuffer
    for (pair <- data.entrySet) {
      queryString.append(pair.getKey + "=")
      queryString.append(URLEncoder.encode(pair.getValue, "UTF-8") + "&")
    }
    if (queryString.length > 0) queryString.deleteCharAt(queryString.length - 1)
    queryString.toString
  }

  // 来自stackoverflow的MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
  private def MD5(md5: String): String = {
    try {
      val md = java.security.MessageDigest.getInstance("MD5")
      val array = md.digest(md5.getBytes)
      val sb = new StringBuffer
      var i = 0
      while ( {
        i < array.length
      }) {
        sb.append(Integer.toHexString((array(i) & 0xFF) | 0x100).substring(1, 3))

        {
          i += 1;
          i
        }
      }
      return sb.toString
    } catch {
      case e: NoSuchAlgorithmException =>
    }
    null
  }
}
