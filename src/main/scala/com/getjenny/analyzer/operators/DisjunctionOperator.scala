package com.getjenny.analyzer.operators

import com.getjenny.analyzer.expressions._

/**
  * Created by mal on 21/02/2017.
  */

class DisjunctionOperator(children: List[Expression]) extends AbstractOperator(children: List[Expression]) {
  override def toString: String = "disjunction(" + children.mkString(", ") + ")"
  def add(e: Expression, level: Int = 0): AbstractOperator = {
    if (level == 0) new DisjunctionOperator(e :: children)
    else children.head match {
      case c: AbstractOperator => new DisjunctionOperator(c.add(e, level - 1) :: children.tail)
      case _ => throw OperatorException("Disjunction: trying to add to smt else than an operator")
    }
  }
  def evaluate(query: String): Result = {
    def compDisjunction(l: List[Expression]): Result = {
      val res = l.head.evaluate(query)
      if (l.tail == Nil) Result(score = 1.0 - res.score, extracted_variables = res.extracted_variables)
      else {
        val comp_disj = compDisjunction(l.tail)
        Result(score = (1.0 - res.score) * comp_disj.score,
          extracted_variables = comp_disj.extracted_variables ++ res.extracted_variables)
      }
    }
    val comp_disj = compDisjunction(children)
    Result(score=1.0 - comp_disj.score, extracted_variables = comp_disj.extracted_variables)
  }
}

