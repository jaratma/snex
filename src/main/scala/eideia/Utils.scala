package eideia

import eideia.atlas.AtlasQuery
import eideia.models.Location

object Utils {

    def formatDateString(zdtString: String): String = {
        val zdt = DateManager.parseDateString(zdtString)
        s"${zdt.getDayOfMonth}/${zdt.getMonthValue}/${zdt.getYear} ${zdt.getHour}:${zdt.getMinute}h. ${zdt.getOffset}"
    }

    def formatGeoData(loc: Location): String = {
        val country = InitApp.countries(loc.country)
        val region = AtlasQuery.getAdmin1Name(loc.country, loc.admin1)
        val name = s"${loc.name} $country($region)"
        val lat = loc.latitude
        val lng = loc.longitude
        val latlet = Map(true -> "N", false -> "S")(lat > 0)
        val lnglet = Map(true -> "E", false -> "W")(lng > 0)
        val lngmin = math.abs(math.round((lng - lng.toInt) * 60))
        val latmin = math.abs(math.round((lat - lat.toInt) * 60))

        s"$name ${math.abs(lng.toInt)}$lnglet$lngmin ${math.abs(lat.toInt)}$latlet$latmin"
    }
}
