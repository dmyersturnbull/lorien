//package kokellab.lorien.simple
//
//
//import java.nio.file.Paths
//
//import breeze.linalg.{DenseMatrix, DenseVector}
//
//import scala.language.implicitConversions
//import kokellab.lorien.core.{RichImages, Roi}
//import kokellab.valar.core.{loadDb, exec}
//import org.scalacheck.Arbitrary
//import org.scalacheck.Gen
//import org.scalatest.prop.GeneratorDrivenPropertyChecks
//import org.scalatest.{Matchers, PropSpec}
//
//class OriginalMiFeatureTest extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {
//
//	val mi = new OriginalMiFeature
//
//	def byteMatrixGen(minValue: Byte = Byte.MinValue, maxValue: Byte = Byte.MaxValue, maxDimension: Short = Short.MaxValue): Gen[DenseMatrix[Byte]] = for {
//			nRows <- Gen.choose(0, maxDimension)
//			nColumns <- Gen.choose(0, maxDimension)
//			seq <- Gen.infiniteStream(Gen.choose(minValue, maxValue))
//		} yield {
//			val data: Array[Byte] = seq.take(nRows * nColumns).toArray
//			DenseMatrix.create(nRows, nColumns, data)
//		}
//
//	def grayscaleMatrixGen(minValue: Byte = Byte.MinValue, maxValue: Byte = Byte.MaxValue): Gen[DenseMatrix[Byte]] =
//		byteMatrixGen(minValue, maxValue)
//
//	def grayscaleVideoGen(minValue: Byte = Byte.MinValue, maxValue: Byte = Byte.MaxValue, maxLength: Short = Byte.MaxValue): Gen[Seq[DenseMatrix[Byte]]] = for {
//			length <- Gen.choose(0, maxLength)
//			frames <- Gen.listOf(grayscaleMatrixGen(minValue, maxValue))
//		} yield {
//			frames take length
//		}
//
//	private val halfByteGen = Arbitrary.arbByte.arbitrary retryUntil (b => b < 64 && b > -64)
//
//	property("Test") {
//		val images = Seq(
//			RichImages.of(Paths.get("core/src/test/resources/2017-04-04-man01-S01-1-1913-195923-background-000002.jpg")),
//			RichImages.of(Paths.get("core/src/test/resources/2017-04-04-man01-S01-1-1913-195923-background-000003.jpg")),
//			RichImages.of(Paths.get("core/src/test/resources/2017-04-04-man01-S01-1-1913-195923-background-000003.jpg")),
//			RichImages.of(Paths.get("core/src/test/resources/2017-04-04-man01-S01-1-1913-195923-background-000004.jpg"))
//		) map (_.crop(new Roi(1, 95, 1, 95)))
//		val feature = new OriginalMiFeature
//		val tensor: Iterator[Float] = feature(images.iterator)
//		for (v <- tensor) println(v)
//	}
//
//	property("from db") {
//
////		implicit val db = loadDb()
////
////		import kokellab.valar.core.Tables._
////		import kokellab.valar.core.Tables.profile.api._
////
////		val plateRun = exec((PlateRuns filter (_.id === 2490.toShort)).result.head)
////		val roi = exec((Rois filter (_.id === 60117)).result.head)
////
////		val feature = new OriginalMiFeature
////		val tensor: Iterator[Float] = feature.apply(plateRun, roi)
////		println()
//	}
//
//}
