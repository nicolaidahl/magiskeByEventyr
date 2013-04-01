package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import models.User
import models.Customer
import models.FairyTale
import play.mvc.Http.Context
import views.html.defaultpages.badRequest

object Internal extends Controller with Secured {
	
	def index = IsAuthenticated { 
	  username => _ =>
	    User.findByEmail(username).map { user =>
	    	Ok(views.html.Internal.index(Customer.findAll, customerForm))
	    }.getOrElse(Forbidden)
	}
	

	
	def customerForm = Form(
	    mapping(
	        "id" -> optional[Int](number),
	        "name" -> text
	    )(Customer.apply)(Customer.unapply)
	)
	
	def saveCustomer = Action { implicit request =>
	  	customerForm.bindFromRequest.fold(
	  			errors => BadRequest(views.html.Internal.index(Customer.findAll, errors)),
	  			customer => {
	  			  Customer.create(customer)
	  			  Redirect(routes.Internal.index)
	  			}
	  	)
	}
	
	def fairyTales (customerId: Int) = IsAuthenticated { username => _ =>
	    User.findByEmail(username).map { user =>
	    	Ok(views.html.Internal.fairytales(customerId, FairyTale.findAllByCustomer(customerId), fairyTaleForm))
	    }.getOrElse(Forbidden)
	}
	
	def fairyTaleForm = Form(
	    mapping(
	        "id" -> optional[Int](number),
	        "customerId" -> number,
	        "name" -> text,
	        "dueDate" -> jodaDate("yyyy-MM-dd"),
	        "briefing" -> text
	    )(FairyTale.apply)(FairyTale.unapply)
	)
	
	def saveFairyTale (customerId: Int) = Action { implicit request =>
	  	fairyTaleForm.bindFromRequest.fold(
	  			errors => BadRequest(views.html.Internal.fairytales(customerId, FairyTale.findAllByCustomer(customerId), errors)),
	  			fairyTale => {
	  			  FairyTale.create(fairyTale)
	  			  Redirect(routes.Internal.fairyTales(customerId))
	  			}
	  	)
	}
	
	def fairyTale (customerId: Int, id: Int) = IsAuthenticated { username => _ =>
	    User.findByEmail(username).map { user =>
	    	Ok(views.html.Internal.fairytale())
	    }.getOrElse(Forbidden)
	}
	
	def newAdventure = Action {
		implicit request => Ok("Internal new adventure")
	}
	
	// -- Authentication

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => User.authenticate(email, password).isDefined
    })
  )

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(views.html.Internal.login(loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.Internal.login(formWithErrors)),
      user => Redirect(routes.Internal.index).withSession("email" -> user._1)
    )
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.index).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }
}

/**
 * Internaæ security features implemented
 */
trait Secured {
  
  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("email")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Internal.login)
  
  // --
  
  /** 
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  /**
   * Check if the connected user is a member of this project.
   * TODO: May be relevant to see if "creators" may edit for a certain customer
   */
  /*def IsMemberOf(project: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
    if(Project.isMember(project, user)) {
      f(user)(request)
    } else {
      Results.Forbidden
    }
  }*/
}