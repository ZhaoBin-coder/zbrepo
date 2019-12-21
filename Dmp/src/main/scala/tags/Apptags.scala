package tags

import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row


object Apptags extends Tags {
  override def makeTags(any:Any*): List[(String, Int)] = {
    val row = any(0).asInstanceOf[Row]
    var list=List[(String,Int)]()
    val adspacetype = row.getAs[Int]("adspacetype")
    val adspacetypename = row.getAs[String]("adspacetypename")
     if(adspacetype>0&&adspacetype<10){
          list :+=("LC0"+adspacetype,1)
         }
    else if(adspacetype>10){
      list:+=("LC"+adspacetype,1)
    }
     if(StringUtils.isNotEmpty(adspacetypename)){
      list:+= ("LN"+adspacetypename,1)
     }
              list
  }
}
