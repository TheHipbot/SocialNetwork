package socialnetwork.actor

import akka.actor.{ActorRef, Actor}
import org.mashupbots.socko.events.WebSocketFrameEvent
import play.api.libs.json.{Writes, Json}
import socialnetwork.common._
import java.util.Date

class ClientHandlerActor(clientSocket: WebSocketFrameEvent,
                         connectedClientsRouter: ActorRef,
                         socialNetworkRouter: ActorRef,
                         tweetRouter: ActorRef) extends Actor {

  // Object to JSON mappings
  // JSON translation for User obj
  implicit val userWrite: Writes[User] = new Writes[User] {
    def writes(user: User) = Json.obj(
      "event" -> "createdUser",
      "id" -> user.uuid,
      "name" -> user.name,
      "following" -> user.following
    )
  }

  // JSON translation for ConnectedUsers obj
  implicit val connectedUsers: Writes[ConnectedUsers] = new Writes[ConnectedUsers] {
    def writes(connectedUsers: ConnectedUsers) = Json.obj(
      "event" -> "connectedUsers",
      "users" -> connectedUsers.users
    )
  }

  // JSON translation for UserList obj
  implicit val userList: Writes[UserList] = new Writes[UserList] {
    def writes(userList: UserList) = Json.obj(
      "event" -> "userList",
      "updateType" -> userList.updateType,
      "userType" -> userList.userType,
      "users" -> userList.following
    )
  }

  // JSON translation for Tweet obj
  implicit val tweetWrite: Writes[Tweet] = new Writes[Tweet] {
    def writes(tweetWrite: Tweet) = Json.obj(
      "event" -> "tweet",
      "id" -> tweetWrite.user,
      "name" -> tweetWrite.name,
      "hash" -> tweetWrite.hash,
      "msg" -> tweetWrite.msg
    )
  }

  // JSON translation for StatusUpdate obj
  implicit val statusUpdate: Writes[StatusUpdate] = new Writes[StatusUpdate] {
    def writes(statusUpdate: StatusUpdate) = Json.obj(
      "event" -> "userUpdate",
      "id" -> statusUpdate.id,
      "name" -> statusUpdate.name,
      "follower" -> statusUpdate.follower,
      "connected" -> statusUpdate.connected
    )
  }

  // JSON translation for Tweets obj
  implicit val tweetsWrites: Writes[Tweets] = new Writes[Tweets] {
    def writes(tweetsWrites: Tweets) = Json.obj(
      "event" -> "tweetsById",
      "ids" -> tweetsWrites.ids,
      "tweets" -> tweetsWrites.tweets
    )
  }

  // JSON translation for Tweets obj
  implicit val tweetsHashWrites: Writes[TweetsByHash] = new Writes[TweetsByHash] {
    def writes(tweetsHashWrites: TweetsByHash) = Json.obj(
      "event" -> "tweetsByHash",
      "ids" -> tweetsHashWrites.hash,
      "tweets" -> tweetsHashWrites.tweets
    )
  }

  def receive = {
    case event: WebSocketFrameEvent =>
      val json = Json.parse(event.readText)
      (json \ "event").as[String] match {
        case "connect" =>
          // TODO invalid id message
          println("Received connect event")
          println(json)
          val id = (json \ "id").as[String]
          if (id.isEmpty)
            event.writeText("{\"event\":\"register\"}")
          else
            connectedClientsRouter ! ConnectUser(id, (json \ "name").as[String])
        case "register" =>
          println("Received register event")
          println(json)
          socialNetworkRouter ! CreateUser((json \ "name").as[String])
        case "disconnect" =>
          println("Received disconnect event")
          println(json)
          socialNetworkRouter ! DisconnectUser
        case "getConnectedFollowers" =>
          println("Received getConnectedFollowers event")
          println(json)
          connectedClientsRouter ! GetFollowers((json \ "id").as[String])
        case "getConnectedUsers" =>
          println("Received getConnectedUsers event")
          println(json)
          connectedClientsRouter ! GetConnectedUsers((json \ "id").as[String])
        case "followUser" =>
          println("Received followUser event")
          println(json)
          socialNetworkRouter ! Follow((json \ "id").as[String], (json \ "follow").as[String])
          connectedClientsRouter ! UpdateUser((json \ "follow").as[String])
        case "getFollowing" =>
          println("Received getFollowing event")
          println(json)
          socialNetworkRouter ! GetFollowing((json \ "id").as[String])
        case "getFollowers" =>
          println("Received getFollowers event")
          println(json)
          socialNetworkRouter ! GetFollowers((json \ "id").as[String])
        case "createTweet" =>
          println("Received createTweet event")
          println(json)
          val newTweet = NewTweet((json \ "id").as[String],
            (json \ "name").as[String],
            (json \ "hash").as[String],
            (json \ "msg").as[String],
            new Date())
          tweetRouter ! newTweet
          connectedClientsRouter ! newTweet
        case "getTweets" =>
          // TODO get updated list from SN ???
          println("Received getTweets event")
          println(json)
          if ((json \ "hash").as[String].isEmpty) {
            if ((json \ "ids").as[List[String]].nonEmpty) {
              tweetRouter ! GetTweets((json \ "ids").as[List[String]])
            }
          } else {
            println("Received getTweets event")
            tweetRouter ! GetTweetsByHash((json \ "hash").as[String])
          }
      }

    // TODO unnescessary??
    case NewTweet(id, name, hash, msg, timestamp) =>
      tweetRouter ! NewTweet(id, name, hash, msg, timestamp)
      connectedClientsRouter ! NewTweet(id, name, hash, msg, timestamp)
    case Tweet(id, name, hash, msg, timestamp) =>
      clientSocket.writeText(Json.toJson(Tweet(id, name, hash, msg, timestamp)).toString())
    case CreatedUser(user) =>
      connectedClientsRouter ! ConnectUser(user.uuid, user.name)
      clientSocket.writeText(Json.toJson(user).toString())
    case DisconnectUser =>
      connectedClientsRouter ! DisconnectUser
      context stop self
    case Connected(id, name) =>
      clientSocket.writeText("{\"event\":\"connected\"}")
    case InvalidUser(connection) =>
      clientSocket.writeText("{\"event\":\"invalid\"}")
    case Following(user) =>
      clientSocket.writeText(Json.toJson(UserList("all", "following", user.following)).toString())
    case Followers(user) =>
      clientSocket.writeText(Json.toJson(UserList("all", "followers", user.following)).toString())
    case ConnectedFollowers(followers) =>
      println("Connected Followers")
      clientSocket.writeText(Json.toJson(UserList("connected", "followers", followers)).toString())
    case ConnectedUsers(users) =>
      clientSocket.writeText(Json.toJson(UserList("connected", "all", users)).toString())
    case StatusUpdate(id, name, connected, follower) =>
      clientSocket.writeText(Json.toJson(StatusUpdate(id, name, connected, follower)).toString())
    case tweets: Tweets =>
      clientSocket.writeText(Json.toJson(tweets).toString())
    case tweets: TweetsByHash =>
      clientSocket.writeText(Json.toJson(tweets).toString())
  }
}
