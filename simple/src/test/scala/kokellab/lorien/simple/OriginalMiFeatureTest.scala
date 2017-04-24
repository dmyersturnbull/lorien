package kokellab.lorien.simple


import java.io.ByteArrayInputStream
import java.nio.file.Paths

import breeze.linalg.{DenseMatrix, DenseVector}
import com.sksamuel.scrimage.Image
import kokellab.lorien.core.GrayscaleU8
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.{GeneratorDrivenPropertyChecks, PropertyChecks}
import org.scalatest.{Matchers, PropSpec}
import kokellab.lorien.core.{readImageAsGrayscaleU8}

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

	def grayscaleMatrixGen(minValue: Byte = Byte.MinValue, maxValue: Byte = Byte.MaxValue): Gen[DenseMatrix[Byte]] =
		byteMatrixGen(minValue, maxValue)

	def grayscaleVideoGen(minValue: Byte = Byte.MinValue, maxValue: Byte = Byte.MaxValue, maxLength: Short = Byte.MaxValue): Gen[Seq[DenseMatrix[Byte]]] = for {
			length <- Gen.choose(0, maxLength)
			frames <- Gen.listOf(grayscaleMatrixGen(minValue, maxValue))
		} yield {
			frames take length
		}

	private val halfByteGen = Arbitrary.arbByte.arbitrary retryUntil (b => b < 64 && b > -64)

	property("Test") {
		val images = DenseVector(Seq(
			readImageAsGrayscaleU8(Paths.get("/run/media/dmyerstu/Kokel_1Tb/2016-11-Reid_Kinser/rawData/DR_2x_full/2016-10-19-DR_2x_full-S01-3-1852/2016-10-19-DR_2x_full-S01-3-1852-190306-softTap1-JPGs/2016-10-19-DR_2x_full-S01-3-1852-190306-softTap1-000001.jpg")),
			readImageAsGrayscaleU8(Paths.get("/run/media/dmyerstu/Kokel_1Tb/2016-11-Reid_Kinser/rawData/DR_2x_full/2016-10-19-DR_2x_full-S01-3-1852/2016-10-19-DR_2x_full-S01-3-1852-190306-softTap1-JPGs/2016-10-19-DR_2x_full-S01-3-1852-190306-softTap1-000001.jpg")),
			readImageAsGrayscaleU8(Paths.get("/run/media/dmyerstu/Kokel_1Tb/2016-11-Reid_Kinser/rawData/DR_2x_full/2016-10-19-DR_2x_full-S01-3-1852/2016-10-19-DR_2x_full-S01-3-1852-190306-softTap1-JPGs/2016-10-19-DR_2x_full-S01-3-1852-190306-softTap1-000002.jpg")),
			readImageAsGrayscaleU8(Paths.get("/run/media/dmyerstu/Kokel_1Tb/2016-11-Reid_Kinser/rawData/DR_2x_full/2016-10-19-DR_2x_full-S01-3-1852/2016-10-19-DR_2x_full-S01-3-1852-190306-softTap1-JPGs/2016-10-19-DR_2x_full-S01-3-1852-190306-softTap1-000003.jpg")),
			readImageAsGrayscaleU8(Paths.get("/run/media/dmyerstu/Kokel_1Tb/2016-11-Reid_Kinser/rawData/DR_2x_full/2016-10-19-DR_2x_full-S01-3-1852/2016-10-19-DR_2x_full-S01-3-1852-190306-softTap1-JPGs/2016-10-19-DR_2x_full-S01-3-1852-190306-softTap1-000003.jpg")),
			readImageAsGrayscaleU8(Paths.get("/run/media/dmyerstu/Kokel_1Tb/2016-11-Reid_Kinser/rawData/DR_2x_full/2016-10-19-DR_2x_full-S01-3-1852/2016-10-19-DR_2x_full-S01-3-1852-190306-softTap1-JPGs/2016-10-19-DR_2x_full-S01-3-1852-190306-softTap1-000004.jpg")),
			readImageAsGrayscaleU8(Paths.get("/run/media/dmyerstu/Kokel_1Tb/2016-11-Reid_Kinser/rawData/DR_2x_full/2016-10-19-DR_2x_full-S01-3-1852/2016-10-19-DR_2x_full-S01-3-1852-190306-softTap1-JPGs/2016-10-19-DR_2x_full-S01-3-1852-190306-softTap1-000004.jpg"))
		):_*)
		val feature = new OriginalMiFeature
		val tensor: DenseVector[Float] = feature.calculate(images)
		for (v <- tensor) println(v)
	}

}
