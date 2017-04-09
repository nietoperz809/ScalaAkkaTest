

import akka.actor._
case object PingMessage
case object PongMessage
case object xStartMessage
case object xStopMessage


class Ping(pong: ActorRef) extends Actor
{
  var count = 0
  def incrementAndPrint()
  {
    count += 1;
    println("ping")
  }

  def receive: PartialFunction[Any, Unit] =
  {
    case xStartMessage =>
      incrementAndPrint()
      pong ! PingMessage

    case PongMessage =>
      incrementAndPrint()
      if (count > 99)
      {
        sender ! xStopMessage
        println("ping stopped")
        context.stop(self)
      }
      else
      {
        sender ! PingMessage
        //Thread.sleep(1000)
      }

    case _ => println("Ping got something unexpected.")
  }
}

class Pong extends Actor
{
  def receive: PartialFunction[Any, Unit] =
  {
    case PingMessage =>
      println(" pong")
      sender ! PongMessage

    case xStopMessage =>
      println("pong stopped")
      context.stop(self)

    case _ => println("Pong got something unexpected.")
  }
}
object PingPongTest extends App
{
  val system = ActorSystem("PingPongSystem")
  val pong = system.actorOf(Props[Pong], name = "pong")
  val ping = system.actorOf(Props(new Ping(pong)), name = "ping")
  // start the action
  ping ! xStartMessage
  // commented-out so you can see all the output
  //system.shutdown
  
}
