package com.forexquant.backtest

import com.forexquant.market.constants.TickerSymbol
import com.forexquant.market.service.MarketDataService
import com.forexquant.strategy.service.StrategyService

/**
 * Created by kallepahajoki on 4.10.15.
 */
class BacktestMarketDataService(strategyService: StrategyService) extends MarketDataService(strategyService) {

  val rdr = new MarketHistoryDataReader("src/test/resources/quotes.txt")


  var currencies: List[TickerSymbol] = List(TickerSymbol.EURUSD)

  override def feed() = {
    for (evt <- rdr.read(currencies)) {
      strategyService.process(evt)
    }
  }

  override def feed(n: Int) = {
    for (evt <- rdr.read(currencies).take(n)) {
      strategyService.process(evt)
    }
  }
}
