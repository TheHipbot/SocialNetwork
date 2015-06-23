package socialnetwork.actor

import akka.actor.{ActorRef, Actor}
import akka.routing.Broadcast
import socialnetwork.common._

class ConnectedClientsActor(socialNetworkActor: ActorRef) extends Actor {

  var connections: Map[String, UserConnection] = Map[String, UserConnection]()

  def receive = {
//    case GetConnectedUsers(frame) =>
//      frame.writeText(Json.toJson(ConnectedUsers(users)).toString())
    case ConnectUser(id, name) =>
      connectUser(id, name, sender)
    case DisconnectUser =>
      for ((i, c) <- connections if sender.equals(c.client)) {
        println("Disconnect user: " + i)
        disconnectUser(i)
      }
//    case StatusUpdate(id, connected) =>
//      socialNetworkActor ! GetFollowingForConnections(id, connected)
//      notifyFollowing(id, connected)
    case GetConnectedUsers(id) =>
      val users = for ((k, c) <- connections if k != id) yield (c.id, c.name)
      sender ! ConnectedUsers(users)
    case Followers(user) =>
      updateUser(user)
    case UpdateUser(id) =>
      if (connections contains id) socialNetworkActor ! GetFollowers(id)
    case FollowingWithConnection(user, connected) =>
      notifyFollowing(user, connected)
    case NewTweet(id, name, hash, msg, timestamp) =>
      socialNetworkActor ! GetFollowingForTweet(id, Tweet(id, name, hash, msg, timestamp))
    case FollowingWithTweet(user, tweet) =>
      directSendTweet(user, tweet)
    case ValidUser(connection) =>
      connections += (connection.id -> connection)
      connectUser(connection.id, connection.name, connection.client)
    case InvalidUser(connection) =>
      connection.client ! InvalidUser(connection)
  }

  def notifyFollowing(user: User, connected: Boolean) = {
    for ((id,connection) <- user.following if connections.contains(id)) {
      connections(id).client ! StatusUpdate(user.uuid, user.name, connected, true)
    }
  }

  def connectUser(id: String, name: String, client: ActorRef) = {
    if (!connections.contains(id)) {
      socialNetworkActor ! IsValidUser(UserConnection(id, name, client, Map()))
    } else {
      //    TODO decide on replication
      //    context.parent ! Broadcast(StatusUpdate(id, true))
      println("User: " + connections(id).name + "/" + id + " has connected")
      socialNetworkActor ! GetFollowers(id)
      socialNetworkActor ! GetFollowingForConnections(id, true)
      client ! Connected(id, name)
      for ((k, v) <- connections) {
        v.client ! StatusUpdate(id, name, true, false)
      }
    }
  }

  def updateUser(user: User) = {
    println("Updating user: " + user.name)
    if (connections contains user.uuid) {
      val followers = for ((id, name) <- user.following if connections.contains(id)) yield (id, name)
      connections(user.uuid).client ! ConnectedFollowers(followers)
    }
  }

  def disconnectUser(id: String) = {
    println("User: " + connections(id).name + "/" + id + " has disconnected")
    val connection = connections(id)
    connections -= id
//    context.parent ! Broadcast(StatusUpdate(id, connection.name, false))
    socialNetworkActor ! GetFollowingForConnections(id, false)
  }

  def directSendTweet(user: User, tweet: Tweet) = {
    println("User: " + user + " direct send")
    for ((id,connection) <- user.following if connections.contains(id)) {
      connections(id).client ! tweet
    }
  }

//  def getConnectedFollowers(user: User) = {
//    for ((name,connection) <- connections if connection.follows.contains(id)) {
//      connection.socket.writeText(Json.toJson(StatusUpdate(id, connected)).toString())
//    }
//  }
}
