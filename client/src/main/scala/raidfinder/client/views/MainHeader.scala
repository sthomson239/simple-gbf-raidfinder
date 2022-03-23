package raidfinder.client.views

import raidfinder.client.RaidFinderClient
import com.thoughtworks.binding.Binding
import org.scalajs.dom.raw.HTMLElement
import org.lrng.binding.html
import org.scalajs.dom.raw._

object MainHeader {
    @html
    def content(client: RaidFinderClient): Binding[HTMLElement] = {
        <header class="p-3 bg-dark text-white">
            <div class="container">
                <div class="d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start">
                    <a class="d-flex align-items-center mb-2 mb-lg-0 text-white text-decoration-none">
                        
                    </a>
                    {tabs().bind}
                </div>
            </div>
        </header>
    }

    @html
    def tabs(): Binding[HTMLUListElement] = {
        <ul class="nav col-12 col-lg-auto me-lg-auto mb-2 justify-content-center mb-md-0">
            <li>
                <a href="#" class="nav-link px-2 text-white" data:data-bs-toggle="modal" data:data-bs-target="#BossModal">Follow/Unfollow <i class="bi bi-bell-fill fa-10x"></i></a>
            </li>
            <li>
                <a href="#" class="nav-link px-2 text-white">Settings <i class="bi bi-gear-fill"></i></a>
            </li>
        </ul>
    }
}