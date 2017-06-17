package kokellab.lorien.core

import kokellab.valar.core.Tables.{PlateRuns, PlateRunsRow, PlateTypes, PlateTypesRow, PlatesRow, Rois, RoisRow}
import kokellab.valar.core.{exec, loadDb}

object RoiUtils {

	private implicit val db = loadDb()

	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	def manual(plateRunId: Short): Seq[RoisRow] = {
		val info = SimplePlateInfo.fetch(plateRunId)
		val rois: Seq[RoisRow] = exec((
			for {
				(roi, well) <- Rois join Wells on (_.wellId === _.id)
				if well.plateRunId === plateRunId && roi.lorienConfig.isEmpty
			} yield roi
		).result)
		if (rois.size != info.plateType.nRows * info.plateType.nColumns) {
			throw new AmbiguousException(s"Plate type ${info.plateType.id} has ${info.plateType.nRows*info.plateType.nColumns} wells, but found ${rois.size} manually defined ROIs") with LorienException
		}
		rois
	}
}


case class SimplePlateInfo(run: PlateRunsRow, plate: PlatesRow, plateType: PlateTypesRow)

object SimplePlateInfo {

	private implicit val db = loadDb()

	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	def fetch(plateRunId: Short): SimplePlateInfo = {
		val stuff = exec((
			for {
				((run, plate), plateType) <- PlateRuns join Plates on (_.plateId === _.id) join PlateTypes on (_._2.plateTypeId === _.id)
				if run.id === plateRunId
			} yield (run, plate, plateType)
		).result).headOption.orNull
		require(stuff != null, s"No plate run with ID $plateRunId exists")
		SimplePlateInfo(stuff._1, stuff._2, stuff._3)
	}
}