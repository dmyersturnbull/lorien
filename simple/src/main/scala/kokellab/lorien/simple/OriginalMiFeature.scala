//package kokellab.lorien.simple
//
//import breeze.linalg._
//import breeze.numerics.abs
//import kokellab.lorien.core.RichMatrices.RichMatrix
//
//import scala.language.implicitConversions
//import kokellab.lorien.core.{RoiUtils, SimplePlateInfo, TimeVectorFeature}
//import kokellab.utils.core._
//
//import scala.util.Try
//
///**
// * The original definition of Motion Index by Dave Kokel.
// */
//class OriginalMiFeature extends TimeVectorFeature[Float] {
//
//	override def name: String = "Motion Index"
//
//	override def abbreviation: String = "MI"
//
//	override def description: String = "Original definition of motion index; sums the difference in pixel intensities over each well for consecutive frames. MI at frame 0 is defined as 0."
//
//	override def valarFeatureId: Byte = 1
//
//	def converter: Array[Float] => Array[Byte] = arr => floatsToBytes(arr).toArray
//
//	def apply(input: Iterator[RichMatrix]): Iterator[Float] = {
//		input map (_.matrix) sliding (2, 1) map (f => sum(abs(f.head - f.last))) map (_.toFloat)
//	}
//}
//
//
//object OriginalMiFeature {
//	def main(args: Array[String]): Unit = {
//		val feature = new OriginalMiFeature()
//		Try(args(0).toShort).toOption match {
//			case Some(runId) =>
//				val run = SimplePlateInfo.fetch(args(0).toShort).run
//				val rois = RoiUtils.manual(run.id)
//				feature.insertOnAll(run, rois, None)
//			case None => println("Must provide a plate run ID")
//		}
//	}
//}