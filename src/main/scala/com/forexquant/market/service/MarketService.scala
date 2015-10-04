package com.forexquant.market.service

import java.util.Date

import com.forexquant.market.constants.TickerSymbol
import com.forexquant.market.{Account, Order}
import com.forexquant.message.MarketEvent
import com.forexquant.strategy.service.StrategyService

class MarketService(val strategyService: StrategyService) {

  var accounts: List[Account] = Nil

  val currentPrice: scala.collection.mutable.Map[TickerSymbol, BigDecimal] = scala.collection.mutable.Map()

  var date: Date = null

  def pipValue(symbol: TickerSymbol): BigDecimal =
    if (symbol.name.contains("JPY"))
      (1.0 / 100) * getCurrentPrice(symbol)
    else
      (1.0 / 10000) * getCurrentPrice(symbol)


  def getCurrentPrice(symbol: TickerSymbol): BigDecimal = currentPrice(symbol).setScale(4, BigDecimal.RoundingMode.HALF_UP)

  def markCurrentPrice(message: MarketEvent): Unit = {
    currentPrice(message.symbol) = message.close
  }


  /**
   * Close orders based on stop losses and limits
   */
  def process[A <: MarketEvent](message: A) = {
    markCurrentPrice(message)
    date = message.dateTime
    for (account <- accounts) {
      for (order <- account.positionsFor(message.symbol)) {
        order.currentValue = message.close
        if (order.shouldBeClosedAt(message.close)) {
          closeOrder(account, order, "due to stoploss/limit ")
        }
      }
      while (account.marginCallRequired) {
        val currentPosition = account.sumOfPositions
        val lossOnPositions = currentPosition - account.sumOfPositionsOriginal

        // Margin call

        println("Margin call. Your position is now " + currentPosition + " and you only have " + (account.balance - lossOnPositions) + " equity, but at least " + account.margin * 100 + "% or " + account.margin * currentPosition + " is required")

        if (account.positions.length == 0) {
          println("Ran out of positions to close. You're busted")
          accounts = accounts.filter(x => x != account)
        }
        closeOrder(account, account.positions.head, "due to margin call")
      }
    }


  }


  def closeOrder(account: Account, order: Order, reason: String = "") = {
    account.positions = account.positions.filter(x => x != order)
    if (order.active) {
      order.active = false
      val borrowedSum = order.enteredAt * order.size * (1.0 - account.margin)
      val incoming = currentPrice(order.symbol) * order.size - borrowedSum
      val initialInvestment = order.enteredAt * order.size * account.margin


      var profit = incoming - initialInvestment
      account.tradeResults += profit

      account.balance += incoming
      account.borrowed -= borrowedSum



      if (account.tradeResults < account.largestLoss) account.largestLoss = account.tradeResults

      if (profit < 0) account.losses += profit else account.wins += profit

      account.totalTrades += initialInvestment
      println(account.name)
      println("=============================")
      println("Closing " + order + " " + reason + " at " + currentPrice(order.symbol) + " at " + date + ". Result: " + profit + ", return " + 100 * ((profit / initialInvestment).setScale(2, BigDecimal.RoundingMode.HALF_UP) - 1) + "%")
      println("Total results to date " + account.tradeResults + " own money placed " + account.totalTrades + " wins " + account.wins + " losses " + account.losses + " largest loss " + account.largestLoss)
      println("Equity = " + account.balance + " borrowed " + account.borrowed + " value of positions " + account.sumOfPositions)
      println("")
    }

  }

  def openAccount(name: String): Account = {
    val account = new Account(name)
    accounts = account :: accounts
    return account
  }

  def placeOrder(account: Account, order: Order) = {
    val price = order.size * order.enteredAt
    account.balance -= price * account.margin
    account.borrowed += price * (1 - account.margin)

    order.active = true
    println(account.name)
    println("=============================")
    println("Placing order " + order + " cost " + price * account.margin + ", borrowed " + price * (1 - account.margin) + " balance left " + account.balance)
    println()
    order.enteredAt = currentPrice(order.symbol)
    order.date = date


    // println("Now order " + order)
    account.positions = order :: account.positions
  }
}