package org.neu

import java.io.Serializable
import org.apache.spark.rdd.RDD

/**
  * created by Rashmi Dwaraka (dwarakarashmi@ccs.neu.edu)
  */
class KMeansGraphs(rDD: RDD[(String,String)],popArtist : RDD[(String,Float)]) extends Serializable {

  //Top 30 Popular Artists are assigned as initial centroids
  def get_initial_centroids: Array[(Int, String)] = {
      popArtist.join(rDD).
        sortBy(_._2._1,ascending = false).take(30).zipWithIndex.
        map { case(x,y) => (y,x._2._2) }
  }

  //Re-calculate centroids every iteration by assigning mean of
  // the cluster as the new centroid
  def get_centroids(clusters : RDD[(Int,(String,String))]) = {
      clusters.reduceByKey{case((x : (String,String), y : (String,String))) =>
        (x._1,x._2.split(' ').intersect(y._2.split(' ')).mkString(" ")) }.
        map { case(x,y) => (x,y._2) }.collect()
  }

  // Distance metric is the similarity between 2 word vector
  def distance(s1 : String, s2 : String) = {
    val term1 = s1.split(' ')
    val term2 = s2.split(' ')
    term1.intersect(term2).length
  }

  //For each data point assign it to the most closest centroid
  def get_clusters(centroids : Array[(Int, String)]) = {
    rDD.map( data => {
      (centroids.map( c => {
        (c._1,distance(c._2 , data._2))
      }).maxBy(_._2)._1,data)
    })
  }
}

