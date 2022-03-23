package raidfinder.server

import akka.actor.ActorSystem
import akka.stream.Materializer
import controllers.Assets.Asset
import raidfinder.server.controller.{HomeController, WebSocketController}
import raidfinder.stream.RaidFinder
import play.api.{ApplicationLoader, BuiltInComponentsFromContext}
import play.api.{BuiltInComponents, Mode}
import play.api.http.{ContentTypes, DefaultHttpErrorHandler}
import play.api.libs.json.Json
import play.api.mvc._
import play.api.routing.Router
import play.api.routing.sird._
import play.core.server._
import play.filters.cors.{CORSConfig, CORSFilter}
import play.filters.gzip.GzipFilterComponents

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.Future

class Components(context: ApplicationLoader.Context) extends BuiltInComponentsFromContext(context) with GzipFilterComponents with controllers.AssetsComponents{
  val raidFinder = RaidFinder()(materializer, actorSystem)
  lazy val webSocketController = new WebSocketController(controllerComponents, raidFinder)(actorSystem, materializer)
  lazy val homeController = new HomeController(controllerComponents, assetsFinder)

  private val corsFilter = new CORSFilter(corsConfig = CORSConfig().withAnyOriginAllowed)

  def httpFilters: Seq[EssentialFilter] = List(gzipFilter, corsFilter)

  def router: Router = Router.from {
    case GET(p"/") =>
      homeController.index()
    case GET(p"/ws/raids") =>
      webSocketController.raidSocket()
    case GET(p"/assets/$file*") =>
      assets.versioned(path = "/public", file: Asset)
  }
}
