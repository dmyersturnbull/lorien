package kokellab.lorien.simple

import breeze.linalg._
import breeze.numerics.abs
import kokellab.lorien.core.RichImages.RichImage
import kokellab.lorien.core.TimeVectorFeature

/**
 * The original definition of Motion Index by Dave Kokel.
 */
class OriginalMiFeature extends TimeVectorFeature[Float] {

	override def name: String = "Motion Index"

	override def abbreviation: String = "MI"

	override def description: String = "Original definition of motion index; sums the difference in pixel intensities over each well for consecutive frames. MI at frame 0 is defined as 0."

	override def newEmpty(): Iterator[Float] = Seq.empty[Float].iterator

	def apply(input: Iterator[RichImage]): Iterator[Float] = {
		val riches = input.map(_.rgbMeans)
		riches.sliding(2, 1) map (f => sum(abs(f.head - f.last))) map (_.toFloat)
	}
}
