package toolbox

import play.api.mvc.MultipartFormData.FilePart
import play.api.libs.Files.TemporaryFile
import org.joda.time.DateTime
import java.io.File

object IOHelper {
  
  object FileType extends Enumeration {
      type FileType = Value
      val Image, Audio = Value
    }
  import FileType._
  
  def leadImagePath(fairytaleId: Int) = "./public/fairytales/" + fairytaleId + "/img/"
  def leadAudioPath(fairytaleId: Int) = "./public/fairytales/" + fairytaleId + "/audio/"
  
  def deleteFromFairyTale(fairytaleId: Int, fileName: String, fileType: FileType) = {
    
    val fileToDelete = fileType match {
      case Image => new File(leadImagePath(fairytaleId) + fileName) 
      case Audio => new File(leadAudioPath(fairytaleId) + fileName)
    } 
    
    fileToDelete.delete()
  }
  
  def saveToFairyTale(fairytaleId: Int, tempFile: FilePart[TemporaryFile], fileType : FileType) = {
      	 
  	val fileExtension = tempFile.filename.split('.').takeRight(1).headOption match {
  	  case None => ""
  	  case Some (head) => "." + head
  	}
  	val now = DateTime.now()
  	val fileName = now.toString() + fileExtension
  	
  	val fileToSave = fileType match {
      case Image => new File(leadImagePath(fairytaleId) + fileName) 
      case Audio => new File(leadAudioPath(fairytaleId) + fileName)
    } 
  	
  	tempFile.ref.moveTo(fileToSave)
  	
  	fileName
  }

}