package io.ergolabs.cardano.explorer.api.v1.endpoints

import io.ergolabs.cardano.explorer.api.v1.HttpError
import io.ergolabs.cardano.explorer.api.v1.models.{Items, Paging, Transaction}
import io.ergolabs.cardano.explorer.core.types.{Addr, TxHash}
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody

class TransactionsEndpoints {

  val pathPrefix = "transactions"

  def getByTxHash: Endpoint[TxHash, HttpError, Transaction, Any] =
    baseEndpoint.get
      .in(pathPrefix / path[TxHash])
      .out(jsonBody[Transaction])

  def getAll: Endpoint[Paging, HttpError, Items[Transaction], Any] =
    baseEndpoint.get
      .in(pathPrefix)
      .in(paging)
      .out(jsonBody[Items[Transaction]])

  def getByBlock: Endpoint[Int, HttpError, Items[Transaction], Any] =
    baseEndpoint.get
      .in(pathPrefix / "byBlockHeight" / path[Int])
      .out(jsonBody[Items[Transaction]])

  def getByAddress: Endpoint[(Addr, Paging), HttpError, Items[Transaction], Any] =
    baseEndpoint.get
      .in(pathPrefix / "byAddress" / path[Addr])
      .in(paging)
      .out(jsonBody[Items[Transaction]])
}
