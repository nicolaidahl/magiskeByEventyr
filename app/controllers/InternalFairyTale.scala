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
  
  def newLead = Action(parse.multipartFormData) { implicit request =>
  	val form = leadForm.bindFromRequest();
  	val lead = form.get
  	val picOpt = request.body.file("leadImage")
  
  	picOpt match {
  	  case None => BadRequest("No file uploaded.") 
  	  case Some (pic) =>	      	  
  	  	import java.io.File
      	 
  	  	val now = DateTime.now()
  	  	val fairyIdPlusNow = lead.fairyTaleId + now.toString()
  	  	
  	  	val fileExtension = pic.filename.split('.').takeRight(1).headOption match {
  	  	  case None => ""
  	  	  case Some (head) => "." + head
  	  	}
  	  	
  	  	val filename = "./public/img/fairytales/" + lead.fairyTaleId + "/leads/" + fairyIdPlusNow + fileExtension
  	  	pic.ref.moveTo(new File(filename))


  	  	lead.imageFile = Some(fairyIdPlusNow + fileExtension)
  	  	val created = Lead.create(lead)
	      
  	  	Redirect(routes.InternalFairyTale.fairyTale(lead.fairyTaleId))
	}	  
  }
  
  def getLead (id: Int) = Action { implicit request =>
    Lead.findById(id) match {
      case None => BadRequest("Error")
      case Some (lead) => Ok(Lead.json(lead))
    }
  }
}