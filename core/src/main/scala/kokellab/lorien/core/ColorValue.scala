package kokellab.lorien.core

trait ColorValue[V, D <: BitDepth] extends Seq[V]

class Rgb[V, D <: BitDepth](val red: V, val green: V, val blue: V) extends ColorValue[V, D] {
	override def length: Int = 3
	override def iterator: Iterator[V] = Seq(red, green, blue).iterator
	override def apply(idx: Int): V = idx match {
		case 0 => red
		case 1 => green
		case 2 => blue
		case _ => throw new IndexOutOfBoundsException(s"Index $idx is out of bounds for type Rgb(red, green, blue)")
	}
}
class RgbU8(override val red: Byte, override val green: Byte, override val blue: Byte) extends Rgb[Byte, U8](red, green, blue)
object RgbU8 {
	def apply(red: Byte, green: Byte, blue: Byte) = new RgbU8(red, green, blue)
}
class RgbU16(override val red: Short, override val green: Short, override val blue: Short) extends Rgb[Short, U16](red, green, blue)
object Rgb {
	def apply(red: Short, green: Short, blue: Short) = new RgbU16(red, green, blue)
}

sealed class Grayscale[V, D <: BitDepth](val value: V) extends ColorValue[V, D] {
	override def length: Int = 1
	override def iterator: Iterator[V] = Seq(value).iterator
	override def apply(idx: Int): V = idx match {
		case 0 => value
		case _ =>  throw new IndexOutOfBoundsException(s"Index $idx is out of bounds for type Rgb(value)")
	}
}
class GrayscaleU8(override val value: Byte) extends Grayscale[Byte, U8](value)
object GrayscaleU8 {
	def apply(value: Byte) = new GrayscaleU8(value: Byte)
}
class GrayscaleU16(override val value: Short) extends Grayscale[Short, U16](value)
object GrayscaleU16 {
	def apply(value: Short) = new GrayscaleU16(value: Short)
}
class GrayscaleU24(override val value: Int) extends Grayscale[Int, U24](value)
object GrayscaleU24 {
	def apply(value: Int) = new GrayscaleU24(value: Int)
}
class GrayscaleU32(override val value: Int) extends Grayscale[Int, U32](value)
object GrayscaleU32 {
	def apply(value: Int) = new GrayscaleU32(value: Int)
}


sealed trait BitDepth { val nBits: Short }
trait U8 extends BitDepth { override val nBits: Short = 8.toShort }
trait U16 extends BitDepth { override val nBits: Short = 16.toShort }
trait U24 extends BitDepth  { override val nBits: Short = 24.toShort }
trait U32 extends BitDepth { override val nBits: Short = 32.toShort }
