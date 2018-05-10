package com.lpfn.searcher

import akka.pattern.ask
import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import akka.util.Timeout
import com.lpfn.searcher.SupervisorWorkerProtocol._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.duration._

class WorkerSpec extends TestKit(ActorSystem("WorkerSpec")) with WordSpecLike with Matchers
  with BeforeAndAfterAll with ScalaFutures {

  implicit val timeout = Timeout(5 seconds)

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "Worker" must {
    "Return a success if string `lpfn` occurs" in {
      val input = () => "abcdlpfnccc".toStream
      val worker = TestActorRef(new Worker(5 seconds, input))

      val response = worker ? Run

      response.futureValue should matchPattern {
        case Success(_, 8) =>
      }
    }

    "Return a failure if string `lpfn` does not occur" in {
      val input = () => "abcdlpdfdnccc".toStream
      val worker = TestActorRef(new Worker(5 seconds, input))

      val response = worker ? Run

      response.futureValue should matchPattern {
        case Failure("End of the stream") =>
      }
    }

    "Return a timeout if string `lpfn` couldn't be found on time" in {
      val input = () => Stream.from(0).flatMap(_.toString)
      val worker = TestActorRef(new Worker(3 millis, input))

      val response = worker ? Run

      response.futureValue should matchPattern {
        case Timeout =>
      }
    }

  }

}
