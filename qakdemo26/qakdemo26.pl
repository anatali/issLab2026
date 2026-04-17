%====================================================================================
% qakdemo26 description   
%====================================================================================
dispatch( msg1, msg1(ARG) ). %da sender a receiver
dispatch( msg2, msg2(ARG) ). %da sender a receiver
event( alarm, alarm(KIND) ). %emesso da sender
%====================================================================================
context(ctxqakdemo26, "localhost",  "TCP", "8095").
 qactor( receiver, ctxqakdemo26, "it.unibo.receiver.Receiver").
 static(receiver).
  qactor( sender, ctxqakdemo26, "it.unibo.sender.Sender").
 static(sender).
  qactor( perceiver, ctxqakdemo26, "it.unibo.perceiver.Perceiver").
 static(perceiver).
msglogging.
