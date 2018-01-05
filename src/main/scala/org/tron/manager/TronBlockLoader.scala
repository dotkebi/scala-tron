package org.tron.manager

import java.nio.file.Paths
import java.util.Scanner

import akka.stream.scaladsl.{FileIO, Sink}
import org.spongycastle.util.encoders.Hex
import org.springframework.beans.factory.annotation.Autowired
import org.tron.config.SystemProperties
import org.tron.core.{TransactionUtils, TronBlockChainImpl}
import org.tron.protos.core.TronBlock


class TronBlockLoader {
  @Autowired
  private val blockchain: TronBlockChainImpl = null

  @Autowired
  private[manager] val config: SystemProperties = null
  private[manager] var scanner: Scanner = null


  def loadBlocks(): Unit = {
    val fileSrc = config.blocksLoader

    FileIO.fromPath(Paths.get(fileSrc))
      // Read line
      .map { line => Hex.decode(line.utf8String) }
      // Parse as Block
      .map { line => TronBlock.Block.parseFrom(line) }
      // Verify sender
      .map { block => {
        if (block.getBlockHeader.getNumber >= blockchain.getBlockStoreInter.getBestBlock.getBlockHeader.getNumber) {
          for (tx <- block.getTransactionsList) {
            TransactionUtils.getSender(tx)
          }
        }
        block
      }
      }
      // Verify block
      .map { block => blockWork(block) }
      .runWith(Sink.ignore)
  }

  private def blockWork(block: TronBlock.Block): Unit = {
    if (block.getBlockHeader.getNumber >= blockchain.getBlockStoreInter.getBestBlock.getBlockHeader.getNumber
      || blockchain.getBlockStoreInter.getBlockByHash(block.getBlockHeader.getHash.toByteArray) == null) {
      if (block.getBlockHeader.getNumber > 0) {
        throw new RuntimeException
      }
    }
  }
}
