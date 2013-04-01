package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import java.util.Date

import org.joda.time.DateTime
import org.joda.convert._

case class FairyTale(id: Option[Int], customerId: Int, name: String, dueDate: DateTime, briefing: String)

object FairyTale {
  
  // -- Parsers
  
  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[Int]("fairy_tale.id") ~
    get[Int]("fairy_tale.customerId") ~
    get[String]("fairy_tale.name") ~
    get[Date]("fairy_tale.dueDate") ~
    get[String]("fairy_tale.briefing") map {
      case id~customerId~name~dueDate~briefing => FairyTale(Some(id), customerId, name, DateTime.parse(dueDate.toString()), briefing)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve all users.
   */
  def findAllByCustomer (customerId: Long): Seq[FairyTale] = {
    DB.withConnection { implicit connection =>
      SQL("select * from fairy_tale where customerId={customerId}").on(
    	'customerId -> customerId
      ).as(FairyTale.simple *)
    }
  }
   
  /**
   * Create a User.
   */
  def create(fairyTale: FairyTale): FairyTale = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into fairy_tale (customerId, name, dueDate, briefing) values (
    		  {customerId}, {name}, {dueDate}, {briefing}
          )
        """
      ).on(
        'customerId -> fairyTale.customerId,
        'name -> fairyTale.name,
        'dueDate -> fairyTale.dueDate.toDate(),
        'briefing -> fairyTale.briefing
      ).executeUpdate()
      
      fairyTale
      
    }
  }
}
