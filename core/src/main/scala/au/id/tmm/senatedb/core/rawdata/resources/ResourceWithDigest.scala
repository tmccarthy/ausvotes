package au.id.tmm.senatedb.core.rawdata.resources

import au.id.tmm.utilities.hashing.Digest

trait ResourceWithDigest extends Resource {
  def digest: Digest
}
