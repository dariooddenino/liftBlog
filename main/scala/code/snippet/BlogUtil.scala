package code.model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{StringRefField, ObjectIdPk}
import net.liftweb.sitemap.Menu
import net.liftweb.sitemap.Loc.{Hidden, If}
import net.liftweb.record.field.{TextareaField, StringField}
import net.liftweb.http.RedirectResponse
import code.controller.{BlogCache, AddEntry}
import com.mongodb.WriteConcern
import net.liftweb.common.{Empty, Box}
import org.bson.types.ObjectId
import net.liftweb.util.Helpers._
import com.foursquare.rogue.Rogue._
import net.liftweb.util._

/**
 * Created with IntelliJ IDEA.
 * User: Dario
 * Date: 24/01/13
 * Time: 22.11
 * To change this template use File | Settings | File Templates.
 */
object Entry extends Entry with MongoMetaRecord[Entry] {

  // Once the transaction is committed, fill in the blog cache with this entry
  // Era un override, l'ho tolto...
  // def afterCommit =
  //   ((entry: Entry) => {BlogCache.cache ! AddEntry(entry, entry.author.is)}) :: Nil

  val sitemap = List(
    Menu.i("CreateEntry") / "entry" >> If(User.loggedIn_? _, () => RedirectResponse("index")),
    Menu.i("ViewEntry") / "view" >> Hidden,
    Menu.i("ViewBlog") / "blog"
  )

  override def save(e: Entry, c: WriteConcern) = {
    BlogCache.cache ! AddEntry(e, e.author.is)

    super.save(e, c)
  }

  // Find an Entry by id
  def getById(id: String) = ObjectId.isValid(id) match {
    case true => Entry.where(_.id eqs (new ObjectId(id))).get
    case false => None
  }

}

class Entry extends MongoRecord[Entry] with ObjectIdPk[Entry] {
  def meta = Entry

  object author extends StringRefField(this, User, 128)

  // non va bene, deve passare l'entry, non il title...
  // override save
  object title extends StringField(this, 128)

  object body extends TextareaField(this, 20000) {
    override def setFilter = notNull _ :: trim _ :: crop _ :: super.setFilter
  }

}
