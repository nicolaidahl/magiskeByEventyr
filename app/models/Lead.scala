package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import toolbox.IOHelper

case class Lead(id: Option[Int], 
				fairyTaleId: Int, 
				name: String, 
				var soundFile: Option[String], 
				var imageFile: Option[String], 
				var story: Option[String],
				var anchoring: Option[String],
				priority: Int)

object Lead {
	// -- Parsers
  
  /**
   * Parse a Lead from a ResultSet
   */
  val simple = {
    get[Int]("lead.id") ~
    get[Int]("lead.fairyTaleId") ~
    get[String]("lead.name") ~
    get[Option[String]]("lead.soundFile") ~
    get[Option[String]]("lead.imageFile") ~ 
    get[Option[String]]("lead.story") ~
    get[Option[String]]("lead.anchoring") ~
    get[Int]("lead.priority") map {
      case id~fairyTaleId~name~soundFile~imageFile~story~anchoring~priority => 
        Lead(Some(id), fairyTaleId, name, soundFile, imageFile, story, anchoring, priority)
    }
  }
  
  val form = Form(
    mapping(
        "id" -> optional[Int](number),
        "fairyTaleId" -> number,
        "name" -> text,
        "soundFile" -> optional[String](text),
        "imageFile" -> optional[String](text),
        "story" -> optional[String](text),
        "anchoring" -> optional[String](text),
        "priority" -> number
    )(Lead.apply)(Lead.unapply)
  )

  /**
   * Parse a JSon lead containing html-ready values (file paths) from a Lead
   */
  
  def json (lead: Lead) = {
    Json.toJson(
	  Map(
	      "id" -> Json.toJson(lead.id),
	      "fairyTaleId" -> Json.toJson(lead.fairyTaleId),
	      "name" -> Json.toJson(lead.name),
	      "imageFile" -> Json.toJson(if (lead.imageFile.isDefined) "/assets/fairytales/" + lead.fairyTaleId + "/img/" + lead.imageFile.get else ""),
	      "anchoring" -> Json.toJson(lead.anchoring.getOrElse("")),
	      "story" -> Json.toJson(lead.story.getOrElse("")),
	      "soundFile" -> Json.toJson(if (lead.soundFile.isDefined) "/assets/fairytales/" + lead.fairyTaleId + "/audio/" + lead.soundFile.get else "")
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
   * Create a Lead.
   */
  def create(lead: Lead): Lead = {
    DB.withConnection { implicit connection =>
      val foo = SQL(
        """
          insert into lead (fairyTaleId, name, soundFile, imageFile, story, anchoring, priority) values (
    		  {fairyTaleId}, {name}, {soundFile}, {imageFile}, {story}, {anchoring}, {priority}
          )
        """
      ).on(
        'fairyTaleId -> lead.fairyTaleId,
        'name -> lead.name,
        'soundFile -> lead.soundFile,
        'imageFile -> lead.imageFile,
        'story -> lead.story,
        'anchoring -> lead.anchoring,
        'priority -> lead.priority
      ).executeUpdate
      
      lead
      
    }
  }
  
  /**
   * Update a Lead.
   */
  def update(lead: Lead): Lead = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update lead set name={name}, soundFile={soundFile}, imageFile={imageFile}, story={story}, anchoring={anchoring}, priority={priority} where id={id}
        """
      ).on(
        'id -> lead.id,
        'name -> lead.name,
        'soundFile -> lead.soundFile,
        'imageFile -> lead.imageFile,
        'story -> lead.story,
        'anchoring -> lead.anchoring,
        'priority -> lead.priority
      ).executeUpdate()
      
      lead
      
    }
  }
}