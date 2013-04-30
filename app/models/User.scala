package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(email: String, name: String, password: String, userType: String)

object User {
  
  // -- Parsers
  
  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[String]("mbe_user.email") ~
    get[String]("mbe_user.name") ~
    get[String]("mbe_user.password") ~
    get[String]("mbe_user.type") map {
      case email~name~password~userType => User(email, name, password, userType)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select email, name, password, salt, type::varchar from mbe_user where email = {email}").on(
        'email -> email
      ).as(User.simple.singleOpt)
    }
  }
  
  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select email, name, password, salt, type::varchar from mbe_user").as(User.simple *)
    }
  }
  
  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select email, name, password, salt, type::varchar from mbe_user where 
         email = {email} and password = {password}
        """
      ).on(
        'email -> email,
        'password -> password
      ).as(User.simple.singleOpt)
    }
  }
   
  /**
   * Create a User.
   */
  def create(user: User): User = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into mbe_user values (
            {email}, {name}, {password}, {type}
          )
        """
      ).on(
        'email -> user.email,
        'name -> user.name,
        'password -> user.password,
        'type -> user.userType
      ).executeUpdate()
      
      user
      
    }
  }
  
}
