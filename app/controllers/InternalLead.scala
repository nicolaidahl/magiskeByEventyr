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
import toolbox.IOHelper
import models.FairyTale
import play.api.libs.json.Json

object InternalLead extends Controller with Secured {
  
  def newLead = IsAuthenticated(BodyParsers.parse.multipartFormData) { _ => implicit request =>
  	val form = Lead.form.bindFromRequest();
  	val lead = form.get
  	val picOpt = request.body.file("leadImage")
  	
  	lead.imageFile = if (picOpt.isDefined) Some(IOHelper.saveToFairyTale(lead.fairyTaleId, picOpt.get, IOHelper.FileType.Image)) else None
  
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
  	    if (lead.soundFile.isDefined) IOHelper.deleteFromFairyTale(lead.fairyTaleId, lead.soundFile.get, IOHelper.FileType.Audio)
  	    
  	    lead.soundFile = Some(IOHelper.saveToFairyTale(lead.fairyTaleId, audio, IOHelper.FileType.Audio))
        Lead.update(lead)
  	  	
  	  	Redirect(routes.InternalFairyTale.fairyTale(lead.fairyTaleId))
	}	  
  }
  
  def updateLeadWithImage = IsAuthenticated(parse.multipartFormData) { _ => implicit request =>
    val lead = Lead.findById(request.body.asFormUrlEncoded.get("id").get(0).toInt).get
    
    def doTheUpdateWithImagePath(path: Option[String]) = {
      val anchoring = request.body.asFormUrlEncoded.get("anchoring").get(0)
		    
	  lead.anchoring = Some(anchoring)
	  if(path.isDefined)
		  lead.imageFile = path
	  Lead.update(lead)
    }
    
    
    val imageOpt = request.body.file("image_file")
    
    imageOpt match {
          case None =>
            doTheUpdateWithImagePath(None)
          case Some(image) =>
            if (lead.imageFile.isDefined) IOHelper.deleteFromFairyTale(lead.fairyTaleId, lead.imageFile.get, IOHelper.FileType.Image)
            
            val imagePath = IOHelper.saveToFairyTale(lead.fairyTaleId, image, IOHelper.FileType.Image)
            
            doTheUpdateWithImagePath(Some(imagePath))
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
  
  def disapproveLead = Action(parse.multipartFormData) { implicit request =>
    Ok("TODO: Implement")
  }

}