package kokellab.lorien.core2

import kokellab.lorien.core.Roi
import kokellab.lorien.core2.RichMatrices.RichMatrix
import kokellab.lorien.core.TraversableImplicits._

import scala.language.implicitConversions

import scala.reflect.ClassTag

/**
  * A Feature maps a time-series of matrices to an output array.
  */
sealed trait VFeature[@specialized(Byte, Short, Int, Long, Float, Double) V] {
	def name: String
	def valarId: Byte
	def apply(input: Iterator[RichMatrix]): Array[V]
//	def bytes(array: Array[V]): Array[Byte]
}


/**
 * A time-dependent feature F has a number of elements n for a video of n frames.
 * They can be broken into consecutive pieces calculated independently; that is:
 * F = [F_1, F_2, ..., F_n] = [F(0, 1), F(1, 2), ..., F(n-1, n)]
 * Each F_t is an element in T.
 */
trait VTimeFeature[@specialized(Byte, Short, Int, Long, Float, Double) V] extends VFeature[V] {

	/**
	  * Calculates a time-dependent feature in chunks, two frames at a time.
	  */
	def applyOnAll(
			video: VideoFile, rois: Traversable[Roi]
	)(implicit tag: ClassTag[V]): Map[Roi, Array[V]] = {
		val nFrames = video.nFrames
		val results: Map[Roi, Array[V]] = (rois map (roi => roi -> Array.ofDim[V](nFrames))).toMap
		val slid = video.reader() map (f => BlazingMatrix.of(f).toBreezeMatrix) sliding 2
		slid.zipWithIndex foreach { case (Seq(prevImage, nextImage), index) =>
			for (roi <- rois) {
				results(roi)(index + 1) = apply( // + 1 so that index 0 is 0
					Iterator(prevImage.crop(roi), nextImage.crop(roi))
				).toTraversable.only(
					excessError = seq => throw new AssertionError(s"The time-dependent feature ${getClass.getSimpleName} returned ${seq.size} != 1 calculated between two frames")
				)
			}
		}
		results
	}
}
