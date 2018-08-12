package eideia.models

case class EntityInfo(first:String, last:String, category: String, comment: String)

case class Location(
        name: String,
        latitude: Double,
        longitude: Double,
        country: String,
        admin1: String,
        admin2: String,
        elevation: Double,
        timezone: String,
        id: Long = 0L)

case class EventDate(date: String)

case class LegacyData(
        first: String,
        last: String,
        category: String,
        date: String,
        city: String,
        region: String,
        country: String,
        longitud: Double,
        latitud: Double,
        zone: String,
        sun: Double,
        moo: Double,
        mer: Double,
        ven: Double,
        mar: Double,
        jup: Double,
        sat: Double,
        ura: Double,
        nep: Double,
        plu: Double,
        nod: Double,
        h1: Double,
        h2: Double,
        h3: Double,
        h4: Double,
        h5: Double,
        h6: Double,
        h7: Double,
        h8: Double,
        h9: Double,
        h10: Double,
        h11: Double,
        h12: Double,
        comment:String)



