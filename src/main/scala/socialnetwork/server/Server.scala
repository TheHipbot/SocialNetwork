package socialnetwork.server

import akka.actor._
import akka.io.IO
import akka.routing.{RoundRobinPool, FromConfig}
import com.typesafe.config.ConfigFactory
import org.mashupbots.socko.handlers.{StaticResourceRequest, StaticFileRequest, StaticContentHandlerConfig, StaticContentHandler}
import org.mashupbots.socko.routes._
import org.mashupbots.socko.webserver.{WebServer, WebServerConfig}
import java.io.File
import play.api.libs.json.{Json, Writes}
import socialnetwork.actor.{ClientHandlerActor, SocialNetworkActor, ConnectedClientsActor, TweetActor}
import socialnetwork.common._

object Server extends App {

  var clients = Map[String, ActorRef]()

  val settings = ConfigFactory.load.getConfig("server")

  val actorSystem = ActorSystem("socialnetwork", settings)


  // TODO replication with routers
//  val tweetActorPool = actorSystem.actorOf(FromConfig.props(Props[TweetActor]), "tweet-router")
  val tweetActorPool = actorSystem.actorOf(Props[TweetActor])

//  val connectedClientsActorPool = actorSystem.actorOf(FromConfig.props(Props[ConnectedClientsActor]), "connection-router")
  val socialNetworkActorPool = actorSystem.actorOf(Props[SocialNetworkActor])

//  val socialNetworkActorPool = actorSystem.actorOf(FromConfig.props(Props[SocialNetworkActor]), "social-network-router")
  val connectedClientsActorPool = actorSystem.actorOf(Props(classOf[ConnectedClientsActor], socialNetworkActorPool))

//  val clientHandlerPool = actorSystem.actorOf(FromConfig.props(Props(classOf[ClientHandlerActor], connectedClientsActorPool, socialNetworkActorPool, tweetActorPool)), "client-handler-router")

  // static content handlers
  val staticHandlerConfig = StaticContentHandlerConfig(actorSystem)

  val staticContentHandlerRouter = actorSystem.actorOf(Props(new StaticContentHandler(staticHandlerConfig))
    .withRouter(FromConfig()).withDispatcher("static-dispatcher"), "static-file-router")

  object StaticContentHandlerConfig extends ExtensionId[StaticContentHandlerConfig] with ExtensionIdProvider {
    override def lookup = StaticContentHandlerConfig
    override def createExtension(system: ExtendedActorSystem) =
      new StaticContentHandlerConfig(system.settings.config, "static-content-handler")
  }

  def onHandshakeComplete(socketId: String): Unit = {
    System.out.println(s"Web Socket $socketId connected")
  }

  def onSocketClose(socketId: String): Unit = {
    clients(socketId) ! DisconnectUser
    clients -= socketId
  }

  val routes = Routes({
    case HttpRequest(request) => request match {
      case GET(Path("/")) =>
        staticContentHandlerRouter ! new StaticResourceRequest(request, settings.getString("static-dir") + "/index.html")
      case GET(Path(path)) =>
        staticContentHandlerRouter ! new StaticResourceRequest(request, settings.getString("static-dir") + path)
    }

    case WebSocketHandshake(handshake) => handshake match {
      case Path("/chat/") =>
        handshake.authorize(
          onComplete = Some(onHandshakeComplete),
          onClose = Some(onSocketClose))
    }

    case WebSocketFrame(wsFrame) =>
      if (clients.contains(wsFrame.webSocketId)) {
        clients(wsFrame.webSocketId) ! wsFrame
      }
      else {
        val socketId = wsFrame.webSocketId
        val handler = actorSystem.actorOf(Props(classOf[ClientHandlerActor], wsFrame, connectedClientsActorPool, socialNetworkActorPool, tweetActorPool))
        clients += (socketId -> handler)
        handler ! wsFrame
        println(s"Created client handler for $socketId")
      }
  })

  val webServer = new WebServer(WebServerConfig(port = settings.getInt("port")), routes, actorSystem)

  webServer.start()
}

