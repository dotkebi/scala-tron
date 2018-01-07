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
 */
package org.tron.command

import org.fusesource.jansi.Ansi.ansi
import org.tron.core.PublicKey
import org.tron.peer.Peer

import scala.collection.JavaConverters._

class GetBalanceCommand() extends Command {
  override def execute(peer: Peer, parameters: Array[String]): Unit = {
    val pubKeyHash = peer.getWallet.getEcKey.getPubKey
    val utxos = peer.getUTXOSet.findUTXO(PublicKey(pubKeyHash))
    var balance = 0L
    for (txOutput <- utxos) {
      balance += txOutput.getValue
    }
    println(balance)
  }

  override def usage(): Unit = {
    println("")
    println(ansi.eraseScreen.render("@|magenta,bold USAGE|@\n\t@|bold getbalance|@"))
    println("")
    println(ansi.eraseScreen.render("@|magenta,bold DESCRIPTION|@\n\t@|bold The command 'getbalance' get your balance.|@"))
    println("")
  }

  override def check(parameters: Array[String]) = true
}
