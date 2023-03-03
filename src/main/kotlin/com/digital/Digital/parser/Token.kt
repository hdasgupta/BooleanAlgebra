package com.digital.Digital.parser

class Token(val name: String, val tagging: Tagging) {
    override fun toString(): String {
        return name + "[" + tagging.name + "]"
    }
}