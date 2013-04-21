package toolbox

import javaxt.io.Image
import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.GpsDirectory

object GeotagRetriever {
  
  def getCoordinates(image: java.io.File) = {
    
    val metadata = ImageMetadataReader.readMetadata(image)
  
    val gps = metadata.getDirectory(classOf[GpsDirectory])
    val location = gps.getGeoLocation()
    
    if (location == null)
      None
    else
      Some(location)
  
  }
}


object Test extends App {
  import GeotagRetriever._
  
  val file = new java.io.File("/IMG_1140.jpg")
  
  print(getCoordinates(file))
}