package com.github.akiomik.dispatch.dropbox.core

import org.specs2.mutable._
import org.specs2.specification.AllExpectations
import com.ning.http.client.oauth._
import dispatch._
import dispatch.Defaults._
import java.util.Locale
import scala.collection.JavaConverters._

class DropboxApiSpec extends Specification {
  val h = host("example.com")

  implicit class MethodOps(m: Method) {
    val req = m.complete(h).toRequest
    def method: String = req.getMethod
    def url: String = m.complete(h).url
    def firstValue: String => String = req.getParams.getFirstValue
  }

  "Account" should {
    "info" in {
      "construct info api url" in {
        val i = Account.info()
        i.url must_== "http://example.com/account/info"
      }

      "use GET method" in {
        val i = Account.info()
        i.method must_== "GET"
      }

      "construct info api url with locale" in {
        val i = Account.info().locale(Locale.JAPAN)
        i.url must_== "http://example.com/account/info?locale=ja_JP"
      }
    }
  }
  
  "File" should {
    "construct files api url with path" in {
      val f = File(_ / "foo" / "bar")
      f.url must_== "http://example.com/files/dropbox/foo/bar"
    }

    "use GET method" in {
      val f = File(_ / "foo" / "bar")
      f.method must_== "GET"
    }

    "construct files api url with path and rev" in {
      val f = File(_ / "foo" / "bar").rev("1234abc")
      f.url must_== "http://example.com/files/dropbox/foo/bar?rev=1234abc"
    }
  }
  
  "FilePut" should {
    "construct files api url with path" in {
      val fp = FilePut(_ / "foo" / "bar")
      fp.url must_== "http://example.com/files_put/dropbox/foo/bar"
    }

    "use POST method" in {
      val fp = FilePut(_ / "foo" / "bar")
      fp.method must_== "POST"
    }

    "construct files api request with locale" in {
      val fp = FilePut(_ / "foo" / "bar").locale(Locale.JAPAN)
      fp.firstValue("locale") must_== "ja_JP"
    }

    "construct files api request with overwrite" in {
      val fp = FilePut(_ / "foo" / "bar").overwrite(true)
      fp.firstValue("overwrite") must_== "true"
    }

    "construct files api request with parent rev" in {
      val fp = FilePut(_ / "foo" / "bar").parentRev("1234abc")
      fp.firstValue("parent_rev") must_== "1234abc"
    }

    "construct files api request with all parameters" in {
      val fp = FilePut(_ / "foo" / "bar")
        .locale(Locale.JAPAN).overwrite(true).parentRev("1234abc")
      Map(
        "locale"     -> fp.firstValue("locale"),
        "overwrite"  -> fp.firstValue("overwrite"),
        "parent_rev" -> fp.firstValue("parent_rev")
      ) must havePairs(
        "locale"     -> "ja_JP",
        "overwrite"  -> "true",
        "parent_rev" -> "1234abc"
      )
    }
  }

  "Metadata" should {
    "construct metadata api url with path" in {
      val m = Metadata(_ / "foo" / "bar")
      m.url must_== "http://example.com/metadata/dropbox/foo/bar"
    }

    "use GET method" in {
      val m = Metadata(_ / "foo" / "bar")
      m.method must_== "GET"
    }

    "construct metadata api url with path and file limit" in {
      val m = Metadata(_ / "foo" / "bar").fileLimit(12345)
      m.url must_== "http://example.com/metadata/dropbox/foo/bar?file_limit=12345"
    }

    "construct metadata api url with path and hash" in {
      val m = Metadata(_ / "foo" / "bar").hash("1234abc")
      m.url must_== "http://example.com/metadata/dropbox/foo/bar?hash=1234abc"
    }

    "construct metadata api url with path and list" in {
      val m = Metadata(_ / "foo" / "bar").list(false)
      m.url must_== "http://example.com/metadata/dropbox/foo/bar?list=false"
    }

    "construct metadata api url with path and include deleted" in {
      val m = Metadata(_ / "foo" / "bar").includeDeleted(true)
      m.url must_== "http://example.com/metadata/dropbox/foo/bar?include_deleted=true"
    }

    "construct metadata api url with path and rev" in {
      val m = Metadata(_ / "foo" / "bar").rev("1234abc")
      m.url must_== "http://example.com/metadata/dropbox/foo/bar?rev=1234abc"
    }

    "construct metadata api url with path and locale" in {
      val m = Metadata(_ / "foo" / "bar").locale(Locale.JAPAN)
      m.url must_== "http://example.com/metadata/dropbox/foo/bar?locale=ja_JP"
    }
  }

