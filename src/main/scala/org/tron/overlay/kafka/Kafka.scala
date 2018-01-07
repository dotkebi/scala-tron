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
package org.tron.overlay.kafka

import java.util.Properties
import java.util.concurrent.{ExecutorService, TimeUnit}

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.tron.core.Constant._
import org.tron.overlay.Net
import org.tron.overlay.listener.ReceiveSource
import org.tron.overlay.message.{Message, Type}

import scala.collection.JavaConverters._

object Kafka {
  val KAFKA_HOST = "kafka.host"
  val KAFKA_PORT = "kafka.port"
  val EMPTY_STRING = ""
  private val EMPTY_KEY = ""
  private val DEFAULT_WORKER_NUM = 4
}

class Kafka(
   actorSystem: ActorSystem,
  val producerProperty: ProducerProperty,
  val consumerProperty: ConsumerProperty,
  var source: ReceiveSource,
  var topics: List[String]) extends Net {

  val producerProperties: Properties = getProducerProperties
  val consumerProperties: Properties = getConsumerProperties
  val producer = new KafkaProducer[String, String](producerProperties)

  buildConsumer()

  private var executors: ExecutorService = null

  private def getProducerProperties = {
    val properties = new Properties
    properties.put("bootstrap.servers", producerProperty.getBootstrapServers)
    properties.put("acks", producerProperty.getAcks)
    properties.put("retries", producerProperty.getRetries)
    properties.put("batch.size", producerProperty.getBatchSize)
    properties.put("linger.ms", producerProperty.getLingerMS)
    properties.put("buffer.memory", producerProperty.getBufferMemory)
    properties.put("key.serializer", producerProperty.getKeySerializer)
    properties.put("value.serializer", producerProperty.getValueSerializer)
    properties
  }

  private def getConsumerProperties = {
    val properties = new Properties
    properties.put("bootstrap.servers", consumerProperty.getBootstrapServers)
    properties.put("group.id", consumerProperty.getGroupID)
    properties.put("enable.auto.commit", consumerProperty.getEnableAutoCommit)
    properties.put("auto.commit.interval.ms", consumerProperty.getAutoCommitIntervalMS)
    properties.put("session.timeout.ms", consumerProperty.getSessionTimeoutMS)
    properties.put("key.deserializer", consumerProperty.getKeyDeserializer)
    properties.put("value.deserializer", consumerProperty.getValueDeserializer)
    properties
  }

  def buildConsumer() = {

    implicit val materializer = ActorMaterializer()(actorSystem)

    val consumerSettings = ConsumerSettings[String, String](actorSystem, new StringDeserializer, new StringDeserializer)
        .withBootstrapServers(consumerProperty.getBootstrapServers)
        .withGroupId(consumerProperty.getGroupID)
      .withProperties(getConsumerProperties.asScala.toMap)

    val topicsSubscription = Subscriptions.topics(topics.toSet)
    Consumer.plainSource[String, String](consumerSettings, topicsSubscription)
      .runForeach { msg =>
        println("got message", msg)
        executors.submit(new ConsumerWorker(msg, this))
      }
  }

  private def send(topic: String, partition: Integer, key: String, value: String) = {
    producer.send(new ProducerRecord[String, String](topic, partition, key, value))
  }

  def shutdown(): Unit = { // TODO: must solve thread unsafe program
    if (executors != null)
      executors.shutdown()

    try {
      if (!executors.awaitTermination(10, TimeUnit.SECONDS)) System.out.println("timeout, ignore for this case")
    }
    catch {
      case e: InterruptedException =>
        System.out.println("other thread interrupted this shutdown, ignore for this case")
        Thread.currentThread.interrupt()
    }
  }

  override def broadcast(message: Message): Unit = {
    var topic = ""
    val value = message.getMessage
    if (message.getType eq Type.BLOCK) topic = TOPIC_BLOCK
    else if (message.getType eq Type.TRANSACTION) topic = TOPIC_TRANSACTION
    send(topic, PARTITION, Kafka.EMPTY_KEY, value)
  }

  override def deliver(messages: Message): Unit = {
    source.notifyReceiveEvent(messages)
  }
}