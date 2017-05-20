package kokellab.lorien.core

import breeze.linalg._
import kokellab.lorien.core.RichImages.RichImage

import scala.language.implicitConversions
import kokellab.valar.core.{exec, loadDb}

/**
  * A Feature maps a time-series of bitmap matrices to an output tensor of type V.
  */
sealed trait Feature[@specialized(Byte, Short, Int) I, @specialized(Byte, Int, Float, Double) V, T <: Tensor[I, V]] {

	private implicit val db = loadDb()

	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	def name: String

	def abbreviation: String = name

	def description: String

	def tensorDef: TensorDef

	def apply(input: Iterator[RichImage]): T

	def apply(plateRun: PlateRunsRow, roi: RoisRow, nFramesPerPage: Int = 100): T = {
		val length: Int = exec((FrameImages filter (_.plateRunId === plateRun.id)).size.result)
		val pages: Iterator[FrameImagesRow] =
			(0 until length by nFramesPerPage).iterator flatMap { page =>
				exec((
					FrameImages filter (_.plateRunId === plateRun.id) drop page take nFramesPerPage // NOTE: NOT SORTING
				).result, waitSeconds = 60 * 5).iterator // yes, 5 minute is a long time, but we don't want a long computation to fail just because the connection is momentarily slow
			}
		apply {
			pages map (frame => RichImages.of(frame, roi))
		}
	}
}


trait TimeVectorFeature[@specialized(Byte, Int, Float, Double) V] extends Feature[Int, V, DenseVector[V]] {
	override def tensorDef: TensorDef = TensorDef.timeDependentVector
}

trait FreeVectorFeature[@specialized(Byte, Int, Float, Double) V] extends Feature[Int, V, DenseVector[V]] {
	override def tensorDef: TensorDef = TensorDef.freeVector
}

/**
  * NOTE: Because Breeze doesn't currently support 3-tensors, a time-series of matrix features should be implemented as exactly that.
  */
trait FreeMatrixFeature[@specialized(Byte, Int, Float, Double) V] extends Feature[(Int, Int), V, DenseMatrix[V]] {
	override def tensorDef: TensorDef = TensorDef.freeMatrix
}
