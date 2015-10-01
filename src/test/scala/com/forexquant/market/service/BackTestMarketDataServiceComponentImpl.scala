package com.forexquant.market.service

/**
 * A component for injecting a backtest market data service
 */
trait BackTestMarketDataServiceImpl  {
  	def marketDataService = new BackTestMarketDataServiceImpl
  	
  	/**
  	 * A class implementing a back test market data service feeding data from ascii text files
  	 */
  	class BackTestMarketDataServiceImpl extends MarketDataService {
  	  
  	}
}
