package au.id.tmm.ausvotes.core

package object tallies {

  type Tally0[A] = A
  type Tally1[G1, A] = Tally[G1, A]
  type Tally2[G1, G2, A] = Tally[G1, Tally[G2, A]]
  type Tally3[G1, G2, G3, A] = Tally[G1, Tally2[G2, G3, A]]
  type Tally4[G1, G2, G3, G4, A] = Tally[G1, Tally3[G2, G3, G4, A]]

}
