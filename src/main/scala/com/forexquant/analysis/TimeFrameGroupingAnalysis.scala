package com.forexquant.analysis

import com.forexquant.market.constants.TimeFrame
import com.forexquant.message.BarEvent
import com.forexquant.strategy.service.StrategyService

import scala.collection.mutable


/**
 * Aggregate events into higher time frames and feed them back into the analysis engines
 */
class TimeFrameGroupingAnalysis(context: StrategyService) extends Analysis(context) {

  var low: scala.collection.mutable.Map[TimeFrame, BigDecimal] = scala.collection.mutable.Map()
  var high: scala.collection.mutable.Map[TimeFrame, BigDecimal] = scala.collection.mutable.Map()
  var open: scala.collection.mutable.Map[TimeFrame, BigDecimal] = scala.collection.mutable.Map()
  var lastMinute: Int = -1
  var lastHour: Int = -1
  var lastDay: Int = -1
  var lastMonth: Int = -1
  var lastYear: Int = -1

  def createAggregateEvent(event: BarEvent, targetTimeFrame: TimeFrame) = {
    val sublist = context.eventStreamStorage.getGroupingSubList(targetTimeFrame, event)


    val high = sublist.reduceLeft((e1, e2) => if (e1.high > e2.high) e1 else e2)
    val low = sublist.reduceLeft((e1, e2) => if (e1.low < e2.low) e1 else e2)

    val evt = new BarEvent(sublist.head.symbol, targetTimeFrame, sublist.head.dateTime, sublist.head.open, high.high, low.low, sublist.last.close)
    //println("Creating aggregate event " + targetTimeFrame.name + " from " + sublist.length + " events of " + sublist.head.timeFrame + " and date " + sublist.head.dateTime)

    context.addDelayed(evt)
  }


  def updateCounter(counter: mutable.Map[TimeFrame, BigDecimal], timeFrame: TimeFrame, value: BigDecimal, predicate: (BigDecimal, BigDecimal) => Boolean) = {
    if (!counter.contains(timeFrame)) {
      counter(timeFrame) = value
    }
    else if (predicate(counter(timeFrame), value)) {
      counter(timeFrame) = value
    }

    if (!open.contains(timeFrame) || open(timeFrame) < 0) open(timeFrame) = value

  }


  def updateCounters(event: BarEvent) = {
    if (event.timeFrame == TimeFrame.M1) {
      updateCounter(low, TimeFrame.M5, event.low, (currentLowest, eventValue) => (currentLowest > eventValue))
      updateCounter(high, TimeFrame.M5, event.high, (currentHighest, eventValue) => (currentHighest < eventValue))
      updateCounter(low, TimeFrame.M15, event.low, (currentLowest, eventValue) => (currentLowest > eventValue))
      updateCounter(high, TimeFrame.M15, event.high, (currentHighest, eventValue) => (currentHighest < eventValue))
      updateCounter(low, TimeFrame.M30, event.low, (currentLowest, eventValue) => (currentLowest > eventValue))
      updateCounter(high, TimeFrame.M30, event.high, (currentHighest, eventValue) => (currentHighest < eventValue))
      updateCounter(low, TimeFrame.H1, event.low, (currentLowest, eventValue) => (currentLowest > eventValue))
      updateCounter(high, TimeFrame.H1, event.high, (currentHighest, eventValue) => (currentHighest < eventValue))
    }

  }

  def crossedMinute(event: BarEvent, crossedMinute: Int): Boolean = {

    if (crossedMinute == 0 && ((lastHour < event.dateTime.getHours && lastMinute > event.dateTime.getMinutes) ||
      (lastDay < event.dateTime.getDate || lastMonth < event.dateTime.getMonth || lastYear < event.dateTime.getYear))) {
      return true
    } else if (lastMinute < crossedMinute && event.dateTime.getMinutes >= crossedMinute) {
      return true
    }
    return false
  }

  def crossedOneOfMinutes(event: BarEvent, mins: List[Int]): Boolean = if (mins.isEmpty) false else if (crossedMinute(event, mins.head)) true else crossedOneOfMinutes(event, mins.tail)

  def createEvent(event: BarEvent, eventMinute: Int, timeframe: TimeFrame) = {

    val date = event.dateTime
    date.setMinutes(eventMinute * (event.dateTime.getMinutes / eventMinute))
    val openVal = open(timeframe)
    val highVal = high(timeframe)
    val lowVal = low(timeframe)
    open(timeframe) = -1
    val evt = new BarEvent(event.symbol, timeframe, date, openVal, highVal, lowVal, event.close)
/*    if (timeframe == TimeFrame.H1) {
      println("Created hour " + evt+" from "+event)
    }*/
    context.addDelayed(evt)

  }

  def createEventsFromCounters(event: BarEvent) = {
    val m = event.dateTime.getMinutes

    if (crossedOneOfMinutes(event, List(5, 10, 20, 25, 35, 40, 50, 55))) {
      createEvent(event, 5, TimeFrame.M5)
    }
    if (crossedOneOfMinutes(event, List(15, 45))) {
      createEvent(event, 5, TimeFrame.M5)
      createEvent(event, 15, TimeFrame.M15)
    }
    if (crossedOneOfMinutes(event, List(30))) {
      createEvent(event, 5, TimeFrame.M5)
      createEvent(event, 15, TimeFrame.M15)
      createEvent(event, 30, TimeFrame.M30)
    }
    if (crossedMinute(event, 0)) {
      createEvent(event, 5, TimeFrame.M5)
      createEvent(event, 15, TimeFrame.M15)
      createEvent(event, 30, TimeFrame.M30)
      createEvent(event, 60, TimeFrame.H1)

    }

    lastMinute = m
    lastHour = event.dateTime.getHours
    lastDay = event.dateTime.getDate
    lastMonth = event.dateTime.getMonth
    lastYear = event.dateTime.getYear
  }

  override def process(event: BarEvent) = {

    event match {
      case BarEvent(_, TimeFrame.CURRENT, _, _, _, _, _) => aggregateEvent(event, TimeFrame.M1, 1)
      case BarEvent(_, TimeFrame.H1, _, _, _, _, _) => aggregateEvent(event, TimeFrame.H4, 4)
      case BarEvent(_, TimeFrame.H4, _, _, _, _, _) => aggregateEvent(event, TimeFrame.D1, 6)
      case BarEvent(_, TimeFrame.D1, _, _, _, _, _) => aggregateEvent(event, TimeFrame.W1, 7)
      case BarEvent(_, TimeFrame.W1, _, _, _, _, _) => aggregateEvent(event, TimeFrame.MN1, 30)
      case _ =>
    }

    if(event.timeFrame == TimeFrame.M1) {
      updateCounters(event)
      createEventsFromCounters(event)

    }
    // Always place the events in the queue
    context.eventStreamStorage.appendToQueue(event)

  }


  def aggregateEvent(event: BarEvent, targetTimeFrame: TimeFrame, count: Int): Unit = {
    // If we have enough window in milliseconds gathered since the last event to create a new event of the specified timeframe
    // then create the event and start the window calculation over from this timestamp
    val wl = context.eventStreamStorage.windowLength(event, targetTimeFrame)
    val haveEnoughDataToGroup = wl >= TimeFrame.milliseconds(targetTimeFrame)

    if (haveEnoughDataToGroup) {
      //println("wl=" + wl / (60000) + " minutes at " + targetTimeFrame)
      createAggregateEvent(event, targetTimeFrame)
      context.eventStreamStorage.setGroupingStartPoint(targetTimeFrame, event)
    }


  }
}
