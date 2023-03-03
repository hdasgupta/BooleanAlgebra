package com.digital.Digital.simplify

import com.digital.Digital.parser.Expression
import com.digital.Digital.parser.Input
import com.digital.Digital.parser.Operator
import com.digital.Digital.parser.Operator.*
import com.digital.Digital.parser.SubExpression
import org.springframework.stereotype.Component
import java.util.stream.Stream

@Component
class SimplifyExpression {
    fun simplify(exp:Expression, steps:Steps): Expression =
        if(exp is SubExpression) {
            when(exp.operator) {
                Or ->
                    if(exp.operands.stream().allMatch { it is Input || (it is SubExpression && it.operator==Not && it.operands[0] is Input) }) {
                        exp
                    } else {
                        val ex = SubExpression(
                            Or,
                            or(exp, steps).toList().toMutableList()
                        )
                        steps.add(Step(exp.toString(), ex.toString(), "Grouping all Or"))
                        ex
                    }
                And ->
                    if(exp.operands.stream().allMatch {
                            it is Input  || (it is SubExpression && it.operator==Not && it.operands[0] is Input)
                    }) {
                        exp
                    } else {

                        val ex = and(exp, steps)

                        steps.add(Step(exp.toString(), ex.toString(), "Distributing And"))
                        ex
                    }
                else ->
                    if(exp.operands[0] is Input) {
                        exp
                    } else {
                        val input = exp.operands[0] as SubExpression
                        val ex = not(input, steps)
                        steps.add(Step(exp.toString(), ex.toString(), "Ungroup Not"))
                        ex
                    }

            }
        } else {
            exp
        }

    fun or(exp:SubExpression, steps: Steps) : Stream<Expression> =
        exp.operands.stream()
            .flatMap {
                val simple = simplify(it, steps)
                if(simple is SubExpression) {
                    if(simple.operator==Or) {
                       or(simple, steps)
                    } else {
                        Stream.of(simple)
                    }
                } else {
                    Stream.of(simple)
                }
            }


    fun and(exp: SubExpression, steps: Steps) : Expression {
        val input = exp.operands
            .toMutableList()
            .filter { it is Input || (it is SubExpression && it.operator==Not && it.operands[0] is Input)}
        val subExp = exp.operands.stream()
            .filter {!input.stream().anyMatch { i->i.deepEquals(it) }}
            .map { simplify(it, steps) }
            .toList()
            .toMutableList() as MutableList<SubExpression>

        var mutableList:MutableList<Expression> =
            if(input.isEmpty())
                mutableListOf()
            else
                mutableListOf(and(input.toMutableList()))

        var list:MutableList<Expression> = mutableListOf()

        for(ex in subExp) {
            var expr = simplify(ex, steps) as SubExpression
            if(mutableList.isEmpty()) {
                if(expr.operator==Or) {
                    mutableList.addAll(expr.operands)
                } else {
                    mutableList.add(expr)
                }
            } else {
                if(expr is SubExpression) {
                    when(expr.operator) {
                        Or->
                            for (exp in expr.operands) {
                                list.addAll(and(mutableList, exp))
                            }
                        else ->
                            list.addAll(and(mutableList, expr))
                    }

                } else {
                    list.addAll(and(mutableList, expr))
                }
            }
        }

        return if(list.size == 1) {
             list[0]
        } else {
            SubExpression(Or, list)
        }

    }

    fun and(exp1: MutableList<Expression>, exp2: Expression): MutableList<Expression> =
        if(exp1.isEmpty()) {
            if (exp2 is SubExpression){
                when(exp2.operator) {
                    And,Not ->
                        mutableListOf(exp2)
                    else ->
                        exp2.operands
                }
            } else {
                mutableListOf(exp2)
            }
        } else {
            val mutableList = mutableListOf<Expression>()
            if (exp2 is SubExpression) {
                when (exp2.operator) {
                    And ->
                        for (exp in exp1) {
                            if(exp is SubExpression) {
                                when(exp.operator) {
                                    Or->
                                        mutableList.addAll(and(exp.operands, exp2))
                                    And->
                                    {
                                        val mList = exp.operands.toMutableList()
                                        mList.addAll(exp2.operands)
                                        mutableList.add(and(mList))

                                    }
                                    else ->
                                    {
                                        val mList = exp2.operands.toMutableList()
                                        mList.add(exp)
                                        mutableList.add(and(mList))

                                    }
                                }
                            } else {
                                val mList = exp2.operands.toMutableList()
                                mList.add(exp)
                                mutableList.add(and(mList))
                            }
                        }
                    Or->
                        for (exp in exp1) {
                            mutableList.addAll(and(exp2.operands, exp))
                        }
                    else ->
                        for (exp in exp1) {
                            if(exp is SubExpression) {
                                when(exp.operator) {
                                    And ->
                                    {
                                        val mList = exp2.operands.toMutableList()
                                        mList.add(exp)
                                        mutableList.add(
                                            and(mList)
                                        )
                                    }
                                    Or ->
                                    {
                                        mutableList.addAll(
                                            and(exp.operands, exp2)
                                        )
                                    }
                                    else ->
                                    {
                                        mutableList.add(
                                            and(
                                                mutableListOf(exp, exp2)
                                            )
                                        )
                                    }
                                }
                            } else {
                                mutableList.add(
                                    and(
                                        mutableListOf(exp, exp2)
                                    )
                                )
                            }
                        }
                }

            } else {
                for (exp in exp1) {
                    if(exp is SubExpression) {
                        when(exp.operator) {
                            And->
                            {
                                val mList = exp.operands.toMutableList()
                                mList.add(exp2)
                                mutableList.add(
                                    and(mList)
                                )
                            }
                            Or->
                            {
                                mutableList.addAll(
                                    and(exp.operands, exp2)
                                )
                            }
                            else->
                            {
                                mutableList.add(
                                    and(
                                        mutableListOf(exp, exp2)
                                    )
                                )
                            }
                        }
                    } else {
                        mutableList.add(
                            and(
                                mutableListOf(exp, exp2)
                            )
                        )
                    }
                }
            }

            mutableList
        }

    fun and(mList: MutableList<Expression>): Expression =
        if (mList.size>1) {

                SubExpression(
                    And,
                    mList
                )
        } else {
            mList[0]
        }



    fun not(exp: SubExpression, steps: Steps): Expression =
        when(exp.operator) {
            And ->
                SubExpression(
                    Or,
                    exp.operands.stream()
                    .map {
                        if(it is SubExpression)
                            not(it, steps)
                        else
                            SubExpression(Not, mutableListOf(it)) }
                    .toList()
                    .toMutableList()
                )
            Or->
                and(
                    exp.operands.stream()
                    .map {
                        if(it is SubExpression)
                            not(it, steps)
                        else
                            SubExpression(Not, mutableListOf(it)) }
                    .toList()
                    .toMutableList()
                )
            else ->
                if(exp.operands[0] is SubExpression) {
                    val input = exp.operands[0] as SubExpression
                    if(input.operator== And) {
                        SubExpression(
                            Or,
                            input.operands.stream()
                                .map {
                                    if(it is SubExpression)
                                        not(it, steps)
                                    else
                                        SubExpression(Not, mutableListOf(it)) }
                                .toList()
                                .toMutableList()
                        )
                    } else {
                        and(
                            input.operands.stream()
                                .map {
                                    if(it is SubExpression)
                                        not(it, steps)
                                    else
                                        SubExpression(Not, mutableListOf(it)) }
                                .toList()
                                .toMutableList()
                        )
                    }

                } else {
                    exp.operands[0]
                }
        }

}