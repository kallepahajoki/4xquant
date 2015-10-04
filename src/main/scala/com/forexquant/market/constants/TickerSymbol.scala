package com.forexquant.market.constants

trait Enum[A] {

  trait Value {
    self: A =>
    _values :+= this
  }

  private var _values = List.empty[A]

  def values = _values

}

sealed abstract class TickerSymbol(val name: String) extends TickerSymbol.Value   {
  override def toString = name
}

object TickerSymbol extends Enum[TickerSymbol] {
  def valueOf(ticker: String): TickerSymbol = values.filter(x => x.name == ticker).head


  var map = Map.empty[String, TickerSymbol]


  val AUDCAD = new TickerSymbol("AUDCAD") {}
  val AUDCHF = new TickerSymbol("AUDCHF") {}
  val AUDJPY = new TickerSymbol("AUDJPY") {}
  val AUDNZD = new TickerSymbol("AUDNZD") {}
  val AUDUSD = new TickerSymbol("AUDUSD") {}
  val CADCHF = new TickerSymbol("CADCHF") {}
  val CADJPY = new TickerSymbol("CADJPY") {}
  val CHFJPY = new TickerSymbol("CHFJPY") {}
  val EURAUD = new TickerSymbol("EURAUD") {}
  val EURCAD = new TickerSymbol("EURCAD") {}
  val EURCHF = new TickerSymbol("EURCHF") {}
  val EURGBP = new TickerSymbol("EURGBP") {}
  val EURJPY = new TickerSymbol("EURJPY") {}
  val EURNZD = new TickerSymbol("EURNZD") {}
  val EURRUB = new TickerSymbol("EURRUB") {}
  val EURSGD = new TickerSymbol("EURSGD") {}
  val EURUSD = new TickerSymbol("EURUSD") {}
  val EURZAR = new TickerSymbol("EURZAR") {}
  val GBPAUD = new TickerSymbol("GBPAUD") {}
  val GBPCAD = new TickerSymbol("GBPCAD") {}
  val GBPCHF = new TickerSymbol("GBPCHF") {}
  val GBPJPY = new TickerSymbol("GBPJPY") {}
  val GBPNZD = new TickerSymbol("GBPNZD") {}
  val GBPUSD = new TickerSymbol("GBPUSD") {}
  val NZDCAD = new TickerSymbol("NZDCAD") {}
  val NZDCHF = new TickerSymbol("NZDCHF") {}
  val NZDJPY = new TickerSymbol("NZDJPY") {}
  val NZDUSD = new TickerSymbol("NZDUSD") {}
  val USDCAD = new TickerSymbol("USDCAD") {}
  val USDCHF = new TickerSymbol("USDCHF") {}
  val USDCZK = new TickerSymbol("USDCZK") {}
  val USDDKK = new TickerSymbol("USDDKK") {}
  val USDHUF = new TickerSymbol("USDHUF") {}
  val USDJPY = new TickerSymbol("USDJPY") {}
  val USDNOK = new TickerSymbol("USDNOK") {}
  val USDPLN = new TickerSymbol("USDPLN") {}
  val USDRUB = new TickerSymbol("USDRUB") {}
  val USDSEK = new TickerSymbol("USDSEK") {}
  val USDSGD = new TickerSymbol("USDSGD") {}
  val USDTRY = new TickerSymbol("USDTRY") {}
  val USDZAR = new TickerSymbol("USDZAR") {}
  val XAGEUR = new TickerSymbol("XAGEUR") {}
  val XAGUSD = new TickerSymbol("XAGUSD") {}
  val XAUEUR = new TickerSymbol("XAUEUR") {}
  val XAUUSD = new TickerSymbol("XAUUSD") {}
}