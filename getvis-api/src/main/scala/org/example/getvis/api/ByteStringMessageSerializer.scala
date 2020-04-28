package org.example.getvis.api


  import akka.util.ByteString
  import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer.{NegotiatedDeserializer, NegotiatedSerializer}
  import com.lightbend.lagom.scaladsl.api.deser.{MessageSerializer, StrictMessageSerializer}
  import com.lightbend.lagom.scaladsl.api.transport.MessageProtocol

  import scala.collection.immutable

  class ByteStringMessageSerializer extends StrictMessageSerializer[ByteString] {

    private final val serialiser: NegotiatedSerializer[ByteString, ByteString] =
      new NegotiatedSerializer[ByteString, ByteString] {


        override def protocol: MessageProtocol = MessageProtocol(Some("application/pdf"))

        override def serialize(data: ByteString): ByteString = {
          data
        }
      }

    private final val deserialiser = new NegotiatedDeserializer[ByteString, ByteString] {
      override def deserialize(wire: ByteString): ByteString = wire
    }

    override def serializerForRequest: MessageSerializer.NegotiatedSerializer[ByteString, ByteString] = serialiser

    override def deserializer(protocol: MessageProtocol): MessageSerializer.NegotiatedDeserializer[ByteString, ByteString] = deserialiser

    override def serializerForResponse(acceptedMessageProtocols: immutable.Seq[MessageProtocol]): MessageSerializer.NegotiatedSerializer[ByteString, ByteString] = serialiser
  }



