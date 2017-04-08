
/*
  Needed SDKS:
    java 1.8
    scala-sdk-2.12.1
    com.typesafe.akka:akka-actor_2.12:2.5-M2
 */

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

class HelloActor extends Actor
{
  def receive =
  {
    case "hello" => println("hello back at you")
    case _       => println("huh?")
  }
}

  object Test2 extends App
  {
    val system = ActorSystem("HelloSystem")
    // default Actor constructor
    val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")
    helloActor ! "hello"
    helloActor ! "buenos dias"
  }
