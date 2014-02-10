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
import plugins.S3Plugin
import toolbox.AmazonS3FileHandler
import toolbox.LocalFileHandler

case class FairyTale(id: Option[Int], customerId: Int, var name: String, var dueDate: DateTime, var briefing: String, imagefile: Option[String], var published: Boolean, var credits: Option[String])

object FairyTale {
  
  // -- Parsers
  
  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[Int]("fairy_tale.id") ~
    get[Int]("fairy_tale.customerid") ~
    get[String]("fairy_tale.name") ~
    get[Date]("fairy_tale.duedate") ~
    get[String]("fairy_tale.briefing") ~
    get[Option[String]]("lead.imagefile") ~
    get[Int]("fairy_tale.published") ~
    get[Option[String]]("fairy_tale.credits") map {
      case id~customerId~name~dueDate~briefing~imagefile~published~credits => 
        FairyTale(Some(id), customerId, name, DateTime.parse(dueDate.toString()), briefing, if(imagefile.isDefined){
          if(S3Plugin.isEnabled)
            Some(AmazonS3FileHandler.getPrefixPath + imagefile.get)
          else
        	Some(LocalFileHandler.getPrefixPath + imagefile.get)
        } else None, published == 1, if(credits.isDefined) Some(credits.get) else None)
    }
  }
  
  val form = Form(
    mapping(
        "id" -> optional[Int](number),
        "customerId" -> number,
        "name" -> text,
        "dueDate" -> jodaDate("yyyy-MM-dd"),
        "briefing" -> text,
        "imageFile" -> optional[String](text),
        "published" -> boolean,
        "credits" -> optional[String](text)
    )(FairyTale.apply)(FairyTale.unapply)
  )
	
  
  
  // -- Queries
  
  /**
   * Retrieve all fairy tales based on customer, include image of first lead if any.
   */
  def findAllByCustomer (customerId: Int): Seq[FairyTale] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          SELECT f.id, f.customerid, f.name, f.duedate, f.briefing, l.imagefile, f.published, f.credits
          FROM fairy_tale f LEFT OUTER JOIN (SELECT lead.fairytaleid, lead.imagefile FROM lead where lead.priority = 0) l ON (f.id = l.fairytaleid) 
          WHERE f.customerId={customerId}
	    """
      ).on(
    	'customerId -> customerId
      ).as(FairyTale.simple *)
    }
  }
  
  def findAllPublishedByCustomer (customerId: Int): Seq[FairyTale] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          SELECT f.id, f.customerid, f.name, f.duedate, f.briefing, l.imagefile, f.published, f.credits
          FROM fairy_tale f LEFT OUTER JOIN (SELECT lead.fairytaleid, lead.imagefile FROM lead where lead.priority = 0) l ON (f.id = l.fairytaleid) 
          WHERE f.customerId={customerId} AND f.published = 1
	    """
      ).on(
    	'customerId -> customerId
      ).as(FairyTale.simple *)
    }
  }
  
  /**
   * Retrieve one fairy tale based on id.
   */
  def findById (id: Int): Option[FairyTale] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          SELECT f.id, f.customerid, f.name, f.duedate, f.briefing, l.imagefile, f.published, f.credits
          FROM fairy_tale f LEFT OUTER JOIN (SELECT lead.fairytaleid, lead.imagefile FROM lead where lead.priority = 0) l ON (f.id = l.fairytaleid) 
          WHERE f.id={id}
        """
      ).on(
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
          INSERT INTO fairy_tale (customerId, name, dueDate, briefing, credits) VALUES (
    		  {customerId}, {name}, {dueDate}, {briefing}, {credits}
          )
        """
      ).on(
        'customerId -> fairyTale.customerId,
        'name -> fairyTale.name,
        'dueDate -> fairyTale.dueDate.toDate(),
        'briefing -> fairyTale.briefing,
        'credits -> fairyTale.credits
      ).executeUpdate()
      
      fairyTale
    }
  }
  
  def update(fairyTale: FairyTale): FairyTale = {
    val published = if (fairyTale.published) 1 else 0
    DB.withConnection { implicit connection =>
      SQL(
        """
          UPDATE fairy_tale
          SET name={name}, dueDate={dueDate}, briefing={briefing}, published={published}, credits={credits} 
          WHERE id={id}
        """
      ).on(
        'id -> fairyTale.id,
        'name -> fairyTale.name,
        'dueDate -> fairyTale.dueDate.toDate(),
        'briefing -> fairyTale.briefing,
        'published -> published,
        'credits -> fairyTale.credits
      ).executeUpdate()
      
      fairyTale
      
    }
  }
  
  def delete(fairyTale: FairyTale) = {
    val leads = Lead.findAllByFairyTale(fairyTale.id.get)
    leads.foreach(lead => Lead.delete(lead))
    DB.withConnection { implicit connection =>
      SQL(
        """
          delete from fairy_tale where id={id}
        """
      ).on(
        'id -> fairyTale.id
      ).executeUpdate()
      
    }
  }
  
  def getLeads(fairyTaleId: Int) : Seq[Lead] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          SELECT * 
          FROM lead 
          WHERE fairyTaleId={fairyTaleId} ORDER BY priority ASC
        """
      ).on(
    	'fairyTaleId -> fairyTaleId
      ).as(Lead.simple *)
    }
  }
  
  def getLeadCount(fairyTaleId: Int) : Long = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          SELECT COUNT(*) 
          FROM lead 
          WHERE fairyTaleId={fairyTaleId}
        """
      ).on(
    	'fairyTaleId -> fairyTaleId
      ).as(scalar[Long].single)
    }
  }
}
