package org.neu

import java.io.Serializable

/**
  * created by Rashmi Dwaraka (dwarakarashmi@ccs.neu.edu)
  */
class Songs(row : String) extends Serializable {

  val columns = row.split(";") //All the columns from the row passed in the constructor

  //Attributes of the song required to perform the queries

  val trackId : String = columns(0)
  val songId : String = columns(23)
  val loudness : Float = try {columns(6).toFloat}
  catch { case e : Throwable => 0.0f}
  val title : String = columns(24)
  val artistId : String = columns(16)
  val artist_name : String = columns(17)
  val album : String = columns(22)
  val duration : Float = try { columns(5).toFloat}
  catch { case e: Throwable => 0.0f}
  val tempo : Float = try { columns(7).toFloat}
  catch { case e: Throwable => 0.0f}

  val artist_familiarity : Float = try { columns(19).toFloat}
  catch { case e: Throwable => 0.0f}

  val artist_hotttnesss : Float = try { columns(20).toFloat}
  catch { case e: Throwable => 0.0f}

  val song_hotttnesss : Float = try { columns(25).toFloat}
  catch { case e: Throwable => 0.0f}

  val key : Int = try { columns(8).toInt}
  catch { case e: Throwable => -1}

  val key_confidence : Float = try { columns(9).toFloat}
  catch { case e: Throwable => 0.0f}

  def getArtistDetails = (artistId,artist_hotttnesss)

  def getLoudnessString = trackId+","+loudness
  def getDurationString = trackId+","+duration
  def getTempoString = trackId+","+tempo
  def getHotnessString = trackId+","+song_hotttnesss
  def getSAHotnessString = trackId+","+song_hotttnesss+","+artist_hotttnesss

}