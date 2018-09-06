package eideia

import eideia.models.Location
import scalafx.beans.property.ObjectProperty

object Utils {

    def formatDateString(zdt: String): String = {
        val (dt,tx) = zdt.split('T') match { case Array(a,b) => (a,b) }
        val date = dt.split('-').reverse.mkString("/")
        val (tm1,zone) = tx.split('[') match { case Array(a,b) => (a,b.init)}
        val tm2 = tm1.split(Array[Char]('+','-'))
        val (h,m) = tm2(0).split(':').take(2) match { case Array(a,b) => (a, if (b.contains('Z)) b.take(2) else b)}
        val time: String = s"${h}:${m}h."
        s"$date $time $zone"
    }

    def formatGeoData(currentLocation: Location): String = {
        val lat = currentLocation.latitude
        val lng = currentLocation.longitude
        val latlet = Map(true -> "N", false -> "S")(lat > 0)
        val lnglet = Map(true -> "E", false -> "W")(lng > 0)
        val lngmin = math.abs(math.round((lng - lng.toInt) * 60))
        val latmin = math.abs(math.round((lat - lat.toInt) * 60))
        s"${math.abs(lng.toInt)}$lnglet$lngmin ${math.abs(lat.toInt)}$latlet$latmin"
    }
}
