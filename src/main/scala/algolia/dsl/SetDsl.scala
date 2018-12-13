package algolia.dsl

import algolia.definitions.SetPersonalizationStrategyDefinition
import algolia.inputs._
import org.json4s.Formats

trait SetDsl {
  implicit val formats: Formats

  case object set {

    def personalizationStrategy(s: Strategy): SetPersonalizationStrategyDefinition =
      SetPersonalizationStrategyDefinition(s)

  }
}
