package kokellab.lorien.core

import Specializable._
import breeze.linalg._

/**
  * A Feature maps a time-series of bitmap matrices to an output tensor of type V.
  */
trait Feature[T, D <: BitDepth, C <: ColorValue[T, D], @specialized(Byte, Int, Float, Double) V] {

	def name: String

	def abbreviation: String = name

	def description: String

	def tensorDef: TensorDef

	def calculate(input: DenseVector[DenseMatrix[C]]): Tensor[Int, V]

}

trait GrayscaleFloatFeature[T, D <: BitDepth] extends Feature[T, D, Grayscale[T, D], Float]

trait GrayscaleTimeDependentVectorFeatureU8 extends GrayscaleFloatFeature[Byte, U8] {
	override def tensorDef: TensorDef = TensorDef.timeDependentVector
}

trait GrayscaleTimeIndependentVectorFeatureU8 extends GrayscaleFloatFeature[Byte, U8] {
	override def tensorDef: TensorDef = TensorDef.freeVector
}
