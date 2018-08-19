package eideia.atlas

import com.softwaremill.sttp._
import scala.xml._

object QueryGeonames {
    def sendQuery(loc: String, countCode: String) = {
        implicit val backend = HttpURLConnectionBackend()
        val uri: Uri =uri"http://api.geonames.org/search?q=$loc&country=$countCode&featureCode=PPL&style=FULL&username=jaratma"
        val resp: String = sttp.get(uri).send().body match {
            case Right(xml) => xml
            case Left(errorMesage) => errorMesage
        }
        XML.loadString(resp)
    }
}
