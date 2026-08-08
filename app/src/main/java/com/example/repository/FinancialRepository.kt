package com.example.repository

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FinancialRepository {

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _stocks = MutableStateFlow(
        listOf(
            StockQuote(
                symbol = "NVDA",
                companyName = "NVIDIA Corporation",
                price = 128.50,
                change = 4.25,
                changePercent = 3.42,
                volume = 48900100L,
                dayHigh = 130.20,
                dayLow = 124.80,
                peRatio = 68.4,
                marketCap = "$3.15T",
                sector = "Technology",
                sparklineData = listOf(118.0, 120.5, 122.1, 121.8, 125.0, 126.4, 128.5)
            ),
            StockQuote(
                symbol = "AAPL",
                companyName = "Apple Inc.",
                price = 224.30,
                change = -1.15,
                changePercent = -0.51,
                volume = 32100400L,
                dayHigh = 226.50,
                dayLow = 223.10,
                peRatio = 33.1,
                marketCap = "$3.44T",
                sector = "Technology",
                sparklineData = listOf(228.0, 227.2, 225.8, 226.0, 224.9, 225.1, 224.3)
            ),
            StockQuote(
                symbol = "TSLA",
                companyName = "Tesla Motors Inc.",
                price = 218.80,
                change = 8.60,
                changePercent = 4.09,
                volume = 65400200L,
                dayHigh = 221.40,
                dayLow = 210.50,
                peRatio = 58.2,
                marketCap = "$698B",
                sector = "Automotive / Tech",
                sparklineData = listOf(198.0, 202.5, 205.1, 209.0, 212.4, 215.0, 218.8)
            ),
            StockQuote(
                symbol = "MSFT",
                companyName = "Microsoft Corporation",
                price = 448.90,
                change = 2.40,
                changePercent = 0.54,
                volume = 19200800L,
                dayHigh = 451.20,
                dayLow = 446.50,
                peRatio = 36.8,
                marketCap = "$3.33T",
                sector = "Technology",
                sparklineData = listOf(440.0, 442.5, 445.1, 444.8, 446.0, 447.4, 448.9)
            ),
            StockQuote(
                symbol = "AMZN",
                companyName = "Amazon.com Inc.",
                price = 186.20,
                change = -0.80,
                changePercent = -0.43,
                volume = 28400000L,
                dayHigh = 188.10,
                dayLow = 185.30,
                peRatio = 42.5,
                marketCap = "$1.93T",
                sector = "Consumer Discretionary",
                sparklineData = listOf(182.0, 184.5, 187.1, 186.8, 188.0, 187.4, 186.2)
            ),
            StockQuote(
                symbol = "GOOGL",
                companyName = "Alphabet Inc.",
                price = 175.40,
                change = 1.90,
                changePercent = 1.10,
                volume = 22100500L,
                dayHigh = 176.80,
                dayLow = 173.20,
                peRatio = 24.6,
                marketCap = "$2.18T",
                sector = "Communication Services",
                sparklineData = listOf(169.0, 171.5, 172.1, 173.8, 174.0, 174.8, 175.4)
            )
        )
    )
    val stocks: StateFlow<List<StockQuote>> = _stocks.asStateFlow()

    private val _watchlist = MutableStateFlow(listOf("NVDA", "AAPL", "TSLA"))
    val watchlist: StateFlow<List<String>> = _watchlist.asStateFlow()

    private val _portfolio = MutableStateFlow(
        listOf(
            PortfolioItem(
                symbol = "NVDA",
                companyName = "NVIDIA Corporation",
                shares = 45.0,
                avgPrice = 105.20,
                currentPrice = 128.50,
                sector = "Technology"
            ),
            PortfolioItem(
                symbol = "AAPL",
                companyName = "Apple Inc.",
                shares = 25.0,
                avgPrice = 210.00,
                currentPrice = 224.30,
                sector = "Technology"
            ),
            PortfolioItem(
                symbol = "MSFT",
                companyName = "Microsoft Corporation",
                shares = 15.0,
                avgPrice = 420.00,
                currentPrice = 448.90,
                sector = "Technology"
            )
        )
    )
    val portfolio: StateFlow<List<PortfolioItem>> = _portfolio.asStateFlow()

    private val _news = MutableStateFlow(
        listOf(
            NewsArticle(
                id = "n1",
                title = "NVIDIA Unveils Next-Gen AI Chips with Double Computing Density",
                source = "Bloomberg Tech",
                summary = "NVIDIA announced breakthroughs in its Blackwell ultra-architecture, driving bullish forecasts across cloud hyperscalers.",
                publishedAt = "25 mins ago",
                relatedSymbol = "NVDA",
                sentiment = "POSITIVE",
                sentimentScore = 0.92f
            ),
            NewsArticle(
                id = "n2",
                title = "Tech Capex Reaches Historic Highs as Hyperscalers Expand Datacenters",
                source = "Financial Times",
                summary = "Major technology providers increased capital expenditure guidance by 18% year-over-year to support generative AI infrastructure.",
                publishedAt = "1 hour ago",
                relatedSymbol = "MSFT",
                sentiment = "POSITIVE",
                sentimentScore = 0.85f
            ),
            NewsArticle(
                id = "n3",
                title = "Regulatory Review Opened on Autonomous Driving Software Updates",
                source = "Reuters",
                summary = "Transportation safety authorities are reviewing software telemetry logs following recent full-self driving updates.",
                publishedAt = "3 hours ago",
                relatedSymbol = "TSLA",
                sentiment = "NEUTRAL",
                sentimentScore = 0.48f
            ),
            NewsArticle(
                id = "n4",
                title = "Global Consumer Electronics Demand Shows Steady Quarter-Over-Quarter Margin Recovery",
                source = "Wall Street Journal",
                summary = "Supply chain reports highlight strong smartphone upgrade cycles across North America and Asia-Pacific regions.",
                publishedAt = "5 hours ago",
                relatedSymbol = "AAPL",
                sentiment = "POSITIVE",
                sentimentScore = 0.78f
            )
        )
    )
    val news: StateFlow<List<NewsArticle>> = _news.asStateFlow()

    fun updateProfile(newProfile: UserProfile) {
        _userProfile.value = newProfile
    }

    fun toggleWatchlist(symbol: String) {
        val current = _watchlist.value.toMutableList()
        if (current.contains(symbol)) {
            current.remove(symbol)
        } else {
            current.add(symbol)
        }
        _watchlist.value = current
    }

    fun executePaperTrade(symbol: String, companyName: String, shares: Double, price: Double, isBuy: Boolean) {
        val currentList = _portfolio.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.symbol == symbol }

        if (isBuy) {
            val cost = shares * price
            if (_userProfile.value.paperBalance >= cost) {
                _userProfile.value = _userProfile.value.copy(
                    paperBalance = _userProfile.value.paperBalance - cost
                )
                if (existingIndex >= 0) {
                    val existing = currentList[existingIndex]
                    val totalShares = existing.shares + shares
                    val newAvgPrice = ((existing.shares * existing.avgPrice) + (shares * price)) / totalShares
                    currentList[existingIndex] = existing.copy(
                        shares = totalShares,
                        avgPrice = newAvgPrice,
                        currentPrice = price
                    )
                } else {
                    currentList.add(
                        PortfolioItem(
                            symbol = symbol,
                            companyName = companyName,
                            shares = shares,
                            avgPrice = price,
                            currentPrice = price,
                            sector = "Technology"
                        )
                    )
                }
            }
        } else { // Sell
            if (existingIndex >= 0) {
                val existing = currentList[existingIndex]
                val sellShares = minOf(shares, existing.shares)
                val revenue = sellShares * price
                _userProfile.value = _userProfile.value.copy(
                    paperBalance = _userProfile.value.paperBalance + revenue
                )
                val remainingShares = existing.shares - sellShares
                if (remainingShares > 0.0001) {
                    currentList[existingIndex] = existing.copy(shares = remainingShares)
                } else {
                    currentList.removeAt(existingIndex)
                }
            }
        }
        _portfolio.value = currentList
    }

    fun getTechnicalIndicators(symbol: String): TechnicalIndicators {
        return when (symbol) {
            "NVDA" -> TechnicalIndicators(
                symbol = "NVDA",
                rsi = 68.5,
                macd = 3.82,
                macdSignal = 2.95,
                sma50 = 115.40,
                sma200 = 98.20,
                volatility = "High (32% Ann.)",
                trend = "Strong Bullish Uptrend"
            )
            "AAPL" -> TechnicalIndicators(
                symbol = "AAPL",
                rsi = 52.1,
                macd = 0.45,
                macdSignal = 0.58,
                sma50 = 220.10,
                sma200 = 205.30,
                volatility = "Moderate (18% Ann.)",
                trend = "Neutral Consolidation"
            )
            "TSLA" -> TechnicalIndicators(
                symbol = "TSLA",
                rsi = 74.2,
                macd = 5.12,
                macdSignal = 3.80,
                sma50 = 195.20,
                sma200 = 188.50,
                volatility = "Very High (45% Ann.)",
                trend = "Aggressive Breakout"
            )
            else -> TechnicalIndicators(
                symbol = symbol,
                rsi = 58.0,
                macd = 1.20,
                macdSignal = 1.05,
                sma50 = 430.00,
                sma200 = 410.00,
                volatility = "Moderate",
                trend = "Steady Uptrend"
            )
        }
    }

    fun getFundamentalMetrics(symbol: String): FundamentalMetrics {
        return FundamentalMetrics(
            symbol = symbol,
            revenueGrowth = "+122% YoY",
            netProfitMargin = "55.4%",
            eps = 2.15,
            peRatio = 62.4,
            pbRatio = 38.2,
            roe = "89.5%",
            debtToEquity = 0.22,
            freeCashFlow = "$14.8B"
        )
    }
}
