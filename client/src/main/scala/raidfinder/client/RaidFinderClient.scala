package raidfinder.client

import raidfinder.protocol.domain._
import raidfinder.protocol.requests._
import raidfinder.protocol.responses._
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.Vars
import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.Binding.BindingSeq
import org.scalajs.dom
import org.scalajs.dom.console
import scala.scalajs.js
import scala.collection.mutable

trait RaidFinderClient {
    def allBossColumns: Vars[RaidBossColumn]
    def followedBossColumns: BindingSeq[RaidBossColumn]
    def truncateColumns(maxColumnSize: Int): Unit
    def toggleFollowBoss(bossColumn: RaidBossColumn): Unit
    def unfollowAllBosses(): Unit
    def allBossesCategoryGrouped(): Binding[Map[String, mutable.Buffer[RaidBossColumn]]]
}

class RaidFinderClientImpl(webSocketUrl: String) extends RaidFinderClient {
    val allBossColumns: Vars[RaidBossColumn] = Vars.empty
    val followedBossColumns: Vars[RaidBossColumn] = Vars.empty
    private var allBossesMap: mutable.Map[String, RaidBossColumn] = mutable.Map.empty

    def allBossesCategoryGrouped() = Binding {
        allBossColumns.value.groupBy(_.raidBoss.category)
    }

    private val onWebSocketMessage = (msg: ResponseMessage) => msg match {
        case response: AllRaidBossesResponse => 
            handleRaidBossesResponse(response.raidBosses)
        case response: RaidInfoResponse =>
            addRaidInfoToCol(response.raidInfo)
    }

    private val onWebSocketOpen = (wsClient: WebSocketConnection) => wsClient.sendRequest(AllRaidBossesRequest())

    private val onWebSocketClose = (wsClient: WebSocketConnection) => {
        console.log("CLOSED")
        wsClient.newConnection()}

    js.timers.setInterval(10000) {
            wsClient.sendRequest(AllRaidBossesRequest())
    }

    private def handleRaidBossesResponse(raidBosses: Seq[RaidBoss]): Unit = {
        raidBosses.foreach{raidBoss =>
            if (!allBossesMap.contains(raidBoss.displayName)) {
                val newColumn = RaidBossColumn.empty(raidBoss)
                allBossesMap += (raidBoss.displayName -> newColumn)
                allBossColumns.value += newColumn
            }    
        }
    }

    private def addRaidInfoToCol(raidInfo: RaidInfo): Unit = {
        allBossesMap.get(raidInfo.raidBoss.displayName).foreach{column =>
            raidInfo +=: column.raidInfos.value    
        }
    }

    private var wsClient: WebSocketConnection = new WebSocketConnectionImpl(webSocketUrl, onWebSocketMessage, onWebSocketOpen, onWebSocketClose)

    private def followBoss(bossColumn: RaidBossColumn): Unit = {
        bossColumn.isFollowing.value = true
        followedBossColumns.value += bossColumn
        wsClient.sendRequest(FollowRequest(bossColumn.raidBoss.displayName))

    }

    private def unfollowBoss(bossColumn: RaidBossColumn): Unit = {
        bossColumn.isFollowing.value = false
        followedBossColumns.value -= bossColumn
        wsClient.sendRequest(UnfollowRequest(bossColumn.raidBoss.displayName))
    }

    def truncateColumns(maxColumnSize: Int): Unit = {
        allBossColumns.value.foreach { column =>
            val raidInfos = column.raidInfos.value
            if (raidInfos.length > maxColumnSize) {
                raidInfos.trimEnd(raidInfos.length - maxColumnSize)
            }
        }
    }

    def toggleFollowBoss(bossColumn: RaidBossColumn): Unit = {
        if (bossColumn.isFollowing.value) unfollowBoss(bossColumn)
        else followBoss(bossColumn)
    }

    def unfollowAllBosses(): Unit = {
        while (!followedBossColumns.value.isEmpty) {
            unfollowBoss(followedBossColumns.value.head)
        }
    }
}

case class RaidBossColumn(raidBoss: RaidBoss, raidInfos: Vars[RaidInfo], isFollowing: Var[Boolean]) {
    def clear(): Unit = raidInfos.value.clear()
}

object RaidBossColumn {
    def empty(raidBoss: RaidBoss): RaidBossColumn = {
        RaidBossColumn(
            raidBoss = raidBoss,
            raidInfos = Vars.empty,
            isFollowing = Var(false)
        )
    }
}
