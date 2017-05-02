package kokellab.lorien.core

import java.nio.file.{Files, Path, Paths}

import scala.language.implicitConversions
import breeze.linalg.DenseMatrix
import com.sksamuel.scrimage.{Image, Pixel}
import breeze.linalg._
import kokellab.utils.core.blobToBytes
import kokellab.valar.core.loadDb

object RichImage {

	private implicit val db = loadDb()

	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	implicit class RichImage(val image: Image) {

		def transpose: Image = image.rotateRight.flipX

		def crop(roi: Roi): Image =
			(image.subimage _).tupled(roi.xyWidthHeight)

		def crop(roi: RoisRow): Image = crop(Roi.of(roi))

		def toPixels: Seq[Seq[Pixel]] = {
			(0 until image.width) map {x =>
				(0 until image.height) map {y =>
					image.pixel(x, y)
				}
			}
		}

		def write(path: String): Unit = {
			Files.write(Paths.get(path), image.bytes)
		}
		def write(path: Path): Unit = {
			Files.write(path, image.bytes)
		}

	}

	implicit class RichGrayscaleU8Image(image: Image) extends RichImage(image) {
		def toGrayscale: DenseMatrix[Int] = {
			val seq = toPixels map (_ map (_.toColor.toGreyscale.grey))
			DenseMatrix(seq:_*)
		}
		def toGrayscaleU8: DenseMatrix[GrayscaleU8] = {
			toGrayscale.map(x => new GrayscaleU8(x.toByte))
		}
	}

	def of(path: Path): Image = Image(Files.readAllBytes(path))
	def of(bytes: Array[Byte]): Image = Image(bytes)
	def of(frame: FrameImagesRow): Image = Image(blobToBytes(frame.image))
	def of(frame: FrameImagesRow, roi: RoisRow): Image = {
		new RichImage(Image(blobToBytes(frame.image))).crop(roi)
	}

}
