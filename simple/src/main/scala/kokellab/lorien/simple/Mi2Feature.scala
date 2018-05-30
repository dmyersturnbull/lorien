package kokellab.lorien.simple

import kokellab.lorien.core.RichMatrices.RichMatrix
import kokellab.lorien.core.{RoiUtils, SimplePlateInfo, VTimeFeature}

class Mi2Feature extends VTimeFeature[Short] {

	override def name: String = "MI2"

	override def valarId: Byte = 2

	override def apply(input: Iterator[RichMatrix]): Array[Short] = {
		(input sliding 2) map {slid =>
			val prev = slid.head.normalize(0.01)
			val next = slid.last.normalize(0.01)
			((next-prev) #:> 15).sum.toShort
		}
	}.toArray

}