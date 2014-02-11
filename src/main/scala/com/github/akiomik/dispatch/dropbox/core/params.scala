package com.github.akiomik.dispatch.dropbox.core

import java.util.Locale

trait Show[A] {
  def shows(a: A): String
}

object Show {
  def showA[A]: Show[A] = new Show[A] {
    def shows(a: A): String = a.toString 
  }

  implicit val stringShow  = showA[String]
  implicit val intShow     = showA[Int]
  implicit val bigIntShow  = showA[BigInt]
  implicit val booleanShow = showA[Boolean]
  implicit val localeShow  = showA[Locale]
}

trait Param[R] {
  val params: Map[String, String]
  def param[A: Show](key: String)(value: A): R
  implicit class SymOp(sym: Symbol) {
    def apply[A: Show]: A => R = param(sym.name)_
  }
}

trait RevParam[R] { self: Param[R] =>
  val rev = 'rev[String]
}

trait LocaleParam[R] { self: Param[R] =>
  val locale = 'locale[Locale]
}

trait FileLimitParam[R] { self: Param[R] =>
  val fileLimit = 'file_limit[Int]
}

trait IncludeDeletedParam[R] { self: Param[R] =>
  val includeDeleted = 'include_deleted[Boolean]
}

trait FileOpsParam[R] { self: Param[R] =>
  val root = 'root[String]
}

trait OneFileOpsParam[R] extends FileOpsParam[R] { self: Param[R] =>
  val path = 'path[String]
}

trait TwoFileOpsParam[R] extends FileOpsParam[R] { self: Param[R] =>
  val fromPath = 'from_path[String]
  val toPath   = 'to_path[String]
}

