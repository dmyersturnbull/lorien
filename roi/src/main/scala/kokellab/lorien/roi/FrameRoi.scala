package kokellab.lorien.roi

import java.nio.file.{Files, Path}

import scala.collection.JavaConverters._
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

case class FrameRoi(roi: Roi, frame: Int)

/**
  * Walks through ROIs, then frames.
  * @param files <strong>must be sorted</strong> in the order first_frame, second_frame, ..., last_frame
  * @param rois The ROIs to extract
  */
class FrameRoiWalker(val files: Stream[Path])(val rois: Seq[Roi]) {

	files foreach println
	val frameNames: Stream[String] = files map (_.getFileName.toString)
	private val tempDirectory = Files.createTempDirectory(ZonedDateTime.now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HHmmss.SSS")))

	for (roi <- rois) tempDirectory.resolve(roi.coordinateString).toFile.mkdir()
	for (inputFile <- files) {

		val extractor = new RoiExtractor(inputFile)
		for (roi <- rois) {
			val outputFile = tempDirectory.resolve(roi.coordinateString).resolve(inputFile.getFileName.toString)
			Files.write(outputFile, extractor(roi))
		}
	}

	def iterator(roi: Roi): TraversableOnce[Array[Byte]] = for (frame <- frameNames) yield {
		val outputFile = tempDirectory.resolve(roi.coordinateString).resolve(frame)
		Files.readAllBytes(outputFile)
	}

}

object FrameRoiWalker {

	def inDirectory(directory: Path)(rois: Seq[Roi]): FrameRoiWalker = filteredAndSorted(
		Files.list(directory).iterator().asScala.toStream, rois
	)

	def inDirectoryRecursive(directory: Path)(rois: Seq[Roi]): FrameRoiWalker = filteredAndSorted(
		Files.walk(directory).iterator().asScala.toStream, rois
	)

	def filteredAndSorted(stream: Stream[Path], rois: Seq[Roi]): FrameRoiWalker = new FrameRoiWalker(
		stream
		filter (p => p.toString.endsWith(".png") || p.toString.endsWith(".jpg"))
		sortBy (_.normalize().toString)
	)(rois)
}