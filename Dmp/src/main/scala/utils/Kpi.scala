package utils

import org.apache.spark.sql.Row


object Kpi {
  def getKpi(row:Row) ={
    val reque = row.getAs[Int]("requestmode")
    val proce = row.getAs[Int]("processnode")
    val iseff = row.getAs[Int]("iseffective")
    val isbil = row.getAs[Int]("isbilling")
    val isbid = row.getAs[Int]("isbid")
    val iswin = row.getAs[Int]("iswin")
    val adord = row.getAs[Int]("adorderid")

    val winPr = row.getAs[Double]("winprice")
    val adpay = row.getAs[Double]("adpayment")


    val adReq: List[Double] = if (reque == 1 && proce == 1) {
      List[Double](1, 0, 0)
    } else if (reque == 1 && proce == 2) {
      List[Double](1, 1, 0)
    } else if (reque == 1 && proce == 3) {
      List[Double](1, 1, 1)
    } else {
      List[Double](0, 0, 0)
    }

    //判断和钱相关的
    val costReq: List[Double] = if (iseff == 1 && isbil == 1 && isbid == 1 && adord != 0) {
      List[Double](1, 0, 0, 0)
    } else if (iseff == 1 && isbil == 1 && iswin == 1) {
      List[Double](0, 1, adpay / 1000, winPr / 1000)
    } else {
      List[Double](0, 0, 0, 0)
    }

    //判断展示相关的
    val showReq: List[Double] = if (reque == 2 && iseff == 1) {
      List[Double](1, 0)
    } else if (reque == 3 && iseff == 1) {
      List[Double](0, 1)
    } else {
      List[Double](0, 0)
    }
    adReq ++ costReq ++ showReq
  }

}
