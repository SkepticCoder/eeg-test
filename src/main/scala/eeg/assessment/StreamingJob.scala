package eeg.assessment

import eeg.assessment.model.Message
import eeg.assessment.serde.MessageSerDe
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer, KafkaDeserializationSchema}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG

import java.util.Properties

object StreamingJob {

  private[StreamingJob] val CONFIG_PATH = "app.properties"
  private[StreamingJob] val RESOURCE_PATH: String = "/" + CONFIG_PATH
  private[StreamingJob] val BOOTSTRAP_SERVER_DEFAULT = "localhost:9092"
  private[StreamingJob] val DEFAULT_TOPIC_OUTPUT = "test"

  def main(args: Array[String]): Unit = {
    val params = ParameterTool.fromArgs(args)
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.getConfig.setGlobalJobParameters(params)

    val serde = new MessageSerDe()
    val kafkaConsumer = KafkaSource.builder()
      .setBootstrapServers(params.get(BOOTSTRAP_SERVERS_CONFIG))
      .setGroupId(params.get(ConsumerConfig.GROUP_ID_CONFIG))
      .setTopics(params.get("source-topic"))
      .setValueOnlyDeserializer(serde)
      .build()

    val producerProps = new Properties()

    val producer = new FlinkKafkaProducer[Message](params.get("dest-topic"), serde,
      producerProps)

    env.fromSource(kafkaConsumer, WatermarkStrategy.noWatermarks[Message], "Kafka Source")
      .addSink(producer).name("Kafka sink")

  }

  private[StreamingJob] def constructParameters(args: Array[String]) = {
    val parameters = ParameterTool.fromArgs(args)
    scala.reflect.io.File("app.properties").exists match {
      case true => parameters.mergeWith(ParameterTool.fromPropertiesFile(CONFIG_PATH))
      case _ => parameters.mergeWith(ParameterTool.fromPropertiesFile(getClass.getResourceAsStream("/" + CONFIG_PATH)))
    }
  }
}
