package kokellab.lorien.core

import java.nio.file.Paths

import com.sksamuel.scrimage.Image
import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.language.implicitConversions

class RichImageTest extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {

	property("test") {
		val x = RichImages.of(Paths.get("roi/src/test/resources/frames/2017-04-04-man01-S01-1-1913-195923-background-000002.jpg"))
		x.grays
	}

}
