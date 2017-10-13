package kokellab.lorien

import java.nio.file.{Files, Path, Paths}

import breeze.linalg.DenseMatrix
import com.sksamuel.scrimage.{Grayscale, Image, Pixel}
import kokellab.utils.core.thisGitCommitSha1Bytes
import kokellab.utils.core._
import kokellab.valar.core.loadDb

package object core {

	private implicit val db = loadDb()

	import kokellab.valar.core.Tables._
	import kokellab.valar.core.Tables.profile.api._

	lazy val lorienCommitHash: Array[Byte] = thisGitCommitSha1Bytes

	def indicate(b: Boolean): Int = if (b) 1 else 0

	trait ThorondorException extends Exception
	trait LorienException extends ThorondorException
	class InconsistencyException(message: String = null) extends Exception(message) with ThorondorException
	class AmbiguousException(message: String = null) extends Exception(message) with ThorondorException

}
