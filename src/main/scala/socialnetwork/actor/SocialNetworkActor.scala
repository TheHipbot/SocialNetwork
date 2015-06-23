package socialnetwork.actor

import akka.actor.{ActorRef, Actor}
import socialnetwork.common._

class SocialNetworkActor() extends Actor {

  var usersFollowing = Map[String, User]()
  var usersFollowers = Map[String, User]()

  def receive = {
    case CreateUser(name) =>
      val id = uuid
      usersFollowing += (id -> new User(id, name, Map()))
      usersFollowers += (id -> new User(id, name, Map()))
      sender ! CreatedUser(usersFollowing(id))

    case Follow(user, toFollow) =>
      println("User: " + user + " following " + toFollow)
      followUser(user, toFollow)

      // TODO update CC actor and client
      // ???? acknowledge
    case GetFollowers(id) =>
      sender ! Followers(getFollowers(id))
    case GetFollowing(id) =>
      sender ! Following(getFollowing(id))
    case GetFollowingForConnections(id, connected) =>
      sender ! FollowingWithConnection(getFollowing(id), connected)
    case GetFollowingForTweet(id, tweet) =>
      sender ! FollowingWithTweet(getFollowers(id), tweet)
    case IsValidUser(connection) =>
      if (usersFollowers contains connection.id) {
        sender ! ValidUser(UserConnection(connection.id, connection.name, connection.client, usersFollowers(connection.id).following))
      } else {
        sender ! InvalidUser(connection)
      }
  }

  def uuid = java.util.UUID.randomUUID.toString

  def getName(id: String): String = usersFollowing(id).name

  def getFollowers(id: String) = {
    if (usersFollowers contains id) {
      usersFollowers(id)
    } else {
      User("", "", Map())
    }
  }

  def getFollowing(id: String) = {
    if (usersFollowing contains id) {
      usersFollowing(id)
    } else {
      User("", "", Map())
    }
  }

  def followUser(user: String, toFollow: String) = {
    val userName = getName(user)
    val followName = getName(toFollow)
    println("User: " + userName + " is now following: " + followName)
    usersFollowing(user).following += (toFollow -> followName)
    usersFollowers(toFollow).following += (user -> userName)
  }
}

