package kokellab.lorien.simple

import kokellab.lorien.core.{RoiUtils, SimplePlateInfo}
import kokellab.lorien.core2.RichMatrices.RichMatrix
import kokellab.lorien.core2.{BlazingMatrix, VFeature, VTimeFeature}

import scala.util.Try

class Mi2Feature extends VTimeFeature[Short] {

	override def name: String = "MI2"

	override def valarId: Byte = 2

	override def apply(input: Iterator[RichMatrix]): Array[Short] = {
		(input sliding 2) map {slid =>
			val prev = slid.head.normalize(0.01)
			val next = slid.last.normalize(0.01)
			((next-prev) #:> 15).sum.toShort
		}
	}.toArray

}

object Mi2Feature {

//	def main(args: Array[String]): Unit = {
//		val feature = new Mi2Feature()
//		Try(args(0).toShort).toOption match {
//			case Some(runId) =>
//				val run = SimplePlateInfo.fetch(args(0).toShort).run
//				val rois = RoiUtils.manual(run.id)
//				feature.insertOnAll(run, rois, None)
//			case None => println("Must provide a plate run ID")
//		}
//	}
}