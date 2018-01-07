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
package org.tron.command

import org.fusesource.jansi.Ansi.ansi
import org.slf4j.LoggerFactory
import org.tron.core.TransactionUtils
import org.tron.overlay.message.{Message, Type}
import org.tron.peer.Peer
import org.tron.utils.ByteArray

import scala.util.{Failure, Success, Try}

class SendCommand() extends Command {

  val logger = LoggerFactory.getLogger("Command")

  override def execute(peer: Peer, parameters: Array[String]): Unit = if (check(parameters)) {
    val to = parameters(0)
    val amount = parameters(1).toLong
    val transaction = TransactionUtils.newTransaction(peer.getWallet, to, amount, peer.getUTXOSet)
    if (transaction != null) {
      val message = Message(ByteArray.toHexString(transaction.toByteArray), Type.TRANSACTION)
//      peer.getNet.broadcast(message)
    }
  }

  override def usage(): Unit = {
    println("")
    println(ansi.eraseScreen.render("@|magenta,bold USAGE|@\n\t@|bold send [receiver] [amount]|@"))
    println("")
    println(ansi.eraseScreen.render("@|magenta,bold DESCRIPTION|@\n\t@|bold The command 'send' send balance to receiver address.|@"))
    println("")
    println(ansi.eraseScreen.render("\t@|bold Example:|@\n\t\t@|bold $ send [address] [amount]|@"))
    println("")
    println(ansi.eraseScreen.render("\t@|bold if [amount] > balance, the command 'send' will fail to execute.|@"))
    println("")
  }

  override def check(parameters: Array[String]): Boolean = {
    if (parameters.length < 2) {
      logger.error("missing parameters")
      return false
    }
    if (parameters(0).length != 40) {
      logger.error("address invalid")
      return false
    }

    Try(parameters(1).toLong) match {
      case Failure(e: NumberFormatException) =>
        logger.error("amount invalid")
        false
      case Success(amount) if amount <= 0 =>
        logger.error("amount required a positive number")
        false
      case Success(amount) =>
        true
    }
  }
}