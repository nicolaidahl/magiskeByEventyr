package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
import anorm.Pk

case class Customer(id: Option[Int], name: String)

object Customer extends {
  
  // -- Parsers
  
  /**
   * Parse a customer from a ResultSet
   */
  val simple = {
    get[Int]("customer.id") ~
    get[String]("customer.name") map {
      case id~name => Customer(Some(id), name)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve all customers.
   */
  def findAll: Seq[Customer] = {
    DB.withConnection { implicit connection =>
      SQL("select * from customer").as(Customer.simple *)
    }
  }
  
  /**
   * Retrieve all customers.
   */
  def findByName(name: String): Option[Customer] = {
    DB.withConnection { implicit connection =>
      SQL("select * from customer where lower(name) = {name}").on(
        'name -> name.toLowerCase()
      ).as(Customer.simple.singleOpt)
    }
  }
   
  /**
   * Create a Customer.
   */
  def create(customer: Customer): Customer = {
    DB.withConnection { implicit connection =>
      SQL("insert into customer (name) values ({name})").on(
        'name -> customer.name
      ).executeUpdate()
      
      customer
      
    }
  }
  
}
