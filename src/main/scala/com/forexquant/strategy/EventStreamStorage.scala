package com.forexquant.strategy

import com.forexquant.market.constants.{TickerSymbol, TimeFrame}
import com.forexquant.message.{IndicatorEvent, BarEvent}

import scala.collection.mutable.ListBuffer

class EventStreamStorage {
  def getQueueForTimeFrame(timeFrame: TimeFrame, symbol: TickerSymbol) = eventQueue(timeFrame)(symbol)


  val MAX_LENGTH = 1000

  var eventQueue: scala.collection.mutable.Map[TimeFrame, scala.collection.mutable.Map[TickerSymbol, ListBuffer[BarEvent]]] = scala.collection.mutable.Map()
  var lastCalculated: scala.collection.mutable.Map[TimeFrame, scala.collection.mutable.Map[TickerSymbol, BarEvent]] = scala.collection.mutable.Map()
  var analysisResults: scala.collection.mutable.Map[TimeFrame, scala.collection.mutable.Map[TickerSymbol, scala.collection.mutable.Map[String, ListBuffer[IndicatorEvent]]]] = scala.collection.mutable.Map()


  for (timeFrame <- TimeFrame.values) {
    eventQueue(timeFrame) = scala.collection.mutable.Map()
    lastCalculated(timeFrame) = scala.collection.mutable.Map()
    analysisResults(timeFrame) = scala.collection.mutable.Map()
    for (currency <- TickerSymbol.values) {
      eventQueue(timeFrame)(currency) = new ListBuffer()
      analysisResults(timeFrame)(currency) = scala.collection.mutable.Map()
    }
  }


  def eventCount(timeFrame: TimeFrame, symbol: TickerSymbol) =
    eventQueue(timeFrame)(symbol).length

  def getGroupingSubList(targetTimeFrame: TimeFrame, event: BarEvent): ListBuffer[BarEvent] = {
    // Grab the queue of events from which aggregate events for this time frame are formed
    val events = getSourceQueueForTimeFrame(event.timeFrame, event.symbol)

    if (lastCalculated(targetTimeFrame).contains(event.symbol)) {
      val last: BarEvent = lastCalculated(targetTimeFrame)(event.symbol)
      //println("last=" + last + " for " + timeFrame)
      val idx = events.lastIndexWhere(e => e.dateTime.getTime == last.dateTime.getTime)

      if (idx > 0) {
        return events.slice(idx, events.length)
        //     println("Slicing creation of "+timeFrame+" from " + idx + " of total " + events.length + " SUBLIST " + sublist)
      }
    }
    return events
  }

  def addAnalysisEvent(timeFrame: TimeFrame, currencyPair: TickerSymbol, name: String, event: IndicatorEvent) = {
    if (!analysisResults(timeFrame).contains(currencyPair)) {
      analysisResults(timeFrame)(currencyPair) = scala.collection.mutable.Map()
    }
    if (!analysisResults(timeFrame)(currencyPair).contains(name)) {
      analysisResults(timeFrame)(currencyPair)(name) = new ListBuffer[IndicatorEvent]
    }
    analysisResults(timeFrame)(currencyPair)(name) += event
  }

  def limitQueueToSize(event: BarEvent, queue: ListBuffer[BarEvent], maxLength: Int): Unit = {
    if (queue.length > maxLength) {
      setQueue(event, queue.drop(1))
    }
  }



  def getMarketEventQueue(event: BarEvent) = eventQueue(event.timeFrame)(event.symbol)

  def getSourceQueueForTimeFrame(timeframe: TimeFrame, symbol: TickerSymbol) = eventQueue(timeframe)(symbol)

  def setQueue(event: BarEvent, list: ListBuffer[BarEvent]) = eventQueue(event.timeFrame)(event.symbol) = list

  def initializeQueue(event: BarEvent): Unit = eventQueue(event.timeFrame)(event.symbol) = new ListBuffer() += event

  def setGroupingStartPoint(timeFrame: TimeFrame, event: BarEvent) = lastCalculated(timeFrame)(event.symbol) = event

  def appendToQueue(event: BarEvent): Unit = {
    eventQueue(event.timeFrame)(event.symbol) += event
    // Drop oldest events after maximum length has been reached
    limitQueueToSize(event, eventQueue(event.timeFrame)(event.symbol), MAX_LENGTH)

  }

  def windowLength(event: BarEvent, timeFrame: TimeFrame): Long = {
    val q = getMarketEventQueue(event)
    if (lastCalculated(timeFrame).keySet.size == 0 && q.length > 0) {
      //if (timeFrame == TimeFrame.M5) println("calculating from " + q.length + " of frame " + q.head.timeFrame)
      //println("q="+q)
      val d = q.head.difference(event)
      //if (timeFrame == TimeFrame.M5) print("distance between " + q.head.dateTime + " and " + event.dateTime + "=" + d)
      return d
    }
    else if (lastCalculated(timeFrame).contains(event.symbol)) {
      return lastCalculated(timeFrame)(event.symbol).difference(event)
    }
    else return 0l
  }

  def windowLength(event: BarEvent): Long = windowLength(event, event.timeFrame)

}
