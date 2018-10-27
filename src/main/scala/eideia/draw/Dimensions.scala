package eideia.draw

trait Dimensions {
    val RadVeryInner: Double = 0.032
    val RadInner: Double = 0.23
    val RadialInset = 0.2
    val RadInnerRuler: Double = 0.65
    val RadOuterRuler: Double = 0.78

    val InnerRuleMarks = List(-0.028,-0.018,-0.008)
    val OuterRuleMarks = List(0.016,0.010,0.006)
    val MidRuleMarks = List(0.014,0.010,0.006)
}

object DimObject extends Dimensions
