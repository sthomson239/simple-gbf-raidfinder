package raidfinder.client

import com.thoughtworks.binding
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding._
import raidfinder.client.RaidFinderClient
import org.lrng.binding.html
import org.scalajs.dom.raw.HTMLElement
import raidfinder.client.ViewModel._
import raidfinder.client.syntax._
import raidfinder.client.views.BossFollowMenu
import org.scalajs.dom.raw.Event

object ViewModel {

    trait DialogTab {
        val headerLabel: String
        def body(raidFinderClient: RaidFinderClient): Binding[HTMLElement]
        def footer(raidFinderClient: RaidFinderClient): Binding[HTMLElement]
    }

    object DialogTab {
        val all: List[DialogTab] = List(Follow, Filter)

        case object Follow extends DialogTab {
            val headerLabel = "Follow"

            def body(raidFinderClient: RaidFinderClient): Binding[HTMLElement] = BossFollowMenu.content(raidFinderClient)

            @html
            def footer(raidFinderClient: RaidFinderClient): Binding[HTMLElement] = {
                <button type="button" class="btn btn-danger" onclick={event: Event => raidFinderClient.unfollowAllBosses()}>Unfollow All</button>
            }
        }
        
        case object Filter extends DialogTab {
            val headerLabel = "Filter"
            
            @html
            def body(raidFinderClient: RaidFinderClient): Binding[HTMLElement] = <div></div>

            @html
            def footer(raidFinderClient: RaidFinderClient): Binding[HTMLElement] = {
                <div></div>
            }


        }
    }
}
