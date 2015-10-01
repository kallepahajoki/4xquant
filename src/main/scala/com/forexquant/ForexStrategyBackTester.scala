package com.forexquant

import akka.actor.{ActorSystem, Props}
import com.forexquant.backtest.MarketHistoryDataReader
import com.forexquant.market.CurrencyPair
import com.forexquant.strategy.service.StrategyService

object ForexStrategyBackTester extends App {

  val system = ActorSystem("4XQuantSystem")

  val strategyService = system.actorOf(Props[StrategyService], name = "strategyService")

  val rdr = new MarketHistoryDataReader("src/test/resources/quotes.txt")

  for (evt <- rdr.read(List(CurrencyPair.EURUSD))) {
    strategyService ! evt
  }

}
