package kokellab.lorien.core

import Specializable._
import breeze.linalg._
import kokellab.lorien.core.RichImages.RichImage

import scala.language.implicitConversions
import kokellab.valar.core.{exec, loadDb}

/**
  * A Feature maps a time-series of bitmap matrices to an output tensor of type V.
  */
trait Feature[@specialized(Byte, Int, Float, Double) V] {

	private implicit val db = loadDb()

	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	def name: String

	def abbreviation: String = name

	def description: String

	def tensorDef: TensorDef

	def apply(input: Iterable[RichImage]): Tensor[Int, V]

	def apply(plateRun: PlateRunsRow, roi: RoisRow): Tensor[Int, V] =
		apply {
			exec((
				FrameImages filter (_.plateRunId === plateRun.id) sortBy (_.frame)
			).result) map (frame => RichImages.of(frame, roi))
		}
}

trait TimeVectorFeature[@specialized(Byte, Int, Float, Double) V] extends Feature[V] {
	override def tensorDef: TensorDef = TensorDef.timeDependentVector
}

trait FreeVectorFeature[@specialized(Byte, Int, Float, Double) V] extends Feature[V] {
	override def tensorDef: TensorDef = TensorDef.freeVector
}
