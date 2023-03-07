package com.digital.Digital.common

import com.digital.Digital.parser.Expression
import com.digital.Digital.simplify.Steps
import java.util.SortedMap
import java.util.TreeMap

var queue = PriorityQ<Expression>()
var simpleQ = PriorityQ<Expression>()
var shortQ = PriorityQ<Expression>()
var stepsQ = PriorityQ<Steps>()
var sopQ = PriorityQ<Expression>()
var posQ = PriorityQ<Expression>()

class PriorityQ<T>(override val size: Int = 10000): TreeMap<String, T>() {
    private var q = mutableListOf<String>()

    override fun put(key: String, value:T): T? =
        if(q.contains(key.trim())) {
            q.removeAll {it==key}
            if(q.isEmpty())
                q.add(key.trim())
            else
                q.add(0, key.trim())
            value
        } else {
            if(q.isEmpty())
                q.add(key.trim())
            else
                q.add(0, key.trim())
            super.put(key.trim(), value)
            if(q.size>size) {
                val extra = q.removeLast()
                this.remove(extra)
            } else {

            }
            value
        }
    override fun get(key: String):T? =
        if(q.contains(key.trim())) {
            q.remove(key.trim())
            if(q.isEmpty())
                q.add(key.trim())
            else
                q.add(0, key.trim())
            super.get(key.trim())
        } else {
            null
        }
}