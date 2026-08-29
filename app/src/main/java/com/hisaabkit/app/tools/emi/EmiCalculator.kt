package com.hisaabkit.app.tools.emi

import kotlin.math.pow

data class EmiResult(
    val monthlyEmi: Double,
    val totalInterest: Double,
    val totalPayment: Double
)

fun calculateEmi(
    principal: Double,
    annualRate: Double,
    years: Int
): EmiResult {

    require(principal > 0)
    require(annualRate >= 0)
    require(years > 0)

    val months = years * 12

    if (annualRate == 0.0) {

        val emi = principal / months

        return EmiResult(
            monthlyEmi = emi,
            totalInterest = 0.0,
            totalPayment = principal
        )
    }

    val monthlyRate = annualRate / 12.0 / 100.0

    val factor = (1 + monthlyRate).pow(months)

    val emi =
        principal * monthlyRate * factor / (factor - 1)

    val totalPayment = emi * months

    val totalInterest = totalPayment - principal

    return EmiResult(
        monthlyEmi = emi,
        totalInterest = totalInterest,
        totalPayment = totalPayment
    )
}
