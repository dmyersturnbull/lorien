package kokellab.lorien.simple

import breeze.linalg._
import breeze.numerics.abs
import com.sksamuel.scrimage.Image
import kokellab.lorien.core.RichImages
import kokellab.lorien.core.RichImages.RichImage
import kokellab.lorien.core.FreeVectorFeature

class OriginalMiFeature extends FreeVectorFeature[Int] {

	override def name: String = "Motion Index"

	override def abbreviation: String = "MI"

	override def description: String = "Original definition of motion index; sums the difference in pixel intensities over each well for consecutive frames. MI at frame 0 is defined as 0."

	def apply(input: Iterable[RichImage]): DenseVector[Int] = {
		val riches = input.map(_.rgbMeans)
		val iter: Iterator[Int] = riches.sliding(2, 1) map (f => sum(abs(f.head - f.last)))
		DenseVector(iter.toArray)
	}
}
