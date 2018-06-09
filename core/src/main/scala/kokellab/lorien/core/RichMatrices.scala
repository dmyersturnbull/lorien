package kokellab.lorien.core

import scala.language.implicitConversions
import breeze.math._
import breeze.numerics._
import breeze.linalg.DenseMatrix
import breeze.linalg._
import breeze.numerics.abs
import breeze.linalg.{sum => breezesum}
import breeze.stats.DescriptiveStats
import com.typesafe.scalalogging.LazyLogging
import kokellab.lorien.core.Roi
import kokellab.valar.core.loadDb

object RichMatrices extends LazyLogging {

	private implicit val db = loadDb()
	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	implicit def richMatrixToMatrix(richMatrix: RichMatrix): Matrix[Int] = richMatrix.matrix

	implicit class RichMatrix(val matrix: DenseMatrix[Int]) {

		def crop(roi: Roi): RichMatrix = {
			if (roi.width > matrix.cols)
				logger.warn(s"Width ${roi.width} for ROI $roi extends outside of image bounds (${matrix.cols} x ${matrix.rows})")
			if (roi.height > matrix.rows)
				logger.warn(s"Height ${roi.height} for ROI $roi extends outside of image bounds (${matrix.cols} x ${matrix.rows})")
			RichMatrix(matrix(
				roi.y0 to roi.y0 + min(roi.height, matrix.rows),
				roi.x0 to roi.x0 + min(roi.width, matrix.cols)
			))
		}
		def normalize(q: Double): RichMatrix = {
			val sub: DenseMatrix[Int] = matrix - quantile(q).toInt
			RichMatrix(sub * 255/(quantile(1-q)-quantile(q)).toInt)
		}
		def quantile(q: Double): Double = DescriptiveStats.percentile(matrix.data map (_.toDouble), q)
		def sum: Int = breezesum(matrix)
		def mean: Double = breezesum(matrix) / (matrix.rows*matrix.cols)
		def +(o: RichMatrix): RichMatrix = RichMatrix(matrix - o.matrix)
		def +(o: Int): RichMatrix = RichMatrix(matrix + o)
		def -(o: Int): RichMatrix = RichMatrix(matrix - o)
		def -(o: RichMatrix): RichMatrix = RichMatrix(matrix - o.matrix)
		def crop(roi: RoisRow, wellId: Int): RichMatrix = crop(Roi.of(roi, wellId))
		def |-|(o: RichMatrix): RichMatrix = abs(matrix - o.matrix)
		def |-|(o: Int): RichMatrix = abs(matrix - o)
		def #:<(o: Int): Int = I(matrix <:< DenseMatrix.fill(matrix.rows, matrix.cols)(o)).sum.toInt
		def #:>(o: Int): Int = I(matrix >:> DenseMatrix.fill(matrix.rows, matrix.cols)(o)).sum.toInt
		/*
		def <>(o: RichMatrix): RichMatrix = signum(matrix - o.matrix)
		def <>(o: Int): RichMatrix = signum(matrix - o)
		def #:<(o: RichMatrix): RichMatrix = RichMatrix(I(matrix <:< o.matrix))
		def #:<(o: Int): RichMatrix = RichMatrix((matrix <:< o) map (_ compareTo false))
		def #:>(o: RichMatrix): RichMatrix = RichMatrix((matrix >:> o.matrix) map (_ compareTo false))
		def #:>(o: Int): RichMatrix = RichMatrix((matrix >:> o) map (_ compareTo false))
		def #:<=(o: RichMatrix): RichMatrix = RichMatrix((matrix <:= o.matrix) map (_ compareTo false))
		def #:<=(o: Int): RichMatrix = RichMatrix((matrix <:= o) map (_ compareTo false))
		def #:>=(o: RichMatrix): RichMatrix = RichMatrix((matrix >:= o.matrix) map (_ compareTo false))
		def #:>=(o: Int): RichMatrix = RichMatrix((matrix >:= o) map (_ compareTo false))
		*/
	}
}
