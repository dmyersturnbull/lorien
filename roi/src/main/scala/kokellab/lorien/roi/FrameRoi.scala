package kokellab.lorien.roi

import java.nio.file.{Files, Path}

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import kokellab.lorien.core.{RichImages, Roi}
import kokellab.lorien.core.RichImages.RichImage

import scala.language.implicitConversions

case class FrameRoi(roi: Roi, frame: Int)

/**
  * Walks through ROIs, then frames.
  * @param files <strong>must be sorted</strong> in the order first_frame, second_frame, ..., last_frame
  * @param rois The ROIs to extract
  */
class FrameRoiWalker(val files: Stream[Path])(val rois: Seq[Roi]) {

	val frameNames: Stream[String] = files map (_.getFileName.toString)
	private val tempDirectory = Files.createTempDirectory(ZonedDateTime.now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HHmmss.SSS")))

	for (roi <- rois) tempDirectory.resolve(roi.coordinateString).toFile.mkdir()
	for (inputFile <- files) {

		val image: RichImage = RichImages.of(inputFile)
		for (roi <- rois) {
			val outputFile = tempDirectory.resolve(roi.coordinateString).resolve(inputFile.getFileName.toString)
			image.crop(roi).write(outputFile)
		}
	}

	def iterator(roi: Roi): Iterator[RichImage] = {
		for (frame <- frameNames.iterator) yield { // I think it's not necessary to convert it to an iterator
			val outputFile = tempDirectory.resolve(roi.coordinateString).resolve(frame)
			RichImages.of(outputFile)
		}
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

object WholeFrameWalker {

	def inDirectory(directory: Path): Iterator[RichImage] =
		Files.list(directory).iterator().asScala map RichImages.of

	def inDirectoryRecursive(directory: Path): Iterator[RichImage] =
		Files.walk(directory).iterator().asScala map RichImages.of

}