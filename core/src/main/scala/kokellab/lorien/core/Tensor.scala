package kokellab.lorien.core

trait TensorDef {
	def order: TensorOrder
}

object TensorDef {

	val scalar = new TensorDef with ValenceConstraint {
		override val valence: Valence = Valence(0, 0)
		override val order: TensorOrder = TensorOrder.Scalar
	}
	val freeVector = new TensorDef with ValenceConstraint {
		override val order: TensorOrder = TensorOrder.Vector
		override def valence: Valence = Valence(order.n, 0)
	}
	val timeDependentVector = new TensorDef with TimeDependence with ValenceConstraint {
		override val order: TensorOrder = TensorOrder.Vector
		override def timeDependentDimensions: Set[Int] = Set(0)
		override def valence: Valence = Valence(order.n, 0)
	}
	val freeMatrix = new TensorDef with ValenceConstraint {
		override val order: TensorOrder = TensorOrder.Matrix
		override def valence: Valence = Valence(order.n, 0)
	}
}

/**
  * Defaults to all covarient.
  */
trait ValenceConstraint extends TensorDef {
	def valence: Valence
}

trait DimensionalityConstraints extends TensorDef {
	def allowed(dimension: Int): Boolean
}

trait TimeDependence extends TensorDef {
	def timeDependentDimensions: Set[Int]
}

class TensorOrder(val n: Int) {
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