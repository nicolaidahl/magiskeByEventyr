package models

import play.api.db._
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

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
  
  val form = Form(
    mapping(
        "id" -> optional[Int](number),
        "customerId" -> number,
        "name" -> text,
        "dueDate" -> jodaDate("yyyy-MM-dd"),
        "briefing" -> text
    )(FairyTale.apply)(FairyTale.unapply)
  )
	
  
  
  // -- Queries
  
  /**
   * Retrieve all fairy tales based on customer.
   */
  def findAllByCustomer (customerId: Int): Seq[FairyTale] = {
    DB.withConnection { implicit connection =>
      SQL("select * from fairy_tale where customerId={customerId}").on(
    	'customerId -> customerId
      ).as(FairyTale.simple *)
    }
  }
  
  /**
   * Retrieve one fairy tale based on id.
   */
  def findById (id: Int): Option[FairyTale] = {
    DB.withConnection { implicit connection =>
      SQL("select * from fairy_tale where id={id}").on(
    	'id -> id
      ).as(FairyTale.simple.singleOpt)
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
  
  def getLeads(fairyTaleId: Int) : Seq[Lead] = {
    DB.withConnection { implicit connection =>
      SQL("select * from lead where fairyTaleId={fairyTaleId}").on(
    	'fairyTaleId -> fairyTaleId
      ).as(Lead.simple *)
    }
  }
}
