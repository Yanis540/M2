package upmc.akka.ppc

import akka.actor.{Props,  Actor,  ActorRef,  ActorSystem}
import upmc.akka.ppc.player.Conductor
object Concert extends App {
  println("starting Mozart's game")
  val system = ActorSystem("Concert")
  val conductor = system.actorOf(Props[Conductor], "conductor")
  conductor ! Conductor.StartGame
}
