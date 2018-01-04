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

import org.tron.consensus.client.Client
import org.tron.consensus.server.Server
import org.fusesource.jansi.Ansi.ansi


class ConcensusCommand {
  
  def server() = {
    Server.serverRun()
  }

  def putClient(args: Array[String]) = {
    Client.putMessage(args)
  }

  def getClient(args: Array[String]) = {
    Client.getMessage(args(0))
  }

  def usage() = {
    println("")
    println(ansi.eraseScreen.render("@|magenta,bold USAGE|@\n\t@|bold consensus|@"))
    println("")
    println(ansi.eraseScreen.render("@|magenta,bold DESCRIPTION|@\n\t@|bold The command 'consensus' create a server.|@"))
    println("")
    println(ansi.eraseScreen.render("@|magenta,bold USAGE|@\n\t@|bold getmessage [key]|@"))
    println("")
    println(ansi.eraseScreen.render("@|magenta,bold DESCRIPTION|@\n\t@|bold The command 'getmessage' get a consensus message.|@"))
    println("")
    println(ansi.eraseScreen.render("@|magenta,bold USAGE|@\n\t@|bold putmessage [key] [value]|@"))
    println("")
    println(ansi.eraseScreen.render("@|magenta,bold DESCRIPTION|@\n\t@|bold The command 'putmessage' put a consensus message.|@"))
    println("")
  }
}
