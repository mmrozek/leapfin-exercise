package com.lpfn.utils

import com.lpfn.{Success, WorkerResult}


object ReportWriter {

  def writeReport(input: List[WorkerResult]) = {
    val (correct, failed) = input.partition {
      case Success(_, _) => true
      case _ => false
    }

    System.out.println(correct.sortBy(_.asInstanceOf[Success].elapsed).mkString("\n"))
    System.err.println(failed.mkString("\n"))
  }

}
