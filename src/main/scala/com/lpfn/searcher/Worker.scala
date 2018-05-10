package com.lpfn.searcher

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import akka.pattern.{after, pipe}
import com.lpfn.searcher.SupervisorWorkerProtocol.{Failure, Run, Success, Timeout}
import java.util.NoSuchElementException
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global

class Worker(timeout: FiniteDuration, streamGen: () => Stream[Char]) extends Actor with ActorLogging {
  val stream = streamGen()

  override def receive: Receive = {
    case Run =>
      log.debug(s"$self started")
      val origin = sender()
      val timeoutF = after(timeout, context.system.scheduler){
        Future.successful(Timeout)
      }
      val resultF = Future {
        val start = System.currentTimeMillis()
        stream.zipWithIndex.sliding(4).find(s => s.map(_._1).mkString == "lpfn").map { x =>
          val time = System.currentTimeMillis() - start
          Success(time, x.last._2+1)
        }.get
      }
      val result = Future.firstCompletedOf(Seq(timeoutF, resultF)).recover {
        case _: NoSuchElementException => Failure("End of the stream")
        case msg => Failure(msg.getMessage)
      }

      pipe(result).to(origin, self)

      result.onComplete(_ => self ! PoisonPill)
  }

}

object Worker {

  def props(timeout: FiniteDuration, streamGen: () => Stream[Char]): Props = Props(new Worker(timeout, streamGen))

}
