package upmc.akka.leader

import akka.actor._
import math._

import javax.sound.midi._
import javax.sound.midi.ShortMessage._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import upmc.akka.leader.DataBaseActor._
import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import upmc.akka.leader
import scala.util.Random
import upmc.akka.leader.Provider
case class  Start()

object Messages {
  case class Heartbeat(t:Terminal)
  case object CheckAvaiableMusicians
  case class CheckAvaiableMusiciansResponse(m:Map[Terminal,Boolean])

}
object Oreille {
	case object CheckTime
}
class Oreille(musiciens: List[Terminal], id: Int) extends Actor {
	import Messages._
	import Oreille._
	var activeMusiciens = musiciens.map(m => m -> false).toMap
	val TIME_TO_ASSUME_DEAD = 3000

	// save all the timelaps of the last heartbeat for each musician
	var musiciansHeartbeats = musiciens.map(m => m -> System.currentTimeMillis()).toMap
	context.system.scheduler.scheduleOnce(TIME_TO_ASSUME_DEAD.milliseconds)(self ! CheckTime)

	var lastConductorHeartbeat = System.currentTimeMillis()

	def receive: Receive = {
		case Heartbeat(t) => 
			println(s"Received Heartbeat from ${t.id}")
			musiciansHeartbeats = musiciansHeartbeats.updated(t, System.currentTimeMillis())
			activeMusiciens = activeMusiciens.updated(t, true)
		case CheckAvaiableMusicians => sender ! CheckAvaiableMusiciansResponse(activeMusiciens)
		case CheckTime => {
			val currentTime = System.currentTimeMillis()
			val deadMusicians = musiciansHeartbeats.filter { case (m, t) => currentTime - t > TIME_TO_ASSUME_DEAD }
			deadMusicians.foreach { case (m, t) => 
				println(s"Assuming ${m.id} is dead")
				activeMusiciens = activeMusiciens.updated(m, false)
			}
			context.system.scheduler.scheduleOnce(TIME_TO_ASSUME_DEAD.milliseconds)(self ! CheckTime)
		}
	}
}
object Coeur {
	case object StartBeat
}
class Coeur(val musician:Terminal,musiciens: List[Terminal]) extends Actor {
  	import Messages._
  	import Coeur._
	// select the musician 
	
	val oreille = context.actorSelection("../Oreille")
  	context.system.scheduler.scheduleOnce(1.seconds) (self ! StartBeat)
  	def receive: Receive = {
		
		case StartBeat => {
			oreille ! CheckAvaiableMusicians
			context.system.scheduler.scheduleOnce(1.seconds) (self ! StartBeat)
		}
		case CheckAvaiableMusiciansResponse(m)=>{
			println("Received the response from the musicians")
			m.foreach(println)
			// send for each 
			m.foreach((v) => v match {
				case (t, _) => {
					if(t.id != musician.id){
						val ip = t.ip.replace("\"", "")
						val actorPath = s"akka.tcp://MozartSystem${t.id}@${ip}:${t.port}/user/Musicien${t.id}/Oreille"
						val actorToSend = context.actorSelection(actorPath)
						println("Sending Heartbeat to " + actorPath)
						actorToSend ! Heartbeat(musician)
					}
				}
			})
		}
	}
}
object Conductor {
  case class ConductorUpdateStatus()
}
class Conductor(musiciens: List[Terminal]) extends Actor {
  import Messages._
  import Conductor._
  var statuses: Map[Int, Int] = musiciens.map(m => m.id -> 1).toMap
  def receive: Receive = {
    case Heartbeat(t) => statuses = statuses.updated(t.id, 1)
  }
}
object Musicien{
	case class StartGame()
	case class PlayMeasure(chords:List[Chord]) 

}
class Musicien (val id:Int, val musiciens:List[Terminal]) extends Actor {
	import Provider._
	import Player._
	import Messages._
	import Musicien._
	import Conductor._
	val currentMusicien = musiciens.filter(m => m.id == id).head

	var measures = List[Measure]()
	var current = 0
	var dbActor = context.actorOf(Props[upmc.akka.leader.DataBaseActor],"dbActor")
	var player = context.actorOf(Props[upmc.akka.leader.Player], "player")
	var provider = context.actorOf(Props[upmc.akka.leader.Provider], "provider")
	val oreille = context.actorOf(Props(new Oreille(musiciens, id)), s"Oreille")
	val coeur = context.actorOf(Props(new Coeur(currentMusicien,musiciens)), s"Coeur")
	val conductor=context.actorOf(Props(new Conductor(musiciens)), s"Conductor")

	var scheduler = context.system.scheduler
	var index = 0 
	// Les differents acteurs du systeme
	val displayActor = context.actorOf(Props[DisplayActor], name = "displayActor")

	def receive = {

		// Initialisation
		case Start => {
			displayActor ! Message ("Musicien " + this.id + " is created")
			self ! StartGame
			conductor ! ConductorUpdateStatus
		}
		case StartGame => {
			var result = Random.nextInt(11)+2
			provider ! FindMeasure(index,result) 
			index=index+1
			if(index==15){
			index=0
			}
		
		}
		case PlayMeasure(chords:List[Chord]) => {
			println("Received Measure, and will be playing it ")
			player ! Measure(chords)
		}
		case Measure(chords:List[Chord]) => {
			val availableMusicians = musiciens
			// val availableMusicians = musiciens.filter(m => oreille.activeMusiciens.getOrElse(m.id, false))
			// val availableMusicians = musiciens.filter(m => m.id != id) // Exclure soi-même
			if (availableMusicians.nonEmpty) {
				val chosenMusicien = availableMusicians(Random.nextInt(availableMusicians.length))
				val musicien = self
				println(s"Chosen Musicien: ${chosenMusicien.id} ${chosenMusicien.ip}:${chosenMusicien.port}")
				val actorPath = s"akka.tcp://MozartSystem${chosenMusicien.id}@${chosenMusicien.ip}:${chosenMusicien.port}/user/Musicien${chosenMusicien.id}"
				musicien ! PlayMeasure(chords)
				println(s"SENT THE CHORDS TO Musicien${chosenMusicien.id} AND SCHEDULING FOR ANOTHER ONE")
				context.system.scheduler.scheduleOnce(1800.milliseconds, self, StartGame)
			} else {
				println("Aucun musicien disponible pour jouer la mesure")
				// todo : attendre 30secondes
			}
		}

	}
}
