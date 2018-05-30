package simple2

import kokellab.lorien.core2.RichMatrices.RichMatrix
import kokellab.lorien.core2.VTimeFeature

class MiFeature extends VTimeFeature[Float] {

	override def name: String = "MI"

	override def valarId: Byte = 1

	override def apply(input: Iterator[RichMatrix]): Array[Float] = {
		(input sliding 2) map {slid =>
			val prev = slid.head
			val next = slid.last
			(next-prev).sum.toFloat
		}
	}.toArray

}
