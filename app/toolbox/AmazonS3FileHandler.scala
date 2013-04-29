package toolbox

import plugins.S3Plugin
import play.api.mvc.MultipartFormData.FilePart
import java.io.File
import play.api.libs.Files.TemporaryFile
import java.util.UUID


object AmazonS3FileHandler extends AbstractFileHandler {
  
  override def deleteFromFairyTale(fileName: String) = S3Plugin.amazonS3.deleteObject(S3Plugin.s3Bucket, fileName)
  
  override def saveToFairyTale(tempFile: FilePart[TemporaryFile]) : String = {
    
    val fileExtension = extension(tempFile)
    val fileName = UUID.randomUUID().toString() + fileExtension
    
    S3Plugin.amazonS3.putObject(S3Plugin.s3Bucket, fileName, tempFile.ref.file)
    
    fileName
  }
  
  override def getPrefixPath : String = {
    "https://s3-eu-west-1.amazonaws.com/" + S3Plugin.s3Bucket + "/"
  }
}