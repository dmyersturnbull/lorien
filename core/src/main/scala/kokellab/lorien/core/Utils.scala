package kokellab.lorien.core

import java.io.IOException
import java.nio.file.{Files, Path, Paths}
import kokellab.lorien.core.RichImages.RichImage
import scala.util.{Failure, Success, Try}
import kokellab.valar.core.Tables.{Runs, RunsRow, PlateTypes, PlateTypesRow, PlatesRow, Rois, RoisRow}
import kokellab.valar.core.{exec, loadDb}

object RoiUtils {

	private implicit val db = loadDb()

	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	def manual(plateRunId: Int): Seq[RoisRow] = {
		val info = SimplePlateInfo.fetch(plateRunId)
		val rois: Seq[RoisRow] = exec((
			for {
				(roi, well) <- Rois join Wells on (_.wellId === _.id)
				if well.runId === plateRunId // TODO && roi.lorienConfig.isEmpty
			} yield roi
		).result)
		if (rois.size != info.plateType.nRows * info.plateType.nColumns) {
			throw new AmbiguousException(s"Plate type ${info.plateType.id} has ${info.plateType.nRows*info.plateType.nColumns} wells, but found ${rois.size} manually defined ROIs") with LorienException
		}
		rois
	}
}

object FeatureUtils {

	private implicit val db = loadDb()

	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	def tryLoad(frame: Path, run: RunsRow): RichImage = Try(RichImages.of(frame)) match {
		case Success(img) => img
		case Failure(e: IOException) => throw new CalculationFailedException(Some(run), None, s"Failed to read image $frame for plate_run ${run.id}", e)
		case Failure(e) => throw e
	}
}


case class SimplePlateInfo(run: RunsRow, plate: PlatesRow, plateType: PlateTypesRow)

object SimplePlateInfo {

	private implicit val db = loadDb()

	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	def fetch(plateRunId: Int): SimplePlateInfo = {
		val stuff = exec((
			for {
				((run, plate), plateType) <- Runs join Plates on (_.plateId === _.id) join PlateTypes on (_._2.plateTypeId === _.id)
				if run.id === plateRunId
			} yield (run, plate, plateType)
		).result).headOption.orNull
		require(stuff != null, s"No plate run with ID $plateRunId exists")
		SimplePlateInfo(stuff._1, stuff._2, stuff._3)
	}
}
