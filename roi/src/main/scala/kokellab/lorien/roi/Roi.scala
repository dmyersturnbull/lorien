package kokellab.lorien.roi

case class Roi(x0: Int, x1: Int, y0: Int, y1: Int) {
	lazy val coordinates: (Int, Int, Int, Int) = (x0, x1, y0, y1)
	lazy val width: Int = x1 - x0
	lazy val height: Int = y1 - y0
	def xyWidthHeight: (Int, Int, Int, Int) = (x0, y0, x1 - x0, y1 - y0)
	def coordinateString = s"($x0,$x1,$y0,$y1)"
}