package com.lpfn

import com.lpfn.searcher.SupervisorWorkerProtocol.WorkerResult

package object searcher {

  object WorkSupervisorProtocol {
    final case class SpawnWorkers(no: Int)
    final case class Report(report: Map[String, WorkerResult])
  }

  object SupervisorWorkerProtocol {
    final case object Run

    sealed trait WorkerResult extends Product with Serializable
    final case object Timeout extends WorkerResult
    final case class Success(elapsed: Long, byte_cnt: Long) extends WorkerResult
    final case class Failure(details: String) extends WorkerResult
  }

}
