package com.greencatsoft.angularjs.core

import scala.concurrent.{ CanAwait, ExecutionContext, Future }
import scala.concurrent.duration.Duration
import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.{ JavaScriptException, UndefOr }
import scala.scalajs.js.Any.fromFunction1
import scala.util.{ Failure, Success, Try }

import com.greencatsoft.angularjs.injectable

@injectable("$http")
trait HttpService extends js.Object {

  def get(url: String): HttpPromise = js.native

  def get(url: String, config: HttpConfig): HttpPromise = js.native

  def post(url: String): HttpPromise = js.native

  def post(url: String, data: js.Any): HttpPromise = js.native

  def post(url: String, data: js.Any, config: HttpConfig): HttpPromise = js.native

  def jsonp(url: String, config: HttpConfig): HttpPromise = js.native

  def put(url: String): HttpPromise = js.native

  def put(url: String, data: js.Any): HttpPromise = js.native

  def put(url: String, data: js.Any, config: HttpConfig): HttpPromise = js.native

  def delete(url: String): HttpPromise = js.native

  def delete(url: String, data: js.Any): HttpPromise = js.native

  def delete(url: String, data: js.Any, config: HttpConfig): HttpPromise = js.native
}

trait HttpConfig extends js.Object {

  var cache : Boolean = js.native

  var responseType : String = js.native

  var headers : js.Array[js.Any] = js.native

  var transformResponse: js.Array[js.Function2[js.Any, js.Any, js.Any]] = js.native

  var transformRequest: js.Array[js.Function2[js.Any, js.Any, js.Any]] = js.native
}

object HttpConfig {

  def apply() = {
    val config = new js.Object().asInstanceOf[HttpConfig]

    config.transformRequest = js.Array()
    config.transformResponse = js.Array()

    config
  }

  def documentHandler(): HttpConfig = {
    val config = apply()

    config.responseType = "document"

    config
  }
}

@injectable("$httpProvider")
trait HttpProvider extends js.Object {

  var defaults: HttpConfig = js.native
}

trait HttpPromise extends Promise {

  def success(callback: js.Function1[js.Any, Unit]): this.type = js.native

  def success(callback: js.Function2[js.Any, Int, Unit]): this.type = js.native

  def success(callback: js.Function3[js.Any, js.Any, Int, Unit]): this.type = js.native

  def success(callback: js.Function4[js.Any, Int, js.Any, js.Any, Unit]): this.type  = js.native

  def success(callback: js.Function5[js.Any, Int, js.Any, js.Any, js.Any, Unit]): this.type = js.native

  def error(callback: js.Function1[js.Any, Unit]): this.type = js.native

  def error(callback: js.Function2[js.Any, Int, Unit]): this.type = js.native

  def error(callback: js.Function3[js.Any, js.Any, Int, Unit]): this.type = js.native

  def error(callback: js.Function4[js.Any, Int, js.Any, js.Any, Unit]): this.type = js.native

  def error(callback: js.Function5[js.Any, Int, js.Any, js.Any, UndefOr[String], Unit]): this.type = js.native
}

object HttpPromise {

  implicit def promise2future[A](promise: Promise): Future[A] = new HttpFuture[A](promise)

  trait HttpResult extends js.Object {

    val config: js.Any = js.native

    val data: js.Any = js.native

    val status: Int = js.native

    val statusText: String = js.native
  }

  class HttpFuture[A](promise: Promise) extends Future[A] {

    type Listener[U] = Try[A] => U

    private var result: Option[Try[A]] = None

    private var listeners: Seq[Listener[_]] = Seq.empty

    private def notify(result: Try[A]): Option[Try[A]] = {
      listeners.foreach(_(result))
      Some(result)
    }

    promise `then` { (r: js.Any) =>
      val httpResult = r.asInstanceOf[HttpResult]
      this.result = notify(Success(httpResult.data.asInstanceOf[A]))
      r
    } `catch` { (error: js.Any) =>
      val httpResult = error.asInstanceOf[HttpResult]
      this.result = notify(Failure(JavaScriptException(httpResult.data)))
    }

    override def ready(atMost: Duration)(implicit permit: CanAwait): this.type =
      throw new UnsupportedOperationException

    override def result(atMost: Duration)(implicit permit: CanAwait): A =
      throw new UnsupportedOperationException

    override def isCompleted: Boolean = result.isDefined

    override def onComplete[U](f: Listener[U])(implicit executor: ExecutionContext): Unit =
      listeners +:= f

    override def value: Option[Try[A]] = result
  }
}
