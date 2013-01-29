package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import net.liftmodules.JQueryModule
import net.liftweb.http.js.jquery._
import mapper._
import mongodb._

import code.model._

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {

    MongoDB.defineDb(
      DefaultMongoIdentifier,
      MongoAddress(MongoHost("127.0.0.1"), "lift_blog")
    )


    // where to search snippet
    LiftRules.addToPackages("code")

    // Get the locale
    LiftRules.localeCalculator = r => User.currentUser.map(_.locale.isAsLocale).openOr(LiftRules.defaultLocaleCalculator(r))

    // Build SiteMap
    def menus = List(
      Menu.i("Home") / "index",
      Menu.i("Request Details") /"request"
    ) ::: Entry.sitemap ::: User.sitemap

    def sitemap = SiteMap(menus:_*)

    LiftRules.setSiteMap(sitemap)

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    LiftRules.jsArtifacts = JQueryArtifacts
    JQueryModule.InitParam.JQuery = JQueryModule.JQuery172
    JQueryModule.init()

  }
}
