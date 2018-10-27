package eideia.charts

import eideia.calc.Huber

class BasicChart(val driver: Huber) {

    def getOffset: Double = driver.AC % 30

    def getSignsCusps: Seq[Double] = {
        for (h <- 0 to 11) yield 30 * h - getOffset
    }

}
