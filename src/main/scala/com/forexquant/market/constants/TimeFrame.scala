package com.forexquant.market.constants


sealed abstract class TimeFrame(val name: String) extends TimeFrame.Value {
  override def toString: String = name
}

object TimeFrame extends Enum[TimeFrame] {
  def valueOf(ticker: String): TimeFrame = values.filter(x => x.name == ticker).head

  def milliseconds(t: TimeFrame): Long = t match {
    case M1 => 60 * 1000l
    case M5 => 5 * 60 * 1000l
    case M15 => 15 * 60 * 1000l
    case M30 => 30 * 60 * 1000l
    case H1 => 60 * 60 * 1000l
    case H4 => 4 * 60 * 60 * 1000l
    case D1 => 24 * 60 * 60 * 1000l
    case W1 => 7 * 24 * 60 * 60 * 1000l
    case MN1 => 30 * 24 * 60 * 60 * 1000l
    case _ => 0

  }

  var map = Map.empty[String, TickerSymbol]


  val CURRENT = new TimeFrame("LIVE") {}
  val M1 = new TimeFrame("M1") {}
  val M5 = new TimeFrame("M5") {}
  val M15 = new TimeFrame("M15") {}
  val M30 = new TimeFrame("M30") {}
  val H1 = new TimeFrame("H1") {}
  val H4 = new TimeFrame("H4") {}
  val D1 = new TimeFrame("D1") {}
  val W1 = new TimeFrame("W1") {}
  val MN1 = new TimeFrame("MN1") {}

}
