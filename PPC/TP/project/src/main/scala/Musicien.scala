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
  case object CheckConductors
  case class CheckConductorResponse(conductors:Map[Terminal,Boolean],activeMusiciens:Map[Terminal,Boolean])
  case object ConductorAlive

}
object Oreille {
	case object CheckTime
}
class Oreille(musiciens: List[Terminal], id: Int) extends Actor {
	import Messages._
	import Oreille._
	import Conductor._
	var activeMusiciens = musiciens.map(m => m -> false).toMap
	var musiciensAndConductor = musiciens.map(m => m -> false).toMap
	val TIME_TO_ASSUME_DEAD = 10000

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
		case ConductorSync(musicien,isConductor) => {
			println(s"Received ConductorSync from ${musicien.id} with value ${isConductor}")
			musiciensAndConductor = musiciensAndConductor.updated(musicien, isConductor)
		} 
		case CheckConductors => sender ! CheckConductorResponse(musiciensAndConductor,activeMusiciens)
		case CheckTime => {
			val currentTime = System.currentTimeMillis()
			val deadMusicians = musiciansHeartbeats.filter { case (m, t) => currentTime - t > TIME_TO_ASSUME_DEAD }
			deadMusicians.foreach { case (m, t) => 
				println(s"Assuming ${m.id} is dead")
				activeMusiciens = activeMusiciens.updated(m, false)
				// todo : if the conductor is dead, we need to elect a new one
				musiciensAndConductor = musiciensAndConductor.updated(m, false)
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
	
	val oreille = context.actorSelection("../oreille")
  	context.system.scheduler.scheduleOnce(1.seconds) (self ! StartBeat)
  	def receive: Receive = {
		
		case StartBeat => {
			oreille ! CheckAvaiableMusicians
			context.system.scheduler.scheduleOnce(1.seconds) (self ! StartBeat)
		}
		case CheckAvaiableMusiciansResponse(m)=>{
			// println("Received the response from the musicians")
			m.foreach(println)
			// send for each 
			m.foreach((v) => v match {
				case (t, isActive) => {
					val ip = t.ip.replace("\"", "")
					val actorPath = s"akka.tcp://MozartSystem${t.id}@${ip}:${t.port}/user/Musicien${t.id}/oreille"
					val actorToSend = context.actorSelection(actorPath)
					actorToSend ! Heartbeat(musician)
					// context.actorSelection(actorPath).resolveOne(1.second).onComplete {
					// 	case scala.util.Success(ref) => println(s"DEBUG: Found Oreille actor at ${actorPath}")
					// 	case scala.util.Failure(ex) => println(s"ERROR: Could not find Oreille actor at ${actorPath} - ${ex.getMessage}")
					// }
				}
			})
		}
	}
}
object Conductor {
  	case object ConductorStart
	case class ConductorSync(musicien:Terminal,isConductor:Boolean) 
}
class Conductor(currentMusicien:Terminal,musiciens: List[Terminal]) extends Actor {
	import Messages._
	import Conductor._
	import Musicien._
	val oreille = context.actorSelection("../oreille")
	val currentMusicienIp = currentMusicien.ip.replace("\"", "")
	val currentMusicienPath = s"akka.tcp://MozartSystem${currentMusicien.id}@${currentMusicienIp}:${currentMusicien.port}/user/Musicien${currentMusicien.id}"
	var currentMusicienActor = context.actorSelection(currentMusicienPath)
	var iamConductor: Boolean = false
	var isFetchingActiveMusician = false
	var currentSelectedMusicienToPlay: Option[Terminal] = None
	def receive: Receive = {
		case ConductorStart =>{
			iamConductor = true
			println(s"Conductor is alive Musicien ${currentMusicien.id}")
			currentSelectedMusicienToPlay match  {
				case Some(m) => println("Conductor is alive, and already has a musician to send the chords to")
				case None => {
					if(isFetchingActiveMusician != true){
						isFetchingActiveMusician = true
						println("No active musicians available to send the chords to, send back to the sender")
						oreille ! CheckAvaiableMusicians
					}
				}
			}
		}
	
		case ConductorSendSync => {
			println("Sending ConductorSync to all the musicians")
			for (m <- musiciens) {
				val ip = m.ip.replace("\"", "")
				val actorPath = s"akka.tcp://MozartSystem${m.id}@${ip}:${m.port}/user/Musicien${m.id}/oreille"
				val actorToSend = context.actorSelection(actorPath)
				println(s"Sending ConductorSync from Musicien ${currentMusicien.id} to Musicien ${m.id}")
				actorToSend ! ConductorSync(currentMusicien, iamConductor)
			}
			context.system.scheduler.scheduleOnce(1000 milliseconds) (oreille ! CheckConductors)
		}
		case CheckConductorResponse(conductors,musiciensWithStatus) => {
			println("Received the response from the conductors")
			val activeConductors = conductors.collect { case (m, true) => m }.toList
			System.out.println("Active conductors: " + activeConductors)
			val tehreIsNoConductor = activeConductors.isEmpty
			if (tehreIsNoConductor) {
				// chooesing the conductor 
				println("No active conductors available. Electing a new one.")
				var activeMusicians = musiciensWithStatus.collect { case (m, true) => m }.toList
				if (activeMusicians.nonEmpty) {
					val newConductor = activeMusicians.head
					val ip = newConductor.ip.replace("\"", "")
					val actorPath = s"akka.tcp://MozartSystem${newConductor.id}@${ip}:${newConductor.port}/user/Musicien${newConductor.id}/conductor"
					val actorToSend = context.actorSelection(actorPath)
					actorToSend ! ConductorStart
					println(s"New conductor elected: ${newConductor.id}")
				} else {
					println("No active musicians available to elect a new conductor.")
				}
				currentSelectedMusicienToPlay = None
			}
		}
		case CheckAvaiableMusiciansResponse(musicianStatus) => {
			println("Conductor : Received the response from the musicians :"+musicianStatus)
			println("I am the conductor :"+iamConductor)
			if(iamConductor){
				currentSelectedMusicienToPlay match {
					case Some(m) if musicianStatus.getOrElse(m, true) =>
					println(s"Current musician ${m.id} is still active, no change needed.")
					case _ =>
					// Find an active musician
					println("Finding an active musician to play the chords")
					val activeMusicians = musicianStatus.collect { case (m, true) => m }.toList.filter(m => m.id != currentMusicien.id)
					System.out.println("Active musicians: " + activeMusicians)
					if (activeMusicians.nonEmpty) {
						val newMusician = activeMusicians(Random.nextInt(activeMusicians.length))
						// après avoir choisi le musicien, on envoie au musicien courant (conduite) le message pour qu'il envoie les accords
						currentSelectedMusicienToPlay = Some(newMusician)
						println(s"New musician selected: ${newMusician.id}, now starting the game on the conductor side")
						currentMusicienActor ! StartGame
						context.actorSelection(currentMusicienPath).resolveOne(1.second).onComplete {
							case scala.util.Success(ref) => println(s"DEBUG: Found Musicien actor at ")
							case scala.util.Failure(ex) => println(s"ERROR: Could not find Musicien actor at  - ${ex.getMessage}")
						}
						
					} else {
						println("No active musicians available.")
						currentSelectedMusicienToPlay = None
						oreille ! CheckAvaiableMusicians
					}
				}
			}
		}
		case Measure(chords:List[Chord]) => {
			println("The conductor received the chords, and will be sending them to the player")
			currentSelectedMusicienToPlay match {
				case Some(m) => {
					val ip = m.ip.replace("\"", "")
					val actorPath = s"akka.tcp://MozartSystem${m.id}@${ip}:${m.port}/user/Musicien${m.id}"
					val actorToSend = context.actorSelection(actorPath)
					actorToSend ! PlayMeasure(chords)
					println(s"Sent the chords to Musicien${m.id}")
				}
				case None => {
					println("No active musicians available to send the chords to, send back to the sender")
					sender ! PlayMeasure(chords)
				}
			}
		}
	}
}
object Musicien{
	case object StartGame
	case class PlayMeasure(chords:List[Chord]) 
	case object ConductorSendSync

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
	val oreille = context.actorOf(Props(new Oreille(musiciens, id)), s"oreille")
	println(s"DEBUG: Oreille actor created for musician $id at path ${oreille.path}")

	val coeur = context.actorOf(Props(new Coeur(currentMusicien,musiciens)), s"coeur")
	val conductor=context.actorOf(Props(new Conductor(currentMusicien,musiciens)), s"conductor")

	var scheduler = context.system.scheduler
	var index = 0 
	// Les differents acteurs du systeme
	val displayActor = context.actorOf(Props[DisplayActor], name = "displayActor")

	def receive = {

		// Initialisation
		case Start => {
			displayActor ! Message ("Musicien " + this.id + " is created")
			conductor ! ConductorSendSync
		}
		case CheckAvaiableMusiciansResponse(musicianStatus) => {
			println(s"Musicien $id received the response from Oreille")
			val activeMusicians = musicianStatus.collect { case (m, true) => m }.toList
			System.out.println("Active musicians: " + activeMusicians)
			// Vérifier si un Conductor existe déjà
			
		}
		case StartGame => {
			println("Received StartGame, and will be starting the game")
			var result = Random.nextInt(11)+2
			provider ! FindMeasure(index,result) 
			index=index+1
			if(index==15){
			index=0
			}
			scheduler.scheduleOnce(1800 milliseconds) (self ! StartGame)
		
		}
		case PlayMeasure(chords:List[Chord]) => {
			println("Received Measure, and will be playing it ")
			player ! Measure(chords)
		}
		case Measure(chords:List[Chord]) => {
			conductor ! Measure(chords)
		}

	}
}
