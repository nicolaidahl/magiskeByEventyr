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
  
  def fairyTales (customerId: Int) = IsAuthenticated { username => _ =>
    User.findByEmail(username).map { user =>
    	Ok(views.html.Internal.fairytales(customerId, FairyTale.findAllByCustomer(customerId), FairyTale.form))
    }.getOrElse(Forbidden)
  }
  
  def saveFairyTale (customerId: Int) = IsAuthenticated { _ => implicit request =>
  	FairyTale.form.bindFromRequest.fold(
	  errors => BadRequest(views.html.Internal.fairytales(customerId, FairyTale.findAllByCustomer(customerId), errors)),
	  fairyTale => {
	    FairyTale.create(fairyTale)
	    Redirect(routes.InternalFairyTale.fairyTales(customerId))
	  }
  	)
  }
  
  def deleteFairyTale (id: Int, customerId: Int) = IsAuthenticated { _ => implicit request =>
  	val fairyTale = FairyTale.findById(id).get;
    FairyTale.delete(fairyTale);
    Redirect(routes.InternalFairyTale.fairyTales(fairyTale.customerId))
  }
	
  //TODO: Return error if None!
  def fairyTale (id: Int, leadId: Int, tab: String) = IsAuthenticated { username => _ =>
    User.findByEmail(username).map { user =>
      FairyTale.findById(id) match {
        case None => BadRequest(views.html.Internal.customers(Customer.findAll, InternalCustomer.customerForm, user.userType))
        case Some (fairyTale) => Ok(views.html.Internal.fairytale(fairyTale, Lead.form, user.userType, leadId, tab))
      }    	
    }.getOrElse(Forbidden)
  }
  
  def publish = IsAuthenticated(parse.multipartFormData) { _ => implicit request =>
    val fairyTale = FairyTale.findById(request.body.asFormUrlEncoded.get("id").get(0).toInt).get
    fairyTale.published = true
    FairyTale.update(fairyTale)
    Redirect(routes.Application.index)
  }
  
  def unpublish = IsAuthenticated(parse.multipartFormData) { _ => implicit request =>
    val fairyTale = FairyTale.findById(request.body.asFormUrlEncoded.get("id").get(0).toInt).get
    fairyTale.published = false
    FairyTale.update(fairyTale)
    Redirect(routes.InternalFairyTale.fairyTale(fairyTale.id.get, -1, ""))
  }
  
  def updateWithCredits (id: Int, credits: String) = IsAuthenticated { _ => implicit request =>
    val fairyTale = FairyTale.findById(id).get;
    fairyTale.credits = Some(credits);
    FairyTale.update(fairyTale);
    Ok("");
  }
  
  def updateWithInfo (id: Int, name: String, dueDate: String, briefing: String) = IsAuthenticated { _ => implicit request =>
    val fairyTale = FairyTale.findById(id).get;
    fairyTale.name = name;
    fairyTale.dueDate = DateTime.parse(dueDate);
    fairyTale.briefing = briefing;
    FairyTale.update(fairyTale);
    Ok("");
  }
}








