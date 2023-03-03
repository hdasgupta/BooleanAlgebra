package com.digital.Digital.parser

import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Component
class Tokenizer {

    fun parse(exp: String): List<Token> {
        val expression: StringBuilder = StringBuilder()
        val tokens = mutableListOf<Token>()

        for (i in exp.indices) {
            if(!exp[i].isWhitespace()) {
                expression.append(exp[i])
            }
        }

        for (i in expression.indices) {
            if(expression[i].isLetter()) {
                if(i>0) {
                    if(expression[i-1].isLetter() || expression[i-1] == '\'' || expression[i-1] == ')') {
                        tokens.add(Token(".", Tagging.And))
                        tokens.add(Token(expression[i].toString(), Tagging.Input))
                    } else {
                        tokens.add(Token(expression[i].toString(), Tagging.Input))
                    }
                } else {
                    tokens.add(Token(expression[i].toString(), Tagging.Input))
                }
            } else if(expression[i] == '\'') {
                tokens.add(Token(expression[i].toString(), Tagging.Not))
            } else if(expression[i] == '+') {
                tokens.add(Token(expression[i].toString(), Tagging.Or))
            } else if(expression[i] == '(') {
                tokens.add(Token(expression[i].toString(), Tagging.Opening))
            } else if(expression[i] == ')') {
                tokens.add(Token(expression[i].toString(), Tagging.Closing))
            } else if(expression[i] == '.') {
                tokens.add(Token(expression[i].toString(), Tagging.And))
            }
        }

        return tokens
    }
}