package eideia.charts

import eideia.calc.Huber

class BasicChart(val driver: Huber) {


    def getOffset: Double = driver.AC % 30

    def getSignsCusps: Seq[Double] = {
        for (h <- 0 to 11) yield 30 * h - getOffset
    }

    def getAscendantIndex: Int = (driver.AC / 30).toInt

    def getSignOffsets : Seq[Double] = {
        val sign = getOffset - 90
        for (i <- 0 until 12) yield sign - 30*i - 15
    }

    def getRotationAngle: Double = 180 + getAscendantIndex * 30 + getOffset - 15
}
