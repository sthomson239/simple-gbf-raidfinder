package raidfinder.stream

import akka.actor.{Actor, ActorRef, Props}
import akka.stream.Materializer
import raidfinder.protocol.domain._
import twitter4j.{Status, TwitterStream, TwitterStreamFactory}
import akka.stream._
import akka.stream.scaladsl._
import scala.collection.mutable

object ActiveBossesManager {
    case class GetRaidBosses()

    lazy val readRaidsJson: mutable.Map[String, RaidBoss] = {
        val jsonString = scala.io.Source.fromFile("./raids.json").mkString
        val data = ujson.read(jsonString)
        for (
            (sectionName, section) <- data("raid bosses").obj;
            (categoryName, category) <- section.obj;
            raidBoss <- category.arr;
            (jpName, enName, level) = (raidBoss("jp").str, raidBoss("en").str, raidBoss("level").num.toInt)
        ) yield (jpName -> RaidBoss(Some(enName), jpName, level, sectionName, categoryName))
    }

    def apply() = Props(new ActiveBossesManager(readRaidsJson))
}

class ActiveBossesManager(var jpRaidBossMap: mutable.Map[String, RaidBoss]) extends Actor {
    import ActiveBossesManager._

    def receive: Receive = {
        case RaidTweet(raidId, text, createdAt, bossName, bossLevel, language) =>
            sender() ! (language match {
                case Japanese =>
                    val raidBoss = jpRaidBossMap.getOrElseUpdate(bossName, RaidBoss(None, bossName, bossLevel, "Unknown", "Unknown"))
                    Some(RaidInfo(raidBoss, raidId, text, createdAt))
                case English =>
                    val raidBoss = jpRaidBossMap.values.find(_.enName.contains(bossName))
                    raidBoss.map(RaidInfo(_, raidId, text, createdAt))
            })
            
        case GetRaidBosses() =>
            sender() ! jpRaidBossMap.map{case (jpName: String, raidBoss: RaidBoss) => (raidBoss.enName.getOrElse(jpName), raidBoss)}
    }

}
