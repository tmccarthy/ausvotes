package au.id.tmm.ausvotes.api.logback

import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.classic.spi.ILoggingEvent

// This is needed because of https://stackoverflow.com/questions/35765615/replace-with-a-newline-n-using-replacepr-t-conversion-in-logback-x
class ApiLogbackLayout extends PatternLayout {
  setPattern("""%d{yyyy-MM-dd HH:mm:ss} level=%-5level %replace(%msg){'\n','\\r'} \\r%replace(%ex){'\n','\\r'}%n""")

  override def doLayout(event: ILoggingEvent): String =
    super.doLayout(event).replace("\\r", "\r")
}
