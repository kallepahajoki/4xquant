package com.forexquant.analysis

import com.forexquant.market.constants.TimeFrame
import com.forexquant.message.BarEvent
import com.forexquant.strategy.service.StrategyService
import com.forexquant.market.constants.TickerSymbol

import scala.collection.mutable.ListBuffer


/**
 * Aggregate events into higher time frames and feed them back into the analysis engines
 */
class TimeFrameGroupingAnalysis(context: StrategyService) extends Analysis(context) {


  def createAggregateEvent(event: BarEvent, targetTimeFrame: TimeFrame) = {
    val sublist = context.eventStreamStorage.getGroupingSubList(targetTimeFrame, event)


    val high = sublist.reduceLeft((e1, e2) => if (e1.high > e2.high) e1 else e2)
    val low = sublist.reduceLeft((e1, e2) => if (e1.low < e2.low) e1 else e2)

    val evt = new BarEvent(sublist.head.symbol, targetTimeFrame, sublist.head.dateTime, sublist.head.open, high.high, low.low, sublist.last.close)
    //println("Creating aggregate event " + targetTimeFrame.name + " from "+sublist.length+" events of "+sublist.head.timeFrame+" and date " + sublist.head.dateTime)

    context.addDelayed(evt)
  }


  override def process(event: BarEvent) = {

    event match {
      case BarEvent(_, TimeFrame.CURRENT, _, _, _, _, _) => aggregateEvent(event, TimeFrame.M1)
      case BarEvent(_, TimeFrame.M1, _, _, _, _, _) => aggregateEvent(event, TimeFrame.M5)
      case BarEvent(_, TimeFrame.M5, _, _, _, _, _) => aggregateEvent(event, TimeFrame.M15)
      case BarEvent(_, TimeFrame.M15, _, _, _, _, _) => aggregateEvent(event, TimeFrame.M30)
      case BarEvent(_, TimeFrame.M30, _, _, _, _, _) => aggregateEvent(event, TimeFrame.H1)
      case BarEvent(_, TimeFrame.H1, _, _, _, _, _) => aggregateEvent(event, TimeFrame.H4)
      case BarEvent(_, TimeFrame.H4, _, _, _, _, _) => aggregateEvent(event, TimeFrame.D1)
      case BarEvent(_, TimeFrame.D1, _, _, _, _, _) => aggregateEvent(event, TimeFrame.W1)
      case BarEvent(_, TimeFrame.W1, _, _, _, _, _) => aggregateEvent(event, TimeFrame.MN1)
      case _ =>
    }
  }


  def aggregateEvent(event: BarEvent, targetTimeFrame: TimeFrame): Unit = {
    // If we have enough window in milliseconds gathered since the last event to create a new event of the specified timeframe
    // then create the event and start the window calculation over from this timestamp
    val wl = context.eventStreamStorage.windowLength(event, targetTimeFrame)
    if (wl >= TimeFrame.milliseconds(targetTimeFrame)) {
      createAggregateEvent(event, targetTimeFrame)
      context.eventStreamStorage.setGroupingStartPoint(targetTimeFrame, event)
    }

    // Always place the events in the queue
    context.eventStreamStorage.appendToQueue(event)

  }
}
