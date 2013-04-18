package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.libs.json.Json

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
    get[String]("lead.soundFile") ~
    get[String]("lead.imageFile") ~ 
    get[String]("lead.story") ~
    get[String]("lead.anchoring") ~
    get[Int]("lead.priority") map {
      case id~fairyTaleId~name~soundFile~imageFile~story~anchoring~priority => 
        Lead(Some(id), fairyTaleId, name, Some(""), Some(imageFile), Some(story), Some(anchoring), priority)
    }
  }

  /**
   * Parse a JSon lead containing html-ready values (file paths) from a Lead
   */
  
  def json (lead: Lead) = {
    Json.toJson(
	  Map(
	      "id" -> Json.toJson(lead.id),
	      "fairyTaleId" -> Json.toJson(lead.fairyTaleId),
	      "name" -> Json.toJson(lead.name),
	      "soundFile" -> Json.toJson("/assets/audio/fairytales/" + lead.fairyTaleId + "/leads/" + lead.id.get + "/" + lead.soundFile.get),
	      "imageFile" -> Json.toJson("/assets/img/fairytales/" + lead.fairyTaleId + "/leads/" + lead.imageFile.get) //TODO: Add lead id to path
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
      SQL(
        """
          insert into lead (fairyTaleId, name, soundFile, imageFile, story, priority) values (
    		  {fairyTaleId}, {name}, {soundFile}, {imageFile}, {story}, {anchoring}, {priority}
          )
        """
      ).on(
        'fairyTaleId -> lead.fairyTaleId,
        'name -> lead.name,
        'soundFile -> DateTime.now().toString(), //TODO
        'imageFile -> lead.imageFile,
        'story -> lead.story.getOrElse(""),
        'anchoring -> lead.anchoring.getOrElse(""),
        'priority -> lead.priority
      ).executeUpdate()
      
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
        'soundFile -> DateTime.now().toString(), //TODO
        'imageFile -> lead.imageFile,
        'story -> lead.story,
        'anchoring -> lead.anchoring,
        'priority -> lead.priority
      ).executeUpdate()
      
      lead
      
    }
  }
}