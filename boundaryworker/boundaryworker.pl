%====================================================================================
% boundaryworker description   
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
 qactor( boundaryworker, ctxboundary, "it.unibo.boundaryworker.Boundaryworker").
 static(boundaryworker).
