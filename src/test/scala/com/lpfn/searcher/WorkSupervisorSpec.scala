package com.lpfn.searcher

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import com.lpfn.searcher.WorkSupervisorProtocol._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.pattern.ask
import akka.util.Timeout
import com.lpfn.searcher.SupervisorWorkerProtocol._
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.duration._

class WorkSupervisorSpec extends TestKit(ActorSystem("WorkSupervisorSpec")) with WordSpecLike with Matchers
  with BeforeAndAfterAll with ScalaFutures {

  implicit val timeout = Timeout(5 seconds)

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  private class EchoActor(probe: ActorRef) extends Actor {
    override def receive: Receive = {
      case msg => probe ! msg
    }
  }

  "WorkSupervisor actor" must {

    "Send Run command to spawned workers" in {
      val probe = TestProbe("workers_probe")

      val supervisor = TestActorRef(new WorkSupervisor(Props(new EchoActor(probe.ref))))
      val workersCount = 2

      supervisor ! SpawnWorkers(workersCount)

      probe.expectMsgAllOf(Run, Run)
    }

    "Collect success responses and return report" in {
      val supervisor = TestActorRef(new WorkSupervisor(Props.empty))
      val workersCount = 1

      val report = supervisor ? SpawnWorkers(workersCount)

      val child = supervisor.children.head
      val response = Success(5, 3)

      supervisor.receive(response, child)
      report.futureValue should be (Report(Map(child.toString() -> response)))
    }

  }



}
