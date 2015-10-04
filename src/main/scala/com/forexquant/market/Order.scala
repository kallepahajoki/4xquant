package com.forexquant.market

import java.util.Date

import com.forexquant.market.constants.TickerSymbol

class Order(val symbol: TickerSymbol, val short: Boolean, val size: Long, var date: Date, var enteredAt: BigDecimal, val stopLoss: BigDecimal, val stopEntry: BigDecimal, var active: Boolean, var currentValue: BigDecimal) {

  def shouldBeClosedAt(close: BigDecimal): Boolean = {
    if (short) {
      if (close > stopLoss || close < stopEntry) return true
    } else {
      if (close < stopLoss || close > stopEntry) return true
    }
    return false
  }

  override def toString = "["+symbol+ " "+(if(short)"short" else "long")+ " "+size+"@"+enteredAt.setScale(4, BigDecimal.RoundingMode.HALF_UP)+", SL"+stopLoss.setScale(4, BigDecimal.RoundingMode.HALF_UP)+", Lim"+stopEntry.setScale(4, BigDecimal.RoundingMode.HALF_UP)+" at "+date+"]"

}