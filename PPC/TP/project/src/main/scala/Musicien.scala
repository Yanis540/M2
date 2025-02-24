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

case class Start ()
object Musicien{
  case class StartGame()
}

class Musicien (val id:Int, val terminaux:List[Terminal]) extends Actor {
     import Provider._
     import Player._
     import Musicien._
     var measures = List[Measure]()
     var current = 0
     var dbActor = context.actorOf(Props[upmc.akka.leader.DataBaseActor],"dbActor")
     var player = context.actorOf(Props[upmc.akka.leader.Player], "player")
     var provider = context.actorOf(Props[upmc.akka.leader.Provider], "provider")
     var scheduler = context.system.scheduler
     var index = 0 
     // Les differents acteurs du systeme
     val displayActor = context.actorOf(Props[DisplayActor], name = "displayActor")

     def receive = {

          // Initialisation
          case Start => {
               displayActor ! Message ("Musicien " + this.id + " is created")
               self ! StartGame
          }
          case StartGame => {
               var result = Random.nextInt(11)+2
               println("SENDING THE RESULT " +result)  
               provider ! FindMeasure(index,result) 
               index=index+1
               if(index==15){
               index=0
               }
          
          }
          case Measure(chords:List[Chord]) => {
               println("RECEIVING THE CHORDS")
               player ! Measure(chords)
               println("SENT THE CHORDS AND SCHEDULING FOR ANOTHER ONE")
               scheduler.scheduleOnce (1800 milliseconds) (self ! StartGame)
          }

     }
}
