package raidfinder.server.controller

import raidfinder.protocol.requests._
import raidfinder.protocol.responses._
import raidfinder.stream.RaidFinder
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import raidfinder.server.actor.RaidSocketActor
import javax.inject.Inject
import play.api.http.websocket.{CloseCodes, CloseMessage, Message, WebSocketCloseException}
import play.api.libs.streams._
import play.api.mvc._
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.mvc.Controller

import scala.concurrent.Future

class WebSocketController(val controllerComponents: ControllerComponents, raidFinder: RaidFinder)(implicit actorSystem: ActorSystem, materializer: Materializer) extends BaseController {

  implicit val protobufMessageFlowTransformer: MessageFlowTransformer[RequestMessage, ResponseMessage] = {
    MessageFlowTransformer.stringMessageFlowTransformer.map(
      upickle.default.read[RequestMessage](_),
      upickle.default.write(_)
    )
  }

  private def closeWebSocket(): WebSocketCloseException = {
    val closeError = CloseMessage(Some(CloseCodes.InconsistentData), "Invalid Input")
    WebSocketCloseException(closeError)
  }

  def raidSocket(): WebSocket = WebSocket.acceptOrResult[RequestMessage, ResponseMessage] { request =>
    val props = (out: ActorRef) => Props(new RaidSocketActor(out, raidFinder))
    val flow = ActorFlow.actorRef[RequestMessage, ResponseMessage](props = props)
    Future.successful(Right(flow))
  }
}
