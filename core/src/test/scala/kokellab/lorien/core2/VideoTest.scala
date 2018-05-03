package kokellab.lorien.core2

import java.nio.file.Paths

import scala.collection.mutable
import kokellab.lorien.core.RichImages
import org.bytedeco.javacpp.avutil.AVFrame
import org.bytedeco.javacpp.indexer.{UByteBufferIndexer, UByteRawIndexer}
import org.bytedeco.javacv.OpenCVFrameConverter
import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class VideoTest extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {

	property("test") {
		val iter = VideoIterator.from(Paths.get("/home/dmyerstu/desktop/x265-crf15.mkv"))
//		var prev: BlazingMatrix = null
//		while (iter.hasNext) {
//			val current = BlazingMatrix.of(iter.next())
//			if (prev != null) println(current.data == prev.data)
//			prev = current
//		}
		for (two <- iter sliding 2) {
			val prev = BlazingMatrix.of(two.head).toRichMatrix.normalize(0.01)
			val next = BlazingMatrix.of(two.last).toRichMatrix.normalize(0.01)
			println(((next-prev) #:> 10).sum)
//			println(next |-| prev)
//			println(next.toBreezeMatrix - prev.toBreezeMatrix)
		}
	}

}
