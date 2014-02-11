package com.github.akiomik.dispatch.dropbox.core

import com.ning.http.client.oauth._
import dispatch._
import dispatch.oauth._

trait DropboxApi {
  val hostName = "api.dropbox.com"
  val version  = "1"
}

trait DropboxEndpoints extends SomeEndpoints with DropboxApi {
  def authorize: String    = s"https://${hostName}/${version}/oauth/authorize"
  def accessToken: String  = s"https://${hostName}/${version}/oauth/access_token"
  def requestToken: String = s"https://${hostName}/${version}/oauth/request_token"
}

case class DropboxExchange(http: HttpExecutor, consumer: ConsumerKey, callback: String) extends Exchange
    with SomeHttp with SomeConsumer with SomeCallback with DropboxEndpoints

trait BaseClient extends DropboxApi {
  val host = :/(hostName).secure / version
  def apply(method: Method): Req = method(host)
}

case class Client(consumer: ConsumerKey, token: RequestToken) extends BaseClient {
  override def apply(method: Method): Req = method(host) sign(consumer, token)
}

