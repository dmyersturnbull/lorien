package kokellab.lorien

import java.nio.file.{Files, Path, Paths}

import breeze.linalg.DenseMatrix
import com.sksamuel.scrimage.{Grayscale, Image, Pixel}
import kokellab.utils.core.thisGitCommitSha1Bytes
import kokellab.utils.core._
import kokellab.valar.core.loadDb

package object core {

	private implicit val db = loadDb()

	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._


	lazy val lorienCommitHash: Array[Byte] = thisGitCommitSha1Bytes

	def readImage(path: Path): Image = Image(Files.readAllBytes(path))
	def readImageAsBytes(path: Path): Array[Byte] = Files.readAllBytes(path)

	def readImageAsPixels(path: Path): Seq[Seq[Pixel]] = {
		bytesToPixels(readImageAsBytes(path))
	}

	def readImageAsGrayscale(path: Path): DenseMatrix[Int] = {
		val seq: Seq[Seq[Int]] = readImageAsPixels(path) map (_ map (_.toColor.toGreyscale.grey))
		DenseMatrix(seq:_*)
	}

	def readImageAsGrayscaleU8(path: Path): DenseMatrix[GrayscaleU8] = {
		readImageAsGrayscale(path) map (g => new GrayscaleU8(g.toByte))
	}

	def bytesToPixels(bytes: Array[Byte]): Seq[Seq[Pixel]] = {
		imageToPixels(Image(bytes))
	}

	def imageToPixels(image: Image): Seq[Seq[Pixel]] = {
		(0 until image.width) map {x =>
			(0 until image.height) map {y =>
				image.pixel(x, y)
			}
		}
	}

	def crop(image: Image, roi: RoisRow): Image = {
		image.subimage(roi.x0, roi.y0, roi.x1 - roi.x0, roi.y1 - roi.y0)
	}

	def bytesToGrayscale(bytes: Array[Byte]): DenseMatrix[Int] = {
		val seq = bytesToPixels(bytes) map (_ map (_.toColor.toGreyscale.grey))
		DenseMatrix(seq:_*)
	}

	def bytesToGrayscaleU8(bytes: Array[Byte]): DenseMatrix[GrayscaleU8] = {
		bytesToGrayscale(bytes) map (g => new GrayscaleU8(g.toByte))
	}

	def frameToImage(frame: FrameImagesRow): Image = {
		Image(blobToBytes(frame.image))
	}

	def cropFrame(frame: FrameImagesRow, roi: RoisRow): Image = {
		val image = Image(blobToBytes(frame.image))
		crop(image, roi)
	}

	def wellFrameAsGrayscaleU8(frame: FrameImagesRow, roi: RoisRow): DenseMatrix[GrayscaleU8] = {
		bytesToGrayscaleU8(cropFrame(frame, roi).bytes)
	}
}
