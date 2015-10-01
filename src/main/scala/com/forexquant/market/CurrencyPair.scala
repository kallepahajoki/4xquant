package com.forexquant.market

trait Enum[A] {

  trait Value {
    self: A =>
    _values :+= this
  }

  private var _values = List.empty[A]

  def values = _values

}

sealed abstract class CurrencyPair(val name: String) extends CurrencyPair.Value

object CurrencyPair extends Enum[CurrencyPair] {
  def valueOf(ticker: String): CurrencyPair = values.filter(x => x.name == ticker).head


  var map = Map.empty[String, CurrencyPair]


  val AUDCAD = new CurrencyPair("AUDCAD") {}
  val AUDCHF = new CurrencyPair("AUDCHF") {}
  val AUDJPY = new CurrencyPair("AUDJPY") {}
  val AUDNZD = new CurrencyPair("AUDNZD") {}
  val AUDUSD = new CurrencyPair("AUDUSD") {}
  val CADCHF = new CurrencyPair("CADCHF") {}
  val CADJPY = new CurrencyPair("CADJPY") {}
  val CHFJPY = new CurrencyPair("CHFJPY") {}
  val EURAUD = new CurrencyPair("EURAUD") {}
  val EURCAD = new CurrencyPair("EURCAD") {}
  val EURCHF = new CurrencyPair("EURCHF") {}
  val EURGBP = new CurrencyPair("EURGBP") {}
  val EURJPY = new CurrencyPair("EURJPY") {}
  val EURNZD = new CurrencyPair("EURNZD") {}
  val EURRUB = new CurrencyPair("EURRUB") {}
  val EURSGD = new CurrencyPair("EURSGD") {}
  val EURUSD = new CurrencyPair("EURUSD") {}
  val EURZAR = new CurrencyPair("EURZAR") {}
  val GBPAUD = new CurrencyPair("GBPAUD") {}
  val GBPCAD = new CurrencyPair("GBPCAD") {}
  val GBPCHF = new CurrencyPair("GBPCHF") {}
  val GBPJPY = new CurrencyPair("GBPJPY") {}
  val GBPNZD = new CurrencyPair("GBPNZD") {}
  val GBPUSD = new CurrencyPair("GBPUSD") {}
  val NZDCAD = new CurrencyPair("NZDCAD") {}
  val NZDCHF = new CurrencyPair("NZDCHF") {}
  val NZDJPY = new CurrencyPair("NZDJPY") {}
  val NZDUSD = new CurrencyPair("NZDUSD") {}
  val USDCAD = new CurrencyPair("USDCAD") {}
  val USDCHF = new CurrencyPair("USDCHF") {}
  val USDCZK = new CurrencyPair("USDCZK") {}
  val USDDKK = new CurrencyPair("USDDKK") {}
  val USDHUF = new CurrencyPair("USDHUF") {}
  val USDJPY = new CurrencyPair("USDJPY") {}
  val USDNOK = new CurrencyPair("USDNOK") {}
  val USDPLN = new CurrencyPair("USDPLN") {}
  val USDRUB = new CurrencyPair("USDRUB") {}
  val USDSEK = new CurrencyPair("USDSEK") {}
  val USDSGD = new CurrencyPair("USDSGD") {}
  val USDTRY = new CurrencyPair("USDTRY") {}
  val USDZAR = new CurrencyPair("USDZAR") {}
  val XAGEUR = new CurrencyPair("XAGEUR") {}
  val XAGUSD = new CurrencyPair("XAGUSD") {}
  val XAUEUR = new CurrencyPair("XAUEUR") {}
  val XAUUSD = new CurrencyPair("XAUUSD") {}
}