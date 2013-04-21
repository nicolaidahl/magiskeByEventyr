package controllers

import play.api.mvc.Action
import play.api.mvc.BodyParsers.parse
import java.io.File
import play.api.mvc.Controller

object InternalAjax extends Controller with Secured {
  
  def saveLeadImage = IsAuthenticated(parse.temporaryFile) { _ => request =>
    request.body.moveTo(new File("/tmp/picture"))
    Ok("File uploaded")
  }
  
  
}

