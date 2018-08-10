package kokellab.lorien.simple

import kokellab.lorien.core.RichMatrices.RichMatrix
import kokellab.lorien.core.VTimeFeature


class MiFeature extends VTimeFeature[Float] {

	override val name: String = "MI"

	override def apply(input: Iterator[RichMatrix]): Array[Float] = {
		(input sliding 2) map {slid =>
			val prev = slid.head
			val next = slid.last
			(next |-| prev).sum.toFloat
		}
	}.toArray

}
