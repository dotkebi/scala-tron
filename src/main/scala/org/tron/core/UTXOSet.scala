/*
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *//*
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.tron
package core

import com.google.protobuf.InvalidProtocolBufferException
import org.tron.crypto.ECKey
import org.tron.protos.core.TronTXOutputs
import org.tron.protos.core.TronTXOutputs.TXOutputs
import org.tron.storage.leveldb.LevelDbDataSourceImpl
import org.tron.utils.ByteArray

import scala.collection.JavaConverters._

class UTXOSet(
  txDB: LevelDbDataSourceImpl,
  val blockchain: Blockchain) {

  def reindex(): Unit = {
    txDB.resetDB()

    for {
      (key, value) <- blockchain.findUTXO.asScala
      txOutput <- value.getOutputsList.asScala
    } {
      txDB.putData(ByteArray.fromHexString(key), value.toByteArray)
    }
  }

  def findSpendableOutputs(pubKey: PublicKey, amount: Long): SpendableOutputs = {
    val unspentOutputs = scala.collection.mutable.Map[String, Array[Long]]()

    var accumulated = 0L
    val keySet = txDB.allKeys
    for (key <- keySet.asScala) {
      val txOutputsData = txDB.getData(key)
      try {
        val txOutputs = TronTXOutputs.TXOutputs.parseFrom(txOutputsData)
        val len = txOutputs.getOutputsCount
        for (i <- 0 to len) {
          val txOutput = txOutputs.getOutputs(i)
          if (pubKey.hex == ByteArray.toHexString(txOutput.getPubKeyHash.toByteArray) && accumulated < amount) {
            accumulated += txOutput.getValue
            val v = unspentOutputs.getOrElse(ByteArray.toHexString(key), Array[Long]())
            unspentOutputs.put(ByteArray.toHexString(key), v :+ i.toLong)
          }
        }
      } catch {
        case e: InvalidProtocolBufferException =>
          e.printStackTrace()
      }
    }

    SpendableOutputs(accumulated, unspentOutputs.toMap)
  }

  def findUTXO(pubKeyHash: PublicKey) = {

    txDB
      // Take all keys
      .allKeys().asScala
      // Retrieve data for each key
      .map(key => txDB.getData(key))
      // Find all outputs
      .flatMap { txData =>
        TXOutputs.parseFrom(txData).getOutputsList.asScala.filter(txOutput => {
          val txOutputHex = ByteArray.toHexString(txOutput.getPubKeyHash.toByteArray)
          pubKeyHash.hex == txOutputHex
        })
      }
      .toArray
  }
}