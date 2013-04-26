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

case class Lead(
    id: Option[Int], 
	fairyTaleId: Int, 
	name: String, 
	var soundFile: Option[String], 
	var imageFile: Option[String], 
	var story: Option[String],
	var anchoring: Option[String],
	var priority: Int,
	var approved: Boolean
)

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
    get[Int]("lead.priority") ~
    get[Boolean]("lead.approved") map {
      case id~fairyTaleId~name~soundFile~imageFile~story~anchoring~priority~approved => 
        Lead(Some(id), fairyTaleId, name, soundFile, imageFile, story, anchoring, priority, approved)
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
        "priority" -> number,
        "approved" -> boolean
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
	      "soundFile" -> Json.toJson(if (lead.soundFile.isDefined) "/assets/fairytales/" + lead.fairyTaleId + "/audio/" + lead.soundFile.get else ""),
	      "imageFile" -> Json.toJson(if (lead.imageFile.isDefined) "/assets/fairytales/" + lead.fairyTaleId + "/img/" + lead.imageFile.get else ""),
	      "story" -> Json.toJson(lead.story.getOrElse("")),
	      "anchoring" -> Json.toJson(lead.anchoring.getOrElse("")),
	      "priority" -> Json.toJson(lead.priority),
	      "approved" -> Json.toJson(lead.approved)
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
        'priority -> FairyTale.getLeadCount(lead.fairyTaleId).toInt
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
          update lead set name={name}, soundFile={soundFile}, imageFile={imageFile}, story={story}, anchoring={anchoring}, priority={priority}, approved={approved} where id={id}
        """
      ).on(
        'id -> lead.id,
        'name -> lead.name,
        'soundFile -> lead.soundFile,
        'imageFile -> lead.imageFile,
        'story -> lead.story,
        'anchoring -> lead.anchoring,
        'priority -> lead.priority,
        'approved -> lead.approved
      ).executeUpdate()
      
      lead
      
    }
  }
    
	def setPriority(id: Int, priority: Int) = {
	  val lead = Lead.findById(id).get
	  
	  val fairyTaleLeads = FairyTale.getLeads(lead.fairyTaleId)
	  
	  fairyTaleLeads.foreach {
	    fairyTaleLead => if (!(fairyTaleLead.id == lead.id)){
	      if (fairyTaleLead.priority > lead.priority && fairyTaleLead.priority <= priority) {
	        fairyTaleLead.priority = fairyTaleLead.priority - 1
	      } else if (fairyTaleLead.priority < lead.priority && fairyTaleLead.priority >= priority) {
	        fairyTaleLead.priority = fairyTaleLead.priority + 1
	      }
	      /*fairyTaleLead.priority match {
	        case x: Int if (x > lead.priority && x <= priority) => fairyTaleLead.priority = fairyTaleLead.priority - 1
	        case x: Int if (x < lead.priority && x >= priority) => fairyTaleLead.priority = fairyTaleLead.priority + 1
	      }*/
	      
	      Lead.update(fairyTaleLead)
	    }
	  }
	  
	  lead.priority = priority
	  
	  Lead.update(lead)
	}
}