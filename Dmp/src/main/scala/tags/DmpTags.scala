package tags

import ch.hsr.geohash.GeoHash
import config.ConfigHelper
import org.apache.spark.sql.SparkSession
import utils.getUser

object DmpTags {
  def main(args: Array[String]): Unit = {
    val session=SparkSession.builder()
      .appName(this.getClass.getSimpleName)
      .master("local[*]")
      .config("serializer",ConfigHelper.SparkSerializer)
      .getOrCreate()

    val source = session.read.parquet("hdfs://10.10.0.12/flume/test")
    import session.implicits._
    source.filter(getUser.filted).map(row=>{
      //给用户打标签
      val user: String = getUser.getUUid(row)
      //给用户打app标签
      val list1 = Apptags.makeTags(row)
      //设备 操作系统 运营商
      val list2 = Phone.getDesTags(row)
        //地域标签
      val list3 = AreaTags.getTags(row)
      //商圈标签
    val list4 = BussinessTags.getTags(row)
      (user,list1++list2++list3++list4)
    }).rdd.reduceByKey{
      (list1,list2) => (list1++list2).groupBy(_._1)//.mapValues(li=>li.map(_._2).sum).toList
        //          .mapValues(li=>li.length).toList
        .mapValues(li=>li.foldLeft(0)(_+_._2)).toList
    }.foreach(println(_))
    //释放资源
    session.stop()
  }
}
