package kokellab.lorien.core

import breeze.linalg._

/**
  * A Feature maps a time-series of bitmap matrices to an output tensor of floats.
  */
trait Feature[C] extends Tensor[DenseVector[DenseMatrix[ColorValue[C]]], Tensor[Float, Float]] {

	def name: String

	def description: String

	def tensor: TensorDef

}
