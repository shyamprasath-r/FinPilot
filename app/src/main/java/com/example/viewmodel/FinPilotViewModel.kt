package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.FinPilotAiAgent
import com.example.model.*
import com.example.repository.FinancialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class FinNavDestination {
    ONBOARDING,
    DASHBOARD,
    MARKET,
    PORTFOLIO,
    WATCHLIST,
    NEWS,
    AI_INSIGHTS,
    ALERTS,
    PROFILE,
    STOCK_DETAIL
}

class FinPilotViewModel(
    val repository: FinancialRepository = FinancialRepository()
) : ViewModel() {

    private val aiAgent = FinPilotAiAgent(repository)

    private val _currentDestination = MutableStateFlow(FinNavDestination.DASHBOARD)
    val currentDestination: StateFlow<FinNavDestination> = _currentDestination.asStateFlow()

    private val _selectedStock = MutableStateFlow<StockQuote?>(null)
    val selectedStock: StateFlow<StockQuote?> = _selectedStock.asStateFlow()

    private val _selectedInsight = MutableStateFlow<AiInsight?>(null)
    val selectedInsight: StateFlow<AiInsight?> = _selectedInsight.asStateFlow()

    private val _isLoadingInsight = MutableStateFlow(false)
    val isLoadingInsight: StateFlow<Boolean> = _isLoadingInsight.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _alerts = MutableStateFlow(
        listOf(
            AlertNotification(
                id = "a1",
                symbol = "NVDA",
                title = "OPPORTUNITY Signal Generated",
                message = "NVDA momentum breakout confirmed with high volume. RSI at 68.5 aligns with current market trend.",
                signalType = SignalType.OPPORTUNITY,
                timestamp = "10m ago"
            ),
            AlertNotification(
                id = "a2",
                symbol = "TSLA",
                title = "WATCH Signal Updated",
                message = "High volatility detected (+4.09% day move). Consider monitoring technical resistance at $222.",
                signalType = SignalType.WATCH,
                timestamp = "1h ago"
            ),
            AlertNotification(
                id = "a3",
                symbol = "AAPL",
                title = "RISK Alert for Conservative Profile",
                message = "Short-term momentum consolidation noted below 50-day moving average.",
                signalType = SignalType.RISK,
                timestamp = "3h ago"
            )
        )
    )
    val alerts: StateFlow<List<AlertNotification>> = _alerts.asStateFlow()

    val stocks: StateFlow<List<StockQuote>> = repository.stocks
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val watchlistSymbols: StateFlow<List<String>> = repository.watchlist
    val portfolio: StateFlow<List<PortfolioItem>> = repository.portfolio
    val news: StateFlow<List<NewsArticle>> = repository.news

    fun navigateTo(destination: FinNavDestination) {
        _currentDestination.value = destination
    }

    fun selectStock(stock: StockQuote) {
        _selectedStock.value = stock
        fetchAiInsightForStock(stock)
        _currentDestination.value = FinNavDestination.STOCK_DETAIL
    }

    fun fetchAiInsightForStock(stock: StockQuote) {
        viewModelScope.launch {
            _isLoadingInsight.value = true
            val insight = aiAgent.generateInsight(stock)
            _selectedInsight.value = insight
            _isLoadingInsight.value = false
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleWatchlist(symbol: String) {
        repository.toggleWatchlist(symbol)
    }

    fun executePaperTrade(symbol: String, companyName: String, shares: Double, price: Double, isBuy: Boolean) {
        repository.executePaperTrade(symbol, companyName, shares, price, isBuy)
    }

    fun updateProfile(profile: UserProfile) {
        repository.updateProfile(profile)
        // Re-evaluate current selected stock insight if active
        _selectedStock.value?.let { fetchAiInsightForStock(it) }
    }

    fun getTechnicalIndicators(symbol: String) = repository.getTechnicalIndicators(symbol)
    fun getFundamentalMetrics(symbol: String) = repository.getFundamentalMetrics(symbol)
}
