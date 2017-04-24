package kokellab.lorien.roi

import java.nio.file.Paths

import kokellab.lorien.core._
import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class FrameRoiTest extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {

	property("test") {
		val rois = Seq(
			Roi(12, 106, 12, 106),
			Roi(12, 106, 109, 203),
			Roi(12, 106, 206, 300)
		)
		val walker = FrameRoiWalker.inDirectoryRecursive(Paths.get("/run/media/dmyerstu/Kokel_1Tb/2016-11-Reid_Kinser/rawData/man01"))(rois)
		val got = walker.iterator(rois(0)) map bytesToGrayscaleU8
	}

}
