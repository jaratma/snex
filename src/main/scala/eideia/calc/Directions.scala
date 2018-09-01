package eideia.calc

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

import eideia.{InitApp, State}
import eideia.ephe.EpheDriver
import eideia.models.{Chart, PlanetData}
import InitApp._

object Directions {

    val naibod: Double = (59 + 8.33/60)/60
    val sidMonthInDays: Double = 27.3215277777777778

    //implicit lazy val now: Chart = InitApp.setNowChart
    def nowChart(implicit state: State): Chart = state.setNowChart

    def yearsAheadFromBirth(ch: Chart): Int = nowChart.date.getYear - ch.date.getYear

    def fractionOfYearInSeconds(userChart: Chart): Double = {
        val unit = ChronoUnit.SECONDS
        val now = nowChart.date
        val currentYear: Int = now.getYear
        val birthDate: ZonedDateTime = userChart.date

        val birthDay = birthDate.withYear(currentYear)
        val lapseOfYear = birthDay.until(now, unit)
        lapseOfYear match {
            case seconds if seconds > 0 =>
                lapseOfYear / birthDay.until(birthDate.withYear(currentYear + 1), unit).toDouble
            case _ =>
                100.0 - lapseOfYear / birthDate.withYear(currentYear - 1).until(birthDay, unit).toDouble
        }
    }

    def secondaryProgressionPlanets(userChart: Chart): Seq[PlanetData] = {
        val fracOfLapse = fractionOfYearInSeconds(userChart)
        val birthDate: ZonedDateTime = userChart.date
        val diffYearsAsDays = yearsAheadFromBirth(userChart)

        val lapseOfDayInSeconds = (fracOfLapse * 24 * 60 * 60).toInt
        val progDate = birthDate.plusDays(diffYearsAsDays).plusSeconds(lapseOfDayInSeconds)
        val progChart = userChart.copy(progDate)
        EpheDriver.huberPoints(progChart)
    }

    // TODO: rename to minor progression
    def tertiaryProgressionPlanets(userChart: Chart): Seq[PlanetData] = {
        val fractionOfYear = fractionOfYearInSeconds(userChart)
        val lapseOfMonthInSeconds = (fractionOfYear * sidMonthInDays * 24 * 60 * 60).toLong
        val birthDate: ZonedDateTime = userChart.date

        val sidMonthsFromBirth: Long = (yearsAheadFromBirth(userChart) * sidMonthInDays * 24 * 60 * 60).toLong
        val progDate = birthDate.plusSeconds(sidMonthsFromBirth + lapseOfMonthInSeconds)
        val progChart = userChart.copy(progDate)
        EpheDriver.huberPoints(progChart)
    }

   def secondaryProgressionHouses(userChart: Chart): Seq[Double] = {
       val yearsFromBirth = yearsAheadFromBirth(userChart)
       val naibodPlus: Double = naibod * yearsFromBirth
       val armc = EpheDriver.calcHousesAndAxis(userChart).drop(12)(2)
       val progARMC = armc + naibodPlus
       EpheDriver.calcHousesARMC(userChart, progARMC)
    }

    def tertiaryProgressedHouses(userChart: Chart, tertiarySun: Double): Seq[Double] = {
        val radixSun: Double = EpheDriver.huberPoints(userChart).head.longitud
        val solarArc: Double = math.abs(tertiarySun - radixSun)
        val radixMC: Double = EpheDriver.calcHousesAndAxis(userChart).drop(12)(1)
        val progMC = radixMC + math.min(solarArc, 360 - solarArc)
        EpheDriver.calcHousesARMC(userChart, progMC)
    }
}
