package raidfinder.client

import raidfinder.protocol.domain._
import raidfinder.protocol.requests._
import raidfinder.protocol.responses._
import org.scalajs.dom
import org.scalajs.dom.console

trait WebSocketConnection {
    def newConnection(): Unit
    def sendRequest(request: RequestMessage): Unit
    def closeConnection(): Unit
}

class WebSocketConnectionImpl(url: String, clientOnMessage: ResponseMessage => Unit,
                            clientOnOpen: WebSocketConnection => Unit,
                            clientOnClose: WebSocketConnection => Unit) extends WebSocketConnection {
    private var ws: Option[dom.WebSocket] = None
    newConnection()

    def newConnection(): Unit = {
        val newWebSocket = new dom.WebSocket(url)
        newWebSocket.binaryType = "arraybuffer"
        newWebSocket.onopen = { _: dom.Event => clientOnOpen(this)}//{_: dom.Event => clientOnOpen}//sendRequest(AllRaidBossesRequest()
        newWebSocket.onmessage = {e: dom.MessageEvent =>
            val responseMessage: ResponseMessage = e.data match {
                case str: String => upickle.default.read[ResponseMessage](str)
            }
            clientOnMessage(responseMessage)
        }
        newWebSocket.onclose = {_: dom.CloseEvent => clientOnClose(this)}
        ws = Some(newWebSocket)
    }

    def sendRequest(request: RequestMessage): Unit = {
        val messageJson = upickle.default.write(request)
        ws.foreach(_.send(messageJson))
    }

    def closeConnection(): Unit = {
        ws.foreach(_.close())
        ws = None
    }
}
