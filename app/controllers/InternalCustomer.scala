package controllers

import models.User
import models.Customer

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

object InternalCustomer extends Controller with Secured {
	
  val customerForm = Form(
    mapping(
        "id" -> optional[Int](number),
        "name" -> text
    )(Customer.apply)(Customer.unapply)
  )
  
  def customers = IsAuthenticated { 
	  username => _ =>
	    User.findByEmail(username).map { user =>
	    	Ok(views.html.Internal.customers(Customer.findAll, customerForm))
	    }.getOrElse(Forbidden)
  }
	
  def saveCustomer = IsAuthenticated { _ => implicit request =>
  	customerForm.bindFromRequest.fold(
		errors => BadRequest(views.html.Internal.customers(Customer.findAll, errors)),
  			customer => {
  			  Customer.create(customer)
  			  Redirect(routes.InternalCustomer.customers)
  			}
  	)
  }
}