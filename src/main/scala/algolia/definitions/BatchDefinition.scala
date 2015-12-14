/*
 * Copyright (c) 2015 Algolia
 * http://www.algolia.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package algolia.definitions

import algolia.http.{HttpPayload, POST}
import algolia.inputs._
import algolia.responses.TasksMultipleIndex
import algolia.{AlgoliaClient, Executable}
import org.json4s.Formats
import org.json4s.native.Serialization._

import scala.concurrent.{ExecutionContext, Future}

case class BatchDefinition(definitions: Iterable[Definition])(implicit val formats: Formats) extends Definition with BatchOperationUtils {

  override private[algolia] def build(): HttpPayload = {
    val operations = definitions.map {
      case IndexingDefinition(index, None, Some(obj)) =>
        hasObjectId(obj) match {
          case (true, o) => UpdateObjectOperation(o, Some(index))
          case (false, o) => AddObjectOperation(o, Some(index))
        }

      case IndexingDefinition(index, Some(objectId), Some(obj)) =>
        UpdateObjectOperation(addObjectId(obj, objectId), Some(index))

      case ClearIndexDefinition(index) =>
        ClearIndexOperation(index)

      case DeleteObjectDefinition(Some(index), Some(oid)) =>
        DeleteObjectOperation(index, oid)

//      case IndexingBatchDefinition(index, defs) =>
//        defs.map {
//          case IndexingDefinition(_, None, Some(obj)) =>
//            hasObjectId(obj) match {
//              case (true, o) => UpdateObjectOperation(o)
//              case (false, o) => AddObjectOperation(o)
//            }
//
//          case IndexingDefinition(_, Some(objectId), Some(obj)) =>
//            UpdateObjectOperation(addObjectId(obj, objectId))
//        }

    }.toSeq

    HttpPayload(
      POST,
      Seq("1", "indexes", "*", "batch"),
      body = Some(write(BatchOperations(operations))),
      isSearch = false
    )
  }
}


trait BatchDefinitionDsl {

  implicit val formats: Formats

  def batch(batches: Iterable[Definition]): BatchDefinition = {
    BatchDefinition(batches)
  }

  def batch(batches: Definition*): BatchDefinition = {
    BatchDefinition(batches)
  }

  implicit object BatchDefinitionExecutable extends Executable[BatchDefinition, TasksMultipleIndex] {
    override def apply(client: AlgoliaClient, query: BatchDefinition)(implicit executor: ExecutionContext): Future[TasksMultipleIndex] = {
      client request[TasksMultipleIndex] query.build()
    }
  }

}
