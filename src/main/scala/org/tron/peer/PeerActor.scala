package org.tron.peer

import java.util
import java.util.Arrays

import akka.actor.Actor
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.stream.scaladsl.Sink
import com.google.protobuf.InvalidProtocolBufferException
import org.tron.config.Configer
import org.tron.core.Constant.{TOPIC_BLOCK, TOPIC_TRANSACTION}
import org.tron.core.{Blockchain, PendingStateImpl, TransactionUtils, UTXOSet}
import org.tron.overlay.kafka.Kafka
import org.tron.overlay.listener.{ReceiveSource, ReceiveSourceStreamFactory}
import org.tron.overlay.message.{Message, Type}
import org.tron.protos.core.{TronBlock, TronTransaction}
import org.tron.utils.ByteArray
import org.tron.wallet.Wallet


object PeerActor {

  /**
    * Builds a new peer actor
    */
  def build = {
    val key = Configer.getMyKey

    // Build the wallet
    val wallet = new Wallet
    wallet.init(key)

    // Build the blockhain
    val blockchain = new Blockchain(ByteArray.toHexString(wallet.getAddress))

    val utxoSet = new UTXOSet
    utxoSet.setBlockchain(blockchain)
    utxoSet.reindex()

    new PeerActor(wallet, blockchain, utxoSet)
  }

}

class PeerActor(
  wallet: Wallet,
  blockchain: Blockchain,
  utxoSet: UTXOSet) extends Actor {

  val source = new ReceiveSource
  val net = new Kafka(source, util.Arrays.asList(TOPIC_BLOCK, TOPIC_TRANSACTION))

  def initNet() = {

    // Handle message errors
    val decider: Supervision.Decider = {
      case e: InvalidProtocolBufferException =>
        e.printStackTrace()
        Supervision.resume
    }

    // Configure supervision strategy
    val materializerSettings = ActorMaterializerSettings(context.system).withSupervisionStrategy(decider)
    implicit val materializer = ActorMaterializer(materializerSettings)(context)

    // Listen to transaction events
    ReceiveSourceStreamFactory.build(source)
      .filter(_.msgType == Type.TRANSACTION)
      .runForeach(handleTransaction)

    // Listen to block events
    ReceiveSourceStreamFactory.build(source)
      .filter(_.msgType == Type.BLOCK)
      .runForeach(handleBlock)
  }

  def handleTransaction(message: Message) = {
    val transaction = TronTransaction.Transaction.parseFrom(ByteArray.fromHexString(message.message))
    System.out.println(TransactionUtils.toPrintString(transaction))
    val pendingState = blockchain.getPendingState.asInstanceOf[PendingStateImpl]
    pendingState.addPendingTransaction(blockchain, transaction, net)
  }

  def handleBlock(message: Message) = {
    val block = TronBlock.Block.parseFrom(ByteArray.fromHexString(message.message))
    blockchain.receiveBlock(block, utxoSet)
  }

  def receive = {
    case x =>
  }
}
