package com.forexquant.strategy.service

import com.forexquant.analysis.Analysis
import com.forexquant.market.constants.{TickerSymbol, TimeFrame}
import com.forexquant.market.service.MarketService
import com.forexquant.message._
import com.forexquant.strategy.{EventStreamStorage, Strategy}

/**
 * A service for registering trading strategies
 */
class StrategyService {
  def getWantedIndicatorsForEvent(event: BarEvent): List[(TimeFrame, TickerSymbol, String, Int)] = wantedIndicators.filter(x => x._1 == event.timeFrame && x._2 == event.symbol)



  var strategies: List[Strategy] = Nil;
  var analyses: List[Analysis] = Nil;
  var eventStreamStorage = new EventStreamStorage
  var marketService = new MarketService(this)

  var wantedIndicators: List[(TimeFrame, TickerSymbol, String, Int)] = Nil

  var delayed: List[MarketEvent] = Nil

  def registerStategy(strategy: Strategy) = {
    strategies = strategy :: strategies
    wantedIndicators = wantedIndicators ::: strategy.indicators
  }

  def registerAnalysis(analysis: Analysis): Unit = {
    analyses = analysis :: analyses;
    analysis.context = this
  }


  def addDelayed[A <: MarketEvent](message: A) = {
    delayed = message :: delayed
  }
  def process[A <: MarketEvent](message: A): Boolean = {
    message match {
      case BarEvent(symbol, timeFrame, date, open, high, low, close) => {
        // Forward message to analysis
        for (analysis <- analyses) {
          analysis process message.asInstanceOf[BarEvent]
        }
        marketService process message
      }
      case SimpleMovingAverage(count, timeFrame, currencyPair, date, close) => {
        for(strategy <- strategies) {
          strategy process message.asInstanceOf[IndicatorEvent]
        }
      }
      case ExponentialMovingAverage(count, timeFrame, currencyPair, date, close) => {
        for(strategy <- strategies) {
          strategy process message.asInstanceOf[IndicatorEvent]
        }
      }
    }


    val delayedCopy = delayed
    delayed = Nil
    for(msg <- delayedCopy) {
      process(msg)
    }

    true
  }

}