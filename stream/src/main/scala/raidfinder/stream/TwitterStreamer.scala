package raidfinder.stream

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import twitter4j._

object TwitterStreamer {
  def newSource(twitterStream: twitter4j.TwitterStream, filterTerms: Seq[String])(implicit materializer: Materializer): Source[Status, NotUsed] = {
    val source1 = Source.actorRef(
      completionMatcher = {
        case Done =>
          CompletionStrategy.immediately
      },
      failureMatcher = PartialFunction.empty,
      bufferSize = 100,
      overflowStrategy = OverflowStrategy.dropHead
    )
    val (statusActor, source) = source1.toMat(BroadcastHub.sink)(Keep.both).run()

    val listener = new StatusAdapter() {
      override def onStatus(status: Status): Unit = statusActor ! status
      override def onException(e: Exception): Unit = println(e)
    }

    twitterStream.addListener(listener)
    twitterStream.filter(new FilterQuery(filterTerms: _*))
    source
  }
}
