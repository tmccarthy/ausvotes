package au.id.tmm.utilities.hashing

import java.lang.Math._

import scala.math.{floor, pow, sqrt}

/**
 * A collection of <a href="https://en.wikipedia.org/wiki/Pairing_function">pairing functions</a>
 */
object Pairing {

  /**
   * A <a href="https://en.wikipedia.org/wiki/Pairing_function">pairing functions</a>, which uniquely encodes two
   * natural numbers into a single number.
   */
  trait PairingFunction {

    /**
     * Uniquely encode the given two numbers to another natural number.
     */
    def pair(left: Long, right: Long): Long

    /**
     * Uniquely encode the given three numbers to another natural number.
     */
    def combine(left: Long, centre: Long, right: Long): Long = combineN(left, centre, right)

    private def combineN(inputs: Long*): Long = inputs.reduceLeft(pair)

    /**
     * Reconstruct the paired numbers that were encoded to the given number.
     */
    def invert(paired: Long): (Long, Long)

    /**
     * Reconstruct the combined numvers that were encoded to the given number.
     */
    def invert3(combined: Long): (Long, Long, Long) = {
      val inverted = invertN(combined, 3)

      (inverted(0), inverted(1), inverted(2))
    }

    private def invertN(combined: Long, numOutputs: Int): List[Long] = {
      invertRemaining(Nil, combined, numOutputs)
    }

    @scala.annotation.tailrec
    private def invertRemaining(outputsSoFar: List[Long], lastCombined: Long, numRemainingOutputs: Int): List[Long] = {
      if (numRemainingOutputs == 0) {
        outputsSoFar
      } else if (numRemainingOutputs == 1) {
        invertRemaining(lastCombined +: outputsSoFar, 0, 0)
      } else {
        val (nextCombined, newOutput) = invert(lastCombined)

        invertRemaining(newOutput +: outputsSoFar, nextCombined, numRemainingOutputs - 1)
      }
    }
  }

  /**
   * An implementation of the <a href="http://szudzik.com/ElegantPairing.pdf">Szudzik pairing function</a>.
   */
  object Szudzik extends PairingFunction {

    override def pair(x: Long, y: Long): Long = {
      require(x >= 0)
      require(y >= 0)

      if (y > x) {
        addExact(multiplyExact(y, y), x)
      } else {
        addExact(addExact(multiplyExact(x, x), x), y)
      }
    }

    override def invert(z: Long): (Long, Long) = {
      require(z > 0)

      val q = floor(sqrt(z)).toLong
      val l = z - pow(q, 2).toLong

      if (l < q) {
        (l, q)
      } else {
        (q, l - q)
      }
    }

  }

}
