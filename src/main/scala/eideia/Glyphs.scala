package eideia

import java.net.URL

import eideia.draw.Colors
import javafx.scene.text.{Font => jfxFont}
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, Text}

object Glyphs {

    val ZodLetters = List("q","w","e","r","t","y","u","i","o","p","a","s")

    val SignColors: Seq[Color] = Colors.signHuberColSeq

    val glyphsFont: Font = {
        val fontFile = "fonts/Astro-Nex2.ttf"
        val url: URL = this.getClass.getClassLoader.getResource(fontFile)
        val font = jfxFont.loadFont(url.toExternalForm, 96)
        new Font(font)
    }

    def ZodGlyphs: Seq[Text] = for( i <- 0 until 12) yield new Text() {
        font = glyphsFont
        text = ZodLetters(i)
        fill = SignColors(i)
    }

}
