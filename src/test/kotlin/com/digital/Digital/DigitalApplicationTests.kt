package com.digital.Digital

import com.digital.Digital.parser.Parser
import com.digital.Digital.parser.Tokenizer
import com.digital.Digital.simplify.Shorten
import com.digital.Digital.simplify.SimplifyExpression
import com.digital.Digital.simplify.Steps
import com.digital.Digital.tt.TruthTable
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DigitalApplicationTests {

	@Autowired
	lateinit var tokenizer: Tokenizer

	@Autowired
	lateinit var parser: Parser

	@Autowired
	lateinit var simplifyExpression: SimplifyExpression

	@Autowired
	lateinit var shorten: Shorten

	@Autowired
	lateinit var truthTable: TruthTable

	@Test
	fun contextLoads() {
		val exp = "A'.B'.C+A.B.C'+B'.C.D+B.C'.D'"
		val e = parser.parse(
			tokenizer.parse(exp)
		)
		val steps = Steps(e.toString())
		println(
			truthTable.get(
			shorten.shorten(
				simplifyExpression.simplify(
					e, steps
				), steps
			)
			)
		)

		steps.prityPrint()
	}

}
