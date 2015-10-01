package com.forexquant.market.service
import com.forexquant.market.Order

abstract class OrderService {
	def placeOrder(order: Order)
	
	def cancelOrder(order: Order)
	
	def modifyOrder(order: Order)
}