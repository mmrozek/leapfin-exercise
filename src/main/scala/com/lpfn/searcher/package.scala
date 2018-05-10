package com.lpfn

import com.lpfn.searcher.SupervisorWorkerProtocol.WorkerResult

package object searcher {

  object WorkSupervisorProtocol {
    case class SpawnWorkers(no: Int)
    case object WorkersSpawned
    case class Report(report: Map[String, WorkerResult])
  }

  object SupervisorWorkerProtocol {
    case object Run

    sealed trait WorkerResult
    case object Timeout extends WorkerResult
    case class Success(elapsed: Long, byte_cnt: Long) extends WorkerResult
    case class Failure(details: String) extends WorkerResult
  }

}
