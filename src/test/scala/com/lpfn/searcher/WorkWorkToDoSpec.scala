package com.lpfn.searcher

import com.lpfn._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.duration._
import monix.execution.Scheduler.Implicits.global

class WorkWorkToDoSpec extends WordSpecLike with Matchers
  with BeforeAndAfterAll with ScalaFutures {

  val timeout = 5 seconds
  val monixTimeout = 7 seconds

  "Worker" must {
    "Return a success if string `lpfn` occurs" in {
      val input = () => "abcdlpfnccc".toStream

      WorkToDo.doTheJob(timeout, input()).runSyncUnsafe(monixTimeout) should matchPattern {
        case Success(_, 8) =>
      }
    }

    "Return a failure if string `lpfn` does not occur" in {
      val input = () => "abcdlpdfdnccc".toStream

      WorkToDo.doTheJob(timeout, input()).runSyncUnsafe(monixTimeout) should be (Failure("End of the stream"))
    }

    "Return a timeout if string `lpfn` couldn't be found on time" in {
      val input = () => Stream.from(0).flatMap(_.toString)

      WorkToDo.doTheJob(3 millis, input()).runSyncUnsafe(monixTimeout) should be (Timeout)
    }
  }

}
