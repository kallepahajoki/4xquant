package com.forexquant.market.service

import java.util.Date

import com.forexquant.market.Order
import com.forexquant.market.constants.TickerSymbol
import com.forexquant.message.MarketEvent
import com.forexquant.strategy.service.StrategyService

class MarketService(val strategyService: StrategyService) {

  def pipValue(symbol: TickerSymbol): BigDecimal =
    if (symbol.name.contains("JPY"))
      (1.0 / 100) * getCurrentPrice(symbol)
    else
      (1.0 / 10000) * getCurrentPrice(symbol)


  def getCurrentPrice(symbol: TickerSymbol): BigDecimal = currentPrice(symbol).setScale(4, BigDecimal.RoundingMode.HALF_UP)

  var positions: List[Order] = Nil
  val currentPrice: scala.collection.mutable.Map[TickerSymbol, BigDecimal] = scala.collection.mutable.Map()

  var date: Date = null

  var tradeResults: BigDecimal = 0

  def markCurrentPrice(message: MarketEvent): Unit = {
    currentPrice(message.symbol) = message.close
  }

  /**
   * Close orders based on stop losses and limits
   */
  def process[A <: MarketEvent](message: A) = {
    markCurrentPrice(message)
    date = message.dateTime
    for (order <- positions) {
      if (order.shouldBeClosedAt(message.close)) {
        closeOrder(order, "due to stoploss/limit ")
      }
    }
  }

  def closeOrder(order: Order, reason: String = "") = {
    positions = positions.filter(x => x != order)
    if (order.active) {
      order.active = false

      var profit = (currentPrice(order.symbol) - order.enteredAt) * order.size
      if (order.short) {
        profit = (order.enteredAt - currentPrice(order.symbol)) * order.size
      }

      val trade = if (order.short) "Short" else "Long"

      tradeResults += profit
      println("Closing " + order +" "+reason+" at " + currentPrice(order.symbol) + " at "+date+". Result: " + profit)
      println("Total results to date " + tradeResults)
      println("")
    }

  }

  def placeOrder(order: Order) = {
    println("Placing order " + order)
    order.active = true
    order.enteredAt = currentPrice(order.symbol)
    order.date = date
   // println("Now order " + order)
    positions = order :: positions
  }
}