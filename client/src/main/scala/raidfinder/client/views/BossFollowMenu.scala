package raidfinder.client.views

import org.lrng.binding.html
import com.thoughtworks.binding.Binding
import org.scalajs.dom.raw._
import raidfinder.client.RaidFinderClient
import raidfinder.protocol.domain._
import raidfinder.client.syntax.StringOps
import raidfinder.client.RaidBossColumn

object BossFollowMenu {
    @html
    def content(raidFinderClient: RaidFinderClient): Binding[HTMLElement] = {
        bossList(raidFinderClient)
    }

    @html
    def bossList(client: RaidFinderClient): Binding[HTMLElement] = {
        <div class="list-group">
            {
                for {
                    bossColumn <- client.allBossColumns
                } yield bossListItem(client, bossColumn, bossColumn.isFollowing)
            }
        </div>
    }

    @html
    def bossListItem(client: RaidFinderClient, bossColumn: RaidBossColumn, isFollowing: Binding[Boolean]): Binding[HTMLElement] = {
        <a href="#" class={"list-group-item list-group-item-action".addIf(isFollowing.bind, " active")} onclick={event: Event => client.toggleFollowBoss(bossColumn)}>
            {bossColumn.raidBoss.displayName}
        </a>
    }
}
