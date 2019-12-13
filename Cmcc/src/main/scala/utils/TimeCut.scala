package utils

import java.text.SimpleDateFormat

import org.apache.commons.lang.time.FastDateFormat

object TimeCut {
  //线程不安全的
  private val format = new SimpleDateFormat("yyyyMMddHHmmssSSS")
  //线程安全的
  // private val format1= new FastDateFormat("yyyyMMddHHmmssSSS")

def cmccfeeTimeCut(start:String,end:String) ={
        format.parse(end).getTime-format.parse(start).getTime
}
}
