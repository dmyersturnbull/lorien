package kokellab.lorien.simple

import breeze.linalg.{DenseMatrix, DenseVector, Tensor, _}
import kokellab.lorien.core.{GrayscaleU8, GrayscaleTimeDependentVectorFeatureU8}

import Specializable._

class OriginalMiFeature extends GrayscaleTimeDependentVectorFeatureU8 {

	override def name: String = "Motion Index"

	override def abbreviation: String = "MI"

	override def description: String = "Original definition of motion index; sums the difference in pixel intensities over each well for consecutive frames. MI at frame 0 is defined as 0."

	def calculate(input: Iterable[DenseMatrix[GrayscaleU8]]): DenseVector[Float] = {
		// Breeze sum isn't clever enough to infer the type Float
		def sumDiff(a: DenseMatrix[GrayscaleU8], b: DenseMatrix[GrayscaleU8]): Float = sum {
			(a.map(_.head.toFloat) :- b.map(_.head.toFloat)) map math.abs
		}
		val iter: Iterator[Float] = input.sliding(2, 1) map (f => sumDiff(f.head, f.last))
		DenseVector(iter.toArray)
	}
}
