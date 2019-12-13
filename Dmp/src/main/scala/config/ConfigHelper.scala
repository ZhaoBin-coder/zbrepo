package config

import com.typesafe.config.{Config, ConfigFactory}

object ConfigHelper {

  //加载配置文件
  private lazy val load: Config = ConfigFactory.load()
  //加载序列化
 val SparkSerializer: String = load.getString("spark.serializer")

}
