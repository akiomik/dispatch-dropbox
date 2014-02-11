package com.github.akiomik.dispatch.dropbox.core

import dispatch._

trait Method {
  def complete: Req => Req
  def apply(req: Req): Req = complete(req)
}

trait Path {
  val path: Req => Req
  def base: Req => Req
  def root: Req => Req
}

trait DropboxPath extends Path {
  def root: Req => Req = _ / "dropbox"
}

trait SandboxPath extends Path {
  def root: Req => Req = _ / "sandbox"
}

object Account {
  def info(): Info = Info()

  case class Info(params: Map[String, String] = Map())
      extends Method with Param[Info] with LocaleParam[Info] {
    def complete = _ / "account" / "info" <<? params
    def param[A: Show](key: String)(value: A): Info =
      copy(params = params + (key -> implicitly[Show[A]].shows(value)))
  }
}

case class File(path: Req => Req, params: Map[String, String] = Map())
    extends Method with DropboxPath with Param[File] with RevParam[File] {
  def base: Req => Req = _ / "files"
  def withParams: Req => Req = _ <<? params
  def complete = base andThen root andThen path andThen withParams
  def param[A: Show](key: String)(value: A): File =
    copy(params = params + (key -> implicitly[Show[A]].shows(value)))
}

case class FilePut(path: Req => Req, params: Map[String, String] = Map())
    extends Method with DropboxPath with Param[FilePut] with LocaleParam[FilePut] {
  def base: Req => Req = _ / "files_put"
  def withParams: Req => Req = _ << params
  def complete = base andThen root andThen path andThen withParams

  def param[A: Show](key: String)(value: A): FilePut =
    copy(params = params + (key -> implicitly[Show[A]].shows(value)))
  val overwrite = 'overwrite[Boolean]
  val parentRev = 'parent_rev[String]
}

case class Metadata(path: Req => Req, params: Map[String, String] = Map())
    extends Method with DropboxPath with Param[Metadata]
    with RevParam[Metadata] with LocaleParam[Metadata]
    with FileLimitParam[Metadata] with IncludeDeletedParam[Metadata] {
  def base: Req => Req = _ / "metadata"
  def withParams: Req => Req = _ <<? params
  def complete = base andThen root andThen path andThen withParams

  def param[A: Show](key: String)(value: A): Metadata =
    copy(params = params + (key -> implicitly[Show[A]].shows(value)))
  val hash = 'hash[String]
  val list = 'list[Boolean]
}

case class Delta(params: Map[String, String] = Map())
    extends Method with Param[Delta] with LocaleParam[Delta] {
  def complete = _ / "delta" << params
  def param[A: Show](key: String)(value: A): Delta =
    copy(params = params + (key -> implicitly[Show[A]].shows(value)))
  val cursor     = 'cursor[String]
  val pathPrefix = 'path_prefix[String]
}

case class Revision(path: Req => Req, params: Map[String, String] = Map())
    extends Method with DropboxPath with Param[Revision] with LocaleParam[Revision] {
  def base: Req => Req = _ / "revisions"
  def withParams: Req => Req = _ <<? params
  def complete = base andThen root andThen path andThen withParams
  def param[A: Show](key: String)(value: A): Revision =
    copy(params = params + (key -> implicitly[Show[A]].shows(value)))
  val revLimit = 'rev_limit[Int]
}

case class Restore(path: Req => Req, params: Map[String, String] = Map())
    extends Method with DropboxPath with Param[Restore]
    with RevParam[Restore] with LocaleParam[Restore] {
  def base: Req => Req = _ / "restore"
  def withParams: Req => Req = _ << params
  def complete = base andThen root andThen path andThen withParams
  def param[A: Show](key: String)(value: A): Restore =
    copy(params = params + (key -> implicitly[Show[A]].shows(value)))
}

// TODO
// ファクトリ化したい
case class Search(path: Req => Req, q: String, params: Map[String, String] = Map())
    extends Method with DropboxPath with Param[Search] with FileLimitParam[Search]
    with IncludeDeletedParam[Search] with LocaleParam[Search] {
  val queryParams = params + ("query" -> q)

  def base: Req => Req = _ / "search"
  def withParams: Req => Req = _ <<? queryParams
  def complete = base andThen root andThen path andThen withParams
  def param[A: Show](key: String)(value: A): Search =
    copy(params = queryParams + (key -> implicitly[Show[A]].shows(value)))
}

