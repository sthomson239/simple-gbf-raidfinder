package raidfinder.protocol.requests

import upickle.default.{ReadWriter => RW, macroRW}

sealed trait RequestMessage
object RequestMessage {
  implicit val rw: RW[RequestMessage] = RW.merge(FollowRequest.rw, UnfollowRequest.rw, AllRaidBossesRequest.rw)
}

case class FollowRequest(bossName: String) extends RequestMessage
object FollowRequest {
  implicit val rw: RW[FollowRequest] = macroRW
}

case class UnfollowRequest(bossName: String) extends RequestMessage
object UnfollowRequest {
  implicit val rw: RW[UnfollowRequest] = macroRW
}

case class AllRaidBossesRequest() extends RequestMessage
object AllRaidBossesRequest {
  implicit val rw: RW[AllRaidBossesRequest] = macroRW
}