  "Delta" should {
    "construct delta api url" in {
      val d = Delta()
      d.url must_== "http://example.com/delta"
    }

    "use POST method" in {
      val d = Delta()
      d.method must_== "POST"
    }

    "construct delta api request with cursor" in {
      val d = Delta().cursor("1234abc")
      d.firstValue("cursor") must_== "1234abc"
    }

    "construct delta api request with locale" in {
      val d = Delta().locale(Locale.JAPAN)
      d.firstValue("locale") must_== "ja_JP"
    }

    "construct delta api request with path prefix" in {
      val d = Delta().pathPrefix("/Photos/Vacation")
      d.firstValue("path_prefix") must_== "/Photos/Vacation"
    }

    "construct delta api request with all parameters" in {
      val d = Delta()
        .cursor("1234abc").locale(Locale.JAPAN).pathPrefix("/Photos/Vacation")
      Map(
        "cursor"      -> d.firstValue("cursor"),
        "locale"      -> d.firstValue("locale"),
        "path_prefix" -> d.firstValue("path_prefix")
      ) must havePairs(
        "cursor"      -> "1234abc",
        "locale"      -> "ja_JP",
        "path_prefix" -> "/Photos/Vacation"
      )
    }
  }

  "Revision" should {
    "construct revisions api url with path" in {
      val r = Revision(_ / "foo" / "bar")
      r.url must_== "http://example.com/revisions/dropbox/foo/bar"
    }

    "use GET method" in {
      val r = Revision(_ / "foo" / "bar")
      r.method must_== "GET"
    }

    "construct revisions api url with path and rev limit" in {
      val r = Revision(_ / "foo" / "bar").revLimit(1000)
      r.url must_== "http://example.com/revisions/dropbox/foo/bar?rev_limit=1000"
    }

    "construct revisions api url with path and locale" in {
      val r = Revision(_ / "foo" / "bar").locale(Locale.JAPAN)
      r.url must_== "http://example.com/revisions/dropbox/foo/bar?locale=ja_JP"
    }
  }

  "Restore" should {
    "construct restore api url with path" in {
      val r = Restore(_ / "foo" / "bar")
      r.url must_== "http://example.com/restore/dropbox/foo/bar"
    }

    "use POST method" in {
      val r = Restore(_ / "foo" / "bar")
      r.method must_== "POST"
    }

    "construct restore api request with locale" in {
      val r = Restore(_ / "foo" / "bar").locale(Locale.JAPAN)
      r.firstValue("locale") must_== "ja_JP"
    }

    "construct restore api request with rev" in {
      val r = Restore(_ / "foo" / "bar").rev("1234abc")
      r.firstValue("rev") must_== "1234abc"
    }

    "construct restore api request with all parameters" in {
      val r = Restore(_ / "foo" / "bar").locale(Locale.JAPAN).rev("1234abc")
      Map(
        "locale" -> r.firstValue("locale"),
        "rev"    -> r.firstValue("rev")
      ) must havePairs(
        "locale" -> "ja_JP",
        "rev"    -> "1234abc"
      )
    }
  }

  "Search" should {
    "construct search api url with path and query" in {
      val s = Search(_ / "foo" / "bar", "baz")
      s.url must_== "http://example.com/search/dropbox/foo/bar?query=baz"
    }

    "construct search api url with path and file limit" in {
      val s = Search(_ / "foo" / "bar", "baz").fileLimit(1000)
      s.url must_== "http://example.com/search/dropbox/foo/bar?query=baz&file_limit=1000"
    }

    "construct search api url with path and include deleted" in {
      val s = Search(_ / "foo" / "bar", "baz").includeDeleted(false)
      s.url must_== "http://example.com/search/dropbox/foo/bar?query=baz&include_deleted=false"
    }

    "construct search api url with path and locale" in {
      val s = Search(_ / "foo" / "bar", "baz").locale(Locale.JAPAN)
      s.url must_== "http://example.com/search/dropbox/foo/bar?query=baz&locale=ja_JP"
    }
  }

