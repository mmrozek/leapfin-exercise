package com.lpfn.searcher

import com.lpfn._
import monix.eval.Task
import scala.concurrent.duration.FiniteDuration

object WorkToDo {

  def doTheJob(timeout: FiniteDuration, streamGen: => Stream[Char]) =
    Task {
      val start = System.currentTimeMillis()
      streamGen.zipWithIndex.sliding(4).find(s => s.map(_._1).mkString == "lpfn").map { x =>
        val time = System.currentTimeMillis() - start
        Success(time, x.last._2 + 1)
      }.getOrElse(Failure("End of the stream"))
    }.timeoutTo(timeout, Task(Timeout))
}
