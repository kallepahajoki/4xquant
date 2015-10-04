package com.forexquant.message

import java.util.Date

import com.forexquant.market.constants.{TickerSymbol, TimeFrame}

case class SimpleMovingAverage(val duration: Int, val timeFrame: TimeFrame, override val symbol: TickerSymbol, override val dateTime: Date, override val close: BigDecimal) extends MarketEvent(symbol, dateTime, close) with IndicatorEvent {

  override def name = "SMA" + duration


}
