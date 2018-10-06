package eideia.models

case class UserData(first:String,
                        last:String,
                        tags: String,
                        date: String,
                        city: String,
                        country: String,
                        admin1: String,
                        admin2: String,
                        id: Long = 0L)
