package com.forexquant

import com.forexquant.analysis.{MovingAverageAnalysis, TimeFrameGroupingAnalysis}
import com.forexquant.backtest.{BacktestMarketDataService, MarketHistoryDataReader}
import com.forexquant.market.constants.TickerSymbol
import com.forexquant.strategy.service.StrategyService
import com.forexquant.strategy.strategies.Simple5And8EMACrossOver

object ForexStrategyBackTester extends App {


  val strategyService = new StrategyService
  strategyService.registerAnalysis(new TimeFrameGroupingAnalysis(strategyService))
  strategyService.registerAnalysis(new MovingAverageAnalysis(strategyService))
  strategyService.registerStategy(new Simple5And8EMACrossOver(strategyService))

  val marketDataService = new BacktestMarketDataService(strategyService)

  marketDataService.feed()



}
