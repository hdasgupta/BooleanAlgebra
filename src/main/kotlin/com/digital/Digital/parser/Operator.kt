package com.digital.Digital.parser

enum class Operator(val symbol: String) {
    And("."), Or("+"), Not("'"), Opening("("), Closing("");
}