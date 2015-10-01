package com.forexquant.market
import java.util.Date

class MarketEvent(val currency: CurrencyPair, val dateTime: Date,val open: BigDecimal,val high: BigDecimal, val low: BigDecimal, val close: BigDecimal) {

}
