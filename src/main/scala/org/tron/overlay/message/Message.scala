package org.tron.overlay.message

case class Message(message: String, msgType: Type) {
  def getType = msgType
  def getMessage = message
}
