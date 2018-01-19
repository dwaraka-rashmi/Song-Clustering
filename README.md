# A7 - Clustering

##  Objective
Performing iterative computations, computations on a graph, learning basic clustering algorithms.

##  Subproblem 1: Clustering
Using Spark, perform hierarchical agglomerative clustering and k-means clustering as follows:

**Fuzzy loudness**: cluster songs into quiet, medium, and loud<br>
**Fuzzy length**: cluster songs into short, medium, and long<br>
**Fuzzy tempo**: cluster songs into slow, medium, and fast<br>
**Fuzzy hotness**: cluster songs into cool, mild, and hot based on song hotness<br>
**Combined hotness**: cluster songs into cool, mild, and hot based on two dimensions: artist hotness, and song hotness<br>

Use each method to perform each clustering task.<br>
Compare the results, as well as the performance of the solutions.

Observe whether:

A song’s loudness, length, or tempo predict its hotness<br>
A song’s loudness, length, tempo, or hotness predict its combined hotness<br>

##  Subproblem 2: Graphs
Let’s define **popularity** as the product of:

The artist’s familiarity<br>
The number of songs created by the artist<br>
The number of similar artists<br>
Let’s define trendsetters as the top 30 most popular artists.

Let’s define **commonality** between two artists as the number of terms shared between the artists.

Let’s define a commonality graph as an undirected graph. The nodes are artists. There is an edge between artists if they are similar. The weight on the edge is the commonality between the artists.

Cluster all artists using k-means in Spark, using the trendsetters as initial centroids and the distance in the commonality graph as distance measure.

Discuss the results of the clustering and performance.


## Instructions to run the project

1.  Set the SPARK_HOME according to the user installed Spark location - Spark - Version - 2.2.0
2.  Set the SCALA_HOME according to the user scala binary location - Scala - Version - 2.11.8
3.  Place the input files in the input folder and set the path accordingly in the makefile
4.  Set the Options to run KMeans or Agglomerative Clustering in Makefile variables
5.  Install ggplot, pandoc for report generation
6.  Once the above changes are made to the Makefile, run the below make commands


build - builds all the implementations

    make build
run - runs the variants

    make run

report - generates the reports

    make report
all - build, run, and report

    make all
