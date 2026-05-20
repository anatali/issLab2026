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
with Diagram('robotservice26Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxrobotservice26', graph_attr=nodeattr):
          robotactor=Custom('robotactor','./qakicons/symActorWithobjSmall.png')
     robotactor >> Edge( label='sonardata', **eventedgeattr, decorate='true', fontcolor='red') >> robotactor
     robotactor >> Edge( label='vrinfo', **eventedgeattr, decorate='true', fontcolor='red') >> robotactor
     robotactor >> Edge( label='sonaralarm', **eventedgeattr, decorate='true', fontcolor='red') >> sys
diag
