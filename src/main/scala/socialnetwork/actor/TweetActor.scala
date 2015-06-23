package socialnetwork.actor

import akka.actor.Actor
import socialnetwork.common._
import java.util.Date

class TweetActor extends Actor {

  var tweets = List[Tweet]()

  def receive = {
    case NewTweet(id, name, hash, msg, timestamp) =>
      createTweet(id, name, hash, msg, timestamp)
    case GetTweets(ids) =>
      sender ! Tweets(ids, getTweets(ids))
    case GetTweetsByHash(hash) =>
      // TODO send hash tweets
      sender ! TweetsByHash(hash, getTweetsByHash(hash))
  }

  def createTweet(id: String, name: String, hash: String, msg: String, timestamp: Date) = {
    tweets = Tweet(id, name, hash, msg, timestamp) :: tweets
    println(tweets)
  }

  def getTweets(ids: List[String]): Seq[Tweet] = {
    println("get tweets: " + ids)
    for (t <- tweets if ids.indexOf(t.user) >= 0) yield t
//    tweets.filter(tweet => ids contains tweet.user).sortBy(tweet => tweet.timestamp).toSeq
  }

  def getTweetsByHash(hash: String):Seq[Tweet] = tweets.filter(tweet => tweet.hash.equals(hash)).sortBy(tweet => tweet.timestamp).toSeq
}
