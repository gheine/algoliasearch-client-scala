package algolia.dsl

import algolia.definitions.InsightsEventDefinition
import algolia.inputs._
import org.json4s.Formats

trait SendDsl {
  implicit val formats: Formats

  case object send {

    def event(e: InsightsEvent): InsightsEventDefinition = InsightsEventDefinition(Seq(e))
    def events(e: Iterable[InsightsEvent]): InsightsEventDefinition = InsightsEventDefinition(e)

  }
}
