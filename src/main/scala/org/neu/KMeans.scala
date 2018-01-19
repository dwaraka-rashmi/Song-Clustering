package org.neu

import java.io.Serializable
import org.apache.spark.rdd.RDD

/**
  * created by Rashmi Dwaraka (dwarakarashmi@ccs.neu.edu)
  */

//Single Dimension K Means
class KMeans(rDD: RDD[(Songs,Float)]) extends Serializable {

    //Initial Centroids is considered as the Quartile1,
    // Quartile2, Quartile3 of the Data-set
    def get_initial_centroids: Array[(Int, Float)] =  {

        val count = rDD.count()

        val q1 = (count/4).toInt
        val q2 = (count/2).toInt
        val q3 = ((3 * count)/4).toInt

        val songsWithIndex = rDD.
          sortBy(_._2, ascending = true).
          zipWithIndex().map{_.swap}.
          filter( x => x._1 == q1 || x._1 == q2 || x._1 == q3)

        Array((1, songsWithIndex.lookup(q1)(0)._2),
            (2, songsWithIndex.lookup(q2)(0)._2),
            (3, songsWithIndex.lookup(q3)(0)._2))

    }

    //Re-calculate centroids every iteration by assigning mean of
    // the cluster as the new centroid
    def get_centroids(clusters : RDD[(Int,(Songs,Float))]) = {
        val c = clusters.map(data => (data._1,data._2._2)).mapValues((_, 1))
          .reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2))
          .mapValues(y => y._1 / y._2).sortBy(_._2,ascending = false)
        c.collect()
    }

    //For each data point assign it to the most closest centroid
    def get_clusters(centroids : Array[(Int, Float)]) = {
        rDD.map( data => {
                (centroids.map( c => {
                    (c._1,math.abs(c._2 - data._2))
                }).minBy(_._2)._1,data)
        })
    }

}

//Two Dimension K Means
class KMeans2D(rDD: RDD[(Songs,(Float,Float))]) extends Serializable {

    //Initial Centroids is considered as the Quartile1,
    // Quartile2, Quartile3 of the Data-set
    def get_initial_centroids: Array[(Int, (Float,Float))] =  {
       val init_c = rDD.takeSample(withReplacement = false,3).zipWithIndex.map{_.swap}.
          map{case(x : Int,y : (Songs,(Float,Float))) => (x, y._2)}
        init_c
    }

    //Re-calculate centroids every iteration by assigning mean of
    // the cluster as the new centroid
    def get_centroids(clusters : RDD[(Int,(Songs,(Float,Float)))]) = {
        val c = clusters.map(data => (data._1,data._2._2)).mapValues((_, 1)).
          reduceByKey { case (x: ((Float, Float), Int), y: ((Float, Float), Int)) =>
              (( x._1._1 + y._1._1 ,x._1._2 + y._1._2), x._2 + y._2)}.
          mapValues(y => (y._1._1 / y._2, y._1._2 / y._2))
        c.collect()
    }

    def distance(d1 : (Float,Float),d2 : (Float,Float)) =
        math.sqrt(math.pow(d1._2 - d2._2, 2) + math.pow(d1._1 - d2._1, 2))

    //For each data point assign it to the most closest centroid
    def get_clusters(centroids : Array[(Int, (Float,Float))]) = {
        rDD.map( data => {
            (centroids.map( c => {
                (c._1,distance(c._2 , data._2))
            }).minBy(_._2)._1,data)
        })
    }

}
