package kokellab.lorien.simple

import breeze.linalg._
import breeze.numerics.abs
import com.sksamuel.scrimage.Image
import kokellab.lorien.core.RichImage.RichGrayscaleU8Image
import kokellab.lorien.core.FreeVectorFeature

class OriginalMiFeature extends FreeVectorFeature[Int] {

	override def name: String = "Motion Index"

	override def abbreviation: String = "MI"

	override def description: String = "Original definition of motion index; sums the difference in pixel intensities over each well for consecutive frames. MI at frame 0 is defined as 0."

	def apply(input: Iterable[Image]): DenseVector[Int] = {
		val riches = input.map(x => new RichGrayscaleU8Image(x).toGrayscale)
		val iter: Iterator[Int] = riches.sliding(2, 1) map (f => sum(abs(f.head - f.last)))
		DenseVector(iter.toArray)
	}
}
