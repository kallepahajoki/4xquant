package com.forexquant.message

import java.util.Date

import com.forexquant.market.constants.{TickerSymbol, TimeFrame}

case class BarEvent(override val symbol: TickerSymbol, val timeFrame: TimeFrame, override val dateTime: Date, val open: BigDecimal, val high: BigDecimal, val low: BigDecimal, override val close: BigDecimal) extends MarketEvent(symbol, dateTime, close) {

  def difference(e: BarEvent): Long = Math.abs(dateTime.getTime - e.dateTime.getTime)

  override def toString: String = "["+symbol.toString+" "+timeFrame.toString+" "+dateTime+" "+close+"]"
}
