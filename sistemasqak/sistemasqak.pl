%====================================================================================
% sistemasqak description   
%====================================================================================
dispatch( eval, arg(V) ).
request( evalr, argr(V) ).
reply( evalreply, value(V) ).  %%for evalr
%====================================================================================
context(ctxsistemas, "localhost",  "TCP", "8010").
 qactor( sistemas, ctxsistemas, "it.unibo.sistemas.Sistemas").
 static(sistemas).
  qactor( callermock, ctxsistemas, "it.unibo.callermock.Callermock").
 static(callermock).
