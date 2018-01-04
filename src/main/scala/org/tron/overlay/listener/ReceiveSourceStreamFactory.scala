package org.tron.overlay.listener

import akka.NotUsed
import akka.stream.scaladsl.Source
import org.tron.overlay.message.Message

object ReceiveSourceStreamFactory {

  /**
    * Build akka stream which listens to events of the receive source
    */
  def build(source: ReceiveSource): Source[Message, Unit] = {
    val bufferSize = 100
    val overflowStrategy = akka.stream.OverflowStrategy.backpressure
    Source.actorRef[Message](bufferSize, overflowStrategy)
        .mapMaterializedValue { actorRef =>
          source.addReceiveListener(message => {
            actorRef ! message
          })
        }
  }

}
