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
	    	Ok(views.html.Internal.customers(Customer.findAll, customerForm, user.userType))
	    }.getOrElse(Forbidden)
  }
	
  def saveCustomer = IsAuthenticated { 
	  username => implicit request =>
	    User.findByEmail(username).map { user =>
	    		customerForm.bindFromRequest.fold(
					errors => BadRequest(views.html.Internal.customers(Customer.findAll, errors, user.userType)),
			  			customer => {
			  			  Customer.create(customer)
			  			  Redirect(routes.InternalCustomer.customers)
			  			}
			  	)
	    }.getOrElse(Forbidden) 
  }
}