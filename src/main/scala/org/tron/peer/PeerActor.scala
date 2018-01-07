package org.tron.peer

import java.util
import java.util.Arrays

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.stream.scaladsl.Sink
import com.google.protobuf.InvalidProtocolBufferException
import org.tron.config.Configer
import org.tron.core.Constant.{TOPIC_BLOCK, TOPIC_TRANSACTION}
import org.tron.core._
import org.tron.overlay.kafka.{ConsumerProperty, Kafka, ProducerProperty}
import org.tron.overlay.listener.{ReceiveSource, ReceiveSourceStreamFactory}
import org.tron.overlay.message.{Message, Type}
import org.tron.protos.core.{TronBlock, TronTransaction}
import org.tron.storage.leveldb.LevelDbDataSourceImpl
import org.tron.utils.ByteArray
import org.tron.wallet.Wallet

class PeerActor(peer: Peer) extends Actor {

  val source = new ReceiveSource
  val net = new Kafka(
    context.system,
    ProducerProperty.getDefault,
    ConsumerProperty.getDefault,
    source,
    List(TOPIC_BLOCK, TOPIC_TRANSACTION))

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
    val pendingState = peer.blockchain.getPendingState.asInstanceOf[PendingStateImpl]
    pendingState.addPendingTransaction(peer.blockchain, transaction, net)
  }

  def handleBlock(message: Message) = {
    val block = TronBlock.Block.parseFrom(ByteArray.fromHexString(message.message))
    peer.blockchain.receiveBlock(block, peer.uTXOSet)
  }

  def receive = {
    case x =>
  }
}
