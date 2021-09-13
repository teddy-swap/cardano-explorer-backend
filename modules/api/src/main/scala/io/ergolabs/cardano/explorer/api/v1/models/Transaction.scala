package io.ergolabs.cardano.explorer.api.v1.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.ergolabs.cardano.explorer.core.db.models.{Asset, Input, Output, Transaction => DbTransaction}
import io.ergolabs.cardano.explorer.core.types.{BlockHash, OutRef, TxHash}
import sttp.tapir.Schema

@derive(encoder, decoder)
final case class Transaction(
  blockHash: BlockHash,
  blockIndex: Long,
  hash: TxHash,
  inputs: List[TxInput],
  outputs: List[TxOutput],
  size: Int
)

object Transaction {

  implicit def schema: Schema[Transaction] = Schema.derived

  def inflate(tx: DbTransaction, inputs: List[Input], outputs: List[Output], assets: List[Asset]): Transaction = {
    val txInputs = inputs.map { i =>
      TxInput(OutRef(i.outTxHash, i.outIndex), i.outTxHash, i.outIndex, i.value, i.value.toString())
    }
    val txOutputs = outputs.map(o => TxOutput.inflate(o, assets.filter(_.outIndex == o.index)))
    Transaction(tx.blockHash, tx.blockIndex, tx.hash, txInputs, txOutputs, tx.size)
  }

  def inflateBatch(
    txs: List[DbTransaction],
    inputs: List[Input],
    outputs: List[Output],
    assets: List[Asset]
  ): List[Transaction] = {
    val inputsByTx  = inputs.groupBy(_.txId)
    val outputsByTx = outputs.groupBy(_.txId)
    val assetsByTx  = assets.groupBy(_.txId)
    txs.map { tx =>
      Transaction.inflate(
        tx,
        inputsByTx.getOrElse(tx.id, List.empty),
        outputsByTx.getOrElse(tx.id, List.empty),
        assetsByTx.getOrElse(tx.id, List.empty)
      )
    }
  }
}
