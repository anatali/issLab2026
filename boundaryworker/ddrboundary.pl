%====================================================================================
% ddrboundary description   
%====================================================================================
dispatch( move, move(M) ). %M = l|r|a|d|h mosse aril ok
request( cmd, cmd(MOVE,T) ). %MOVE = w|s mosse aril asynch
reply( cmddone, cmddone(R) ).  %%for cmd
reply( cmdfailed, cmdfailed(T,CAUSE) ).  %%for cmd
request( step, step(TIME) ).
reply( stepdone, stepdone(V) ).  %%for step
reply( stepfailed, stepfailed(DURATION,CAUSE) ).  %%for step
%====================================================================================
context(ctxboundary, "localhost",  "TCP", "8120").
context(ctxrobotservice26, "127.0.0.1",  "TCP", "8125").
 qactor( robotactor, ctxrobotservice26, "external").
  qactor( boundaryworker, ctxboundary, "it.unibo.boundaryworker.Boundaryworker").
 static(boundaryworker).
  qactor( radaractor, ctxboundary, "it.unibo.radaractor.Radaractor").
 static(radaractor).
