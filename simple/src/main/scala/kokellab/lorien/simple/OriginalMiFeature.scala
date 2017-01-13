package kokellab.lorien.simple

import breeze.linalg.{DenseMatrix, DenseVector, Tensor, _}
import kokellab.lorien.core.Grayscale

import Specializable._

class OriginalMiFeature extends GrayscaleTimeDependentVectorFeature {

	override def name: String = "Motion Index"

	override def abbreviation: String = "MI"

	override def description: String = "Original definition of motion index; sums the difference in pixel intensities over each well for consecutive frames. MI at frame 0 is defined as 0."

	def calculate (input: DenseVector[DenseMatrix[Grayscale]] ): Tensor[Int, Float] = {
		// Breeze sum isn't clever enough to infer the type Float
		val iter: Iterator[Float] = input.valuesIterator.sliding(2, 2) map (f => sum(f.tail - f.head))
		DenseVector(iter.toArray)
	}
}
