package database

case class PagedDBResult[T](
  pageSize: Int,
  pageNr: Int,
  totalCount: Int,
  data: Seq[T])
