package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.Application.index("Hello World!"))
  }
  
  def about = Action {
    Ok("About")
  }
  
  def fairyTale (id: Long) = Action {
	Ok("Adventure: " + id)
  }  
}