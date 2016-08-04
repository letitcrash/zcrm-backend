package utils 

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import play.api.db.slick.HasDatabaseConfig
import play.api.Play
import database.tables._

trait DBComponentWithSlickQueryOps extends DBComponent {
 //databaseConfig: HasDatabaseConfig[ JdbcProfile ] =>

  import cats.Apply
  import cats.std.list._
 // import driver.api._
  import dbConfig.driver.api._

  type BooleanOp = ( Rep[ Boolean ], Rep[ Boolean ] ) => Rep[ Boolean ]

  implicit class OptionFilter[ X, Y ]( query: Query[ X, Y, Seq ] ) {

    /*
      Filters collection from an optional value. If it is None, returns the unfiltered collection.
      It can be applied consecutively to the query.
      Used for finding a collection of elements
    */
    def filteredBy[ T ]( op: Option[ T ] )
                       ( f: ( X, T ) => Rep[ Boolean ]): Query[ X, Y, Seq ] = {
      op map { o => query.filter( f( _, o ) ) } getOrElse query
    }

    /*
      Applies a list of functions that return predicates to the query filter method. 
      If the result of the functions is None, returns an empty collection.
      Used for finding the first match.
    */
    def foundBy[ T ]( ops: List[ ( X ) => Option[ Rep[ Boolean ] ] ] )
                    ( f: BooleanOp ): Query[ X, Y, Seq ] =
      query.filter { q =>
        val res = Apply[ List ].ap( ops )( List( q ) ).collect { 
          case Some( y ) => y 
        } reduceLeftOption f
        res.getOrElse( false: Rep[ Boolean ] )
      } take 1
  }
}
