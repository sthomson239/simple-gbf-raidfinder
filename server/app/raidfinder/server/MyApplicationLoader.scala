package raidfinder.server

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import raidfinder.stream.RaidFinder
import play.api._
import play.api.routing.Router
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

class MyApplicationLoader extends ApplicationLoader{

  def load(context: ApplicationLoader.Context): Application = {
    new Components(context).application
  }
}
