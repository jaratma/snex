package eideia.atlas

import scala.xml.XML
import com.softwaremill.sttp._
import eideia.models.Location


object QueryGeonames {

    def sendQuery(loc: String, countCode: String) : String = {
        implicit val backend = HttpURLConnectionBackend()
        val uri: Uri =uri"http://api.geonames.org/search?q=$loc&country=$countCode&featureCode=PPL&style=FULL&username=jaratma"
        sttp.get(uri).send().body.right.get
    }

    def parseQuery(response: String) : Seq[Location] = {
        val cities = XML.loadString(response)
        //assert((cities \\ "totalResultsCount").text.toInt > 0)

        for { el <- cities \\ "geoname" } yield Location((el \\ "name").text,
            (el \\ "lat").text.toDouble,
            (el \\ "lng").text.toDouble,
            (el \\ "countryCode").text,
            (el \\ "adminCode1").text,
            (el \\ "adminCode2").text,
            (el \\ "srtm3").text.toDouble,
            (el \\ "timezone").text)
    }
}
