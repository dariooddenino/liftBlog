package code.comet

import net.liftweb.http.CometActor
import net.liftweb.common.Full
import code.model.Entry
import xml.Node
import code.controller.{BlogUpdate, AddBlogWatcher, BlogCache}

/**
 * Created with IntelliJ IDEA.
 * User: Dario
 * Date: 24/01/13
 * Time: 22.59
 * To change this template use File | Settings | File Templates.
 */
class DynamicBlogView extends CometActor {
  override def defaultPrefix = Full("blog")

  var blogtitle = ""
  var blog: List[Entry] = Nil
  var blogid: String = ""

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

  def render = {
    bind("view" -> <span>
      {blog.flatMap(e => _entryview(e))}
    </span>)
  }

  // localSetup is the fist thing run, we use it to setup the blogid or
  // redirect them to / if no blogid was given.
  override def localSetup {
    name map {
      t => this.blogid = t
    }

    // Let the BlogCache know that we are watching for updates for this blog.
    (BlogCache.cache !? AddBlogWatcher(this, this.blogid)) match {
      case BlogUpdate(entries) => this.blog = entries
    }
  }

  // lowPriority will receive message sent from the BlogCache
  override def lowPriority: PartialFunction[Any, Unit] = {
    case BlogUpdate(entries: List[_]) => this.blog = entries; reRender(true)
  }
}
