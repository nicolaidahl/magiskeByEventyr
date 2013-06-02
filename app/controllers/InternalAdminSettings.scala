package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import models.User

object InternalAdminSettings extends Controller with Secured {

  
  def settings = IsAdmin { _ => implicit request =>
    Ok(views.html.Internal.adminSettings(userForm, ""))
    
  }
  
  val userForm = Form(
    mapping(
        "email" -> text,
        "name" -> text,
        "password" -> text,
        "userType" -> text
    )(User.apply)
     (User.unapply)
  )
  
  
  def createUser = IsAdmin { _ => implicit request =>
    userForm.bindFromRequest.fold(
		errors => BadRequest(views.html.Internal.adminSettings(userForm, errors.errors.toString())),
  			user => {
  			  User.create(user)
  			  Redirect(routes.InternalAdminSettings.settings)
  			}
  	) 
  }
}