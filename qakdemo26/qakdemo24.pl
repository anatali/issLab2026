%====================================================================================
% qakdemo24 description   
%====================================================================================
request( distance, distance(D) ).
reply( distanceack, ack(D) ).  %%for distance
request( r2, r2(X) ).
reply( rr2, rr2(X) ).  %%for r2
%====================================================================================
context(ctxcreate, "localhost",  "TCP", "8045").
 qactor( creator, ctxcreate, "it.unibo.creator.Creator").
 static(creator).
  qactor( producer, ctxcreate, "it.unibo.producer.Producer").
dynamic(producer). %%Oct2023 
  qactor( consumer, ctxcreate, "it.unibo.consumer.Consumer").
 static(consumer).
  qactor( consumerhelper, ctxcreate, "it.unibo.consumerhelper.Consumerhelper").
dynamic(consumerhelper). %%Oct2023 
