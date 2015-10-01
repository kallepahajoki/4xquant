package com.forexquant.strategy.service

import akka.actor.Actor
import com.forexquant.market.MarketEvent
import com.forexquant.strategy.Strategy

/**
 * A service for registering trading strategies
 */
class StrategyService extends Actor {
	def processEvent(evt: MarketEvent): Unit = {

	}

	var strategies: List[Strategy] = Nil;
  
	def registerStategy(strategy: Strategy) = {
		strategies = strategy :: strategies;
	}

	def receive = {
		case _ => println("Got data");
	}
}