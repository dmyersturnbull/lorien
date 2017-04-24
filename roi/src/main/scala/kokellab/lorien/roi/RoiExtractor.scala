package kokellab.lorien.roi

import com.sksamuel.scrimage.Image
import java.nio.file.{Files, Path, Paths}

import scala.io.Source


class RoiExtractor(bytes: Array[Byte]) extends (Roi => Array[Byte]) {

	def this(file: Path) = this(Files.readAllBytes(file))

	val image = Image(bytes)

	override def apply(roi: Roi): Array[Byte] = {
		image.subimage(roi.x0, roi.y0, roi.x1 - roi.x0, roi.y1 - roi.y0).bytes
	}

}


object RoiExtractor {

	def extractFromFile(file: Path, outputDir: Path, rois: Seq[Roi]): Unit = {
		val bytes = Files.readAllBytes(file)
		val extractor = new RoiExtractor(bytes)
		for (roi <- rois) {
			Files.write(outputDir.resolve(roi.coordinateString), extractor(roi))
		}
	}

}
