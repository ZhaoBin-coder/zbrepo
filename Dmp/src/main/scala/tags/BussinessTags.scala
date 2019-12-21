package tags

import java.sql.{DriverManager, ResultSet}

import ch.hsr.geohash.GeoHash
import config.ConfigHelper
import org.apache.spark.sql.Row
import utils.TurnType

object BussinessTags {
  def getTags(row:Row) ={
    var list=List[(String,Int)]()

    val lng = TurnType.toDouble(row.getAs[String]("lng"))
    val lat = TurnType.toDouble(row.getAs[String]("lat"))
    if (lng < 135 && lng > 73 && lat > 4 && lat < 53) {
      //转geohash
      val geohash = GeoHash.withCharacterPrecision(lat,lng.toDouble,5).toBase32
      val conn = DriverManager.getConnection(ConfigHelper.url,ConfigHelper.user,ConfigHelper.password)
      val statement = conn.prepareStatement("select business from bussiness where geohash = ?")
      statement.setString(1,geohash)
      val set: ResultSet = statement.executeQuery()
      while (set.next()){
        val str = set.getString("business")
        list :+= (str,1)
      }
      conn.close()
   }
    list
  }
}
