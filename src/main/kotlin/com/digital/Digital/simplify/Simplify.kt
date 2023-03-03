//package com.digital.Digital.simplify2sop
//
//import com.digital.Digital.parser.Expression
//import com.digital.Digital.parser.Input
//import com.digital.Digital.parser.Operator
//import com.digital.Digital.parser.SubExpression
//import sun.jvm.hotspot.asm.Operand
//
//class Simplify {
//    fun simplify(expression: Expression) : Expression {
//        return if (expression is Input) {
//            expression
//        } else if (expression is SubExpression) {
//            val newOperands: List<Expression> = expression.operands.stream()
//                .map {
//                    return
//                    if (!isSimpleSop(it)) {
//                        simplify(it)
//                    } else {
//                        it
//                    }
//                }
//
//            return
//            if (expression.operator == Operator.Not) {
//                val newOperand = expression.operands[0]
//                if (newOperand is Input) {
//                    expression
//                } else if (newOperand is SubExpression) {
//                    if (newOperand.operator == Operator.Not) {
//                        newOperand.operands[0]
//                    } else if (newOperand.operator == Operator.Or) {
//                        val newOperands = newOperand.operands.stream()
//                            .map {
//                                if (it is Input) {
//                                    SubExpression(Operator.Not, mutableListOf(it))
//                                } else if (it is SubExpression) {
//                                    SubExpression(Operator.Or,
//                                        it.operands.stream()
//                                            .map { leaf ->
//                                                if (leaf is Input) {
//                                                    SubExpression(Operator.Not, mutableListOf(leaf))
//                                                } else if (leaf is SubExpression) {
//                                                    leaf.operands[0]
//                                                } else {
//                                                    leaf
//                                                }
//                                            }
//                                    )
//                                }
//                            }
//
//                        SubExpression(Operator.And, newOperands)
//                    }
//                }
//            } else if (expression.operator == Operator.Or) {
//                SubExpression(
//                    Operator.Or,
//                    newOperands.stream()
//                        .flatMap {
//                            if (it is Input) {
//                                listOf(it)
//                            } else if (it is SubExpression) {
//                                if (it.operator == Operator.Not) {
//                                    listOf(it)
//                                } else if (it.operator == Operator.Or) {
//                                    it.operands
//                                } else {
//                                    listOf(it)
//                                }
//                            }
//                        }
//                )
//            } else {
//                val operands = mutableListOf<Expression>()
//                expression.operands
//                    .forEach {
//                        if (it is Input) {
//                            operands.add(it)
//                        } else if (it is SubExpression) {
//                            if (it.operator == Operator.Not) {
//                                operands.indices.forEach { index ->
//                                    if (operands[index] is Input) {
//                                        operands[index] =
//                                            SubExpression(Operator.And, mutableListOf(operands[index], it.operands[0]))
//                                    } else if (operands[index] is SubExpression) {
//                                        val operands = mutableListOf<Expression>()
//                                        operands.addAll((operands[index] as SubExpression).operands)
//                                        operands.add(it.operands[0])
//                                        operands[index] = SubExpression(Operator.And, operands)
//                                    }
//                                }
//                                val operands = mutableListOf<Expression>()
//                            } else if (it.operator == Operator.And) {
//                                operands.indices.forEach { index ->
//                                    if (operands[index] is Input) {
//                                        operands.add(operands[index])
//                                        operands.addAll(it.operands)
//                                        operands[index] = SubExpression(Operator.And, operands)
//                                    } else if (operands[index] is SubExpression) {
//                                        val operands = mutableListOf<Expression>()
//                                        operands.addAll((operands[index] as SubExpression).operands)
//                                        operands.addAll(it.operands)
//                                        operands[index] = SubExpression(Operator.And, operands)
//                                    }
//                                }
//                            } else if (it.operator == Operator.Or) {
//                                operands.indices.forEach { index ->
//                                    if (operands[index] is Input) {
//                                        operands.add(operands[index])
//                                        operands.addAll(it.operands)
//                                        operands[index] = SubExpression(Operator.And, operands)
//                                    } else if (operands[index] is SubExpression) {
//                                        val operands = mutableListOf<Expression>()
//                                        (operands[index] as SubExpression).operands
//                                            .forEach { operand ->
//                                                if (operand is Input) {
//
//                                                }
//                                            }
//                                        operands.addAll((operands[index] as SubExpression).operands)
//                                        operands.addAll(it.operands)
//                                        operands[index] = SubExpression(Operator.And, operands)
//                                    } else {
//                                        expression
//                                    }
//                                }
//                            }
//                            else {
//                                expression
//                            }
//                        } else {
//                            expression
//                        }
//                    }
//            }
//        }
//    }
//
//    fun isSimpleSop(expression: Expression): Boolean {
//        return if(expression is Input) {
//            true
//        } else if(expression is SubExpression) {
//            if(expression.operator==Operator.Or) {
//                expression.operands.stream()
//                    .allMatch {
//
//                        if(it is Input) {
//                            true
//                        } else if(it is SubExpression) {
//                            if(it.operator == Operator.Not) {
//                                it.operands[0] is Input
//                            } else if(it.operator == Operator.And) {
//                                it.operands.stream()
//                                    .allMatch { leaf ->
//
//                                        if (leaf is Input) {
//                                            true
//                                        } else if (leaf is SubExpression) {
//                                            if (leaf.operator == Operator.Not) {
//                                                leaf.operands[0] is Input
//                                            } else {
//                                                false
//                                            }
//
//                                        } else {
//                                            false
//                                        }
//                                    }
//                            } else {
//                                false
//                            }
//
//                        } else {
//                            false
//                        }
//                    }
//            } else {
//                false
//            }
//        } else {
//            false
//        }
//    }
//}