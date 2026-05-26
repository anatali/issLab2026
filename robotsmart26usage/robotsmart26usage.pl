%====================================================================================
% robotsmart26usage description   
%====================================================================================
mqttBroker("192.168.1.163", "1883", "robotsmartusage26in").
request( buildPlan, buildPlan(PX,PY,TX,TY) ). %create plan from (PX,PY) to (TX,TY)
reply( buildPlanDone, buildPlanDone(PLAN) ).  %%for buildPlan
request( moverobot, moverobot(TARGETX,TARGETY,STEPTIME) ). %move from current pos to (TARGETX,TARGETY)
reply( moverobotdone, moverobotok(ARG) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(PLANDONE,PLANTODO) ).  %%for moverobot
dispatch( noplan, noplan(X) ).
dispatch( setplanbuildelay, value(V) ). %parameter = V >= 0
request( doplan, doplan(PLAN,STEPTIME) ). %execute PLAN with STEPTIME
reply( doplandone, doplandone(ARG) ).  %%for doplan
reply( doplanfailed, doplanfailed(PLANTODO) ).  %%for doplan
request( step, step(TIME) ). %step command for TIME duration
reply( stepdone, stepdone(V) ).  %%for step
reply( stepfailed, stepfailed(DURATION,CAUSE) ).  %%for step
dispatch( move, move(M) ). %MOVE = l|r|a|d|h   mosse aril sincrone ok
dispatch( setrobotstate, setpos(X,Y,D) ). %set robot position to (X,Y) direction D=up|down|left|right
request( setdirection, dir(D) ). %set robot direction to D=up|down|left|right
reply( setdirectiondone, pos(PX,PY) ).  %%for setdirection
request( tuneAtHome, tuneAtHome(X) ). %reposition in home X don't care
reply( tuneDone, tuneDone(X) ).  %%for tuneAtHome
request( getrobotstate, getrobotstate(ARG) ). %request robot state ARG unused
reply( robotstate, robotstate(POS,DIR) ). %%for getrobotstate | POS->pos(X,Y) DIR->up|down|left|right
%====================================================================================
context(ctxrobotsmartusage, "localhost",  "TCP", "8120").
context(ctxrobotsmart, "127.0.0.1",  "TCP", "8020").
 qactor( robotsmart, ctxrobotsmart, "external").
  qactor( robotsnartusage, ctxrobotsmartusage, "it.unibo.robotsnartusage.Robotsnartusage").
 static(robotsnartusage).
