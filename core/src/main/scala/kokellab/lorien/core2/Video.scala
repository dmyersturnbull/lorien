package kokellab.lorien.core2

import java.io.{Closeable, File}
import java.nio.file.{Files, Path, Paths}
import java.util.NoSuchElementException

import breeze.linalg.DenseMatrix
import kokellab.lorien.core2.Codec.H265
import kokellab.utils.misc.{FileHasher, ValidationFailedException}
import org.bytedeco.javacpp.avformat.AVFormatContext
import org.bytedeco.javacpp.opencv_core.Mat
import org.bytedeco.javacv.{FFmpegFrameGrabber, Frame, OpenCVFrameConverter}
import org.bytedeco.javacpp.opencv_imgproc.cvtColor
import org.bytedeco.javacpp.opencv_imgproc.COLOR_YUV2GRAY_420

import scala.io.Source
import scala.util.{Failure, Success, Try}

trait ContainerFormat {
	def name: String
	def ext: String
}
object ContainerFormat {
	case object Mkv extends ContainerFormat {
		override val ext = ".mkv"
		override def name: String = "Matroska Multimedia Container"
	}
	case object Avi extends ContainerFormat {
		val ext = ".avi"
		override def name = "Audio Video Interleaved"
	}
}

trait Codec {
	def name: String
	def ext: String
}
object Codec {
	case class H265(crf: Byte) extends Codec with Comparable[H265] {
		require(crf > 0 && crf < 52, s"Invalid CRF $crf")
		override def name: String = "High Efficiency Video Coding"
		override def ext: String = ".x265-" + crf
		override def compareTo(o: H265): Int = crf compareTo o.crf
	}
	case class H264(crf: Byte) extends Codec with Comparable[H265] {
		require(crf > 0 && crf < 52, s"Invalid CRF $crf")
		override def name: String = "MPEG-4 Part 10, Advanced Video Codingg"
		override def ext: String = ".x264-" + crf
		override def compareTo(o: H265): Int = crf compareTo o.crf
	}
}


case class VideoFile(path: Path, containerFormat: ContainerFormat, codec: Codec)  {
	require(Files.isRegularFile(path), s"$path is not a file")
	require(Files.isRegularFile(Paths.get(path.toString + ".sha256")), s"No sha256 file for $path exists")
	def validates: Boolean = {
		val sha256 = Source.fromFile(new File(path.toString + ".sha256")).mkString.trim
		Try(new FileHasher("SHA-256").validate(path, sha256)) match {
			case Success(_) => true
			case Failure(e: ValidationFailedException) => false
			case Failure(e) => throw e
		}
	}
	def peekAt[V](getter: VideoIterator => V): V = VideoIterator.peekAt(path)(getter)
	def nFrames: Int = peekAt(_.lengthInFrames)
	def nMicroseconds: Long = peekAt(_.lengthInTime)
	def reader(): VideoIterator = VideoIterator.from(path)
}

class VideoIterator(grabber: FFmpegFrameGrabber) extends Iterator[Frame] with Closeable {

	grabber.start()

	private var prev: Frame = grabber.grab()
	private var trulyDone = false

	override def hasNext: Boolean = !trulyDone

	override def next(): Frame = {
		if (trulyDone) throw new NoSuchElementException(s"No frames left in video")
		val temp = prev
		if (prev == null) {
			trulyDone = true
			close()
		}
		else prev = grabber.grab()
		temp
	}

	override def close(): Unit = {
		grabber.stop()
		grabber.release()
	}

	def frameNumber(frameNumber: Int): Unit = grabber.setFrameNumber(frameNumber)
	def sampleRate: Int = grabber.getSampleRate
	def videoMetadata(key: String): String = grabber.getVideoMetadata(key)
	def audioBitrate: Int = grabber.getAudioBitrate
	def audioChannels: Int = grabber.getAudioChannels
	def videoBitrate: Int = grabber.getVideoBitrate
	def sampleFormat: Int = grabber.getSampleFormat
	def audioMetadata(key: String): String = grabber.getAudioMetadata(key)
	def formatContext: AVFormatContext = grabber.getFormatContext
	def imageHeight: Int = grabber.getImageHeight
	def audioCodec: Int = grabber.getAudioCodec
	def aspectRatio: Double = grabber.getAspectRatio
	def format: String = grabber.getFormat
	def pixelFormat: Int = grabber.getPixelFormat
	def metadata(key: String): String = grabber.getMetadata(key)
	def lengthInFrames: Int = grabber.getLengthInFrames
	def gamma: Double = grabber.getGamma
	def videoCodec: Int = grabber.getVideoCodec
	def frameRate: Double = grabber.getFrameRate
	def imageWidth: Int = grabber.getImageWidth
	def lengthInTime: Long = grabber.getLengthInTime
}
object VideoIterator {
	def from(path: Path) = new VideoIterator(new FFmpegFrameGrabber(path.toFile))
	def peekAt[V](path: Path)(getter: VideoIterator => V): V = peekAt(from(path))(getter)
	def peekAt[V](iterator: VideoIterator)(getter: VideoIterator => V): V = {
		var iter = null: VideoIterator
		try {
			getter(iterator)
		} finally {
			if (iter != null) iter.close()
		}
	}
}


trait FrameConverter[T] extends (Frame => T) {
	def apply(frame: Frame): T
}
object FrameConverter {
	private val matConverter = new OpenCVFrameConverter.ToMat
	class ToMat(imageFormat: Int) extends FrameConverter[Mat] {
		override def apply(frame: Frame): Mat = {
			val conv = matConverter.convert(frame)
			val toFill = new Mat()
			cvtColor(conv, toFill, imageFormat)
			toFill
		}
	}
	class ToFloatMatrix extends FrameConverter[DenseMatrix[Float]] {
		override def apply(frame: Frame): DenseMatrix[Float] = {
			val data = grayscaleMat(frame).asByteBuffer.array map (_.toFloat)
			val matrix = new DenseMatrix[Float](frame.imageWidth, frame.imageHeight)
			data.copyToArray(matrix.data)
			matrix
		}
	}
	class ToDoubleMatrix extends FrameConverter[DenseMatrix[Double]] {
		override def apply(frame: Frame): DenseMatrix[Double] = {
			val data = grayscaleMat(frame).asByteBuffer.array map (_.toDouble)
			val matrix = new DenseMatrix[Double](frame.imageWidth, frame.imageHeight)
			data.copyToArray(matrix.data)
			matrix
		}
	}
	class ToIntMatrix extends FrameConverter[DenseMatrix[Int]] {
		override def apply(frame: Frame): DenseMatrix[Int] = {
			val data = grayscaleMat(frame).asByteBuffer.array map (_.toInt)
			val matrix = new DenseMatrix[Int](frame.imageWidth, frame.imageHeight)
			data.copyToArray(matrix.data)
			matrix
		}
	}
	class ToLongMatrix extends FrameConverter[DenseMatrix[Long]] {
		override def apply(frame: Frame): DenseMatrix[Long] = {
			val data = grayscaleMat(frame).asByteBuffer.array map (_.toLong)
			val matrix = new DenseMatrix[Long](frame.imageWidth, frame.imageHeight)
			data.copyToArray(matrix.data)
			matrix
		}
	}
	val grayscaleMat = new ToMat(COLOR_YUV2GRAY_420)
}
