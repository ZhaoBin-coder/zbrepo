package config

import com.typesafe.config.{Config, ConfigFactory}

object ConfigHelper {

  //加载配置文件
  private lazy val load: Config = ConfigFactory.load()
  //加载序列化
 val SparkSerializer: String = load.getString("spark.serializer")

  //加载JDBC相关
  val driver: String = load.getString("db.default.driver")
  val url: String = load.getString("db.default.url")
  val user: String = load.getString("db.default.user")
  val password: String = load.getString("db.default.password")

}
