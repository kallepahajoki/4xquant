package com.forexquant.market

import com.forexquant.market.constants.TickerSymbol

/**
 * Created by kallepahajoki on 5.10.15.
 */
class Account(val name: String) {
  var positions: List[Order] = Nil
  var tradeResults: BigDecimal = 0
  var losses: BigDecimal = 0
  var wins: BigDecimal = 0
  var totalTrades: BigDecimal = 0
  var largestLoss: BigDecimal = 0

  val STARTING_BALANCE = 15000

  var balance: BigDecimal = STARTING_BALANCE
  var borrowed: BigDecimal = 0

  // 20% margin => 5x leverage
  var margin: BigDecimal = 0.20
  def positionsFor(symbol: TickerSymbol) = positions.filter(x => x.symbol == symbol)

  def sumOfPositions: BigDecimal = if (positions.length > 0) positions.map(x => x.currentValue * x.size).reduce((x, y) => x + y) else 0

  def sumOfPositionsOriginal: BigDecimal = if (positions.length > 0) positions.map(x => x.enteredAt * x.size).reduce((x, y) => x + y) else 0

  def marginCallRequired: Boolean = {
    val currentPosition = sumOfPositions
    val lossOnPositions = currentPosition - sumOfPositionsOriginal

    val marginCallRequired = (balance - lossOnPositions) < (margin * currentPosition)
    marginCallRequired
  }
}
