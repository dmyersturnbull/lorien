package kokellab.lorien.core

import java.io.IOException
import java.nio.file.{Files, Path, Paths}
import java.awt.image.BufferedImage

import breeze.linalg._
import kokellab.lorien.core.RichImages.RichImage

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}
import kokellab.valar.core.ImageStore
import kokellab.valar.core.Tables.{PlateRunsRow, RoisRow}
import kokellab.valar.core.{exec, loadDb}

/**
  * A Feature maps a time-series of bitmap images to an output tensor of type T over a field V.
  */
sealed trait Feature[@specialized(Byte, Int, Float, Double) V, T] {

	private implicit val db = loadDb()

	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	def name: String

	def abbreviation: String = name

	def description: String

	def tensorDef: TensorDef

	/**
	 * Constructs a new, empty feature vector.
	 * <strong>This is not required to be implemented.</strong>
 	 */
	def newEmpty(length: Int): T = ???

	/**
	 * Constructs a new feature vector.
	 * <strong>This is not required to be implemented.</strong>
	*/
	def newEmpty(): T = ???

	def apply(input: Iterator[RichImage]): T

	def apply(plateRun: PlateRunsRow, roi: RoisRow): T = apply {
		ImageStore.walk(plateRun) map (frame => RichImages.of(frame)) map (_.crop(roi))
	}
}

/**
 * A time-dependent feature F has a number of elements n for a video of n frames.
 * They can be broken into consecutive pieces calculated independently; that is:
 * F = [F_1, F_2, ..., F_n] = [F(0, 1), F(1, 2), ..., F(n-1, n)]
 * Each F_t is an element in T.
 */
trait TimeDependentFeature[@specialized(Byte, Int, Float, Double) V, T] extends Feature[V, Iterator[T]] {

	private implicit val db = loadDb()

	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	/**
	 * Calculates a time-dependent feature in chunks, two frames at a time.
	 */
	def applyAll(plateRun: PlateRunsRow, rois: Traversable[RoisRow]): Map[RoisRow, Seq[T]] = {
		val length = ImageStore.walk(plateRun).size
		val results = collection.mutable.Map.empty[RoisRow, Seq[T]]
		for (roi <- rois) {
			results += roi -> newEmpty().toSeq
		}
		var prevFrame: RichImage = null
		ImageStore.walk(plateRun).toList foreach {frame =>
			val image = Try(RichImages.of(frame)) match {
				case Success(img) => img
				case Failure(e: IOException) => throw new CalculationFailedException(Some(plateRun), None, s"Failed to read image $frame for plate_run ${plateRun.id}", e)
				case Failure(e) => throw e
			}
			if (prevFrame != null) {for (roi <- rois) Try {
                          	val first = prevFrame.crop(roi)
                                val second = image.crop(roi)
				val wtf: Seq[T] = apply(List(prevFrame.crop(roi), image.crop(roi)).iterator).toList
				results(roi) = results(roi) ++ wtf
			} match {
				case Success(partial) => partial
				case Failure(e) => throw new CalculationFailedException(Some(plateRun), Some(roi), "Calculation of time-dependent feature failed for plate_run ${plateRun.id} and ROI ${roi.id}", e)
			}}
			prevFrame = image
		}
		results.toMap
	}
}

/**
 * A time-dependent feature vector over V of length n for a video of n frames.
 * Like all time-dependent features, these can be calculated in pieces.
 */
trait TimeVectorFeature[@specialized(Byte, Int, Float, Double) V] extends Feature[V, Iterator[V]] with TimeDependentFeature[V, V] {
	override def tensorDef: TensorDef = TensorDef.timeDependentVector
}

/**
 * A feature vector over V whose length is independent of the number of frames. The dimension may or may not be finite.
 */
trait FreeVectorFeature[@specialized(Byte, Int, Float, Double) V] extends Feature[V, Iterator[V]] {
	override def tensorDef: TensorDef = TensorDef.freeVector
}

/**
 * A feature matrix over V whose length is independent of the number of frames. Either dimension may or may not be finite.
 */
trait FreeMatrixFeature[@specialized(Byte, Int, Float, Double) V] extends Feature[V, Iterator[Iterator[V]]] {
	override def tensorDef: TensorDef = TensorDef.freeMatrix
}

/**
 * A time-independent feature of matricies with elements in V defined for a fixed dimension (n, m).
 * This is a concrete trait that requires each matrix to fit in memory.
 * Note that this is not a subclass of FreeMatrixFeature.
 */
trait FiniteMatrixFeature[@specialized(Byte, Int, Float, Double) V] extends Feature[V, DenseMatrix[V]] {
   override def tensorDef: TensorDef = TensorDef.freeMatrix
}

class CalculationFailedException(plateRun: Option[PlateRunsRow], roi: Option[RoisRow], message: String = null, cause: Throwable = null) extends Exception(message)