// object Search {
//   def apply(path: Req => Req, q: String): Search = Search(path, Map("query" -> q))
// }

case class Share(path: Req => Req, params: Map[String, String] = Map())
    extends Method with DropboxPath with Param[Share] with LocaleParam[Share] {
  def base: Req => Req = _ / "shares"
  def withParams: Req => Req = _ << params
  def complete = base andThen root andThen path andThen withParams
  def param[A: Show](key: String)(value: A): Share =
    copy(params = params + (key -> implicitly[Show[A]].shows(value)))
  val shortUrl = 'short_url[Boolean]
}

case class Media(path: Req => Req, params: Map[String, String] = Map())
    extends Method with DropboxPath with Param[Media] with LocaleParam[Media] {
  def base: Req => Req = _ / "media"
  def withParams: Req => Req = _ << params
  def complete = base andThen root andThen path andThen withParams
  def param[A: Show](key: String)(value: A): Media =
    copy(params = params + (key -> implicitly[Show[A]].shows(value)))
}

case class CopyRef(path: Req => Req) extends Method with DropboxPath {
  def base: Req => Req = _ / "copy_ref"
  def complete = base andThen root andThen path
}

case class Thumbnail(path: Req => Req, params: Map[String, String] = Map())
    extends Method with DropboxPath with Param[Thumbnail] {
  def base: Req => Req = _ / "thumbnails"
  def withParams: Req => Req = _ <<? params
  def complete = base andThen root andThen path andThen withParams

  def param[A: Show](key: String)(value: A): Thumbnail =
    copy(params = params + (key -> implicitly[Show[A]].shows(value)))
  val format = 'format[String]
  val size   = 'size[String]
}

// TODO
// PUT&POST
case class ChunkedUpload(path: Req => Req, params: Map[String, String] = Map())
    extends Method with DropboxPath {
  def base: Req => Req = _ / "chunked_upload"
  def withParams: Req => Req = _ << params
  def complete = base andThen root andThen path andThen withParams
}

object FileOps {
  def copy(to: String): Copy =
    Copy(Map("root" -> "dropbox", "to_path" -> to))

  case class Copy(params: Map[String, String] = Map())
      extends Method with Param[Copy]
      with TwoFileOpsParam[Copy] with LocaleParam[Copy] {
    def complete = _ / "fileops" / "copy" << params
    def param[A: Show](key: String)(value: A): Copy =
      copy(params = params + (key -> implicitly[Show[A]].shows(value)))
    val fromCopyRef = 'from_copy_ref[String]
  }

  def createFolder(path: String): CreateFolder =
    CreateFolder(Map("root" -> "dropbox", "path" -> path))

  case class CreateFolder(params: Map[String, String] = Map())
      extends Method with Param[CreateFolder]
      with OneFileOpsParam[CreateFolder] with LocaleParam[CreateFolder] {
    def complete = _ / "fileops" / "create_folder" << params
    def param[A: Show](key: String)(value: A): CreateFolder =
      copy(params = params + (key -> implicitly[Show[A]].shows(value)))
  }

  def delete(path: String): Delete =
    Delete(Map("root" -> "dropbox", "path" -> path))

  case class Delete(params: Map[String, String] = Map())
      extends Method with Param[Delete]
      with OneFileOpsParam[Delete] with LocaleParam[Delete] {
    def complete = _ / "fileops" / "delete" << params
    def param[A: Show](key: String)(value: A): Delete =
      copy(params = params + (key -> implicitly[Show[A]].shows(value)))
  }

  def move(to: String): Move =
    Move(Map("root" -> "dropbox", "to_path" -> to))

  case class Move(params: Map[String, String] = Map())
      extends Method with Param[Move]
      with TwoFileOpsParam[Move] with LocaleParam[Move] {
    def complete = _ / "fileops" / "move" << params
    def param[A: Show](key: String)(value: A): Move =
      copy(params = params + (key -> implicitly[Show[A]].shows(value)))
  }
}

