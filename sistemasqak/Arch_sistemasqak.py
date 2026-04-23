### conda install diagrams
from diagrams import Cluster, Diagram, Edge
from diagrams.custom import Custom
import os
os.environ['PATH'] += os.pathsep + 'C:/Program Files/Graphviz/bin/'

graphattr = {     #https://www.graphviz.org/doc/info/attrs.html
    'fontsize': '22',
}

nodeattr = {   
    'fontsize': '22',
    'bgcolor': 'lightyellow'
}

eventedgeattr = {
    'color': 'red',
    'style': 'dotted'
}
evattr = {
    'color': 'darkgreen',
    'style': 'dotted'
}
with Diagram('sistemasqakArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxsistemas', graph_attr=nodeattr):
          sistemas=Custom('sistemas','./qakicons/symActorWithobjSmall.png')
          callermock=Custom('callermock','./qakicons/symActorWithobjSmall.png')
     callermock >> Edge(color='magenta', style='solid', decorate='true', label='<evalr<font color="darkgreen"> evalreply</font> &nbsp; >',  fontcolor='magenta') >> sistemas
     callermock >> Edge(color='blue', style='solid',  decorate='true', label='<eval &nbsp; >',  fontcolor='blue') >> sistemas
diag
