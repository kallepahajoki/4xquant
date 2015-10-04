package com.forexquant.backtest

import java.text.SimpleDateFormat

import com.forexquant.market.constants.{TickerSymbol, TimeFrame}
import com.forexquant.message.BarEvent

import scala.io.Source


class MarketHistoryDataReader(filename: String) {
  val df = new SimpleDateFormat("yyyyMMdd HHmmss")

  def generateEvent(symbols: List[String], line: String): Option[BarEvent] = {
    val components: Array[String] = line.split(",")

    val ticker = components(0)

    if (symbols contains ticker) {

      val date = df.parse(components(1) + " " + components(2))
      val open = BigDecimal(components(3)).setScale(4, BigDecimal.RoundingMode.HALF_UP)
      val high = BigDecimal(components(4)).setScale(4, BigDecimal.RoundingMode.HALF_UP)
      val low = BigDecimal(components(5)).setScale(4, BigDecimal.RoundingMode.HALF_UP)
      val endval = BigDecimal(components(6)).setScale(4, BigDecimal.RoundingMode.HALF_UP)
      return Some(new BarEvent(TickerSymbol.valueOf(ticker), TimeFrame.M1, date, open, high, low, endval))
    }
    return None
  }

  def read(currencies: List[TickerSymbol] = TickerSymbol.values): Iterator[BarEvent] = {

    val symbols = currencies.map(x => x.name)
    val res = for (line <- Source.fromFile(filename).getLines()) yield generateEvent(symbols, line)

    return res.flatten;
  }
}