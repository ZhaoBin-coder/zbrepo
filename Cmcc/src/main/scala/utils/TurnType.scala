package utils

object TurnType {
      def toDouble(str:String):Double={
        try {
          str.toDouble
        }catch {
          case _:Exception=>0.0
        }
      }
}
