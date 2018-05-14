package com.lpfn

import com.lpfn.searcher.WorkToDo.doTheJob
import com.lpfn.utils.ConsoleParser
import com.lpfn.utils.ReportWriter.writeReport
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

object Main extends App {

  ConsoleParser.parseArguments(args.toList) match {
    case Right(timeout) =>
      System.out.println(s"App started with timeout = $timeout")

      val streamGen: () => Stream[Char] = () => Random.alphanumeric

      val tasks = (1 to 5).map(_ => doTheJob(timeout seconds, streamGen()))
      val value = Task.gatherUnordered(tasks).runSyncUnsafe(timeout + 5 seconds)

      writeReport(value)

    case Left(details) => System.out.println(details)
  }
}
