package utils

object TurnType {
  def toInt(str:String)={
      try{
        str.toInt
      }catch {
        case _:Exception=>0;
      }

  }
  def toDouble(str:String)={
    try{
      str.toDouble
    }catch {
      case _:Exception=>0.0;
    }


  }
  def result(tp1:Double,tp2:Double): Double ={
    if(
      tp1==0.0 ||tp2==0.0
    ){
      0.0
    }else{
      tp2/tp1
    }
  }
}
