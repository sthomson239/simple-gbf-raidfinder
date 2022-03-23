package raidfinder.server.controller

import raidfinder.protocol.requests._
import raidfinder.protocol.responses._
import raidfinder.stream.RaidFinder
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import controllers.AssetsFinder
import raidfinder.server.actor.RaidSocketActor
import javax.inject.Inject
import play.api.http.Writeable.wByteArray
import play.api.http.websocket.{CloseCodes, CloseMessage, Message, WebSocketCloseException}
import play.api.libs.streams._
import play.api.mvc._
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.mvc.Controller
import play.api.libs.json._
import raidfinder.server.views

class HomeController(val controllerComponents: ControllerComponents, val assetsFinder: AssetsFinder) extends BaseController {

  def index(): Action[AnyContent] = Action {
    Ok(views.html.index(assetsFinder))
  }
}
