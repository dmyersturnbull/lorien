package kokellab.lorien.core

case class Rgb[Byte](red: Byte, green: Byte, blue: Byte) extends ColorValue[Byte]

case class Grayscale(value: Byte) extends ColorValue[Byte]

trait ColorValue[A]
