dispatch-dropbox
================

A dispatch wrapper for Dropbox API.

This library is inspired by [repatch-twitter](https://github.com/eed3si9n/repatch-twitter).

[![Build Status](https://travis-ci.org/akiomik/dispatch-dropbox.png?branch=master)](https://travis-ci.org/akiomik/dispatch-dropbox)

## Sample

```scala
import com.ning.http.client.oauth._
import dispatch._
import dispatch.Defaults._
import scala.concurrent._
import com.github.akiomik.dispatch.dropbox.core.clients._
import com.github.akiomik.dispatch.dropbox.core.requests._

val consumer = new ConsumerKey("foo", "bar") // FIXME
val exchange = DropboxExchange(http, consumer, "")

val res1 = exchange.fetchRequestToken
val requestToken = res1 map {
 case Right(t) => t
 case Left(m)  => sys.error(m)
}

val verifier = requestToken map { t =>
  blocking {
    val url = DropboxExchange.signedAuthorize(t)
    println(url)
    println("Please press enter after authorize.")
    readLine()
  }
}

val res2 = requestToken flatMap { t =>
  verifier flatMap { v =>
    DropboxExchange.fetchAccessToken(t, v)
  }
}

val accessToken = res2 map {
  case Right(token)  => token
  case Left(message) => sys.error(message)
}

val client = Client(consumer, accessToken)
val info = Http(client(Account.info) OK as.String)
```

## License

MIT License. See `License` file.

