package kokellab.lorien.simple

import breeze.linalg._
import breeze.numerics.abs
import kokellab.lorien.core._
import kokellab.lorien.core.TimeVectorFeature
import kokellab.lorien.core.RichImages.RichImage
import kokellab.lorien.core.RichMatrices.RichMatrix


/**
  * Motion index with naive normalization by image intensity and n pixels, thresholding of difference, and (normalized) conversion to byte.
  * Note that this converts to a signed Java byte.
  * @param threshold Grayscale byte intensity required for a pixel to count
  * @param expectedMax Expected maximum total MI2 value after thresholding but before conversion to byte;
  *                    an excessively large value will cause a narrow range (0–50, etc.), and an excessively small value will result in a lot of 0s and 255s.
  */
class NormalizedMiFeature(val threshold: Int = 5, val expectedMax: Double = 40) extends TimeVectorFeature[Byte] {

	override def name: String = "Normalized Motion Index"

	override def abbreviation: String = "MI2"

	override def description: String = "Normalizes by brightness, thresholds, and takes a mean."

	override def valarFeatureId: Byte = 2

	private var minV = 0.0

	private var maxV = 0.0

	def apply(input: Iterator[RichMatrix]): Iterator[Byte] = {
		input map (_.matrix) sliding (2, 1) map {f => {
			val summed: Double = sum(threshold(normAbsDiff(f.last, f.head))).toDouble
			val s1: Double = summed / (f.last.cols * f.last.rows)
			minV = max(s1, minV)
			maxV = min(s1, maxV)
			val r = math.round(((min(255.0, s1) - 128.0) * (127.0 + 128.0) / expectedMax) - 128.0)
			if (r > 255) 127 else r - 128
		}
		} map (d => d.toByte)
	}

	/**
	  * @return The minimum and maximum values <em>before converting to a byte</em> seen over this Feature's lifespan.
	  *         Used only as a diagnostic.
	  */
	def rangeSeen: (Double, Double) = (minV, maxV)

	private def threshold(M: DenseMatrix[Int]): DenseMatrix[Int] =
		M map (m => indicate(m > threshold) * m)

	private def normAbsDiff(first: DenseMatrix[Int], second: DenseMatrix[Int]): DenseMatrix[Int] = {
		abs(normalize(second) - normalize(first))
	}

	private def normalize(I: DenseMatrix[Int]): DenseMatrix[Int] = {
		val minI = min(I)
		val maxI = max(I)
		255 * (I - minI) / (maxI - minI) + minI
	}

}

object NormalizedMiFeature {
	def main(args: Array[String]): Unit = {

	}
}