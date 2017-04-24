package kokellab.lorien

import java.nio.file.{Files, Path, Paths}

import breeze.linalg.DenseMatrix
import com.sksamuel.scrimage.{Grayscale, Image, Pixel}

package object core {

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
		val image = Image(bytes)
		(0 until image.width) map {x =>
			(0 until image.height) map {y =>
				image.pixel(x, y)
			}
		}
	}

	def bytesToGrayscale(bytes: Array[Byte]): DenseMatrix[Int] = {
		val seq = bytesToPixels(bytes) map (_ map (_.toColor.toGreyscale.grey))
		DenseMatrix(seq:_*)
	}

	def bytesToGrayscaleU8(bytes: Array[Byte]): DenseMatrix[GrayscaleU8] = {
		bytesToGrayscale(bytes) map (g => new GrayscaleU8(g.toByte))
	}
}
