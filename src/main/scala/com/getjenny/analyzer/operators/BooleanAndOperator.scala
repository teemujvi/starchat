package com.getjenny.analyzer.operators

import com.getjenny.analyzer.expressions._

/**
  * Created by mal on 21/02/2017.
  */

class BooleanAndOperator(children: List[Expression]) extends AbstractOperator(children: List[Expression]) {
  override def toString: String = "booleanAnd(" + children.mkString(", ") + ")"
  def add(e: Expression, level: Int = 0): AbstractOperator = {
    if (level == 0) new BooleanAndOperator(e :: children)
    else children.head match {
      case c: AbstractOperator => new BooleanAndOperator(c.add(e, level - 1) :: children.tail)
      case _ => throw OperatorException("booleanAnd: trying to add to smt else than an operator")
    }
  }

  def evaluate(query: String): Result = {
    def loop(l: List[Expression]): Result = {
      val first_res = l.head.matches(query)
      if (first_res.score != 1) {
        Result(score=0, extracted_variables = first_res.extracted_variables)
      }
      else if (l.tail == Nil) {
        Result(score=1, extracted_variables = first_res.extracted_variables)
      }
      else {
        val res = loop(l.tail)
        Result(score = res.score, extracted_variables = res.extracted_variables ++ first_res.extracted_variables)
      }
    }

    loop(children)
  }
}
