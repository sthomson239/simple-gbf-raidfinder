package raidfinder.client

import com.thoughtworks.binding.Binding
import scalatags.JsDom
import org.lrng.binding.ElementFactories
import org.scalajs.dom.raw.HTMLElement
import org.lrng.binding.html
import org.scalajs.dom
import org.scalajs.dom.{document, window}
import org.scalajs.dom.raw._

import scala.scalajs.js
import scala.scalajs.js.annotation._

package object syntax {
    //implicit def toSvgTags(a: TagsAndTags2) = JsDom.svgTags

    implicit class StringOps(val string: String) extends AnyVal {
        def addIf(condition: Boolean, s: String): String =
            if (condition) s"$string$s" else string
        def addIf(condition: Boolean, s: String, elseStr: String): String =
            if (condition) s"$string$s" else s"$string$elseStr"
    }
}