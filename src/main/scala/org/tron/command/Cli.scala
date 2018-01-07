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
import java.util.Scanner

import cats.effect.IO
import org.tron.peer.Peer

class Cli(peer: Peer) {

  def run(): Unit = {
    readLines(new Scanner(System.in)).unsafeRunSync()
  }

  def readLines(scanner: Scanner): IO[Unit] = {

    def readLine = IO { scanner.nextLine.trim.split("\\s+") }

    for {
      commands <- readLine
      _ <- if (commands.nonEmpty) handleCommand(commands) else IO.unit
      _ <- readLines(scanner)
    } yield ()
  }

  def handleCommand(cmdArray: Array[String]): IO[Unit] = IO {
    val cmdParameters = cmdArray.tail
    cmdArray.head match {
      case "version" =>
        new VersionCommand().execute(peer, cmdParameters)

      case "account" =>
        new AccountCommand().execute(peer, cmdParameters)

      case "getbalance" =>
        new GetBalanceCommand().execute(peer, cmdParameters)

      case "send" =>
        new SendCommand().execute(peer, cmdParameters)

      case "printblockchain" =>
        new PrintBlockchainCommand().execute(peer, cmdParameters)

      case "consensus" =>
        new ConcensusCommand().server()

      case "getmessage" =>
        new ConcensusCommand().getClient(cmdParameters)

      case "putmessage" =>
        new ConcensusCommand().putClient(cmdParameters)

      case "exit" | "quit" | "bye" =>
        new ExitCommand().execute(peer, cmdParameters)

      case "help" | _ =>
        new HelpCommand().execute(peer, cmdParameters)
    }
  }
}