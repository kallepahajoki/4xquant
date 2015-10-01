package com.forexquant.backtest

import java.math.BigDecimal
import java.text.SimpleDateFormat

import com.forexquant.market.{CurrencyPair, MarketEvent}

import scala.io.Source


class MarketHistoryDataReader(filename: String) {
  val df = new SimpleDateFormat("yyyyMMdd HHmmss")

  def generateEvent(symbols: List[String], line: String): Option[MarketEvent] = {
    val components: Array[String] = line.split(",")

    val ticker = components(0)

    if (symbols contains ticker) {

      val date = df.parse(components(1) + " " + components(2))
      val open = new BigDecimal(components(3))
      val high = new BigDecimal(components(4))
      val low = new BigDecimal(components(5))
      val endval = new BigDecimal(components(6))
      return Some(new MarketEvent(CurrencyPair.valueOf(ticker), date, open, high, low, endval))
    }
    return None
  }

  def read(currencies: List[CurrencyPair] = CurrencyPair.values): Iterator[MarketEvent] = {

    val symbols = currencies.map(x => x.name)
    val res = for (line <- Source.fromFile(filename).getLines()) yield generateEvent(symbols, line)

    return res.flatten;
  }
}