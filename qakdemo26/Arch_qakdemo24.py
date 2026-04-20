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
with Diagram('qakdemo24Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxcreate', graph_attr=nodeattr):
          creator=Custom('creator','./qakicons/symActorWithobjSmall.png')
          producer=Custom('producer','./qakicons/symActorDynamicWithobj.png')
          consumer=Custom('consumer','./qakicons/symActorWithobjSmall.png')
          consumerhelper=Custom('consumerhelper','./qakicons/symActorDynamicWithobj.png')
     consumer >> Edge(color='magenta', style='dotted', decorate='true', label='<currentMsg &nbsp; >',  fontcolor='black') >> consumerhelper
     producer >> Edge(color='magenta', style='solid', decorate='true', label='<distance<font color="darkgreen"> distanceack</font> &nbsp; r2<font color="darkgreen"> rr2</font> &nbsp; >',  fontcolor='magenta') >> consumer
diag
