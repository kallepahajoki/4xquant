package com.forexquant.backtest

import com.forexquant.market.CurrencyPair
import org.scalatest._

class MarketHistoryDataReaderTest extends FunSuite {
  test("Reading data is succesful") {
    val rdr = new MarketHistoryDataReader("src/test/resources/quotes.txt")


    val evt = rdr.read(List(CurrencyPair.EURUSD)).drop(100).next();
    assert(0.9496 == evt.close, "wrong value" + evt.close);

  }
}