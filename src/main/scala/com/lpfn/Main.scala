package com.lpfn

import com.lpfn.searcher.WorkToDo
import com.lpfn.utils.{ConsoleParser, ReportWriter}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

object Main extends App {

  ConsoleParser.parseArguments(args.toList) match {
    case Right(timeout) =>
      System.out.println(s"App started with timeout = $timeout")

      val stringGen: () => Stream[Char] = () => Random.alphanumeric

      val tasks = (1 to 5).map(_ => WorkToDo.doTheJob(timeout seconds, stringGen()))
      val value = Task.gatherUnordered(tasks).map(_.toList).runSyncUnsafe(timeout + 5 seconds)

      ReportWriter.writeReport(value)

    case Left(details) => System.out.println(details)
  }
}
