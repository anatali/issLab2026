%====================================================================================
% qakdemo23 description   
%====================================================================================
dispatch( start, start(ARG) ).
event( alarm, alarm(DATA) ).
%====================================================================================
context(ctxdemocodedqactor, "localhost",  "TCP", "8065").
 qactor( w1, ctxdemocodedqactor, "codedActor.workactor").
 static(w1).
  qactor( w2, ctxdemocodedqactor, "codedActor.workactor").
 static(w2).
  qactor( w3, ctxdemocodedqactor, "codedActor.workactor").
 static(w3).
  qactor( datahandler, ctxdemocodedqactor, "it.unibo.datahandler.Datahandler").
 static(datahandler).
