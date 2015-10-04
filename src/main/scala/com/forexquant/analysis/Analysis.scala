package com.forexquant.analysis

import com.forexquant.market.constants.{TickerSymbol, TimeFrame}
import com.forexquant.message.BarEvent
import com.forexquant.strategy.service.StrategyService


/**
 * Created by kallepahajoki on 1.10.15.
 */
class Analysis(var context: StrategyService) {


  def process(e: BarEvent) = {}

}
