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

    val n = 60 * 30
    marketDataService.feed(n)

    val m5queue = strategyService.eventStreamStorage.getQueueForTimeFrame(TimeFrame.M5, TickerSymbol.EURUSD)
    val m1queue = strategyService.eventStreamStorage.getQueueForTimeFrame(TimeFrame.M1, TickerSymbol.EURUSD)
    val h1queue = strategyService.eventStreamStorage.getQueueForTimeFrame(TimeFrame.H1, TickerSymbol.EURUSD)
    val h4queue = strategyService.eventStreamStorage.getQueueForTimeFrame(TimeFrame.H4, TickerSymbol.EURUSD)
    val d1queue = strategyService.eventStreamStorage.getQueueForTimeFrame(TimeFrame.D1, TickerSymbol.EURUSD)

    val totalMinutes = (m1queue.last.dateTime.getTime - h1queue.head.dateTime.getTime) / 60000

    val numberOfMinutesStored = if (n > strategyService.eventStreamStorage.MAX_LENGTH) strategyService.eventStreamStorage.MAX_LENGTH else n

    println("Total minutes "+totalMinutes+" hours "+totalMinutes / 60+" from "+h1queue.head.dateTime+" to "+m1queue.last.dateTime)

    assert(m1queue.length == numberOfMinutesStored, "Wrong length for M1: " + m1queue.length + " expected " + n)

    assert(h1queue.length == 1+(n / 60), "Wrong length for H1: " + h1queue.length + ", expected " + n / 60 + ", total minutes " + totalMinutes + " at " + m1queue.last.dateTime)
    assert(h4queue.length == totalMinutes / (4 * 60), "Wrong length for H4: " + h4queue.length + ", expected " + totalMinutes / (4 * 60) + ", total minutes " + totalMinutes)
    assert(d1queue.length == totalMinutes / (60 * 24), "Wrong length for D1: " + d1queue.length + ", expected " + totalMinutes / (60 * 24) + ", total minutes " + totalMinutes)
    assert(m5queue.length == 1+(totalMinutes / 5), "Wrong length  for M5: " + m5queue.length + ", expected " + (totalMinutes / 5) + ", total minutes " + totalMinutes)
  }
}
