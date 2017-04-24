package kokellab.lorien.core

import Specializable._
import breeze.linalg._

/**
  * A Feature maps a time-series of bitmap matrices to an output tensor of type V.
  */
trait Feature[D, C <: ColorValue[D], @specialized(Byte, Int, Float, Double) V] {

	def name: String

	def abbreviation: String = name

	def description: String

	def tensorDef: TensorDef

	def calculate(input: DenseVector[DenseMatrix[C]]): Tensor[Int, V]

}

trait GrayscaleU8FloatFeature extends Feature[Byte, GrayscaleU8, Float]

trait GrayscaleTimeDependentVectorFeatureU8 extends GrayscaleU8FloatFeature{
	override def tensorDef: TensorDef = TensorDef.timeDependentVector
}

trait GrayscaleTimeIndependentVectorFeatureU8 extends GrayscaleU8FloatFeature {
	override def tensorDef: TensorDef = TensorDef.freeVector
}
