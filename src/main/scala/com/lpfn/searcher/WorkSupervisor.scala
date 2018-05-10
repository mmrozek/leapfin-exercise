package com.lpfn.searcher

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.lpfn.searcher.SupervisorWorkerProtocol._
import com.lpfn.searcher.WorkSupervisorProtocol._
import scala.collection.mutable

class WorkSupervisor(worker: Props) extends Actor with ActorLogging {

  override def receive: Receive = idle

  val report: mutable.Map[String, WorkerResult] = mutable.Map.empty

  def working(workers: Int, caller: ActorRef): Receive = {
    case msg: WorkerResult =>
      log.debug(s"$msg from ${sender()} arrived")
      report += (sender().toString() -> msg)
      if(report.size == workers)
        caller ! Report(report.toMap)
        self ! PoisonPill
  }

  val idle: Receive = {
    case SpawnWorkers(n) =>
      log.debug(s"Supervisor will spawn $n workers")
      val workers = for {
        no <- 1 to n
      } yield context.actorOf(worker, s"worker_$no")
      context.become(working(n, sender()))
      workers.foreach(_ ! Run)
  }
}

object WorkSupervisor {

  def props(worker: Props) = Props(new WorkSupervisor(worker))

}