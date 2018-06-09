package kokellab.lorien.simple

import breeze.linalg._
import kokellab.lorien.core.RichMatrices.RichMatrix
import kokellab.lorien.core.{RoiUtils, SimplePlateInfo, VTimeFeature}

class Mi2Feature extends VTimeFeature[Float] {

	override def name: String = "MI2"

	override def valarId: Byte = 2

	override def apply(input: Iterator[RichMatrix]): Array[Float] = {
		(input sliding 2) map {slid =>
			((slid.last-slid.head) #:> 15).toFloat
		}
	}.toArray

}
