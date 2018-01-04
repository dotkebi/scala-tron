package org.tron.core

import java.io.Serializable

/**
  * Classes implementing this interface represent a monetary value, such as a Bitcoin  amount.
  */
trait Monetary extends Serializable {

  /**
    * Returns the absolute value of exponent of the value of a "smallest unit" in scientific notation. For Bitcoin, a
    * satoshi is worth 1E-8 so this would be 8.
    */
  def smallestUnitExponent: Int

  /**
    * Returns the number of "smallest units" of this monetary value. For Bitcoin, this would be the number of satoshis.
    */
  def value: Long

  def signum: Int
}
