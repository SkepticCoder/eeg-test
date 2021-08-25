package eeg.assessment.serde

import eeg.assessment.model.Message
import eeg.assessment.model.MessageRich._
import org.apache.flink.api.common.serialization.{DeserializationSchema, SerializationSchema}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.connectors.kafka.{KafkaDeserializationSchema, KafkaSerializationSchema}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import spray.json._

import java.lang
import java.nio.charset.Charset

class MessageSerDe extends KafkaSerializationSchema[Message] with KafkaDeserializationSchema[Message]
  with SerializationSchema[Message] with DeserializationSchema[Message] {

  val charset = Charset.forName("UTF-8")

  override def serialize(element: Message): Array[Byte] = {
    element.toJson.compactPrint.getBytes(charset)
  }

  override def deserialize(message: Array[Byte]): Message = {
    new String(message, charset).parseJson.convertTo[Message]
  }

  override def isEndOfStream(nextElement: Message): Boolean = {
    false
  }

  override def getProducedType: TypeInformation[Message] = {
    TypeInformation.of(classOf[Message])
  }

  override def serialize(element: Message, timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
    new ProducerRecord[Array[Byte], Array[Byte]]("", null, serialize(element))
  }

  override def deserialize(record: ConsumerRecord[Array[Byte], Array[Byte]]): Message = {
    deserialize(record.value)
  }
}
