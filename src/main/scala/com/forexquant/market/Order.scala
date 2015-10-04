package com.forexquant.market

import java.util.Date

import com.forexquant.market.constants.TickerSymbol

class Order(val symbol: TickerSymbol, val short: Boolean, val size: Long, var date: Date, var enteredAt: BigDecimal, val stopLoss: BigDecimal, val stopLimit: BigDecimal, var active: Boolean) {

  def shouldBeClosedAt(close: BigDecimal): Boolean = {
    if (short) {
      if (close > stopLoss || close < stopLimit) return true
    } else {
      if (close < stopLoss || close > stopLimit) return true
    }
    return false
  }

  override def toString = "["+symbol+ " "+(if(short)"short" else "long")+ " "+size+"@"+enteredAt.setScale(4, BigDecimal.RoundingMode.HALF_UP)+", SL"+stopLoss+", Lim"+stopLimit+" at "+date+"]"

}