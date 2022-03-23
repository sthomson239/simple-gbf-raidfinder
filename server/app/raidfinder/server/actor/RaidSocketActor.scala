package raidfinder.server.actor

import akka.Done
import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import raidfinder.protocol.requests._
import raidfinder.protocol.responses._
import akka.stream.scaladsl.{Keep, Sink}
import akka.stream.{KillSwitches, Materializer, UniqueKillSwitch}
import akka.util.Timeout
import raidfinder.stream.RaidFinder
import raidfinder.protocol.domain._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class RaidSocketActor(out: ActorRef, raidFinder: RaidFinder)(implicit materializer: Materializer) extends Actor {
  implicit val timeout: Timeout = 2.seconds
  private var following: mutable.Map[String, UniqueKillSwitch] = mutable.Map()

  def receive: Receive = {
    case request: RequestMessage => handleRequest(request)
  }

  private def push(response: ResponseMessage): Unit = out ! response

  private val handleRequest: PartialFunction[RequestMessage, _] = {
    case _: AllRaidBossesRequest =>
      raidFinder.getActiveBosses onComplete {
        case Success(activeBosses) => push(AllRaidBossesResponse(activeBosses.values.toSeq))
        case Failure(_) => {}
      }
    case req: FollowRequest =>
      follow(req.bossName)
    case req: UnfollowRequest =>
      unfollow(req.bossName)
  }

  private def follow(bossName: String): Unit = {
    raidFinder.getRaidInfos(bossName) onComplete {
      case Success(bossSource) =>
        val sink: Sink[RaidInfo, Future[Done]] = Sink.foreach{
          raidInfo => push(RaidInfoResponse(raidInfo))
        }
        val killSwitch = bossSource.viaMat(KillSwitches.single)(Keep.right).toMat(sink)(Keep.left).run()
        following += bossName -> killSwitch

      case Failure(_) => {}
    }
  }

  private def unfollow(bossName: String): Unit = {
    following(bossName).shutdown()
    following -= bossName
  }


}
