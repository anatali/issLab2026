%====================================================================================
% firefly description   
%====================================================================================
mqttBroker("192.168.1.132", "1883", "firefly").
dispatch( cellstate, cellstate(X,Y,COLOR) ). %commute cell state
event( cellstate, cellstate(X,Y,COLOR) ). %commute cell state
%====================================================================================
context(ctxfirefly, "localhost",  "TCP", "8040").
 qactor( firefly1, ctxfirefly, "it.unibo.firefly1.Firefly1").
 static(firefly1).
