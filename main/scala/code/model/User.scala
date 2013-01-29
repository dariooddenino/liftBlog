package code
package model

import net.liftweb.common._
import net.liftweb.record.field.StringField

import lib._


class User extends MegaProtoUser[User] {
	def meta = User

  object blogtitle extends StringField(this, 128)
}

object User extends User with MetaMegaProtoUser[User] {
  override def screenWrap = Full(<lift:surround with="default" at="content"><lift:bind /></lift:surround>)
  
  override def skipEmailValidation = true

  override def signupFields = firstName :: lastName :: email :: locale :: timezone :: password :: blogtitle :: Nil

  override val basePath: List[String] = "user_mgt" :: "usr" :: Nil

}