package com.lpfn

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.lpfn.searcher.SupervisorWorkerProtocol.Success
import com.lpfn.searcher.WorkSupervisorProtocol.{Report, SpawnWorkers}
import com.lpfn.searcher.{WorkSupervisor, Worker}
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

object Main extends App {

  val system: ActorSystem = ActorSystem("lpfn")


  val stringGen: () => Stream[Char] = () => Random.alphanumeric

  val workSupervisor = system.actorOf(WorkSupervisor.props(
    Worker.props(10 seconds, stringGen)), "supervisor"
  )

  implicit val askTimeout = Timeout(30 seconds)
  val result = workSupervisor ? SpawnWorkers(10)

  val report = result.map {
    case Report(r) =>
      r.partition {
        case (_, Success(_, _)) => true
        case _ => false
      }
  }

  report.onComplete { _ =>
    system.terminate()
    Await.result(system.whenTerminated, 30 seconds)
  }

  val (correct, failed) = Await.result(report, askTimeout.duration)

  System.out.println("REPORT:")
  System.out.println(correct.toList.sortBy(_._2.asInstanceOf[Success].elapsed).mkString("\n"))
  System.out.println(failed.toList.sortBy(_._1).mkString("\n"))

  scala.sys.addShutdownHook {
    system.terminate()
    Await.result(system.whenTerminated, 30 seconds)
  }
}
