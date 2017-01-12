package kokellab.lorien.simple

import breeze.linalg._
import kokellab.lorien.core.{Grayscale, GrayscaleTimeDependentVectorFeature}

class OriginalMiFeature extends GrayscaleTimeDependentVectorFeature {

	override def name: String = "Motion Index"

	override def abbreviation: String = "MI"

	override def description: String = "Original definition of motion index; sums the difference in pixel intensities over each well for consecutive frames. MI at frame 0 is defined as 0."

	override def calculate(input: DenseVector[DenseMatrix[Grayscale]]): Tensor[Int, Float] =
		input map (frame => sum(frame))
}
