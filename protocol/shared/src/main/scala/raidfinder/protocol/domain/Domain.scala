package raidfinder.protocol.domain

import java.util.Date
import upickle.default.{ReadWriter => RW, macroRW}
import upickle.default.readwriter

sealed trait Language
case object English extends Language
case object Japanese extends Language

case class RaidInfo(raidBoss: RaidBoss, raidId: String, text: String, createdAt: Date)
object RaidInfo {
  implicit val rw: RW[RaidInfo] = macroRW
  implicit val drw: RW[java.util.Date] = readwriter[Long].bimap[java.util.Date](
    x => x.getTime,
    x => new java.util.Date(x)
  )
}

case class RaidTweet(raidId: String, text: String, createdAt: Date, bossName: String, bossLevel: Int, language: Language)

case class RaidBoss(enName: Option[String], jpName: String, level: Int, section: String, category: String) {
  val displayName = enName.getOrElse(jpName)
}
object RaidBoss {
  implicit val rw: RW[RaidBoss] = macroRW
}