package kokellab.lorien.simple

import breeze.linalg._
import breeze.numerics.abs
import kokellab.lorien.core.RichImages.RichImage
import kokellab.lorien.core.TimeVectorFeature

class OriginalMiFeature extends TimeVectorFeature[Float] {

	override def name: String = "Motion Index"

	override def abbreviation: String = "MI"

	override def description: String = "Original definition of motion index; sums the difference in pixel intensities over each well for consecutive frames. MI at frame 0 is defined as 0."

	def apply(input: Iterator[RichImage]): DenseVector[Float] = {
		val riches = input.map(_.rgbMeans)
		val iter: Iterator[Int] = riches.sliding(2, 1) map (f => sum(abs(f.head - f.last)))
		DenseVector(iter.toArray map (_.toFloat))
	}
}
