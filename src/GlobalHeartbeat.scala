import akka.actor._
import akka.actor.Actor
import akka.actor.Props
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case object BeatMessage
case object GetTicks
case object ClearTicks

class BeatTicker (interval : Int) extends Actor
{
  private object PrivateBeat

  val ticker: Cancellable = context.system.scheduler.schedule(
    interval milliseconds,
    interval milliseconds,
    context.self,
    PrivateBeat)

  val alltasks: ActorSelection = context.system.actorSelection("/user/*")

  def receive: PartialFunction[Any, Unit] =
  {
    case PrivateBeat => alltasks ! BeatMessage
    case "stop" => ticker.cancel()
  }
}

class TestActor extends Actor
{
  var ticks : Long = 0

  override def receive: PartialFunction[Any, Unit] =
  {
    case BeatMessage =>
      ticks = ticks + 1

    // Send long value
    case GetTicks =>
      sender ! ticks

    case ClearTicks =>
      ticks = 0
  }
}

class QueryActor (act : ActorRef) extends Actor
{
  override def receive: PartialFunction[Any, Unit] =
  {
    case "query" =>
      act ! GetTicks

    // Receive long value
    case x: Long =>
      println("Received: " + x)
  }
}


object starter extends App
{
  val system = akka.actor.ActorSystem("mySystem")
  val test1 = system.actorOf(Props(new TestActor()), name = "test1")
  val test2 = system.actorOf(Props(new TestActor()), name = "test2")
  val ticker = system.actorOf(Props(new BeatTicker(1)), name = "btt")
  val test3 = system.actorOf(Props(new TestActor()), name = "test3")
  val query = system.actorOf(Props(new QueryActor(test3)), name = "query0")

  Thread.sleep(5000)
  test1 ! ClearTicks
  Thread.sleep (500)
  query ! "query"
}

