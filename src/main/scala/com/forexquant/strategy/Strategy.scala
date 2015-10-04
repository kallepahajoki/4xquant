package com.forexquant.strategy

import com.forexquant.market.constants.{TickerSymbol, TimeFrame}
import com.forexquant.message.{MarketEvent, IndicatorEvent}
import com.forexquant.strategy.service.StrategyService

class Strategy(val strategyService: StrategyService) {
  def process[A <: IndicatorEvent](message: A) = {

  }

  def indicators: List[(TimeFrame, TickerSymbol, String,Int)] = Nil
}