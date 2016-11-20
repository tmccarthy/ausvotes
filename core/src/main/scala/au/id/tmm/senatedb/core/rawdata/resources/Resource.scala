package au.id.tmm.senatedb.core.rawdata.resources

import java.net.URL
import java.nio.file.Path

trait Resource {
  def url: URL
  def localFileName: Path
}
