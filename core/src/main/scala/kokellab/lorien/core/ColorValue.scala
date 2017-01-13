package kokellab.lorien.core

case class Rgb[Byte](red: Byte, green: Byte, blue: Byte) extends ColorValue[Byte] {
	override def length: Int = 3
	override def iterator: Iterator[Byte] = Seq(red, green, blue).iterator
}

case class Grayscale(value: Byte) extends ColorValue[Byte] {
	override def length: Int = 1
	override def iterator: Iterator[Byte] = Seq(value).iterator
}

trait ColorValue[A <: Numeric[A]] extends Seq[A]
