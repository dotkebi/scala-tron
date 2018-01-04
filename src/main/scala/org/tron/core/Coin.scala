package org.tron.core

import com.google.common.primitives.Longs
import java.io.Serializable

/**
  * Represents a monetary Bitcoin value. This class is immutable.
  */
object Coin {
  /**
    * Number of decimals for one Bitcoin. This constant is useful for quick adapting to other coins because a lot of
    * constants derive from it.
    */
  val SMALLEST_UNIT_EXPONENT = 8

  def valueOf(satoshis: Long) = new Coin(satoshis)
}

case class Coin(value: Long) extends Monetary with Comparable[Coin] with Serializable {

  def compareTo(other: Coin) = {
    Longs.compare(value, other.value)
  }

  def smallestUnitExponent = {
    Coin.SMALLEST_UNIT_EXPONENT
  }

  def signum = {
    if (this.value == 0) 0
    else if (this.value < 0) -1
    else 1
  }
}
