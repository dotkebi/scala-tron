package org.tron.peer

import akka.actor.{ActorSystem, Props}
import org.tron.config.Configer
import org.tron.core.{Blockchain, Constant, UTXOSet}
import org.tron.crypto.ECKey
import org.tron.storage.leveldb.LevelDbDataSourceImpl
import org.tron.utils.ByteArray
import org.tron.wallet.Wallet

object Peer {
  val PEER_NORMAL = "normal"
  val PEER_SERVER = "server"


  /**
    * Builds a new peer actor
    */
  def build(peerType: String) = {
    val key = Configer.getMyKey

    // Build the wallet
    val wallet = new Wallet
    wallet.init(key)

    // Build the blockhain
    val blockchain = new Blockchain(ByteArray.toHexString(wallet.getAddress))

    val utxoSet = new UTXOSet(new LevelDbDataSourceImpl(Constant.TRANSACTION_DB_NAME), blockchain)

    Peer(key, wallet, blockchain, utxoSet, peerType)
  }
}


case class Peer(key: ECKey, wallet: Wallet, blockchain: Blockchain, uTXOSet: UTXOSet, peerType: String) {
  def getWallet = wallet
  def getUTXOSet = uTXOSet
  def getMyKey = key
  def getBlockchain = blockchain
  def getType = peerType
}
