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

import org.tron.peer.Peer
import org.tron.utils.ByteArray
import org.fusesource.jansi.Ansi.ansi

class AccountCommand extends Command {

  override def execute(peer: Peer, parameters: Array[String]): Unit = {
    println(ByteArray.toHexString(peer.getMyKey.getAddress))
  }

  override def usage(): Unit = {
    println("")
    println(ansi.eraseScreen.render("@|magenta,bold USAGE|@\n\t@|bold account|@"))
    println("")
    println(ansi.eraseScreen.render("@|magenta,bold DESCRIPTION|@\n\t@|bold The command 'account' get your wallet address.|@"))
    println("")
  }

  override def check(parameters: Array[String]) = true
}
