package com.lpfn

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.lpfn.searcher.SupervisorWorkerProtocol.Success
import com.lpfn.searcher.WorkSupervisorProtocol.{Report, SpawnWorkers}
import com.lpfn.searcher.{WorkSupervisor, Worker}
import com.lpfn.utils.ConsoleParser
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

object Main extends App {

  ConsoleParser.parseArguments(args.toList) match {
    case Right(timeout) =>

      System.out.println(s"App started with timeout=$timeout")

      val system: ActorSystem = ActorSystem("lpfn")

      val stringGen: () => Stream[Char] = () => Random.alphanumeric

      val workSupervisor = system.actorOf(WorkSupervisor.props(
        Worker.props(timeout seconds, stringGen)), "supervisor"
      )

      implicit val askTimeout = Timeout(timeout+15 seconds)

      val result = workSupervisor ? SpawnWorkers(10)

      val report = result.map {
        case Report(r) =>
          r.partition {
            case (_, Success(_, _)) => true
            case _ => false
          }
      }

      val (correct, failed) = Await.result(report, askTimeout.duration)

      System.out.println("REPORT:")
      System.out.println(correct.toList.sortBy(_._2.asInstanceOf[Success].elapsed).mkString("\n"))
      System.err.println(failed.toList.sortBy(_._1).mkString("\n"))

      scala.sys.addShutdownHook {
        system.terminate()
        Await.result(system.whenTerminated, 30 seconds)
      }
    case Left(details) => System.out.println(details)
  }
}
