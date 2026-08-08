package com.example.ai

import com.example.BuildConfig
import com.example.model.*
import com.example.repository.FinancialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class FinPilotAiAgent(
    private val repository: FinancialRepository
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    /**
     * Generate an evidence-driven, personalized AI Insight for a stock.
     */
    suspend fun generateInsight(stock: StockQuote): AiInsight = withContext(Dispatchers.IO) {
        val userProfile = repository.userProfile.value
        val tech = repository.getTechnicalIndicators(stock.symbol)
        val fund = repository.getFundamentalMetrics(stock.symbol)
        val relatedNews = repository.news.value.filter { it.relatedSymbol == stock.symbol }

        // Attempt Gemini API call if API key is present
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val geminiResult = queryGeminiModel(apiKey, stock, tech, fund, relatedNews, userProfile)
                if (geminiResult != null) {
                    return@withContext geminiResult
                }
            } catch (e: Exception) {
                // Fallback to deterministic agent synthesis
            }
        }

        // Deterministic Evidence-Driven FinPilot AI Agent Pipeline:
        // OBSERVE -> COLLECT -> ANALYZE -> REASON -> PERSONALIZE -> DECIDE -> EXPLAIN
        return@withContext synthesizePersonalizedAgentInsight(stock, tech, fund, relatedNews, userProfile)
    }

    private fun synthesizePersonalizedAgentInsight(
        stock: StockQuote,
        tech: TechnicalIndicators,
        fund: FundamentalMetrics,
        news: List<NewsArticle>,
        profile: UserProfile
    ): AiInsight {
        val newsSentimentScore = news.firstOrNull()?.sentimentScore ?: 0.5f
        val newsTitle = news.firstOrNull()?.title ?: "Broad sector stability noted in latest analyst coverage."

        // Personalization Logic:
        // High RSI (>70) + Aggressive = Opportunity (Momentum trade)
        // High RSI (>70) + Conservative = Risk (Overbought, potential pull-back)
        val signal: SignalType
        val confidence: Float
        val reasons = mutableListOf<String>()
        val evidence = mutableListOf<String>()
        val risks = mutableListOf<String>()

        evidence.add("Price: $${stock.price} (${if (stock.change >= 0) "+" else ""}${stock.changePercent}% today)")
        evidence.add("Technical RSI(14): ${tech.rsi} | Trend: ${tech.trend}")
        evidence.add("Fundamental P/E Ratio: ${stock.peRatio} | YoY Rev Growth: ${fund.revenueGrowth}")
        if (news.isNotEmpty()) {
            evidence.add("Latest News: \"$newsTitle\" (${news.first().sentiment} sentiment, score ${news.first().sentimentScore})")
        }

        when {
            tech.rsi > 70.0 -> {
                when (profile.riskTolerance) {
                    RiskTolerance.CONSERVATIVE -> {
                        signal = SignalType.RISK
                        confidence = 0.88f
                        reasons.add("RSI of ${tech.rsi} signals an overbought condition which breaches Conservative risk boundaries.")
                        reasons.add("High P/E multiplier (${stock.peRatio}x) increases vulnerability to market corrections.")
                        risks.add("Short-term downside volatility exceeds conservative drawdown tolerances.")
                        risks.add("Broad market profit-taking could trigger price contraction.")
                    }
                    RiskTolerance.MODERATE -> {
                        signal = SignalType.WATCH
                        confidence = 0.82f
                        reasons.add("Strong technical momentum, but elevated valuation warrants waiting for a pullback.")
                        reasons.add("Solid fundamental growth (${fund.revenueGrowth}) balances short-term overbought technicals.")
                        risks.add("Resistance near recent high of $${stock.dayHigh}.")
                    }
                    RiskTolerance.AGGRESSIVE -> {
                        signal = SignalType.OPPORTUNITY
                        confidence = 0.85f
                        reasons.add("Strong breakout momentum aligns with Aggressive capital expansion strategy.")
                        reasons.add("High institutional volume (${stock.volume / 1_000_000}M shares) supports upward continuation.")
                        risks.add("Sharp tail risk if momentum volume dissipates abruptly.")
                    }
                }
            }
            stock.changePercent > 2.0 -> {
                signal = SignalType.OPPORTUNITY
                confidence = 0.89f
                reasons.add("Bullish earnings and news sentiment momentum (+${stock.changePercent}% price expansion).")
                reasons.add("Healthy technical alignment above 50-day SMA ($${tech.sma50}).")
                risks.add("Sector rotation into defensive assets.")
            }
            stock.changePercent < -1.5 -> {
                if (profile.riskTolerance == RiskTolerance.CONSERVATIVE) {
                    signal = SignalType.RISK
                    confidence = 0.84f
                    reasons.add("Recent downward price pressure (-${stock.changePercent}%) violates capital protection bounds.")
                    risks.add("Potential support break below 200-day SMA.")
                } else {
                    signal = SignalType.WATCH
                    confidence = 0.79f
                    reasons.add("Price pullback presents a potential valuation re-entry point for watchlist monitoring.")
                    risks.add("Continued downward drift if general macro liquidity contracts.")
                }
            }
            else -> {
                signal = SignalType.WATCH
                confidence = 0.80f
                reasons.add("Consolidating within normal trading bounds ($${stock.dayLow} - $${stock.dayHigh}).")
                reasons.add("Fundamental margins (${fund.netProfitMargin}) remain stable.")
                risks.add("Low volatility limits short-term alpha generation.")
            }
        }

        val summary = "FinPilot AI analysis for ${stock.companyName} (${stock.symbol}) evaluates a $signal signal with ${ (confidence * 100).toInt() }% confidence, tailored specifically for your ${profile.riskTolerance.name.lowercase()} risk profile and ${profile.investmentGoal} goals."

        return AiInsight(
            id = "insight_${stock.symbol}_${System.currentTimeMillis()}",
            symbol = stock.symbol,
            signal = signal,
            confidence = confidence,
            summary = summary,
            reasons = reasons,
            supportingEvidence = evidence,
            risks = risks,
            timeHorizon = when(profile.investmentHorizon) {
                InvestmentHorizon.SHORT_TERM -> "1-3 Months"
                InvestmentHorizon.MEDIUM_TERM -> "6-18 Months"
                InvestmentHorizon.LONG_TERM -> "2-5 Years"
            },
            timestamp = "Just updated"
        )
    }

    private fun queryGeminiModel(
        apiKey: String,
        stock: StockQuote,
        tech: TechnicalIndicators,
        fund: FundamentalMetrics,
        news: List<NewsArticle>,
        profile: UserProfile
    ): AiInsight? {
        val prompt = """
            You are FinPilot, an expert AI financial research assistant.
            Analyze ${stock.companyName} (${stock.symbol}) for a user with:
            - Risk Tolerance: ${profile.riskTolerance}
            - Goal: ${profile.investmentGoal}
            - Horizon: ${profile.investmentHorizon}

            Market Data:
            - Current Price: ${stock.price} (Change: ${stock.changePercent}%)
            - P/E Ratio: ${stock.peRatio}, Market Cap: ${stock.marketCap}
            - RSI(14): ${tech.rsi}, Trend: ${tech.trend}
            - Fundamentals: Rev Growth ${fund.revenueGrowth}, Net Margin ${fund.netProfitMargin}
            - Recent News: ${news.firstOrNull()?.title ?: "N/A"}

            Respond STRICTLY with valid JSON format:
            {
              "signal": "OPPORTUNITY" or "WATCH" or "RISK",
              "confidence": 0.85,
              "summary": "Brief 2-sentence summary tailored to user risk profile",
              "reasons": ["Reason 1", "Reason 2"],
              "supportingEvidence": ["Evidence 1", "Evidence 2"],
              "risks": ["Risk 1", "Risk 2"],
              "timeHorizon": "1-3 Years"
            }
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("contents", listOf(
                JSONObject().apply {
                    put("parts", listOf(
                        JSONObject().apply { put("text", prompt) }
                    ))
                }
            ))
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return null

        val responseString = response.body?.string() ?: return null
        val rootObj = JSONObject(responseString)
        val candidates = rootObj.optJSONArray("candidates") ?: return null
        val firstCandidate = candidates.optJSONObject(0) ?: return null
        val content = firstCandidate.optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        val text = parts.optJSONObject(0)?.optString("text") ?: return null

        // Parse JSON from code block if needed
        val cleanJson = text.replace("```json", "").replace("```", "").trim()
        val parsed = JSONObject(cleanJson)

        val signalStr = parsed.optString("signal", "WATCH")
        val signal = try { SignalType.valueOf(signalStr) } catch (e: Exception) { SignalType.WATCH }

        val reasonsJson = parsed.optJSONArray("reasons")
        val reasonsList = mutableListOf<String>()
        if (reasonsJson != null) {
            for (i in 0 until reasonsJson.length()) {
                reasonsList.add(reasonsJson.getString(i))
            }
        }

        val evidenceJson = parsed.optJSONArray("supportingEvidence")
        val evidenceList = mutableListOf<String>()
        if (evidenceJson != null) {
            for (i in 0 until evidenceJson.length()) {
                evidenceList.add(evidenceJson.getString(i))
            }
        }

        val risksJson = parsed.optJSONArray("risks")
        val risksList = mutableListOf<String>()
        if (risksJson != null) {
            for (i in 0 until risksJson.length()) {
                risksList.add(risksJson.getString(i))
            }
        }

        return AiInsight(
            id = "gemini_${stock.symbol}_${System.currentTimeMillis()}",
            symbol = stock.symbol,
            signal = signal,
            confidence = parsed.optDouble("confidence", 0.85).toFloat(),
            summary = parsed.optString("summary", "Gemini AI financial analysis completed."),
            reasons = if (reasonsList.isNotEmpty()) reasonsList else listOf("Strong growth trajectory", "Supportive macro headwinds"),
            supportingEvidence = if (evidenceList.isNotEmpty()) evidenceList else listOf("P/E: ${stock.peRatio}", "RSI: ${tech.rsi}"),
            risks = if (risksList.isNotEmpty()) risksList else listOf("Volatile market sentiment"),
            timeHorizon = parsed.optString("timeHorizon", "1-3 Years"),
            timestamp = "Gemini Live AI"
        )
    }
}
