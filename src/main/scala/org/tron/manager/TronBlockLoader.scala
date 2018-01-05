package org.tron.manager

import java.nio.file.Paths
import java.util.Scanner

import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Sink}
import org.spongycastle.util.encoders.Hex
import org.springframework.beans.factory.annotation.Autowired
import org.tron.config.SystemProperties
import org.tron.core.{TransactionUtils, TronBlockChainImpl}
import org.tron.protos.core.TronBlock
import scala.collection.JavaConverters._


class TronBlockLoader(blockchain: TronBlockChainImpl) {

  @Autowired
  private[manager] val config: SystemProperties = null

  def isBlockNewer(block: TronBlock.Block) = {
    block.getBlockHeader.getNumber >= blockchain.getBlockStoreInter.getBestBlock.getBlockHeader.getNumber
  }

  /**
    * Load blocks by path
    */
  def loadBlocks(fileSrc: String)(implicit actorMaterializer: ActorMaterializer): Unit = {

    FileIO.fromPath(Paths.get(fileSrc))
      // Read line
      .map { line => Hex.decode(line.utf8String) }
      // Parse as Block
      .map { line => TronBlock.Block.parseFrom(line) }
      // Verify sender
      .map { block =>
        if (isBlockNewer(block)) {
          for (tx <- block.getTransactionsList.asScala) {
            TransactionUtils.getSender(tx)
          }
        }
        block
      }
      // Verify block
      .map { block =>
        if (isBlockNewer(block) || blockchain.getBlockStoreInter.getBlockByHash(block.getBlockHeader.getHash.toByteArray) == null) {
          if (block.getBlockHeader.getNumber > 0) {
            throw new RuntimeException
          }
        }
      }
      .runWith(Sink.ignore)
  }
}