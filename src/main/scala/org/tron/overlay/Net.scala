package org.tron.overlay

import org.tron.overlay.message.Message

trait Net {
  def broadcast(message: Message): Unit
  def deliver(message: Message): Unit
}
