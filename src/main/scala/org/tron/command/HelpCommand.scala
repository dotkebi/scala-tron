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

import org.tron.peer.Peer
import org.fusesource.jansi.Ansi.ansi

class HelpCommand() extends Command {

  override def execute(peer: Peer, parameters: Array[String]): Unit = {
    if (parameters.isEmpty) {
      usage()
      return
    }
    parameters.head match {
      case "version" =>
        new VersionCommand().usage()
       
      case "account" =>
        new AccountCommand().usage()
       
      case "getbalance" =>
        new GetBalanceCommand().usage()
       
      case "send" =>
        new SendCommand().usage()
       
      case "printblockchain" =>
        new PrintBlockchainCommand().usage()
       
      case "consensus" =>
      case "getmessage" =>
      case "putmessage" =>
        new ConcensusCommand().usage()
       
      case "exit" =>
      case "quit" =>
      case "bye" =>
        new ExitCommand().usage()
       
      case "help" =>
      case _ =>
        new HelpCommand().usage()
       
    }
  }

  override def usage(): Unit = {
    println("")
    println(ansi.eraseScreen.render("@|magenta,bold USAGE|@\n\t@|bold help [arguments]|@"))
    println("")
    println(ansi.eraseScreen.render("@|magenta,bold AVAILABLE COMMANDS|@"))
    println("")
    println(ansi.eraseScreen.render(String.format("\t@|bold %-20s\tPrint the current java-tron version|@", "version")))
    println(ansi.eraseScreen.render(String.format("\t@|bold %-20s\tGet your wallet address|@", "account")))
    println(ansi.eraseScreen.render(String.format("\t@|bold %-20s\tGet your balance|@", "getbalance")))
    println(ansi.eraseScreen.render(String.format("\t@|bold %-20s\tSend balance to receiver address|@", "send")))
    println(ansi.eraseScreen.render(String.format("\t@|bold %-20s\tPrint blockchain|@", "printblockchain")))
    println(ansi.eraseScreen.render(String.format("\t@|bold %-20s\tCreate a server|@", "consensus")))
    println(ansi.eraseScreen.render(String.format("\t@|bold %-20s\tGet a consensus|@", "getmessage")))
    println(ansi.eraseScreen.render(String.format("\t@|bold %-20s\tPut a consensus message|@", "putmessage")))
    println(ansi.eraseScreen.render(String.format("\t@|bold %-20s\tExit java-tron application|@", "exit")))
    println("")
    println(ansi.eraseScreen.render("Use @|bold help [topic] for more information about that topic.|@"))
    println("")
  }

  override def check(parameters: Array[String]) = true
}