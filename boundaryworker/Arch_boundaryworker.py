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
with Diagram('boundaryworkerArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxboundary', graph_attr=nodeattr):
          boundaryworker=Custom('boundaryworker','./qakicons/symActorWithobjSmall.png')
     with Cluster('ctxrobotservice26', graph_attr=nodeattr):
          robotactor=Custom('robotactor(ext)','./qakicons/externalQActor.png')
     boundaryworker >> Edge(color='magenta', style='solid', decorate='true', label='<step<font color="darkgreen"> stepdone stepfailed</font> &nbsp; >',  fontcolor='magenta') >> robotactor
     boundaryworker >> Edge(color='blue', style='solid',  decorate='true', label='<move &nbsp; >',  fontcolor='blue') >> robotactor
diag
