package kokellab.lorien.core

import Specializable._
import breeze.linalg._

/**
  * A Feature maps a time-series of bitmap matrices to an output tensor of floats.
  */
trait Feature[C <: ColorValue[_], @specialized(Byte, Int, Float, Double) V] {

	def name: String

	def abbreviation: String = name

	def description: String

	def tensorDef: TensorDef

	def calculate(input: DenseVector[DenseMatrix[C]]): Tensor[Int, V]

}

trait GrayscaleTimeDependentVectorFeature extends Feature[Grayscale, Float] {
	override def tensorDef: TensorDef = TensorDef.timeDependentVector
}

trait GrayscaleTimeIndependentVectorFeature extends Feature[Grayscale, Float] {
	override def tensorDef: TensorDef = TensorDef.freeVector
}

trait GrayscaleScalarFeature extends Feature[Grayscale, Float] {
	override def tensorDef: TensorDef = TensorDef.scalar
}
