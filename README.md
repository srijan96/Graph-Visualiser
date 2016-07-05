# Graph-Visualiser
A bunch of java programs to generate and visualise Graphs

GraphGen.java   -   
    A sample program that generates a random graph constrained by degree distribution. It outputs the graph in a file named "edges.csv".

GraphDraw.java  -   
    This is the main visualisation program. It shows the graph in a simple layout.User can re-position nodes by dragging.Other supported actions are zooming and panning.
    
GraphDrawMod.java   -   
    Modified Version of drawing class . Uses Force-Based graph drawing algorithm. 
    Features are - Node repositioning , zooming(in,out,normal) , Panning , Resetting, Hiding/Showing Nodes , Step-by-step & block wise manual Force-based repositioning , drawing Minimum Spanning tree and finding shortest path between a pair of vertices.
  
run.sh  -   
    A shell script to run both programs back-to-back
