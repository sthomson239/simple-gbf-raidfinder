package raidfinder.protocol.responses

import raidfinder.protocol.domain._
import upickle.default.{ReadWriter => RW, macroRW}

sealed trait ResponseMessage
object ResponseMessage {
  implicit val rw: RW[ResponseMessage] = RW.merge(RaidBossResponse.rw, AllRaidBossesResponse.rw, RaidInfoResponse.rw)
}

case class RaidBossResponse(raidBoss: RaidBoss) extends ResponseMessage
object RaidBossResponse {
  implicit val rw: RW[RaidBossResponse] = macroRW
}

case class AllRaidBossesResponse(raidBosses: Seq[RaidBoss]) extends ResponseMessage
object AllRaidBossesResponse {
  implicit val rw: RW[AllRaidBossesResponse] = macroRW
}

case class RaidInfoResponse(raidInfo: RaidInfo) extends ResponseMessage
object RaidInfoResponse {
  implicit val rw: RW[RaidInfoResponse] = macroRW
}
