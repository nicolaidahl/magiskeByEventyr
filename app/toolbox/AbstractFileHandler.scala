package toolbox

import play.api.mvc.MultipartFormData.FilePart
import play.api.libs.Files.TemporaryFile

trait AbstractFileHandler {
  
  def deleteFromFairyTale(fileName: String) : Unit
  
  def saveToFairyTale(tempFile: FilePart[TemporaryFile]) : String
  
  def getPrefixPath : String
  
  def extension(tempFile: FilePart[TemporaryFile]) = tempFile.filename.split('.').takeRight(1).headOption match {
  	case None => ""
    case Some (head) => "." + head
  }
}