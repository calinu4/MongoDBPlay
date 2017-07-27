package controllers

import javax.inject.Inject

import scala.concurrent.Future
import play.api.mvc.{Action, AnyContent, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import reactivemongo.api.Cursor
import models._
import models.JsonFormats._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json._
import collection._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.data._
import play.api.data.Forms._
class MongoDBController @Inject()(val messagesApi: MessagesApi)(val reactiveMongoApi: ReactiveMongoApi) extends Controller
  with MongoController with ReactiveMongoComponents with I18nSupport{

  // TODO - keep in mind you need to have mongod.exe running before attempting to play around
  //Read from table persons
  def collection: Future[JSONCollection] = database.map(
    _.collection[JSONCollection]("persons"))

  //display users from database
  def listUsers:Action[AnyContent] = Action.async {
    val cursor: Future[Cursor[User]] = collection.map {
      _.find(Json.obj())
        .sort(Json.obj("created" -> -1))
        .cursor[User]
    }
    val futureUsersList: Future[List[User]] = cursor.flatMap(_.collect[List]())
    futureUsersList.map { persons =>
      Ok(views.html.list(persons))
    }
  }
  def displayCreate=Action{
    Ok(views.html.create(User.createUserForm))
  }
  def create=Action{ implicit request =>
    val formValidationResult = User.createUserForm.bindFromRequest
    formValidationResult.fold({ formWithErrors =>
      BadRequest(views.html.create(formWithErrors))
    }, { user =>val futureResult = collection.flatMap(_.insert(user))
      futureResult.map(_ => Ok("Added user " + user.firstName + " " + user.lastName))
      Redirect(routes.MongoDBController.listUsers())
    })
}


  def findByName: Action[AnyContent] = Action.async {
    val cursor: Future[Cursor[User]] = collection.map {
      _.find(Json.obj("lastName" -> "Lastname"))
        .sort(Json.obj("created" -> -1))
        .cursor[User]
    }
    val futureUsersList: Future[List[User]] = cursor.flatMap(_.collect[List]())
    futureUsersList.map { persons =>
      Ok(persons.head.toString)
    }
  }

}