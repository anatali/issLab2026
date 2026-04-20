package qakdemo26
import it.unibo.kactor.*
import unibo.basicomm23.*
import unibo.basicomm23.utils.*
import kotlinx.coroutines.delay
import unibo.basicomm23.interfaces.*

class workactor(name:String):
/*1*/      ActorBasic(name,
/*2*/                 confined=true){
var i = 0
  override suspend fun actorBody(msg:IApplMessage){
	
/*3*/if( msg.msgId() == "start"){
	  logger.info(  "workactor($name | starts as ActorBasic"  )
      workStep(   )
    }else {
    	CommUtils.outgreen("$name|received  $msg ")
    }
  }

  suspend fun workStep(    ){
	CommUtils.outgreen("$name|workStep  $i ")
    i++
    val alarm=CommUtils.buildEvent( name,"alarm","alarm$name-$i")
    CommUtils.outgreen("$name|  emit $alarm at step $i")
    logger.info(  "$name|  emit $alarm at step $i" )
  /*4*/emit( alarm )
    if( i == 3 ) terminate()
    else {
      delay( 2000L   )
      CommUtils.outgreen("$name| forward start to  $name ")
      logger.info("$name| forward tart to  $name ")
      forward("start", "start(do)" , name )
    }
  }
}

/*
1 - La classe workactor estende ActorBasic e ridefinisce il metodo actorBody.

2- Il valore confined=true trasferito al costruttore implica che le istanza di questa classe sono 
attivate usando il dispatcher newSingleThreadContext -> che utilizza solo 1 Thread. 
(si veda Coroutine context and dispatchers ->).

Ponendo :confined=false (valore di default), viene usato il dispatcher newFixedThreadPoolContext ->, 
che gestisce gli attori attivando tanti thread quante le CPU disponibili.

3 - Attivazione del metodo workStep in risposta a un messaggio start.

4 - Il metodo workStep emette un evento alarm e, dopo un certo numero di iterazioni, termina.
*/