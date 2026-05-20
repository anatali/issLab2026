%====================================================================================
% robotservice26 description   
%====================================================================================
request( cmd, cmd(MOVE,T) ). %MOVE = w | s  mosse aril asynch
reply( cmddone, cmddone(R) ).  %%for cmd
reply( cmdfailed, cmdfailed(T,CAUSE) ).  %%for cmd
dispatch( move, move(M) ). %MOVE = l|r|a|d|h   mosse aril sincrone ok
event( vrinfo, vrinfo(SOURCE,INFO) ). %streamed
event( sonaralarm, distance(DISTANCE) ). %emesso per i client di sistema
event( sonardata, sonar(DISTANCE) ). %emesso dal SONAR
request( step, step(TIME) ).
reply( stepdone, stepdone(V) ).  %%for step
reply( stepfailed, stepfailed(DURATION,CAUSE) ).  %%for step
%====================================================================================
context(ctxrobotservice26, "localhost",  "TCP", "8125").
 qactor( robotactor, ctxrobotservice26, "it.unibo.robotactor.Robotactor").
 static(robotactor).
