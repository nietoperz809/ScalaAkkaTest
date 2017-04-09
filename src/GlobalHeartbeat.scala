import akka.actor._
import akka.actor.Actor
import akka.actor.Props
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case object BeatMessage
case object PrivateBeat

class BeatTicker extends Actor
{
  var ticker : Cancellable = context.system.scheduler.schedule (
    500 milliseconds,
    500 milliseconds,
    context.self,
    PrivateBeat)

  var alltasks: ActorSelection = context.system.actorSelection("/user/*")

  def receive: PartialFunction[Any, Unit] =
  {
    case PrivateBeat => alltasks ! BeatMessage
    case "stop" => ticker.cancel()
  }
}

class TestActor extends Actor
{
  override def receive: PartialFunction[Any, Unit] =
  {
    case BeatMessage => println ("got beat" + self)
  }
}

object starter extends App
{
  val system = akka.actor.ActorSystem("mySystem")
  val test1 = system.actorOf(Props(new TestActor()), name = "test1")
  val test2 = system.actorOf(Props(new TestActor()), name = "test2")
  val ticker = system.actorOf(Props(new BeatTicker()), name = "btt")
  val test3 = system.actorOf(Props(new TestActor()), name = "test3")
}

