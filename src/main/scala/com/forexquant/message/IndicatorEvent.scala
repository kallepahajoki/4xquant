package com.forexquant.message

trait IndicatorEvent extends MarketEvent {
  def name: String = "INDICATOR BASE"
}
