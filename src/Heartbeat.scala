
import akka.actor._
import akka.actor.Actor
import akka.actor.Props
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class Gps extends Actor
{
  var ticker : Cancellable = _

  override def preStart()
  {
    println ("Gps prestart")
    // val child = context.actorOf(Props(new Cadencer(500)), name = "cadencer")
    ticker = context.system.scheduler.schedule (
      500 milliseconds,
      500 milliseconds,
      context.self,
      "beat")
  }

  def receive: PartialFunction[Any, Unit] =
  {
    case "beat" =>
      println ("got a beat")

    case "stop" =>
      ticker.cancel()

    case _ =>
      println("gps: wut?")
  }
}

object main extends App
{
  val system = akka.actor.ActorSystem("mySystem")
  val gps = system.actorOf(Props[Gps], name = "gps")

  Thread.sleep (5000)
  gps ! "stop"
  println ("stop")
}