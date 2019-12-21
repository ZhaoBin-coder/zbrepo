package tags

trait Tags {
  def makeTags(any: Any*): List[(String, Int)]
}
