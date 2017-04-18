package kokellab.lorien.simple


import java.io.ByteArrayInputStream
import java.nio.file.Paths

import breeze.linalg.{DenseMatrix, DenseVector}
import kokellab.lorien.core.Grayscale
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.{GeneratorDrivenPropertyChecks, PropertyChecks}
import org.scalatest.{Matchers, PropSpec}

import scala.io.Source

class OriginalMiFeatureTest extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {

	val mi = new OriginalMiFeature

	def byteMatrixGen(minValue: Byte = Byte.MinValue, maxValue: Byte = Byte.MaxValue, maxDimension: Short = Short.MaxValue): Gen[DenseMatrix[Byte]] = for {
			nRows <- Gen.choose(0, maxDimension)
			nColumns <- Gen.choose(0, maxDimension)
			seq <- Gen.infiniteStream(Gen.choose(minValue, maxValue))
		} yield {
			val data: Array[Byte] = seq.take(nRows * nColumns).toArray
			DenseMatrix.create(nRows, nColumns, data)
		}

	def grayscaleMatrixGen(minValue: Byte = Byte.MinValue, maxValue: Byte = Byte.MaxValue): Gen[DenseMatrix[Grayscale]] =
		byteMatrixGen(minValue, maxValue) map (m => m map (b => Grayscale(b)))

	def grayscaleVideoGen(minValue: Byte = Byte.MinValue, maxValue: Byte = Byte.MaxValue, maxLength: Short = Byte.MaxValue): Gen[Seq[DenseMatrix[Grayscale]]] = for {
			length <- Gen.choose(0, maxLength)
			frames <- Gen.listOf(grayscaleMatrixGen(minValue, maxValue))
		} yield {
			frames take length
		}

	private val halfByteGen = Arbitrary.arbByte.arbitrary retryUntil (b => b < 64 && b > -64)


}
