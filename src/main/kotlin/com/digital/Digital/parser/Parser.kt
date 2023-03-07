package com.digital.Digital.parser

import com.digital.Digital.common.queue
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.Stack

@Component
class Parser {
    fun parse(tokens: List<Token>) : Expression {
        val operators: Stack<Operator> = Stack()
        val operands: Stack<Expression> = Stack()

        operators.push(Operator.Opening)

        for (token in tokens) {
            if (token.tagging == Tagging.Input) {
                operands.push(Input(token.name))
            } else if(token.tagging == Tagging.Not) {
                val operand: Expression = operands.pop()
                operands.push(SubExpression(Operator.Not, mutableListOf(operand)))
            } else if(token.tagging == Tagging.Opening) {
                operators.push(Operator.Opening)
            } else if(token.tagging == Tagging.And || token.tagging == Tagging.Or) {
                if(!operators.isEmpty() && operators.peek() == Operator.And) {
                    val operand2: Expression = operands.pop()
                    val operand1: Expression = operands.pop()
                    val operatr: Operator = operators.pop()
                    operands.push(SubExpression(operatr, mutableListOf(operand1, operand2)))
                }
                operators.push(Operator.valueOf(token.tagging.name))
            } else {
                autoPop(operators, operands)
            }
        }

        autoPop(operators, operands)

        if (operands.size == 1) {
            queue[operands[0].toString()] = operands[0]
            return operands[0]
        } else {
            throw RuntimeException("Invalid Expression")
        }
    }

    fun autoPop(operators: Stack<Operator>, operands: Stack<Expression>) {
        var operator: Operator = operators.pop()
        while (operator != Operator.Opening) {
            val operand2: Expression = operands.pop()
            val operand1: Expression = operands.pop()
            operands.push(SubExpression(operator, mutableListOf(operand1, operand2)))

            operator = operators.pop()
        }
    }
}