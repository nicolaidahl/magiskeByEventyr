package controllers

import play.api._
import play.api.mvc._
import models.Customer
import models.FairyTale
import scala.collection.immutable.HashMap
import play.api.libs.json._
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
  
  def fairyTales (customerName: String) = Action {
    val customer = Customer.findByName(customerName);
    if (customer.isDefined) {
	  val fairyTales = FairyTale.findAllPublishedByCustomer(customer.get.id.get);
      if (fairyTales.length == 1) {
        Ok(views.html.Application.fairytale(fairyTales(0).id.get))
      } else if (fairyTales.isEmpty) {
    	Ok(views.html.Application.index(new HashMap[Option[String], Option[Seq[FairyTale]]]))
      } else {
        val fairyTaleMap = HashMap[Option[String], Option[Seq[FairyTale]]] (
        		Option(customer.get.name) -> Option(fairyTales) 
        )
        Ok(views.html.Application.index(fairyTaleMap))
      }
    } else {
      Ok(views.html.Application.index(new HashMap[Option[String], Option[Seq[FairyTale]]]))
    }
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
        controllers.routes.javascript.Application.getLeads,
        controllers.routes.javascript.Application.getCredits
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
  
  def getCredits(fairyTaleId: Int) = Action { implicit request =>
    val f = FairyTale.findById(fairyTaleId).get;
    Ok(JsObject(List("credits" -> (if (f.credits.isDefined) JsString(f.credits.get) else JsString("")))))
  }
}