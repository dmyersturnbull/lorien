package kokellab.lorien.simple

import breeze.linalg._
import kokellab.lorien.core.RichMatrices.RichMatrix
import kokellab.lorien.core.{RoiUtils, SimplePlateInfo, VTimeFeature}

class Mi2Feature(tau: Int) extends VTimeFeature[Float] {

	override val name: String = s"MI2($tau)"

	override def apply(input: Iterator[RichMatrix]): Array[Float] = {
		(input sliding 2) map {slid =>
			((slid.last.matrix |-| slid.head.matrix) |< tau).toFloat
		}
	}.toArray

}
