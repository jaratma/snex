package eideia.draw

import eideia.Glyphs
import scalafx.Includes._
import scalafx.scene.shape._

import scala.collection.mutable
import scalafx.beans.property.DoubleProperty
import scalafx.scene.layout.{Border, Pane}
import scalafx.scene.paint.Color
import eideia.InitApp.state
import eideia.calc.Huber
import eideia.charts.BasicChart
import eideia.draw.DimObject._
import scalafx.scene.canvas.Canvas
import scalafx.scene.text.{Font, Text}

object MasterDraft {

    def makeCrown(pane: Pane): Unit = {
        println("reached here")
    }


    def drawRadix(pane: Pane): Unit = {
        val driver = new Huber(state.currentChart.value)
        val chartType = new BasicChart(driver)
        pane.children_=(drawRadialLines(pane,chartType) ++
            drawInnerCircles(pane) ++
            makeInnerRule(pane, chartType) ++
            makeOuterRule(pane, chartType)  ++
            drawSigns(pane,chartType)
        )
    }

    def drawSigns(pane: Pane, chart: BasicChart): Seq[Text] = {
        lazy val radius = DoubleProperty(Math.min(pane.widthProperty.doubleValue()/2,pane.heightProperty.doubleValue()/2))
        radius <== min(pane.widthProperty/2 * RadInnerRuler, pane.heightProperty/2 * RadInnerRuler)
        val angle = chart.getRotationAngle
        val zodGlyphs: Seq[Text] = Glyphs.ZodGlyphs
        for ((z,i) <- zodGlyphs zipWithIndex) {
            val b = z.layoutBounds
            z.translateX <== pane.width/2 - b.value.getWidth/2
            z.translateY <== pane.height/2 + b.value.getHeight/2
            z.scaleX <== radius * Scl
            z.scaleY <== radius * Scl
            z.rotate = angle - i*30 + 90
            z.layoutX <== radius * SignRadFac * Math.cos(Math.toRadians(angle - i*30))
            z.layoutY <== radius * SignRadFac * Math.sin(Math.toRadians(angle - i*30))
        }
        zodGlyphs
    }


    def makeInnerRule(pane: Pane, chart: BasicChart) = {
        val offset = chart.getOffset
        lazy val radius = DoubleProperty(Math.min(pane.widthProperty.doubleValue()/2,pane.heightProperty.doubleValue()/2))
        radius <== min(pane.widthProperty/2 * RadInnerRuler, pane.heightProperty/2 * RadInnerRuler)
        val defaultMark = InnerRuleMarks.last
        val coupleInsets = mutable.Map(0 -> InnerRuleMarks.head, 5 -> InnerRuleMarks(1))
        val angles = for (i <- 0 until 360) yield Math.toRadians(offset + i)
        for { (angle,i) <- angles.zipWithIndex
              inset = radius - radius * coupleInsets.getOrElse(i % 10, defaultMark)
        } yield new Line() {
            startX <==  inset * Math.cos(angle)
            startY <==  inset * Math.sin(angle)
            endX <== radius * Math.cos(angle)
            endY <== radius * Math.sin(angle)
            stroke = Color.Black
            strokeWidth = 1d
            translateX <== pane.width/2
            translateY <== pane.height/2
        }
    }

    def makeOuterRule(pane: Pane, chart: BasicChart) = {
        val offset = chart.getOffset
        lazy val radius = DoubleProperty(Math.min(pane.widthProperty.doubleValue()/2,pane.heightProperty.doubleValue()/2))
        radius <== min(pane.widthProperty/2 * RadOuterRuler, pane.heightProperty/2 * RadOuterRuler)
        val defaultMark = OuterRuleMarks.last
        val coupleInsets = mutable.Map(0 -> OuterRuleMarks.head, 5 -> OuterRuleMarks(1))
        val angles = for (i <- 0 until 360) yield Math.toRadians(offset + i)
        for { (angle,i) <- angles.zipWithIndex
              inset = radius - radius * coupleInsets.getOrElse(i % 10, defaultMark)
        } yield new Line() {
            startX <==  inset * Math.cos(angle)
            startY <==  inset * Math.sin(angle)
            endX <== radius * Math.cos(angle)
            endY <== radius * Math.sin(angle)
            stroke = Color.Black
            strokeWidth = 1d
            translateX <== pane.width/2
            translateY <== pane.height/2
        }
    }
    def drawRadialLines(pane: Pane, chart: BasicChart): Seq[Shape] = {
        val signCups: Seq[Double] = chart.getSignsCusps
        lazy val radius = DoubleProperty(Math.min(pane.widthProperty.doubleValue()/2,pane.heightProperty.doubleValue()/2))
        radius <== min(pane.widthProperty/2 * RadInnerRuler, pane.heightProperty/2 * RadInnerRuler)
        val inset =  DoubleProperty(radius.value * RadialInset)
        inset <==  radius * RadialInset
        for {
            s <- signCups
            angle = Math.toRadians(180 - s)
        } yield new Line() {
            startX <==  (radius + inset) * Math.cos(angle)
            startY <==  (radius + inset) * Math.sin(angle)
            endX <== radius * Math.cos(angle)
            endY <== radius * Math.sin(angle)
            stroke = Color.Black
            strokeWidth = 1d
            translateX <== pane.width/2
            translateY <== pane.height/2
        }
    }

    def drawInnerCircles(pane: Pane): Seq[Shape] = {
        val VeryInnerRad = DoubleProperty(RadVeryInner)
        val InnerRad = DoubleProperty(RadInner)
        val size = DoubleProperty(0.0)
        size <== min(pane.widthProperty, pane.heightProperty)

        val veryInnerCircle = new Circle {
            fill = Color.White
            stroke = Color.Black
        }

        val innerCircle = new Circle {
            fill = Color.Transparent
            stroke = Color.Black
        }

        veryInnerCircle.centerX <== pane.width/2
        veryInnerCircle.centerY <== pane.height/2
        veryInnerCircle.radius <== size * VeryInnerRad

        innerCircle.centerX <== pane.width/2
        innerCircle.centerY <== pane.height/2
        innerCircle.radius <== size * InnerRad

        val testPoint = new Circle {
            fill = Color.Black
            radius = 3
            centerX = 300 * Math.cos(Math.toRadians(180))
            centerY = 300 * Math.sin(Math.toRadians(180))
            translateX <== pane.width/2
            translateY <== pane.height/2
        }

        Seq( innerCircle, veryInnerCircle )
    }

}
