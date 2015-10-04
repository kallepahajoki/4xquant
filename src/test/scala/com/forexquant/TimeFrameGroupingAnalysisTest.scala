package com.forexquant

import com.forexquant.analysis.TimeFrameGroupingAnalysis
import com.forexquant.backtest.BacktestMarketDataService
import com.forexquant.market.constants.{TickerSymbol, TimeFrame}
import com.forexquant.strategy.service.StrategyService
import org.scalatest.FunSuite

class TimeFrameGroupingAnalysisTest extends FunSuite {
  test("Events are grouped succesfully") {
    val strategyService = new StrategyService
    strategyService.registerAnalysis(new TimeFrameGroupingAnalysis(strategyService))

    val marketDataService = new BacktestMarketDataService(strategyService)

    marketDataService.feed(5)

    val m5queue = strategyService.eventStreamStorage.getQueueForTimeFrame(TimeFrame.M5, TickerSymbol.EURUSD)
    assert(m5queue.length == 1)
  }
}
