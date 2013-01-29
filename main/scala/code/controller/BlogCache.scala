package code.controller

import net.liftweb.actor.LiftActor
import code.model.Entry
import net.liftweb.common.SimpleActor
import com.foursquare.rogue.Rogue._
import net.liftweb.mongodb.record.field.ObjectIdField

/**
 * Created with IntelliJ IDEA.
 * User: Dario
 * Date: 24/01/13
 * Time: 22.47
 * To change this template use File | Settings | File Templates.
 */
class BlogCache extends LiftActor {
  private var cache: Map[String, List[Entry]] = Map()
  private var sessions: Map[String, List[SimpleActor[Any]]] = Map()

  def getEntries(id: String): List[Entry] = Entry where (_.author eqs id) orderDesc (_.id) fetch (20)

  protected def messageHandler = {
    case AddBlogWatcher(me, id) =>
      val blog = cache.getOrElse(id, getEntries(id)).take(20)
      reply(BlogUpdate(blog))
      cache += (id -> blog)
      sessions += (id -> (me :: sessions.getOrElse(id, Nil)))
    case AddEntry(e, id) =>
      cache += (id -> (e :: cache.getOrElse(id, getEntries(id))))
      sessions.getOrElse(id, Nil).foreach(_ ! BlogUpdate(cache.getOrElse(id, Nil)))
    case DeleteEntry(e, id) =>
      cache += (id -> cache.getOrElse(id, getEntries(id)).filterNot(_ == e))
      sessions.getOrElse(id, Nil).foreach(_ ! BlogUpdate(cache.getOrElse(id, Nil)))
    case EditEntry(e, id) =>
      cache += (id -> getEntries(id))
    case _ =>
  }
}

case class AddEntry(e: Entry, id: String)

case class EditEntry(e: Entry, id: String)

case class DeleteEntry(e: Entry, id: String)

case class AddBlogWatcher(me: SimpleActor[Any], id: String)

case class BlogUpdate(xs: List[Entry])

object BlogCache {
  lazy val cache = new BlogCache
}