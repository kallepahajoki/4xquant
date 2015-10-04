package com.forexquant

import java.util.Date

import com.forexquant.analysis.{MovingAverageAnalysis, TimeFrameGroupingAnalysis}
import com.forexquant.backtest.{BacktestMarketDataService, MarketHistoryDataReader}
import com.forexquant.market.constants.TickerSymbol
import com.forexquant.strategy.service.StrategyService
import com.forexquant.strategy.strategies.SimpleEMACrossOverStrategy

object ForexStrategyBackTester extends App {


  val strategyService = new StrategyService
  strategyService.registerAnalysis(new TimeFrameGroupingAnalysis(strategyService))
  strategyService.registerAnalysis(new MovingAverageAnalysis(strategyService))
  strategyService.registerStategy(new SimpleEMACrossOverStrategy(strategyService))

  val marketDataService = new BacktestMarketDataService(strategyService)

  val t1 = new Date().getTime
  marketDataService.feed()
  val t2 = new Date().getTime
  println("Total strategy takes "+(t2-t1)/(1000)+ " seconds")


}
