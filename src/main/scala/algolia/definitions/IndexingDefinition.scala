package algolia.definitions

import algolia.http.HttpPayload
import algolia.{Index, _}
import org.json4s.native.Serialization.write

case class IndexingDefinition(index: Index,
                              objectId: Option[String] = None,
                              obj: Option[AnyRef] = None,
                              objects: Option[Seq[AnyRef]] = None,
                              objectsWithIds: Option[Map[String, AnyRef]] = None) extends Definition {


  def objectId(objectId: String): IndexingDefinition =
    copy(index, objectId = Some(objectId), obj = obj)

  def document(objectId: String, obj: AnyRef): IndexingDefinition =
    copy(index, objectId = Some(objectId), obj = Some(obj))

  def document(obj: AnyRef): IndexingDefinition =
    copy(index, objectId = objectId, obj = Some(obj))

  def documents(objects: Seq[AnyRef]): IndexingDefinition = ???

  //    copy(index, objects = Some(objects))

  def documents(objectsWithIds: Map[String, AnyRef]): IndexingDefinition = ???

  //    copy(index, objectsWithIds = Some(objectsWithIds))

  implicit val formats = org.json4s.DefaultFormats

  override private[algolia] def build(): HttpPayload = {
    val body: Option[String] = obj.map(o => write(o))
    val verb = objectId match {
      case Some(_) => http.PUT
      case None => http.POST
    }

    HttpPayload(verb, Seq("1", "indexes", index.name) ++ objectId, body = body)
  }
}