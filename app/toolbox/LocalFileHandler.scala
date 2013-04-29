package toolbox

import play.api.mvc.MultipartFormData.FilePart
import play.api.libs.Files.TemporaryFile
import org.joda.time.DateTime
import java.io.File
import java.util.UUID

object LocalFileHandler extends AbstractFileHandler {
  
  override def getPrefixPath = "/assets/fairytales/"
    
  val localPrefixPath = "./public/fairytales/"
  
  override def deleteFromFairyTale(fileName: String) = {
    new File(localPrefixPath + fileName).delete()
  }
  
  override def saveToFairyTale(tempFile: FilePart[TemporaryFile]) = {
    val fileExtension = extension(tempFile)
  	val fileName = UUID.randomUUID().toString() + fileExtension
  	
  	tempFile.ref.moveTo(new File(localPrefixPath + fileName))
  	tempFile.ref.file.createNewFile()
  	
  	fileName
  }
}