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
import raidfinder.client.RaidBossColumn
import raidfinder.protocol.domain._
import org.scalajs.dom.raw._
import org.scalajs.dom.{console, document, window}

object RaidInfos {
    @html
    def raidInfoColumn(column: RaidBossColumn, currentTime: Binding[Double], client: RaidFinderClient): Binding[HTMLDivElement] = {
        <div class="card" style="width: 18rem;">
            <div class="card-header">
                {raidInfoColumnHeader(column, client)}
            </div>
            <div class="card-body px-0">
                {raidInfoList(client, column.raidInfos, currentTime).bind}
            </div>
        </div>
    }

    @html
    def raidInfoList(client: RaidFinderClient, raidInfos: BindingSeq[RaidInfo], currentTime: Binding[Double]): Binding[HTMLElement] = {
        <div class="list-group-flush">
            {
                raidInfos.map{raidInfo =>
                    raidInfoListItem(raidInfo, currentTime).bind    
               }
            }
        </div>
    }

    @html
    def raidInfoListItem(raidInfo: RaidInfo, currentTime: Binding[Double]): Binding[HTMLElement] = {
        val clicked = Var(false)
        <a href="#" class={"list-group-item list-group-item-action list-group-item".addIf(clicked.bind == true, "-dark")} onclick={e: Event => copyToClipboard(raidInfo.raidId);clicked.value = !clicked.value}>
            <div>
                {formatTime(raidInfo.createdAt, currentTime.bind)}
            </div>
            <span>{raidInfo.raidId}</span>
        </a>
    }

    @html
    def raidInfoColumnHeader(column: RaidBossColumn, client: RaidFinderClient): Binding[HTMLElement] = {
        <div class="row">
            <div class="col-10">{column.raidBoss.displayName}</div>
            <div class="col-2"><button type="button" class="btn-close ml-auto" onclick={e: Event => client.toggleFollowBoss(column)}></button></div>
        </div>
    }

    def formatTime(tweetTime: java.util.Date, currentTime: Double): String = {
        (Math.round((Math.round(currentTime - tweetTime.getTime) / 5000) * 5000) / 1000).toString + "s"
    }

    def copyToClipboard(stringToCopy: String): Unit = {
        val selection = document.getSelection()
        val selectedRange =
            if (selection.rangeCount > 0) Some(selection.getRangeAt(0))
            else None

        val textArea = createInvisibleTextArea()
        textArea.value = stringToCopy
        document.body.appendChild(textArea)
        textArea.select()

        val result = try {
            document.execCommand("copy")
        } catch {
            case e: Throwable => false 
        } finally {
            textArea.blur()
            document.body.removeChild(textArea)
        }

        selectedRange.foreach { range =>
            selection.removeAllRanges()
            selection.addRange(range)
        }
    }

    private def createInvisibleTextArea(): HTMLTextAreaElement = {
        val textArea = document.createElement("textarea").asInstanceOf[HTMLTextAreaElement]
        val s = textArea.style

        s.position = "fixed"
        s.top = "0"
        s.left = "0"
        s.width = "2em"
        s.height = "2em"
        s.padding = "0"
        s.border = "none"
        s.outline = "none"
        s.boxShadow = "none"
        s.background = "transparent"
        s.fontSize = "16px"

        textArea
    }
}
