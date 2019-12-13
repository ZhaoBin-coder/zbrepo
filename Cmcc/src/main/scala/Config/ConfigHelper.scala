package Config

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.common.serialization.StringDeserializer
import java.util

object ConfigHelper {
  //加载配置文件
  private lazy val load: Config = ConfigFactory.load()
  //加载序列化
  val serializer: String = load.getString("spark.serializer")
  //加载jdbc一套
  val driver: String = load.getString("db.default.driver")
  val url: String = load.getString("db.default.url")
  val user: String = load.getString("db.default.user")
  val password: String = load.getString("db.default.password")
  //topic
  val topics = load.getString("topics").split(",")
  //kafkaborkers
  private val kafkaBrokers: String = load.getString("kafka.brokers")
  //group
  val groupId: String = load.getString("gourd.id")
  //kafkaparams
  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> kafkaBrokers,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> groupId,
    "auto.offset.reset" -> "earliest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  //获取省市代码
  val map: util.Map[String, AnyRef] = load.getObject("pCode2Name").unwrapped()

  }
