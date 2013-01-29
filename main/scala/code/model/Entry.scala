package code.model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{StringRefField, ObjectIdField, ObjectIdPk}
import net.liftweb.sitemap.{Loc, Menu}
import net.liftweb.sitemap.Loc.{Hidden, If}
import net.liftweb.record.field.{TextareaField, StringField}
import net.liftweb.http.{RedirectResponse, S}
import code.controller.{BlogCache, AddEntry}
import net.liftweb.record.LifecycleCallbacks
import com.mongodb.WriteConcern

/**
 * Created with IntelliJ IDEA.
 * User: Dario
 * Date: 24/01/13
 * Time: 22.11
 * To change this template use File | Settings | File Templates.
 */
object Entry extends Entry with MongoMetaRecord[Entry] {

  val sitemap = List(
    Menu.i("CreateEntry") / "entry" >> If(User.loggedIn_? _, () => RedirectResponse("index")),
    Menu.i("ViewEntry") / "view" >> Hidden,
    Menu.i("ViewBlog") / "blog"
  )

  // Adds the entry to the cache
  override def save(e: Entry, c: WriteConcern) = {
     BlogCache.cache ! AddEntry(e, e.author.is)

    super.save(e, c)
  }

}

class Entry extends MongoRecord[Entry] with ObjectIdPk[Entry] {
  def meta = Entry

  object author extends StringRefField(this, User, 128)

  object title extends StringField(this, 128)

  object body extends TextareaField(this, 20000) {
    override def setFilter = notNull _ :: trim _ :: crop _ :: super.setFilter
  }

}