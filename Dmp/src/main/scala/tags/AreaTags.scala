package tags

import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row


object AreaTags {
  def getTags(row:Row) ={
    val provincename = row.getAs[String]("provincename")
    val cityname = row.getAs[String]("cityname")
    var list=List[(String,Int)]()
    if(StringUtils.isNotEmpty(provincename)) list:+=("ZP"+provincename,1)
    if(StringUtils.isNotEmpty(cityname)) list:+=("ZC"+cityname,1)

    list

  }
}
