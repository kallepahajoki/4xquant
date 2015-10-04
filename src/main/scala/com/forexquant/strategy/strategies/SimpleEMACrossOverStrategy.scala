package com.forexquant.strategy.strategies

import com.forexquant.market.Order
import com.forexquant.market.constants.{TickerSymbol, TimeFrame}
import com.forexquant.message.{IndicatorEvent, MarketEvent}
import com.forexquant.strategy.Strategy
import com.forexquant.strategy.service.StrategyService

/**
 * Created by kallepahajoki on 3.10.15.
 */
class SimpleEMACrossOverStrategy(strategyService: StrategyService) extends Strategy(strategyService) {
  override def indicators: List[(TimeFrame, TickerSymbol, String, Int)] = (TimeFrame.D1, TickerSymbol.EURUSD, "EMA", 20) ::(TimeFrame.D1, TickerSymbol.EURUSD, "EMA", 10) :: Nil

  var fasterPrevious: MarketEvent = null
  var slowerPrevious: MarketEvent = null
  var faster: MarketEvent = null
  var slower: MarketEvent = null
  var order: Order = null

  val account = strategyService.marketService.openAccount("Simple EMA strategy")

  /**
   * Check whether the EMAs cross within the same hour
   */
  def withinPeriod(faster: MarketEvent, slower: MarketEvent): Boolean = Math.abs(faster.dateTime.getTime - slower.dateTime.getTime) < 60 * 60 * 1000

  def bullishCrossover: Boolean = fasterPrevious.close < slowerPrevious.close && faster.close > slower.close

  def bearishCrossover: Boolean = fasterPrevious.close > slowerPrevious.close && faster.close < slower.close

  override def process[A <: IndicatorEvent](message: A) = {
    updateMovingAverages(message)
    var stopLimit: BigDecimal = 0
    var stopLoss: BigDecimal = 0
    val volume = 10000
    var short = false
    var go = false

    if (haveValidDataForCrossOver) {
      val price = strategyService.marketService.getCurrentPrice(faster.symbol)
      val pip = strategyService.marketService.pipValue(faster.symbol)


      // If fast was below slow and now crossed over to above, bullish
      if (bullishCrossover) {
        // go long
        stopLimit = price + 1500 *  pip
        stopLoss = price - 600 * pip
        short = false
        go = true
      } else if (bearishCrossover) {
        // go short
        stopLimit = price - 1500 *  pip
        stopLoss = price + 600 *  pip
        short = true
        go = true
      }
      if (go) {

        if (order != null && order.active) {
          strategyService.marketService.closeOrder(account, order)
        }

        order = new Order(message.symbol, short, volume, message.dateTime, price, stopLoss, stopLimit, true, price)
        strategyService.marketService.placeOrder(account, order)
      }
    }
  }

  def haveValidDataForCrossOver: Boolean = {
    faster != null && slower != null && fasterPrevious != null && slowerPrevious != null && withinPeriod(faster, slower)
  }

  def updateMovingAverages[A <: IndicatorEvent](message: A): Unit = {
    if (message.name == "EMA20") {
      slowerPrevious = slower
      slower = message
    } else {
      fasterPrevious = faster
      faster = message
    }
  }
}
