package org.neu

import org.apache.spark.{SparkConf, SparkContext}

/**
  * created by Rashmi Dwaraka (dwarakarashmi@ccs.neu.edu)
  */
object Graphs {

  def main(args: Array[String]) {

    // create Spark context with Spark configuration
    val sc = new SparkContext(new SparkConf().setAppName("Graphs"))
    sc.setLogLevel("ERROR")

    // read input file paths and output path
    val songsInfo = sc.textFile(args(0))
    val artistSimilar = sc.textFile(args(1))
    val artistTerms = sc.textFile(args(2))
    val opPath = args(3)

    //Remove the header row from the file and get the data
    val songsInfoData = songsInfo.mapPartitionsWithIndex {
      case (0, iter) => iter.drop(1)
      case (_, iter) => iter
    }.map(row => new Songs(row)).persist()

    //Remove the header row from the file and get the data
    val similarArtists = artistSimilar.mapPartitionsWithIndex {
      case (0, iter) => iter.drop(1)
      case (_, iter) => iter
    }.map(row => new SimilarArtist(row)).persist()

    //Remove the header row from the file and get the data
    val artistTermsData = artistTerms.mapPartitionsWithIndex {
      case (0, iter) => iter.drop(1)
      case (_, iter) => iter
    }.map(row => new ArtistTerms(row)).persist()

    //Subproblem2

    //Artist Familiarity
    val artistFamiliarity =
      songsInfoData.map(song => (song.artistId, song.artist_familiarity)).
        mapValues((_, 1)).
        reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2)).
        mapValues(y => y._1 / y._2).map { case (x: String, y: Float) => (x, y) }.persist()

    //Number of Songs created by the artist
    val artistSongCount =
      songsInfoData.map(song => (song.artistId, song.songId)).
        groupByKey().mapValues(_.iterator.length).map { case (x: String, y: Int) => (x, y) }.persist()


    //The number of similar artists
    val similarArtistsCount =
      similarArtists.map(artist => (artist.artist_id, artist.similar_artist)).
        groupByKey().mapValues(_.iterator.length).map { case (x: String, y: Int) => (x, y) }.persist()

    //Get popular artists
    val popularArtists =
      artistFamiliarity.join(artistSongCount).
        map { case (x, y) => (x, y._1 * y._2) }.
        join(similarArtistsCount).
        map { case (x, y) => (x, y._1 * y._2) }.persist


    //K-Means Implementation
    val artistsWithTerms = artistTermsData.map { artist => (artist.artist_id, artist.artist_term) }.persist
    val termClusters = new KMeansGraphs(artistsWithTerms, popularArtists)
    var centroids = termClusters.get_initial_centroids

    for (i <- 1 to 10) {
      val clusters = termClusters.get_clusters(centroids)
      clusters.map(a => a._1 + "," + a._2._1 + "," + a._2._2).
        coalesce(1, shuffle = false).saveAsTextFile(opPath + "kmeans_commonality/" + i)
      centroids = termClusters.get_centroids(clusters)
      sc.broadcast(centroids)
    }

  }

}
