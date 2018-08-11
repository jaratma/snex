package eideia.swebind

import com.sun.jna._


trait Swebind extends Library {
    def swe_version(ver:String): String
    def swe_set_ephe_path(path:String): Unit
    def swe_get_library_path(path:String): String
    def swe_close():Unit
    def swe_julday(y:Int,m:Int,d:Int,h:Double,flag:Int): Double
    def swe_calc_ut(jd: Double, pl: Int, flag: Long, xx:Array[Double], err: String): Int
    def swe_houses(jd: Double, lat: Double, lng: Double, hsys: Int, cusps: Array[Double], asmc: Array[Double]) :Int
}

object Swebind {
    private var _libswe:Swebind = null
    def run():Swebind = {
        if ( _libswe == null ) {
          _libswe = Native.loadLibrary("swe", classOf[Swebind])
        }
        _libswe
    }
}

