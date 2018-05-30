package kokellab.lorien.simple

import java.nio.file.{Path, Paths}

import breeze.linalg.DenseVector
import com.typesafe.scalalogging.LazyLogging
import kokellab.valar.core.{exec, loadDb}
import kokellab.lorien.core.VFeature
import kokellab.lorien.core._
import kokellab.utils.core._
import kokellab.valar.core.Tables.{Features, FeaturesRow, RunsRow, WellFeatures, WellFeaturesRow}

import scala.util.{Failure, Success, Try}


trait GenFeatureInserter[V] extends LazyLogging {

	private implicit val db = loadDb()
	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	protected lazy val insertQuery = WellFeatures returning (WellFeatures map (_.id)) into ((newRow, id) => newRow.copy(id = id))

	def valar: FeaturesRow
	def lorien: VFeature[V]
	def apply(run: RunsRow, videoFile: Path): Unit

	protected def insert(bytes: Traversable[Byte], roi: Roi): Unit = {
		exec(insertQuery += WellFeaturesRow(
			id = 0,
			wellId = roi.wellId,
			typeId = valar.id,
			lorienConfigId = 1,
			lorienCommitSha1 = bytesToBlob(lorienCommitHash),
			floats = bytesToBlob(bytes),
			sha1 = bytesToHashBlob(bytes)
		))
	}
}


class MiFeatureInserter(container: ContainerFormat, codec: Codec) extends GenFeatureInserter[Float] {

	private implicit val db = loadDb()
	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	override val valar: FeaturesRow = exec((Features filter (_.name === "MI")).result).head
	override val lorien = new MiFeature()

	def apply(run: RunsRow, videoFile: Path): Unit = {

		logger.info("Calculating and inserting MI. This will take a while...")

		val rois = RoiUtils.manual(SimplePlateInfo.fetch(run.id)) map { case (well, roi) => Roi.of(roi, well.id)}

		val results: Map[Roi, Array[Float]] = Try {
			val video = VideoFile(videoFile, container, codec)
			lorien.applyOnAll(video, rois)
		} match {
			case Success(array) => array
			case Failure(e) => throw new FeatureCalculationFailedException(s"MI calculation failed", e)
		}

		for (((roi, floats), i) <- results.zipWithIndex) {
			insert(floatsToBytes(floats), roi)
			if (i % 12 == 0) logger.info(s"Processed MI for well $i.")
		}

		logger.info(s"Finished inserting MI.")
	}
}

trait GenFeatureProcessor

class StandardFeatureProcessor(container: ContainerFormat, codec: Codec) extends GenFeatureProcessor with LazyLogging {
	def apply(run: RunsRow, videoFile: Path): Unit = {
		new MiFeatureInserter(container, codec).apply(run, videoFile)
	}
}


object FeatureProcessor {

	private implicit val db = loadDb()
	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	def main(args: Array[String]): Unit = {
		val run: Try[RunsRow] = Try(args(0).toInt) map (r => exec((Runs filter (_.id === r)).result).head)
		val path = Paths.get(args(1))
		run match {
			case Success(r) =>
				new StandardFeatureProcessor(ContainerFormat.Mkv, Codec.H265Crf(15))(r, path)
			case Failure(e) => throw e
		}
	}
}