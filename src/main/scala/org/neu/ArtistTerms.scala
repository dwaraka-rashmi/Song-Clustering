package org.neu

import java.io.Serializable

/**
  * created by Rashmi Dwaraka (dwarakarashmi@ccs.neu.edu)
  */
class ArtistTerms(row : String) extends Serializable {
  val columns = row.split(";") //All the columns from the row passed in the constructor

  val artist_id : String = columns(0)
  val artist_term : String = columns(1)
}
