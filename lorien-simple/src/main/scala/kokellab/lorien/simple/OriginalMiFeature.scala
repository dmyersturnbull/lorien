package kokellab.lorien.simple

import breeze.linalg._
import breeze.numerics.abs
import scala.reflect.runtime.universe._
import kokellab.lorien.core.RichMatrices.RichMatrix
import kokellab.lorien.core.RichMatrices.richMatrixToMatrix
import scala.language.implicitConversions
import kokellab.lorien.core.{RoiUtils, SimplePlateInfo, TimeVectorFeature}

/**
 * The original definition of Motion Index by Dave Kokel.
 */
class OriginalMiFeature extends TimeVectorFeature[Float] {

	override def name: String = "Motion Index"

	override def abbreviation: String = "MI"

	override def description: String = "Original definition of motion index; sums the difference in pixel intensities over each well for consecutive frames. MI at frame 0 is defined as 0."

	override def valarFeatureId: Byte = 1

	def apply(input: Iterator[RichMatrix]): Iterator[Float] = {
		input map (_.matrix) sliding (2, 1) map (f => sum(abs(f.head - f.last))) map (_.toFloat)
	}
}


object OriginalMiFeature {
	def main(args: Array[String]): Unit = {
		new OriginalMiFeature().applyOnAll(SimplePlateInfo.fetch(1).run, RoiUtils.manual(1))
	}
}
