package com.lpfn.searcher

import com.lpfn._
import monix.eval.Task
import scala.concurrent.duration.FiniteDuration

object WorkToDo {
  private def timeoutT(timeout: FiniteDuration) = Task(Timeout).delayExecution(timeout)
  private def resultT(streamGen: =>Stream[Char]) = Task {
    val start = System.currentTimeMillis()
    streamGen.zipWithIndex.sliding(4).find(s => s.map(_._1).mkString == "lpfn").map { x =>
      val time = System.currentTimeMillis() - start
      Success(time, x.last._2 + 1)
    }.getOrElse(Failure("End of the stream"))
  }

  def doTheJob(timeout: FiniteDuration, streamGen: =>Stream[Char]) =
    Task.racePair(timeoutT(timeout), resultT(streamGen)).flatMap {
      case Left((a, fiberB)) =>
        fiberB.cancel.map(_ => a)
      case Right((fiberA, b)) =>
        fiberA.cancel.map(_ => b)
    }
}
