
package upmc.akka.ppc.player

import math._

import javax.sound.midi._
import javax.sound.midi.ShortMessage._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import upmc.akka.ppc.DataBaseActor._

import akka.actor.{Props, Actor, ActorRef, ActorSystem}


object PlayerActor {
  case class MidiNote (pitch:Int, vel:Int, dur:Int, at:Int) 
  val info = MidiSystem.getMidiDeviceInfo().filter(_.getName == "Gervill").headOption
  // or "SimpleSynth virtual input" or "Gervill"
  val device = info.map(MidiSystem.getMidiDevice).getOrElse {
    println("[ERROR] Could not find Gervill synthesizer.")
    sys.exit(1)
}

val rcvr = device.getReceiver()

/////////////////////////////////////////////////
def note_on (pitch:Int, vel:Int, chan:Int): Unit = {
    val msg = new ShortMessage
    msg.setMessage(NOTE_ON, chan, pitch, vel)
    rcvr.send(msg, -1)
}

def note_off (pitch:Int, chan:Int): Unit = {
    val msg = new ShortMessage
    msg.setMessage(NOTE_ON, chan, pitch, 0)
    rcvr.send(msg, -1)
}

}

//////////////////////////////////////////////////



object Provider {
  case class FindMeasure(index:Int,num:Int)
}
class Provider () extends Actor {
  import Provider._
  var requestor=sender;
  var partie1 = Array.ofDim[Int](11,8)
  partie1 = Array(
    Array(96,22,141,41,105,122,11,30),
    Array(32,6,128,63,146,46,134,81),
    Array(69,95,158,13,153,55,110,24),
    Array(40,17,113,85,161,2,159,100),
    Array(148,74,163,45,80,97,36,107),
    Array(104,157,27,167,154,68,118,91),
    Array(152,60,171,53,99,133,21,127),
    Array(119,84,114,50,140,86,169,94),
    Array(98,142,42,156,75,129,62,123),
    Array(3,87,165,61,135,47,147,33),
    Array(54,130,10,103,28,37,106,5)
  )


  var partie2 = Array.ofDim[Int](11,8)
  partie2 = Array(
    Array(70,121,26,9,112,49,109,14),
    Array(117,39,126,56,174,18,116,83),
    Array(66,139,15,132,73,58,145,79),
    Array(90,176,7,34,67,160,52,170),
    Array(25,143,64,125,76,136,1,93),
    Array(138,71,150,29,101,162,23,151),
    Array(16,155,57,175,43,168,89,172),
    Array(120,88,48,166,51,115,72,111),
    Array(65,77,19,82,137,38,149,8),
    Array(102,4,31,164,144,59,173,78),
    Array(35,20,108,92,12,124,44,131)
  )
  
  def receive = {
    case FindMeasure (index:Int,num:Int) => {
      requestor= sender
      var value = 0
      if(index <8){
        value = ((partie1))(num - 2)(index)
      }else {
        value = ((partie2))(num -2 )(index-8)
      }
      context.actorSelection("../dbActor")  ! GetMeasure(value-1)
    }
    case Measure(chords:List[Chord])=>{
      println("SENDING THE REQUEST TO THE SENDER")
      requestor ! Measure(chords) 
    }
  }
}

class PlayerActor () extends Actor{
  import PlayerActor._
  import Conductor._
  PlayerActor.device.open()

  def receive = {
    case Measure(chords:List[Chord]) => {
      println("RECEIVING THE CHORDS ON PLAYER : ")
      println(chords.length)
      chords.foreach (c => c.notes.foreach (note => 
        self ! MidiNote(note.pitch, note.vol, note.dur, c.date)
      ))
    }
    case MidiNote(p,v, d, at) => {
      println("PLAYING THE CHORD")
      context.system.scheduler.scheduleOnce ((at) milliseconds) (PlayerActor.note_on (p,v,10))
      context.system.scheduler.scheduleOnce ((at+d) milliseconds) (PlayerActor.note_off (p,10))
    }
  }
}

object Conductor{
  case class StartGame()
}

class Conductor ()extends Actor{
  import Conductor._
  import Provider._
  import PlayerActor._

  
  

  var measures = List[Measure]()
  var current = 0
  var dbActor = context.actorOf(Props[upmc.akka.ppc.DataBaseActor],"dbActor")
  var player = context.actorOf(Props[PlayerActor], "player")
  var provider = context.actorOf(Props[Provider], "provider")
  var scheduler = context.system.scheduler
  var index = 0 
  def receive = {
    case StartGame => {
      // choose a random number from 1 to 6
      var de1 = (random * 6).toInt + 1
      var de2 = (random * 6).toInt + 1
      var result = de1 + de2
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

