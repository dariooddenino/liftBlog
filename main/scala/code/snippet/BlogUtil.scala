package code.snippet

import xml.{Node, NodeSeq, Group}
import code.model.{User, Entry}
import net.liftweb.http.{SHtml, S}
import com.foursquare.rogue.Rogue._
import net.liftweb.common.{Empty, Full, Failure}
import net.liftweb.record._
import org.bson.types.ObjectId
import net.liftweb.util.Helpers._


/**
 * Created with IntelliJ IDEA.
 * User: Dario
 * Date: 24/01/13
 * Time: 22.23
 * To change this template use File | Settings | File Templates.
 */
class BlogUtil {

  var title = ""
  var body = ""


  def saveEntry() {
    val e = Entry.createRecord.author(User.currentUser.get.id.toString).title(title).body(body).save
    S.redirectTo("/view?id=" + e.id)
  }

  def entry = {
    "#etitle" #> SHtml.text(title, title = _) &
      "#ebody" #> SHtml.text(body, body = _) &
      "#esubmit" #> SHtml.submit("Save", saveEntry)
  }



  def viewentry = {
    S.param("id").map {
      id: String =>
        Entry.getById(id) match {
          case None => ".error *" #> "Invalid id!"
          case Some(e) =>
            ".title *" #> e.title &
            ".body *" #> e.body
        }
    }.openOr(".error *" #> "No id!")
  }

  def _entryview(e: Entry): Node = {
    <div>
      <strong>
        {e.title}
      </strong> <br/>
      <span>
        {e.body}
      </span>
    </div>
  }


  def viewblog(xhtml: Group): NodeSeq = {
    // Find all Entries by author using the parameter
    val t = Entry where (_.author eqs S.param("id").getOrElse("")) orderDesc (_.id) fetch (20)
    t match {
      // If no 'id' was requested, then show a listing of all users.
      case Nil => User fetch() map (u => <span>
        <a href={"/blog?id=" + u.id}>
          {u.firstName + " " + u.lastName}
        </a> <br/>
      </span>)

      case entries =>
        <lift:comet type="DynamicBlogView" name={S.param("id").get}>
          <blog:view>Loading...</blog:view>
        </lift:comet>
    }
  }


  def requestDetails: NodeSeq = {
    <span>
      <p>
        Request's Locale:
        {S.locale}
      </p>
      <p>
        Request(User): Locale :
        {User.currentUser.map(ignore => S.locale.toString).openOr("No User logged in.")}
      </p>
    </span>
  }

}
