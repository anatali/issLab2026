%====================================================================================
% griddisplay description   
%====================================================================================
dispatch( cellstate, cellstate(X,Y,COLOR) ).
event( cellstate, cellstate(X,Y,COLOR) ).
%====================================================================================
context(ctxgrid, "localhost",  "TCP", "8050").
 qactor( griddisplay, ctxgrid, "it.unibo.griddisplay.Griddisplay").
 static(griddisplay).
