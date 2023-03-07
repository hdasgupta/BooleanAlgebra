package com.digital.Digital.controller

import com.digital.Digital.common.queue
import com.digital.Digital.parser.Parser
import com.digital.Digital.parser.Tokenizer
import com.digital.Digital.pos.POS
import com.digital.Digital.simplify.Shorten
import com.digital.Digital.simplify.SimplifyExpression
import com.digital.Digital.simplify.Step
import com.digital.Digital.simplify.Steps
import com.digital.Digital.sop.SOP
import com.digital.Digital.tt.TruthTable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PrintController {

    @RequestMapping(value = ["/html"])
    fun getTTHtml(@RequestParam formula: String, map: ModelMap): String {
        try {
            val exp = queue[formula.replace("&#39;", "'")]

            map["operand"] = exp

        } catch (t: Throwable) {
            map["results"] = ArrayList<Step>()
        }

        return "operands/MainOperand"
    }

}