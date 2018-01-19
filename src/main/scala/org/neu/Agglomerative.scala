package org.neu

import org.apache.spark.rdd.RDD

/**
  * created by Rashmi Dwaraka (dwarakarashmi@ccs.neu.edu)
  */

//Single Dimensional Agglomerative
class Agglomerative(rDD: RDD[(String, Float)]) extends Serializable {

  def distance(d1: Float, d2: Float) = math.abs(d1 - d2)

  def runAlgo = {

    var distanceMatrix =
      rDD.cartesian(rDD).
        filter { case (a, b) => a._1 < b._1 }.
        map { case (a, b) => ((a._1, b._1), distance(a._2, b._2)) }
        .persist()

    for (i <- 1 to 96) {

      var minOfDMatrix = distanceMatrix.min()(Ordering.by(_._2))
      var trackId1 = minOfDMatrix._1._1
      var trackId2 = minOfDMatrix._1._2

      var filterCluster = distanceMatrix.
        filter { case (a, b) =>
          (a._1 == trackId1 || a._1 == trackId2 || a._2 == trackId1 || a._2 == trackId2) &&
            !((a._1 == trackId1 || a._1 == trackId2) && (a._2 == trackId1 || a._2 == trackId2))
        }.persist

      var filterMinLeft = filterCluster.filter { case (a, b) => a._1 == trackId1 || a._1 == trackId2 }
      var filterMinRight = filterCluster.filter { case (a, b) => a._2 == trackId1 || a._2 == trackId2 }

      var filteredClusterPair = filterMinLeft.map { case (a, b) => (a.swap, b) }.union(filterMinRight)
      var filterAddPair = filteredClusterPair.groupBy(_._1._1).
        map { case (x, y) => ((trackId1 + "-" + trackId2, x), y.iterator.minBy(_._2)._2) }

      distanceMatrix =
        distanceMatrix.filter { case (a, b) =>
          !(a._1 == trackId1 || a._1 == trackId2 || a._2 == trackId1 || a._2 == trackId2)
        }

      distanceMatrix = distanceMatrix.union(filterAddPair).persist()

    }
    distanceMatrix.map { case (a, b) => a._1 + "," + a._2 + "," + b.toString }
  }

}


//Two Dimensional Agglomerative
class Agglomerative2D(rDD: RDD[(String, (Float, Float))]) extends Serializable {

  def distance(d1: (Float, Float), d2: (Float, Float)) = {
    math.sqrt(math.pow(d1._2 - d2._2, 2) + math.pow(d1._1 - d2._1, 2))
  }

  def runAlgo = {

    var distanceMatrix =
      rDD.cartesian(rDD).
        filter { case (a, b) => a._1 < b._1 }.
        map { case (a, b) => ((a._1, b._1), distance(a._2, b._2)) }
        .persist()

    for (i <- 1 to 96) {

      var minOfDMatrix = distanceMatrix.min()(Ordering.by(_._2))
      var trackId1 = minOfDMatrix._1._1
      var trackId2 = minOfDMatrix._1._2

      var filterCluster = distanceMatrix.
        filter { case (a, b) =>
          (a._1 == trackId1 || a._1 == trackId2 || a._2 == trackId1 || a._2 == trackId2) &&
            !((a._1 == trackId1 || a._1 == trackId2) && (a._2 == trackId1 || a._2 == trackId2))
        }.persist

      var filterMinLeft = filterCluster.filter { case (a, b) => a._1 == trackId1 || a._1 == trackId2 }
      var filterMinRight = filterCluster.filter { case (a, b) => a._2 == trackId1 || a._2 == trackId2 }

      var filteredClusterPair = filterMinLeft.map { case (a, b) => (a.swap, b) }.union(filterMinRight)
      var filterAddPair = filteredClusterPair.groupBy(_._1._1).
        map { case (x, y) => ((trackId1 + "-" + trackId2, x), y.iterator.minBy(_._2)._2) }

      distanceMatrix =
        distanceMatrix.filter { case (a, b) =>
          !(a._1 == trackId1 || a._1 == trackId2 || a._2 == trackId1 || a._2 == trackId2)
        }

      distanceMatrix = distanceMatrix.union(filterAddPair).persist()

    }

    distanceMatrix.map { case (a, b) => a._1 + "," + a._2 + "," + b.toString }
  }

}
