package tags

import config.ConfigHelper
import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import utils.{getAllUser, getUser}

import scala.collection.mutable.ListBuffer


object GraphTag {
  def main(args: Array[String]): Unit = {
    val session=SparkSession.builder()
      .appName(this.getClass.getSimpleName)
      .master("local[*]")
      .config("serializer",ConfigHelper.SparkSerializer)
      .getOrCreate()

    //读取数据
    val frame = session.read.parquet("hdfs://10.10.0.12:8020/flume/test").filter(getUser.filted)

  //创建点集合
    import session.implicits._
    val pointRDD: RDD[(VertexId, List[(String, Int)])] = frame.flatMap(row => {
      //获取所有uuid
      val uuids = getAllUser.getUUID(row)
      val list1 = Apptags.makeTags(row)
      val list2 = Phone.getDesTags(row)
      val list3 = AreaTags.getTags(row)
      val list4 = BussinessTags.getTags(row)
      val res: ListBuffer[(Long, List[(String, Int)])] = uuids.map(uuid => {
        if (uuid.equals(uuids.head)) {
          (uuid.hashCode.toLong, list1 ++ list2 ++ list3 ++ list4)
        } else {
          (uuid.hashCode.toLong, List.empty)
        }
      })
      res
    }).rdd

    //创建边集合
    val edgeRDD=frame.flatMap(row=>{
      val uuids = getAllUser.getUUID(row)
      uuids.map(uuid=>Edge(uuids.head.hashCode,uuid.hashCode,0))
    }).rdd

    //创建图对象
    val graph: Graph[List[(String, Int)], Int] = Graph(pointRDD,edgeRDD)

    //调用连通图算法
    val ver = graph.connectedComponents().vertices
    ver.join(pointRDD).map{
      case (maxid,(minid,list))=>(minid,list)
    }.reduceByKey{
      (list1,list2)=> (list1++list2).groupBy(_._1).mapValues(li=>li.map(_._2).sum).toList
    }.foreach(println(_))
    //释放资源
    session.stop()
  }
}
