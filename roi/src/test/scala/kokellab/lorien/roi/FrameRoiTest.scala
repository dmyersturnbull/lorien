package kokellab.lorien.roi

import java.nio.file.Paths

import scala.language.implicitConversions
import kokellab.lorien.core._
import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class FrameRoiTest extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {

	property("test") {
		val rois = Seq(
			Roi(12, 106, 12, 106-4),
			Roi(12, 106, 109, 203-4),
			Roi(12, 106, 206, 300-4)
		)
		val walker = FrameRoiWalker.inDirectoryRecursive(Paths.get("roi/src/test/resources/frames"))(rois)
		for (roi <- rois) {
			val wellFrames = walker.iterator(roi).toList
			wellFrames.size should equal (3)
			for (wellFrame <- wellFrames) {
				wellFrame.image.height should equal (94-4)
				wellFrame.image.width should equal (94)
			}
		}
	}

}
