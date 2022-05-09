package raidfinder.client

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding
import org.lrng.binding.html
import org.scalajs.dom
import org.scalajs.dom.{console, document, window}
import org.scalajs.dom.raw._

import scala.scalajs.js
import scala.scalajs.js.annotation._
import raidfinder.client.views._

object Application {
    @JSExport
    def main(args: Array[String]): Unit = {
        val url = "ws://" + window.location.host + "/ws/raids"
        val client: RaidFinderClient = new RaidFinderClientImpl(url)

        val currentTime: Var[Double] = Var(js.Date.now())
        js.timers.setInterval(2000) {
            client.truncateColumns(50)
            currentTime.value = js.Date.now()
        }

        val rootDiv = dom.document.createElement("div")
        val header = MainContent.content(client, currentTime)
        html.render(rootDiv, header)
        dom.document.body.appendChild(rootDiv)
    }
}