package org.neu

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
  * created by Rashmi Dwaraka (dwarakarashmi@ccs.neu.edu)
  */
object Clustering {

  // Agglomerative Clustering
  def agglomerative(songsInfoData: RDD[(Songs)], sc: SparkContext, op: String): Unit = {

    //  1.  Fuzzy loudness: cluster songs into quiet, medium, and loud
    val agglomerativeLoudness = new Agglomerative(songsInfoData.map(song => (song.trackId, song.loudness)))
    agglomerativeLoudness.runAlgo.coalesce(1, shuffle = false).saveAsTextFile(op + "agg_loudness/")

    //  2.  Fuzzy length: cluster songs into short, medium, and long
    val agglomerativeDuration = new Agglomerative(songsInfoData.map(song => (song.trackId, song.duration)))
    agglomerativeDuration.runAlgo.coalesce(1, shuffle = false).saveAsTextFile(op + "agg_duration/")

    //  3.  Fuzzy tempo: cluster songs into slow, medium, and fast
    val agglomerativeTempo = new Agglomerative(songsInfoData.map(song => (song.trackId, song.tempo)))
    agglomerativeTempo.runAlgo.coalesce(1, shuffle = false).saveAsTextFile(op + "agg_tempo/")

    //  4.  Fuzzy hotness: cluster songs into cool, mild, and hot based on song hotness
    val agglomerativeHotness = new Agglomerative(songsInfoData.map(song => (song.trackId, song.song_hotttnesss)))
    agglomerativeHotness.runAlgo.coalesce(1, shuffle = false).saveAsTextFile(op + "agg_hotness/")

    //  5.  Combined hotness: cluster songs into cool, mild, and hot based on
    //   two dimensions: artist hotness, and song hotness
    val agglomerativeSongArtHotness = new Agglomerative2D(songsInfoData.
      map(song => (song.trackId, (song.song_hotttnesss, song.artist_hotttnesss))))
    agglomerativeSongArtHotness.runAlgo.coalesce(1, shuffle = false).saveAsTextFile(op + "agg_s_a_hotness/")

  }


  // K-Means Clustering
  def kmeans(songsInfoData: RDD[(Songs)], sc: SparkContext, op: String): Unit = {

    //  1.  Fuzzy loudness: cluster songs into quiet, medium, and loud
    val kMeansLoudness = new KMeans(songsInfoData.map(song => (song, song.loudness)))
    var centroids = kMeansLoudness.get_initial_centroids
    for (i <- 1 to 10) {
      val clusters = kMeansLoudness.get_clusters(centroids)
      clusters.map(a => a._1 + "," + a._2._1.getLoudnessString).
        coalesce(1, shuffle = false).
        saveAsTextFile(op + "kmeans_loudness/" + i)
      centroids = kMeansLoudness.get_centroids(clusters)
      sc.broadcast(centroids)
    }

    //  2.  Fuzzy length: cluster songs into short, medium, and long
    val kMeansDuration = new KMeans(songsInfoData.map(song => (song, song.duration)))
    centroids = kMeansDuration.get_initial_centroids
    for (i <- 1 to 10) {
      val clusters = kMeansDuration.get_clusters(centroids)
      clusters.map(a => a._1 + "," + a._2._1.getDurationString).
        coalesce(1, shuffle = false).
        saveAsTextFile(op + "kmeans_duration/" + i)
      centroids = kMeansDuration.get_centroids(clusters)
      sc.broadcast(centroids)
    }

    //  3.  Fuzzy tempo: cluster songs into slow, medium, and fast
    val kMeansTempo = new KMeans(songsInfoData.map(song => (song, song.tempo)))
    centroids = kMeansTempo.get_initial_centroids
    for (i <- 1 to 10) {
      val clusters = kMeansTempo.get_clusters(centroids)
      clusters.map(a => a._1 + "," + a._2._1.getTempoString).
        coalesce(1, shuffle = false).
        saveAsTextFile(op + "kmeans_tempo/" + i)
      centroids = kMeansTempo.get_centroids(clusters)
      sc.broadcast(centroids)
    }

    //  4.  Fuzzy hotness: cluster songs into cool, mild, and hot based on song hotness
    val kMeansSongHotness = new KMeans(songsInfoData.map(song => (song, song.song_hotttnesss)))
    centroids = kMeansSongHotness.get_initial_centroids
    for (i <- 1 to 10) {
      val clusters = kMeansSongHotness.get_clusters(centroids)
      clusters.map(a => a._1 + "," + a._2._1.getHotnessString).
        coalesce(1, shuffle = false).
        saveAsTextFile(op + "kmeans_song_hotness/" + i)
      centroids = kMeansSongHotness.get_centroids(clusters)
      sc.broadcast(centroids)
    }

    //  5.  Combined hotness: cluster songs into cool, mild, and hot based on
    //   two dimensions: artist hotness, and song hotness
    val kMeansSongArtHotness = new KMeans2D(songsInfoData.
      map(song => (song, (song.song_hotttnesss, song.artist_hotttnesss))))
    var centroids2D = kMeansSongArtHotness.get_initial_centroids
    for (i <- 1 to 10) {
      val clusters = kMeansSongArtHotness.get_clusters(centroids2D)
      clusters.map(a => a._1 + "," + a._2._1.getSAHotnessString).
        coalesce(1, shuffle = false).
        saveAsTextFile(op + "kmeans_song__artist_hotness/" + i)
      centroids2D = kMeansSongArtHotness.get_centroids(clusters)
      sc.broadcast(centroids2D)
    }

  }


  def main(args: Array[String]) {

    // create Spark context with Spark configuration
    val sc = new SparkContext(new SparkConf().setAppName("Clustering"))
    sc.setLogLevel("ERROR")

    // read input file paths and output path
    val songsInfo = sc.textFile(args(1))
    val opPath = args(2)

    //Remove the header row from the file and get the data
    val songsInfoData = songsInfo.mapPartitionsWithIndex {
      case (0, iter) => iter.drop(1)
      case (_, iter) => iter
    }.map(row => new Songs(row)).persist()

    //Subproblem1
    args(0) match {
      case "0" => kmeans(songsInfoData, sc, opPath)
      case "1" => agglomerative(songsInfoData, sc, opPath)
    }
  }

}
