package models

case class PagedResult[T](
  pageSize: Int,
  pageNr: Int,
  totalCount: Int,
  data: Seq[T])
