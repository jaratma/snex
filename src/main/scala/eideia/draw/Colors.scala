package eideia.draw

import scalafx.scene.paint.Color

object Colors {

    val SignsHuber = Map[String,Color](
        "fire" -> Color.Maroon,
        "earth" -> Color.DarkGreen,
        "air" -> Color.Gold,
        "water" -> Color.DarkBlue)

    def signHuberColSeq : List[Color] = {
        val col = List(SignsHuber("fire"),SignsHuber("earth"),SignsHuber("air"),SignsHuber("water"))
        col ++ col ++ col ++ col
    }

    val Planets = Map[String,Color](
        "personal" -> Color.web("#ff5600"),
        "tool" -> Color.web("#0000ff"),
        "transpersonal" -> Color.web("#0000ff"),
        "other" -> Color.web("#0000ff"),
    )

    val Aspects= Map[String,Color](
        "orange" -> Color.web("#ff8000"),
        "red" -> Color.web("#ee0000"),
        "green" -> Color.web("#00cc00"),
        "blue" -> Color.web("#0000f7"),
    )

    val HousePoints = Map[String,Color](
        "inv" -> Color.web("#7f7f99") ,
        "low" ->  Color.web("#7f997f")
    )
}
