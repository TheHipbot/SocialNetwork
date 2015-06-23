package socialnetwork.common

import java.security.Timestamp

import akka.actor.ActorRef
import org.mashupbots.socko.events.WebSocketFrameEvent
import java.util.Date

case class GetTweets(id: List[String])
case class GetTweetsByHash(hash: String)
case class Tweets(ids: List[String], tweets: Seq[Tweet])
case class TweetsByHash(hash: String, tweets: Seq[Tweet])
case class NewTweet(id: String, name: String, hash: String, msg: String, timestamp: Date)

// Create users
//case class Register(name: String, socket: WebSocketFrameEvent)
case class CreateUser(name: String)
case class CreatedUser(user: User)
case class IsValidUser(connection: UserConnection)
case class ValidUser(connection: UserConnection)
case class InvalidUser(connection: UserConnection)

// Connection messages
case class GetConnectedUsers(id: String)
case class ConnectUser(id: String, name: String)
case class Connected(id: String, name: String)
case object DisconnectUser
case class GetStatus(id: String, socket: WebSocketFrameEvent)
case class ConnectedFollowers(followers: Map[String, String])

case class Follow(user: String, toFollow: String)
case class Followers(user: User)
case class Following(user: User)
case class GetFollowers(id: String)
case class GetFollowing(id: String)
case class GetFollowingForConnections(id: String, connected: Boolean)
case class FollowingWithConnection(user: User, connected: Boolean)
case class GetFollowingForTweet(id: String, tweet: Tweet)
case class FollowingWithTweet(user: User, tweet: Tweet)
case class UpdateUser(id: String)

// Json Messages
case class ConnectedUsers(users: Map[String, String])
case class User(uuid: String, var name: String, var following: Map[String, String])

//TODO better name for following
case class UserConnection(id: String, name: String, client: ActorRef, var following: Map[String, String])
case class UserList(updateType: String, userType: String, following: Map[String, String])
case class Tweet(user: String, name: String, hash: String, msg: String, timestamp: Date)
case class StatusUpdate(id: String, name: String, connected: Boolean, follower: Boolean)