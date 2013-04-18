package controllers

import models.FairyTale
import models.Lead
import models.User
import models.Customer
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import org.joda.time.DateTime
import play.api.libs.json
import play.api.libs.MimeTypes
import play.data.DynamicForm
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData.FilePart

object InternalFairyTale extends Controller with Secured {
  
  val fairyTaleForm = Form(
    mapping(
        "id" -> optional[Int](number),
        "customerId" -> number,
        "name" -> text,
        "dueDate" -> jodaDate("yyyy-MM-dd"),
        "briefing" -> text
    )(FairyTale.apply)(FairyTale.unapply)
  )
  
  val leadForm = Form(
    mapping(
        "id" -> optional[Int](number),
        "fairyTaleId" -> number,
        "name" -> text,
        "soundFile" -> optional[String](text),
        "imageFile" -> optional[String](text),
        "story" -> optional[String](text),
        "anchoring" -> optional[String](text),
        "priority" -> number
    )(Lead.apply)(Lead.unapply)
  )
	
  def fairyTales (customerId: Int) = IsAuthenticated { username => _ =>
    User.findByEmail(username).map { user =>
    	Ok(views.html.Internal.fairytales(customerId, FairyTale.findAllByCustomer(customerId), fairyTaleForm))
    }.getOrElse(Forbidden)
  }
  
  def saveFairyTale (customerId: Int) = Action { implicit request =>
  	fairyTaleForm.bindFromRequest.fold(
	  errors => BadRequest(views.html.Internal.fairytales(customerId, FairyTale.findAllByCustomer(customerId), errors)),
	  fairyTale => {
	    FairyTale.create(fairyTale)
	    Redirect(routes.InternalFairyTale.fairyTales(customerId))
	  }
  	)
  }
	
  //TODO: Return error if None!
  def fairyTale (id: Int) = IsAuthenticated { username => _ =>
    User.findByEmail(username).map { user =>
      FairyTale.findById(id) match {
        case None => BadRequest(views.html.Internal.customers(Customer.findAll, InternalCustomer.customerForm))
        case Some (fairyTale) => Ok(views.html.Internal.fairytale(fairyTale, leadForm))
      }    	
    }.getOrElse(Forbidden)
  }
  
  def leadImagesPath(fairytaleId: Int) = "./public/img/fairytales/" + fairytaleId + "/leads/"
  
  def saveImageToDisk(pic: FilePart[TemporaryFile], fairytaleId: Int) = {
    import java.io.File
      	 
  	val fileExtension = pic.filename.split('.').takeRight(1).headOption match {
  	  case None => ""
  	  case Some (head) => "." + head
  	}
  	val now = DateTime.now()
  	val fileName = now.toString() + fileExtension
  	
  	
  	val file = leadImagesPath(fairytaleId) + fileName
  	pic.ref.moveTo(new File(file))
  	
  	fileName
  }
  
  def newLead = Action(parse.multipartFormData) { implicit request =>
  	val form = leadForm.bindFromRequest();
  	val lead = form.get
  	val picOpt = request.body.file("leadImage")
  
  	picOpt match {
  	  case None => BadRequest("No file uploaded.") 
  	  case Some (pic) =>	      
  	    
  	    val fileIdent = saveImageToDisk(pic, lead.fairyTaleId)

  	  	lead.imageFile = Some(fileIdent)	
  	  	//Create lead
  	  	val created = Lead.create(lead)
	    Redirect(routes.InternalFairyTale.fairyTale(created.fairyTaleId))
	}	  
  }
  
  def getLead (id: Int) = Action { implicit request =>
    Lead.findById(id) match {
      case None => BadRequest("Error")
      case Some (lead) => Ok(Lead.json(lead))
    }
  }
  
  def uploadAudio = Action(parse.multipartFormData) { implicit request =>
    val lead = Lead.findById(request.body.asFormUrlEncoded.get("id").get(0).toInt).get
  	val soundOpt = request.body.file("leadAudio")
  
  	soundOpt match {
  	  case None => BadRequest("No file uploaded.") 
  	  case Some (audio) =>	      	  
  	  	import java.io.File
      	 
  	  	val fileExtension = audio.filename.split('.').takeRight(1).headOption match {
  	  	  case None => ""
  	  	  case Some (head) => "." + head
  	  	}
  	  	val now = DateTime.now()
  	  	val fileName = now.toString() + fileExtension
  	  	
  	  	val file = "./public/audio/fairytales/" + lead.fairyTaleId + "/leads/" + lead.id.get + "/" + fileName
  	  	audio.ref.moveTo(new File(file))

  	  	lead.soundFile = Some(fileName)
  	  	//Update lead
  	  	Lead.update(lead);
  	  	Redirect(routes.InternalFairyTale.fairyTale(lead.fairyTaleId))
	}	  
  }
  
  def updateLeadWithImage = Action(parse.multipartFormData) { implicit request =>
    val lead = Lead.findById(request.body.asFormUrlEncoded.get("id").get(0).toInt).get
    
    val imageOpt = request.body.file("image_file")
    
    def doTheUpdateWithImagePath(path: Option[String]) = {
      val anchoring = request.body.asFormUrlEncoded.get("anchoring").get(0)
		    
	  lead.anchoring = Some(anchoring)
	  if(path.isDefined)
		  lead.imageFile 
	  Lead.update(lead)
    }
    
    lead.imageFile match {
      case None => BadRequest("No file exists on lead.") 
      case Some(existingImageFile) =>
        imageOpt match {
          case None =>
            doTheUpdateWithImagePath(None)
            Redirect(routes.InternalFairyTale.fairyTale(lead.fairyTaleId))
          case Some(pic) =>
            val imagePath = saveImageToDisk(pic, lead.fairyTaleId)
          
            doTheUpdateWithImagePath(Some(imagePath))
            Redirect(routes.InternalFairyTale.fairyTale(lead.fairyTaleId))
      }
    }
  } 
  
  def updateLeadWithStory = Action(parse.multipartFormData) { implicit request =>
    val lead = Lead.findById(request.body.asFormUrlEncoded.get("id").get(0).toInt).get
    val story = request.body.asFormUrlEncoded.get("story").get(0)
    
    lead.story = Some(story)
    
    Lead.update(lead)
    
    Redirect(routes.InternalFairyTale.fairyTale(lead.fairyTaleId))
  }
  
  
}








