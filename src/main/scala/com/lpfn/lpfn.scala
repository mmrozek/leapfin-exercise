package com

package object lpfn {

  sealed trait WorkerResult extends Product with Serializable
  final case object Timeout extends WorkerResult
  final case class Success(elapsed: Long, byte_cnt: Long) extends WorkerResult
  final case class Failure(details: String) extends WorkerResult

}
