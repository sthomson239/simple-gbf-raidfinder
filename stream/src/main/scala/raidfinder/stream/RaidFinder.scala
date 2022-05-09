package raidfinder.stream

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import raidfinder.protocol.domain._
import twitter4j.{Status, TwitterStream, TwitterStreamFactory}
import akka.stream._
import akka.stream.scaladsl._
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.{DurationInt, FiniteDuration}


object RaidFinder {
    val filters = Seq("参加者募集！", ":参戦ID", "I need backup!", ":Battle ID")

    def apply(twitterStream: TwitterStream = TwitterStreamFactory.getSingleton)(implicit materializer: Materializer, actorSystem: ActorSystem): RaidFinder = {
        val statuses = TwitterStreamer.newSource(twitterStream, filters)
        new RaidFinder(statuses) {
            override def onShutdown(): Unit = {
            twitterStream.cleanUp()
            twitterStream.shutdown()
            }
        }
        }
}

class RaidFinder(statuses: Source[Status, _])(implicit materializer: Materializer, actorSystem: ActorSystem) {
    import RaidFinder._
    implicit val timeout: Timeout = 10.seconds
    private val activeBossesManager = actorSystem.actorOf(ActiveBossesManager())
    private val raidInfos = statuses.map(StatusParser.parse).collect{case Some(x) => x}.ask[Option[RaidInfo]](parallelism=5)(activeBossesManager).collect{case Some(x) => x}

    protected def onShutdown(): Unit = ()

    def shutdown(): Unit = {
        actorSystem.stop(activeBossesManager)
        onShutdown()
    }

    def getRaidInfos(bossName: String): Future[Source[RaidInfo, _]] = {
        Future.successful(raidInfos.filter(_.raidBoss.displayName == bossName))
    }

    def getActiveBosses: Future[collection.mutable.Map[String, RaidBoss]] = {
        activeBossesManager ? ActiveBossesManager.GetRaidBosses() match {
        case response: Future[collection.mutable.Map[String, RaidBoss]] =>
            response
        }
    }
}
