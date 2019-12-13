package utils

import redis.clients.jedis.JedisPool

object RedisConn {
  private lazy val pool = new JedisPool()

  def getJedis(index: Int = 5) = {
    val jedis = pool.getResource
    jedis.select(index)
    jedis
  }
}