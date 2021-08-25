package eeg.assessment.model

import spray.json.{DefaultJsonProtocol, JsNumber, JsValue, JsonFormat}

import java.sql.Timestamp
import java.time.Instant


case class Message(messageCreationDate: Timestamp, loginId: Long, customerId: Long, loginName: String)

object MessageRich extends DefaultJsonProtocol {

  implicit val timestampFormat: JsonFormat[Timestamp] = new JsonFormat[Timestamp] {
    override def write(obj: Timestamp): JsValue = JsNumber(obj.getTime)

    override def read(json: JsValue): Timestamp = json match {
      case JsNumber(x) => Timestamp.from(Instant.ofEpochMilli(x.toLong))
      case _ =>
        throw new IllegalArgumentException(
          s"Can not parse json value [$json] to a timestamp object")
    }
  }

  implicit val messageJsonFormat = jsonFormat4(Message)
}
