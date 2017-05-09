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

}
