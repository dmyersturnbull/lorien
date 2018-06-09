package kokellab.lorien.simple

import java.nio.file.{Path, Paths}

import scala.reflect.ClassTag
import breeze.linalg.DenseVector
import com.typesafe.scalalogging.LazyLogging
import kokellab.valar.core.{exec, loadDb}
import kokellab.lorien.core.VTimeFeature
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
	def lorien: VTimeFeature[V]
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


abstract class PlainFeatureInserter[V](container: ContainerFormat, codec: Codec)(implicit ct: ClassTag[V]) extends GenFeatureInserter[V] {

	private implicit val db = loadDb()
	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	def converter(arr: Array[V]): Array[Byte]

	def apply(run: RunsRow, videoFile: Path): Unit = {

		logger.info(s"Calculating and inserting ${valar.name} for run ${run.tag}.")

		val rois = RoiUtils.manual(SimplePlateInfo.fetch(run.id)) map { case (well, roi) => Roi.of(roi, well.id)}

		// TODO only 1 delete query
		//logger.warn(s"Deleting ${previous.size} previous features")
		rois map (_.wellId) foreach { wellId =>
			val previous = exec((WellFeatures filter (wf => wf.wellId === wellId && wf.typeId === valar.id && wf.lorienConfigId === 1.toShort)).result)
			if (previous.nonEmpty) {
			assert(previous.size == 1)
			exec(WellFeatures filter (wf => wf.wellId === wellId && wf.typeId === valar.id && wf.lorienConfigId === 1.toShort) delete)
			}
		}

		val results: Map[Roi, Array[V]] = Try {
			val video = VideoFile(videoFile, container, codec)
			lorien.applyOnAll(video, rois)
		} match {
			case Success(array) => array
			case Failure(e) => throw new FeatureCalculationFailedException(s"${valar.name} calculation on run ${run.tag} failed", e)
		}

		for (((roi, floats), i) <- results.zipWithIndex) {
			insert(converter(floats), roi)
			if (i % 12 == 0) logger.info(s"Processed ${valar.name} for well $i.")
		}

		logger.info(s"Finished inserting ${valar.name} for run ${run.tag}.")
	}
}

class MiFeatureInserter(container: ContainerFormat, codec: Codec) extends PlainFeatureInserter[Float](container, codec) {
	private implicit val db = loadDb()
	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._
	override val valar: FeaturesRow = exec((Features filter (_.name === "MI")).result).head
	override val lorien = new MiFeature()
	override def converter(arr: Array[Float]): Array[Byte] = floatsToBytes(arr).toArray
}

class Mi2FeatureInserter(container: ContainerFormat, codec: Codec) extends PlainFeatureInserter[Float](container, codec) {
	private implicit val db = loadDb()
	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._
	override val valar: FeaturesRow = exec((Features filter (_.name === "MI2")).result).head
	override val lorien = new Mi2Feature()
	override def converter(arr: Array[Float]): Array[Byte] = floatsToBytes(arr).toArray
}


object FeatureProcessor {

	private implicit val db = loadDb()
	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	def main(args: Array[String]): Unit = {
		val feature = args(0) match {
			case "MI" => new MiFeatureInserter(ContainerFormat.Mkv, Codec.H265Crf(15))
			case "MI2" => new Mi2FeatureInserter(ContainerFormat.Mkv, Codec.H265Crf(15))
			case _ => throw new IllegalArgumentException(s"Unrecognized feature ${args(0)}")
		}
		val run: Try[RunsRow] = Try(args(1).toInt) map (r => exec((Runs filter (_.id === r)).result).head)
		val path = Paths.get(args(2))
		run match {
			case Success(r) =>
				feature.apply(r, path)
			case Failure(e) => throw e
		}
	}
}
