%====================================================================================
% actorgrid description   
%====================================================================================
event( clearCell, clearCell(X) ).
event( flashed, flashed(CELL) ).
event( start, start(RowsN,ColsN) ).
%====================================================================================
context(ctxgrid, "localhost",  "TCP", "8460").
 qactor( gamebuilder, ctxgrid, "it.unibo.gamebuilder.Gamebuilder").
 static(gamebuilder).
  qactor( cell, ctxgrid, "it.unibo.cell.Cell").
dynamic(cell). %%Oct2023 
