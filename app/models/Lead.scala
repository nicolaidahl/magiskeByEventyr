package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.libs.json.Json

case class Lead(id: Option[Int], fairyTaleId: Int, name: String, soundFile: Option[String], var imageFile: Option[String], priority: Int)

object Lead {
	// -- Parsers
  
  /**
   * Parse a Lead from a ResultSet
   */
  val simple = {
    get[Int]("lead.id") ~
    get[Int]("lead.fairyTaleId") ~
    get[String]("lead.name") ~
    get[String]("lead.soundFile") ~
    get[String]("lead.imageFile") ~ 
    get[Int]("lead.priority") map {
      case id~fairyTaleId~name~soundFile~imageFile~priority => Lead(Some(id), fairyTaleId, name, Some(""), Some(imageFile), priority)
    }
  }

  /**
   * Parse a JSon lead from a Lead
   */
  
  def json (lead: Lead) = {
    Json.toJson(
	  Map(
	      "name" -> Json.toJson(lead.name),
	      "soundFile" -> Json.toJson(lead.soundFile),
	      "imageFile" -> Json.toJson(lead.imageFile)
      )
	)
  }
  
  /**
   * Retrieve all fairy tales based on customer.
   */
  def findAllByFairyTale (fairyTaleId: Int): Seq[Lead] = {
    DB.withConnection { implicit connection =>
      SQL("select * from lead where fairyTaleId={fairyTaleId}").on(
    	'fairyTaleId -> fairyTaleId
      ).as(Lead.simple *)
    }
  }
  
  /**
   * Retrieve one fairy tale based on id.
   */
  def findById (id: Int): Option[Lead] = {
    DB.withConnection { implicit connection =>
      SQL("select * from lead where id={id}").on(
    	'id -> id
      ).as(Lead.simple.singleOpt)
    }
  }
   
  /**
   * Create a User.
   */
  def create(lead: Lead): Lead = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into lead (fairyTaleId, name, soundFile, imageFile, priority) values (
    		  {fairyTaleId}, {name}, {soundFile}, {imageFile}, {priority}
          )
        """
      ).on(
        'fairyTaleId -> lead.fairyTaleId,
        'name -> lead.name,
        'soundFile -> DateTime.now().toString(), //TODO
        'imageFile -> lead.imageFile,
        'priority -> lead.priority
      ).executeUpdate()
      
      lead
      
    }
  }
}