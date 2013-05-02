package controllers

import play.api._
import play.api.mvc._
import models.Customer
import models.FairyTale
import scala.collection.immutable.HashMap
import play.api.libs.json.Json
import models.Lead

object Application extends Controller {
  
  def index = Action {
    val customers = Customer.findAll
    val fairyTaleMap = new HashMap[Option[String], Option[Seq[FairyTale]]] ++ (customers map (c => { 
    	if (FairyTale.findAllPublishedByCustomer(c.id.get).length > 0) 
    	  (Some(c.name), Some(FairyTale.findAllPublishedByCustomer(c.id.get)))
	    else
	      (None, None)
      }))
    Ok(views.html.Application.index(fairyTaleMap))
  }
  
  def about = Action {
    Ok("About")
  }
  
  def fairyTale (id: Int) = Action {
	Ok(views.html.Application.fairytale(id))
  }
  
  def javascriptRoutes = Action { implicit request =>
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        controllers.routes.javascript.Application.getLeads
      )
    ).as(JAVASCRIPT)
  }
  
  def getLeads(fairyTaleId: Int) = Action { implicit request =>
    Ok(Json.toJson(FairyTale.getLeads(fairyTaleId).map(l => 
      		Lead.json(l)
    	)
      )
    )
  }
}