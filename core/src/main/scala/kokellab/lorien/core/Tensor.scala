package kokellab.lorien.core

trait TensorDef {
	def order: TensorOrder
}

object TensorDef {

	val scalar = new TensorDef with DimensionalityConstraints with ValenceConstraint {
		override val valence: Valence = Valence(0, 0)
		override val order: TensorOrder = TensorOrder.Scalar
	}
	val freeVector = new TensorDef with DimensionalityConstraints with ValenceConstraint {
		override val order: TensorOrder = TensorOrder.Vector
	}
	val timeDependentVector = new TensorDef with DimensionalityConstraints with ValenceConstraint {
		override def matchesFrames(dimension: Int) = true
		override val order: TensorOrder = TensorOrder.Vector
	}
}

/**
  * Defaults to all covarient.
  */
trait ValenceConstraint extends TensorDef {
	def valence: Valence = Valence(order.n, 0)
}

/**
  * The function constraint (Int => Boolean) just refers to <em>general</em> constraints, such as n < 100
  * Defaults to allowed everywhere and non time-dependent.
  */
trait DimensionalityConstraints extends (Int => Boolean) with TensorDef {
	override def apply(dimension: Int): Boolean = true
	def matchesFrames(dimension: Int): Boolean = false
}

case class TensorOrder(n: Int) {
	require(n > -1)
}

object TensorOrder {
	case object Scalar extends TensorOrder(0)
	case object Vector extends TensorOrder(1)
	case object Matrix extends TensorOrder(2)
}

case class Valence(nCovarient: Int, nContravarient: Int) {
	require(nCovarient > 0)
	require(nContravarient > 0)
	def this(covariance: Boolean*) = this(covariance count (b => b), covariance count (b => !b))
}