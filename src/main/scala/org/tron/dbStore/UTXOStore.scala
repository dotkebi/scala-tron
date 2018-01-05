package org.tron.dbStore

import java.util

import org.tron.core.Constant.TRANSACTION_DB_NAME
import org.tron.storage.leveldb.LevelDbDataSourceImpl

class UTXOStore() {

  val uTXODataSource = new LevelDbDataSourceImpl(TRANSACTION_DB_NAME)
  uTXODataSource.initDB()

  def reset(): Unit = {
    uTXODataSource.resetDB()
  }

  def find(key: Array[Byte]): Array[Byte] = uTXODataSource.getData(key)

  def getKeys: util.Set[Array[Byte]] = uTXODataSource.allKeys

  /**
    * save utxo
    */
  def saveUTXO(utxoKey: Array[Byte], utxoData: Array[Byte]): Unit = {
    uTXODataSource.putData(utxoKey, utxoData)
  }

  def close(): Unit = {
    uTXODataSource.closeDB()
  }
}
