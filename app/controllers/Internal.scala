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
import java.io.File
import models.Lead
import org.joda.time.DateTime
import java.security.MessageDigest

import play.api.libs.Files.TemporaryFile


object Internal extends Controller with Secured {
	
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
      user => Redirect(routes.InternalCustomer.customers).withSession("email" -> user._1)
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
  
  def javascriptRoutes() = Action { implicit request =>
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        controllers.routes.javascript.InternalLead.getLead
      )
    ).as(JAVASCRIPT)
  }
}

/**
 * Internal security features implemented
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
  
  /** 
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = 
    Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  /** 
   * Action for authenticated users.
   */
  def IsAuthenticated[A](parser: BodyParser[A])(f: => String => Request[A] => Result) = 
    Security.Authenticated(username, onUnauthorized) { user =>
    Action[A](parser)(request => f(user)(request))
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