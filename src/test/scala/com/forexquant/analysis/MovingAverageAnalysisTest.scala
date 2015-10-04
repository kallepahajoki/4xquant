package com.forexquant.analysis

import java.util.Date

import com.forexquant.market.constants.{TickerSymbol, TimeFrame}
import com.forexquant.message.BarEvent
import org.scalatest.FunSuite

import scala.collection.mutable.ListBuffer

/**
 * Created by kallepahajoki on 3.10.15.
 */
class MovingAverageAnalysisTest extends FunSuite {
  val avga = new MovingAverageAnalysis(null)

  def createEvent(d: Double): BarEvent = new BarEvent(TickerSymbol.EURUSD, TimeFrame.H1, new Date(), d, d, d, d)

  test("Simple Moving Average is calculated correctly") {
    val queue = new ListBuffer[BarEvent]

    queue += createEvent(0.5)
    queue += createEvent(1.5)
    queue += createEvent(0.5)
    queue += createEvent(1.5)

    val sma = avga.calculateSMA(queue, 4)
    assert(sma == 1)
  }

  def assertEquals(decimal: BigDecimal, d: Double) = {

    if (Math.abs(decimal.doubleValue() - d) > 0.01) {
      assert(false, "Expected " + d + " got " + decimal)
    }
  }

  /**
  Validated using data from http://www.dummies.com/how-to/content/how-to-calculate-exponential-moving-average-in-tra.html
    */
  test("Exponential Moving Average is calculated correctly, with our without previous value") {
    val queue = new ListBuffer[BarEvent]

    queue += createEvent(22.81)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, -1), 22.81)
    queue += createEvent(23.09)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, -1), 22.87)
    queue += createEvent(22.91)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, avga.previousEma(TimeFrame.H1)), 22.87)
    queue += createEvent(23.23)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, avga.previousEma(TimeFrame.H1)), 22.95)
    queue += createEvent(22.83)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, avga.previousEma(TimeFrame.H1)), 22.92)
    queue += createEvent(23.05)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, avga.previousEma(TimeFrame.H1)), 22.95)
    queue += createEvent(23.02)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, avga.previousEma(TimeFrame.H1)), 22.96)
    queue += createEvent(23.29)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, avga.previousEma(TimeFrame.H1)), 23.03)
    queue += createEvent(23.41)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, avga.previousEma(TimeFrame.H1)), 23.10)
    queue += createEvent(23.49)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, avga.previousEma(TimeFrame.H1)), 23.18)
    queue += createEvent(24.60)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, avga.previousEma(TimeFrame.H1)), 23.47)
    queue += createEvent(24.63)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, -1), 23.70)
    queue += createEvent(24.51)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, avga.previousEma(TimeFrame.H1)), 23.86)
    queue += createEvent(23.73)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, avga.previousEma(TimeFrame.H1)), 23.83)
    queue += createEvent(23.31)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, avga.previousEma(TimeFrame.H1)), 23.73)
    queue += createEvent(23.53)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9,-1), 23.69)
    queue += createEvent(23.06)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9,-1), 23.56)
    queue += createEvent(23.25)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9,-1), 23.50)
    queue += createEvent(23.12)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, avga.previousEma(TimeFrame.H1)), 23.42)
    queue += createEvent(22.80)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, avga.previousEma(TimeFrame.H1)), 23.30)
    queue += createEvent(22.84)
    assertEquals(avga.calculateAndStoreEMA(TimeFrame.H1, queue, 9, 9, avga.previousEma(TimeFrame.H1)), 23.21)

  }

}
