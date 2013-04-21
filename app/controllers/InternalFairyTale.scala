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
	
  //TODO: Return error if None!
  def fairyTale (id: Int) = IsAuthenticated { username => _ =>
    User.findByEmail(username).map { user =>
      FairyTale.findById(id) match {
        case None => BadRequest(views.html.Internal.customers(Customer.findAll, InternalCustomer.customerForm))
        case Some (fairyTale) => Ok(views.html.Internal.fairytale(fairyTale, Lead.form, user.userType))
      }    	
    }.getOrElse(Forbidden)
  }
}








