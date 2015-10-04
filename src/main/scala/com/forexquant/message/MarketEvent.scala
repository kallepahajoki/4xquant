package com.forexquant.message

import java.util.Date

import com.forexquant.market.constants.TickerSymbol


case class MarketEvent(val symbol: TickerSymbol, val dateTime: Date, val close: BigDecimal) {

}
