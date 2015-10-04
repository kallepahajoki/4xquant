package com.forexquant.analysis

import com.forexquant.market.constants.TimeFrame
import com.forexquant.message.{BarEvent, ExponentialMovingAverage, IndicatorEvent, SimpleMovingAverage}
import com.forexquant.strategy.service.StrategyService

import scala.collection.mutable.ListBuffer

/**
 * Created by kallepahajoki on 1.10.15.
 */
class MovingAverageAnalysis(context: StrategyService) extends Analysis(context) {

  val previousEma: scala.collection.mutable.Map[TimeFrame, BigDecimal] = scala.collection.mutable.Map()

  override def process(event: BarEvent) = {
    var items = context.getWantedIndicatorsForEvent(event)

    for (item <- items) {
      item match {
        case (timeframe, symbol, "SMA", window) => createMovingAverageEvent(event, window, window, timeframe, calculateSimpleMovingAverage)
        case (timeframe, symbol, "EMA", window) => createMovingAverageEvent(event, window, 1, timeframe, calculateExponentialMovingAverage)

      }
    }
    /*    event match {
          case BarEvent(symbol, timeFrame, _, _, _, _, _) => {
            createMovingAverageEvent(event, 30, 30, TimeFrame.H1, calculateSimpleMovingAverage)
            createMovingAverageEvent(event, 30, 1, TimeFrame.H1, calculateExponentialMovingAverage)

          }
          // case BarEvent(_, TimeFrame.D1, _, _, _, _, _) => createMovingAverageEvent(event, 30, TimeFrame.D1, calculateSimpleMovingAverage)
          case _ =>
        }
                   */
  }

  def createMovingAverageEvent(event: BarEvent, count: Int, reqCount: Int, timeFrame: TimeFrame, averageCalculationFunc: (BarEvent, ListBuffer[BarEvent], Int, TimeFrame) => IndicatorEvent) = {

    val queue = context.eventStreamStorage.getMarketEventQueue(event)
    val len = context.eventStreamStorage.eventCount(timeFrame, event.symbol)

    if (len >= reqCount) {
      val average = averageCalculationFunc(event, queue, count, timeFrame)
      context.eventStreamStorage.addAnalysisEvent(timeFrame, event.symbol, average.name, average)

      context.addDelayed(average)

    }

  }


  def calculateSimpleMovingAverage(event: BarEvent, queue: ListBuffer[BarEvent], window: Int, timeFrame: TimeFrame): IndicatorEvent = {
    //println("Calculating SMA" + window + " in timeframe " + timeFrame + " from " + queue.length + " events")
    val avg: BigDecimal = calculateSMA(queue, window)

    return new SimpleMovingAverage(window, timeFrame, queue.head.symbol, event.dateTime, avg)
  }


  def calculateSMA(queue: ListBuffer[BarEvent], window: Int): BigDecimal =
    queue.takeRight(window.asInstanceOf[Int]).map(x => x.close).reduceLeft((e1, e2) => e1 + e2) / window

  def calculateEMA(queue: ListBuffer[BarEvent], window: Int, days: Int, prev: BigDecimal): BigDecimal = {
    val k = (2.0 / (window + 1))

    if (queue.length == 1)
      queue.head.close
    else
      (queue.last.close * k) + (1.0 - k) * (if (prev > 0) prev else calculateEMA(queue.dropRight(1), window, days - 1, -1))
  }

  def calculateAndStoreEMA(timeFrame: TimeFrame, queue: ListBuffer[BarEvent], window: Int, days: Int, prev: BigDecimal): BigDecimal = {
    var previous: BigDecimal = -1
    if (previousEma.contains(timeFrame)) previous = previousEma(timeFrame)
    val ema = calculateEMA(queue, window, window, previous)

    previousEma(timeFrame) = ema
    return ema
  }

  def calculateExponentialMovingAverage(event: BarEvent, queue: ListBuffer[BarEvent], window: Int, timeFrame: TimeFrame): IndicatorEvent = {
    //println("Calculating EMA" + window + " in timeframe " + timeFrame + " from " + queue.length + " events")
    return new ExponentialMovingAverage(window, timeFrame, queue.head.symbol, event.dateTime, calculateAndStoreEMA(timeFrame, queue, window, window, -1))
  }
}
