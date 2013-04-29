package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import models.Lead
import org.joda.time.DateTime
import play.api.mvc.MultipartFormData.FilePart
import play.api.libs.Files.TemporaryFile
import toolbox.LocalFileHandler
import models.FairyTale
import play.api.libs.json.Json
import toolbox.GeotagRetriever
import plugins.S3Plugin
import toolbox.AmazonS3FileHandler
import java.io.File

object InternalLead extends Controller with Secured {
  
  val fileSaver = if (S3Plugin.isEnabled) { AmazonS3FileHandler } else { LocalFileHandler } 
  
  def newLead = IsAuthenticated(BodyParsers.parse.multipartFormData) { _ => implicit request =>
  	val form = Lead.form.bindFromRequest();
  	val lead = form.get
  	val picOpt = request.body.file("leadImage")
  	
  	val fileName = if (picOpt.isDefined) Some(fileSaver.saveToFairyTale(picOpt.get)) else None
  	if (picOpt.isDefined && fileName.isDefined) {
  	  lead.imageFile = fileName
  	  val location = GeotagRetriever.getCoordinates(
  	      if (S3Plugin.isEnabled) 
  	        picOpt.get.ref.file 
  	      else 
  	        new File(LocalFileHandler.localPrefixPath + fileName.get))
  	  if (location.isDefined) {
  	    lead.latitude = Some(location.get.getLatitude())
  	    lead.longitude = Some(location.get.getLongitude())
  	  } else {
  	    lead.latitude = None
  	    lead.longitude = None
  	  }
  	} else {
  	  lead.imageFile = None
  	  lead.latitude = None
  	  lead.longitude = None
  	} 
  
  	Lead.create(lead)
    Redirect(routes.InternalFairyTale.fairyTale(lead.fairyTaleId))
  }
  
  def getLead (id: Int) = IsAuthenticated { _ => implicit request =>
    Lead.findById(id) match {
      case None => BadRequest("Error")
      case Some (lead) => Ok(Lead.json(lead))
    }
  }
  
  def setLeadPriority (id: Int, priority: Int) = Action { implicit request =>
    val updated = Lead.setPriority(id, priority)
    Ok(Json.toJson(FairyTale.getLeads(updated.fairyTaleId).map(l => Map(
        		"id" -> Json.toJson(l.id),
        		"priority" -> Json.toJson(l.priority)
        	
          )
    	)
      )
    )
  }
  
  def updateLeadWithAudio = IsAuthenticated(parse.multipartFormData) { _ => implicit request =>
    val lead = Lead.findById(request.body.asFormUrlEncoded.get("id").get(0).toInt).get
  	val soundOpt = request.body.file("leadAudio")
  
  	soundOpt match {
  	  case None => BadRequest("No file uploaded.") 
  	  case Some (audio) =>
  	    if (lead.soundFile.isDefined) fileSaver.deleteFromFairyTale(lead.soundFile.get)
  	    
  	    lead.soundFile = Some(fileSaver.saveToFairyTale(audio))
        Lead.update(lead)
  	  	
  	  	Redirect(routes.InternalFairyTale.fairyTale(lead.fairyTaleId))
	}	  
  }
  
  def updateLeadWithImage = IsAuthenticated(parse.multipartFormData) { _ => implicit request =>
    val lead = Lead.findById(request.body.asFormUrlEncoded.get("id").get(0).toInt).get
    import java.io.File
    def doTheUpdateWithImagePath(file: Option[File], fileName: Option[String]) = {
      val anchoring = request.body.asFormUrlEncoded.get("anchoring").get(0)
		    
	  lead.anchoring = Some(anchoring)
	  if (file.isDefined && fileName.isDefined) {
	    lead.imageFile = fileName
	    val location = GeotagRetriever.getCoordinates(file.get)
	  	if (location.isDefined) {
	  	  lead.latitude = Some(location.get.getLatitude())
	  	  lead.longitude = Some(location.get.getLongitude())
	  	} else {
	  	  lead.latitude = None
	  	  lead.longitude = None
	  	}
	  }
		  
	  Lead.update(lead)
    }
    
    
    val imageOpt = request.body.file("image_file")
    
    imageOpt match {
          case None =>
            doTheUpdateWithImagePath(None, None)
          case Some(image) =>
            if (lead.imageFile.isDefined) fileSaver.deleteFromFairyTale(lead.imageFile.get)
            
            val imageFile = fileSaver.saveToFairyTale(image)
            
            doTheUpdateWithImagePath(Some(image.ref.file), Some(imageFile))
    }
    Redirect(routes.InternalFairyTale.fairyTale(lead.fairyTaleId))
  } 
  
  def updateLeadWithStory = IsAuthenticated(parse.multipartFormData) { _ => implicit request =>
    val lead = Lead.findById(request.body.asFormUrlEncoded.get("id").get(0).toInt).get
    val story = request.body.asFormUrlEncoded.get("story").get(0)
    
    lead.story = Some(story)
    
    Lead.update(lead)
    
    Redirect(routes.InternalFairyTale.fairyTale(lead.fairyTaleId))
  }
  
  def approveLead (id: Int) = Action { implicit request =>
    val lead = Lead.findById(id).get;
    lead.approved = true;
    Lead.update(lead)
    //Find the priority of the lead which has the lowest priority among all un-approved leads
    val prio = FairyTale.getLeads(lead.fairyTaleId).filter(l => !l.approved).foldLeft(Int.MaxValue)((p, e) => p min e.priority)
    //Return the priority if one was found (not Int.maxvalue) - otherwise -1
    Ok(Json.toJson(Map(
    		"nextLeadPriority" -> Json.toJson(if (prio == Int.MaxValue) -1 else prio)
    )))
  }
  
  def disapproveLead = Action(parse.multipartFormData) { implicit request =>
    Ok("TODO: Implement")
  }

}