  "Share" should {
    "construct shares api url with path" in {
      val s = Share(_ / "foo" / "bar")
      s.url must_== "http://example.com/shares/dropbox/foo/bar"
    }

    "use POST method" in {
      val s = Share(_ / "foo" / "bar")
      s.method must_== "POST"
    }

    "construct shares api request with locale" in {
      val s = Share(_ / "foo" / "bar").locale(Locale.JAPAN)
      s.firstValue("locale") must_== "ja_JP"
    }

    "construct shares api request with short url" in {
      val s = Share(_ / "foo" / "bar").shortUrl(false)
      s.firstValue("short_url") must_== "false"
    }

    "construct shres api request with all parameters" in {
      val s = Share(_ / "foo" / "bar").locale(Locale.JAPAN).shortUrl(true)
      Map(
        "locale"    -> s.firstValue("locale"),
        "short_url" -> s.firstValue("short_url")
      ) must havePairs(
        "locale"    -> "ja_JP",
        "short_url" -> "true"
      )
    }
  }

  "Media" should {
    "construct media api url with path" in {
      val m = Media(_ / "foo" / "bar")
      m.complete(h).url must_== "http://example.com/media/dropbox/foo/bar"
    }

    "use POST method" in {
      val m = Media(_ / "foo" / "bar")
      m.method must_== "POST"
    }

    "construct media api request with locale" in {
      val m = Media(_ / "foo" / "bar").locale(Locale.JAPAN)
      m.firstValue("locale") must_== "ja_JP"
    }
  }

  "CopyRef" should {
    "construct copy ref api url with path" in {
      val cr = CopyRef(_ / "foo" / "bar")
      cr.url must_== "http://example.com/copy_ref/dropbox/foo/bar"
    }

    "use GET method" in {
      val cr = CopyRef(_ / "foo" / "bar")
      cr.method must_== "GET"
    }
  }

  "Thumbnail" should {
    "construct thumnails api url with path" in {
      val t = Thumbnail(_ / "foo" / "bar")
      t.url must_== "http://example.com/thumbnails/dropbox/foo/bar"
    }

    "use GET method" in {
      val t = Thumbnail(_ / "foo" / "bar")
      t.method must_== "GET"
    }

    "construct thumnails api url with path and format" in {
      val t = Thumbnail(_ / "foo" / "bar").format("png")
      t.url must_== "http://example.com/thumbnails/dropbox/foo/bar?format=png"
    }

    "construct thumnails api url with path" in {
      val t = Thumbnail(_ / "foo" / "bar").size("m")
      t.url must_== "http://example.com/thumbnails/dropbox/foo/bar?size=m"
    }
  }

  "ChunkedUpload" should {
    "construct chunked upload api url with path" in {
      val cu = ChunkedUpload(_ / "foo" / "bar")
      cu.url must_== "http://example.com/chunked_upload/dropbox/foo/bar"
    }
  }

