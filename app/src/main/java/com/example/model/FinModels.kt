package com.example.model

enum class SignalType {
    OPPORTUNITY,
    WATCH,
    RISK
}

enum class RiskTolerance {
    CONSERVATIVE,
    MODERATE,
    AGGRESSIVE
}

enum class InvestmentHorizon {
    SHORT_TERM, // < 1 year
    MEDIUM_TERM, // 1-5 years
    LONG_TERM // 5+ years
}

enum class InvestmentExperience {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

data class UserProfile(
    val name: String = "Alex Mercer",
    val email: String = "alex@finpilot.ai",
    val riskTolerance: RiskTolerance = RiskTolerance.MODERATE,
    val investmentGoal: String = "Wealth Accumulation & Capital Growth",
    val investmentHorizon: InvestmentHorizon = InvestmentHorizon.LONG_TERM,
    val experience: InvestmentExperience = InvestmentExperience.INTERMEDIATE,
    val preferredSectors: List<String> = listOf("Technology", "Clean Energy", "Healthcare"),
    val paperBalance: Double = 100000.00
)

data class StockQuote(
    val symbol: String,
    val companyName: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val volume: Long,
    val dayHigh: Double,
    val dayLow: Double,
    val peRatio: Double,
    val marketCap: String,
    val sector: String,
    val sparklineData: List<Double> = emptyList()
)

data class TechnicalIndicators(
    val symbol: String,
    val rsi: Double, // 14-day RSI
    val macd: Double,
    val macdSignal: Double,
    val sma50: Double,
    val sma200: Double,
    val volatility: String,
    val trend: String // e.g. "Bullish Momentum"
)

data class FundamentalMetrics(
    val symbol: String,
    val revenueGrowth: String,
    val netProfitMargin: String,
    val eps: Double,
    val peRatio: Double,
    val pbRatio: Double,
    val roe: String,
    val debtToEquity: Double,
    val freeCashFlow: String
)

data class NewsArticle(
    val id: String,
    val title: String,
    val source: String,
    val summary: String,
    val publishedAt: String,
    val relatedSymbol: String,
    val sentiment: String, // POSITIVE, NEUTRAL, NEGATIVE
    val sentimentScore: Float // 0.0 to 1.0
)

data class AiInsight(
    val id: String,
    val symbol: String,
    val signal: SignalType,
    val confidence: Float,
    val summary: String,
    val reasons: List<String>,
    val supportingEvidence: List<String>,
    val risks: List<String>,
    val timeHorizon: String,
    val timestamp: String,
    val disclaimer: String = "AI-generated financial research for educational & paper-trading purposes only. Not financial advice."
)

data class PortfolioItem(
    val symbol: String,
    val companyName: String,
    val shares: Double,
    val avgPrice: Double,
    val currentPrice: Double,
    val sector: String
) {
    val totalValue: Double get() = shares * currentPrice
    val totalCost: Double get() = shares * avgPrice
    val profitLoss: Double get() = totalValue - totalCost
    val profitLossPercent: Double get() = if (totalCost > 0) (profitLoss / totalCost) * 100 else 0.0
}

data class AlertNotification(
    val id: String,
    val symbol: String,
    val title: String,
    val message: String,
    val signalType: SignalType,
    val timestamp: String,
    val isRead: Boolean = false
)
