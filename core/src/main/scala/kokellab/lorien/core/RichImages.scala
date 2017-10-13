package kokellab.lorien.core

import java.nio.file.{Files, Path, Paths}

import scala.language.implicitConversions
import breeze.linalg.DenseMatrix
import com.sksamuel.scrimage.{Image, Pixel}
import breeze.linalg._
import com.sksamuel.scrimage.composite.AverageComposite
import com.typesafe.scalalogging.LazyLogging
import kokellab.utils.core.blobToBytes
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
				roi.x0 to roi.x0 + min(roi.width, matrix.cols),
				roi.y0 to roi.y0 + min(roi.height, matrix.rows)
			))
		}
		def crop(roi: RoisRow): RichMatrix = crop(Roi.of(roi))
	}
}

object RichImages extends LazyLogging {

	private implicit val db = loadDb()
	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	implicit def richImageToImage(richImage: RichImage): Image = richImage.image

	implicit class RichImage(val image: Image) {

		def transpose: Image = image.rotateRight.flipX

		def crop(roi: Roi): RichImage = {
			if (roi.width > image.width)
				logger.warn(s"Width ${roi.width} for ROI $roi extends outside of image bounds (${image.dimensions})")
			if (roi.height > image.height)
				logger.warn(s"Height ${roi.height} for ROI $roi extends outside of image bounds (${image.dimensions})")
			val boundedXyWidthHeight = (roi.x0, roi.y0, min(image.width, roi.width), min(image.height, roi.height))
			RichImage((image.subimage _).tupled(boundedXyWidthHeight))
		}

		def crop(roi: RoisRow): RichImage = crop(Roi.of(roi))

		def toPixels: Seq[Seq[Pixel]] = {
			(0 until image.width) map {x =>
				(0 until image.height) map {y =>
					image.pixel(x, y)
				}
			}
		}
		def to[A](converter: Pixel => A): Seq[Seq[A]] = {
			toPixels map (seq => seq map converter)
		}
		def rgbMeans: DenseMatrix[Int] = DenseMatrix(to(p => (p.red + p.green + p.blue) / 3):_*)
		def reds: DenseMatrix[Int] = DenseMatrix(to(_.red):_*)
		def greens: DenseMatrix[Int] = DenseMatrix(to(_.green):_*)
		def blues: DenseMatrix[Int] = DenseMatrix(to(_.blue):_*)
		def argbs: DenseMatrix[Int] = DenseMatrix(to(_.argb):_*)
		def rgbs: DenseMatrix[Int] = DenseMatrix(to(p => p.argb & 0x00FFFFFF):_*)
		def alphas: DenseMatrix[Int] = DenseMatrix(to(_.alpha):_*)
		def grays: DenseMatrix[Int] =  DenseMatrix(to(_.toColor.toGreyscale.grey):_*)

		def write(path: String): Unit = {
			Files.write(Paths.get(path), image.bytes)
		}
		def write(path: Path): Unit = {
			Files.write(path, image.bytes)
		}

	}

	def of(path: Path): RichImage = RichImage(Image(Files.readAllBytes(path)))
	def of(bytes: Array[Byte]): RichImage = RichImage(Image(bytes))

	def apply(image: Image) = RichImage(image)

}
