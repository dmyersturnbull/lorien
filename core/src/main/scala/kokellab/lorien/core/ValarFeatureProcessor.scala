package kokellab.lorien.core

import breeze.linalg.{DenseMatrix, Tensor}
import com.typesafe.scalalogging.LazyLogging
import kokellab.valar.core.{exec, loadDb}
import kokellab.utils.core.blobToBytes
import kokellab.valar.core.Tables.{FrameImagesRow, RoisRow}
import slick.jdbc.JdbcBackend.Database


class ValarFeatureProcessor[D, C <: ColorValue[D], @specialized(Byte, Int, Float, Double) V](feature: Feature[D, C, V], converter: (FrameImagesRow, RoisRow) => DenseMatrix[C])(implicit database: Database) extends LazyLogging {

	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	def apply(plateRun: PlateRunsRow, roi: RoisRow): Tensor[Int, V] = feature.calculate {
		exec((
			FrameImages filter (_.plateRunId === plateRun.id) sortBy (_.frame)
		).result) map (c => converter(c, roi))
	}
}


class GrayscaleU8FloatFeatureProcessor(feature: GrayscaleU8FloatFeature)(implicit database: Database) extends ValarFeatureProcessor[Byte, GrayscaleU8, Float](feature, wellFrameAsGrayscaleU8)(database) {

}
