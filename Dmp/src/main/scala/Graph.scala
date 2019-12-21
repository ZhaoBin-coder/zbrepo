import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object Graph {
  def main(args: Array[String]): Unit = {
    val session = SparkSession.builder()
      .master("local[*]")
      .appName(this.getClass.getSimpleName)
      .getOrCreate()
    val sc = session.sparkContext
    //创建点集合
    val pointRDD: RDD[(Long, String)] = sc.makeRDD(
      Seq((1L, "zhangsan"),
        (2L, "lisi"),
        (9L, "wangwu"),
        (133L, "zhaoliu"),
        (6L, "chenqi"),
        (138L, "zhangba"),
        (16L, "haojiu"),
        (44L, "wangermazi"),
        (21L, "wangmazi"),
        (5L, "beijigng"),
        (7L, "liergouzi"),
        (158L, "goudan")
      )
    )
    pointRDD
    //创建边集合
    val edgeRDD: RDD[Edge[Int]] = sc.makeRDD(
      Seq(
        Edge(1, 133, 0),
        Edge(2, 133, 0),
        Edge(9, 133, 0),
        Edge(6, 133, 0),
        Edge(6, 138, 0),
        Edge(16, 138, 0),
        Edge(21, 138, 0),
        Edge(44, 138, 0),
        Edge(5, 158, 0),
        Edge(7, 158, 0))
    )
    edgeRDD
    val graph: Graph[String, Int] = Graph(pointRDD, edgeRDD)
    //调用连通图算法
    //connectedComponents是将所有的点边还原成图
    //vertices是讲每个图上的大点与最小点配对
    val ver = graph.connectedComponents().vertices
    //(5,5)    (5L, "beijigng"),    (beijigng,5)
    //(7,5)   (7L, "liergouzi"),    (liergouzi,5)
    //(158,5)  (158L, "goudan")     (goudan,5)
    //掉个
    //    ver.map(x=>(x._2,Set(x._1))).reduceByKey(_++_).foreach(println(_))
    //join
    ver.join(pointRDD).map{
      case (maxid,(minid,name)) => (minid,name)
    }.reduceByKey((x,y)=>x.concat(",").concat(y)).foreach(println(_))

    //释放资源
    session.stop()

  }
}
