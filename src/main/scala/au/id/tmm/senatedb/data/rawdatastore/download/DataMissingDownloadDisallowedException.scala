package au.id.tmm.senatedb.data.rawdatastore.download

import java.net.URL

class DataMissingDownloadDisallowedException(val targetUrl: URL)
  extends RuntimeException(s"Resource at $targetUrl needs to be downloaded, but downloading was disallowed") {

}
