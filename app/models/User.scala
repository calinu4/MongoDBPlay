package models

import play.api.data.Form
import play.api.data.Forms._
case class User(
                 age: Int,
                 firstName: String,
                 lastName: String
               )
object User{
  val createUserForm = Form(
    mapping(
      "age"->number(min=0,max=150),
      "firstName" -> nonEmptyText,
      "lastName"->nonEmptyText
    )(User.apply)(User.unapply)
  )
}

object JsonFormats {
  import play.api.libs.json.Json

  // Generates Writes and Reads for Feed and User thanks to Json Macros
  implicit val userFormat = Json.format[User]
}