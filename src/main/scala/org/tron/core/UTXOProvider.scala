package org.tron.core

trait UTXOProvider {

  /**
    * Get the height of the chain head.
    *
    * @return The chain head height.
    * @throws UTXOProvider If there is an error.
    */
  def getChainHeadHeight: Int

  def getParams: NetworkParameters
}