  "FileOps" should {
    "copy" in {
      "construct copy api url" in {
        val c = FileOps.copy("/To")
        c.url must_== "http://example.com/fileops/copy"
      }

      "use POST method" in {
        val c = FileOps.copy("/To")
        c.method must_== "POST"
      }

      "construct copy api request with required params" in {
        val c = FileOps.copy("/To")
        Map(
          "root"      -> c.firstValue("root"),
          "to_path"   -> c.firstValue("to_path")
        ) must havePairs(
          "root"      -> "dropbox",
          "to_path"   -> "/To"
        )
      }

      "construct copy api request with from path" in {
        val c = FileOps.copy("/To").fromPath("/From")
        c.firstValue("from_path") must_== "/From"
      }

      "construct copy api request with locale" in {
        val c = FileOps.copy("/To").locale(Locale.JAPAN)
        c.firstValue("locale") must_== "ja_JP"
      }

      "construct copy api request with from copy ref" in {
        val c = FileOps.copy("/To").fromCopyRef("1234abc")
        c.firstValue("from_copy_ref") must_== "1234abc"
      }

      "construct copy api request with all params" in {
        val c = FileOps.copy("/To")
          .fromPath("/From").locale(Locale.JAPAN).fromCopyRef("1234abc")
        Map(
          "root"          -> c.firstValue("root"),
          "to_path"       -> c.firstValue("to_path"),
          "from_path"     -> c.firstValue("from_path"),
          "locale"        -> c.firstValue("locale"),
          "from_copy_ref" -> c.firstValue("from_copy_ref")
        ) must havePairs(
          "root"          -> "dropbox",
          "to_path"       -> "/To",
          "from_path"     -> "/From",
          "locale"        -> "ja_JP",
          "from_copy_ref" -> "1234abc"
        )
      }
    }

    "createFolder" in {
      "construct create folder api url" in {
        val cf = FileOps.createFolder("/Foo")
        cf.url must_== "http://example.com/fileops/create_folder"
      }

      "use POST method" in {
        val cf = FileOps.createFolder("/Foo")
        cf.method must_== "POST"
      }

      "construct create folder api request with required params" in {
        val cf = FileOps.createFolder("/Foo")
        Map(
          "root" -> cf.firstValue("root"),
          "path" -> cf.firstValue("path")
        ) must havePairs(
          "root" -> "dropbox",
          "path" -> "/Foo"
        )
      }

      "construct create folder api request with locale" in {
        val cf = FileOps.createFolder("/Foo").locale(Locale.JAPAN)
        cf.firstValue("locale") must_== "ja_JP"
      }
    }

    "delete" in {
      "construct delete api url" in {
        val d = FileOps.delete("/Foo")
        d.url must_== "http://example.com/fileops/delete"
      }

      "use POST method" in {
        val d = FileOps.delete("/Foo")
        d.method must_== "POST"
      }

      "construct delete api request with required params" in {
        val d = FileOps.delete("/Foo")
        Map(
          "root" -> d.firstValue("root"),
          "path" -> d.firstValue("path")
        ) must havePairs(
          "root" -> "dropbox",
          "path" -> "/Foo"
        )
      }

      "construct delete api request with locale" in {
        val d = FileOps.delete("/Foo").locale(Locale.JAPAN)
        d.firstValue("locale") must_== "ja_JP"
      }

      "construct delete api request with all params" in {
        val d = FileOps.delete("/Foo").locale(Locale.JAPAN)
        Map(
          "root"   -> d.firstValue("root"),
          "path"   -> d.firstValue("path"),
          "locale" -> d.firstValue("locale")
        ) must havePairs(
          "root"   -> "dropbox",
          "path"   -> "/Foo",
          "locale" -> "ja_JP"
        )
      }
    }

    "move" in {
      "construct move api url" in {
        val m = FileOps.move("/To")
        m.complete(h).url must_== "http://example.com/fileops/move"
      }

      "use POST method" in {
        val m = FileOps.move("/To")
        m.method must_== "POST"
      }

      "construct move api request with required params" in {
        val m = FileOps.move("/To")
        Map(
          "root"      -> m.firstValue("root"),
          "to_path"   -> m.firstValue("to_path")
        ) must havePairs(
          "root"      -> "dropbox",
          "to_path"   -> "/To"
        )
      }

      "construct move api request with from path" in {
        val m = FileOps.move("/To").fromPath("/From")
        m.firstValue("from_path") must_== "/From"
      }

      "construct move api request with locale" in {
        val m = FileOps.move("/To").locale(Locale.JAPAN)
        m.firstValue("locale") must_== "ja_JP"
      }

      "construct move api request with all params" in {
        val m = FileOps.move("/To")
          .fromPath("/From").locale(Locale.JAPAN)
        Map(
          "root"          -> m.firstValue("root"),
          "to_path"       -> m.firstValue("to_path"),
          "from_path"     -> m.firstValue("from_path"),
          "locale"        -> m.firstValue("locale")
        ) must havePairs(
          "root"          -> "dropbox",
          "to_path"       -> "/To",
          "from_path"     -> "/From",
          "locale"        -> "ja_JP"
        )
      }
    }
  }
}
