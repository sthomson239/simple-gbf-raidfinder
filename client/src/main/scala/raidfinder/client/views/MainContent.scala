package raidfinder.client.views

import com.thoughtworks.binding
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding._
import raidfinder.client.RaidFinderClient
import org.lrng.binding.html
import org.scalajs.dom.raw.HTMLElement
import raidfinder.client.ViewModel._
import raidfinder.client.syntax._
import org.scalajs.dom.raw.Event
import scala.scalajs.js
import org.scalajs.dom
import js.Dynamic.global

object MainContent {
    @html
    def content(client: RaidFinderClient, currentTime: Binding[Double]): Binding[Constants[HTMLElement]] = Binding {
        val mainHeader = MainHeader.content(client).bind
        val bossDialog = BossDialog.content(client).bind
        val main =
            <div>
                {mainHeader}
                <div class="container-fluid py-2">
                    <div class="d-flex flex-row flex-nowrap">
                        {
                            client.followedBossColumns.map{ column =>
                                RaidInfos.raidInfoColumn(column, currentTime, client).bind
                            }
                        }
                    </div>
                </div>
            </div>
        Constants(bossDialog, main.bind)
    }
}