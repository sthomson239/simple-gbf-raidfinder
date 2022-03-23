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


object BossDialog {
    @html
    def content(client: RaidFinderClient): Binding[HTMLElement] = {
        val currentTab: Var[DialogTab] = Var(DialogTab.Follow)
        <div class="container">
            <div class="modal fade text-left" id="BossModal" tabindex="-1">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        {modalHeader(currentTab)}
                        <div class="modal-body">
                            {currentTab.bind.body(client)}
                        </div>
                        {modalFooter(client, currentTab)}
                        <div class="line"></div>
                    </div>
                </div>
            </div>
        </div>
    }

    @html
    def modalHeader(currentTab: Var[DialogTab]): Binding[HTMLElement] = {
        <div class="modal-header row d-flex justify-content between mx-1 mx-sm-3 mb-0 pb-0 border-0">
            {
                Constants(DialogTab.all: _*).map{tab =>
                    val onClick = {e: Event => currentTab.value = tab}
                    <div class={"col rf-tab".addIf(currentTab.bind == tab, " active")} onclick={onClick}>
                        <h6 class={"".addIf(currentTab.bind == tab, " font-weight-bold", " text-muted")}>
                            {tab.headerLabel}
                        </h6>
                    </div>
                }
            }
        </div>
    }

    @html
    def modalFooter(client: RaidFinderClient, currentTab: Binding[DialogTab]): Binding[HTMLElement] = {
        <div class="modal-footer d-flex justify-content-right border-0">
            {currentTab.bind.footer(client)}
            <button type="button" class="btn btn-secondary" data:data-bs-dismiss="modal">Close</button>
        </div>
    }
}