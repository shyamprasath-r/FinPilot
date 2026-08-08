package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.StockQuoteCard
import com.example.ui.theme.*
import com.example.viewmodel.FinPilotViewModel

@Composable
fun WatchlistScreen(
    viewModel: FinPilotViewModel,
    modifier: Modifier = Modifier
) {
    val stocks by viewModel.stocks.collectAsState()
    val watchlist by viewModel.watchlistSymbols.collectAsState()

    val watchlistedStocks = stocks.filter { watchlist.contains(it.symbol) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinNavyDark)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Monitored Watchlist Assets",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = FinTextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (watchlistedStocks.isEmpty()) {
            Text(
                text = "No assets added to watchlist yet. Explore the market directory to add stocks.",
                style = MaterialTheme.typography.bodyMedium,
                color = FinTextSecondary
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(watchlistedStocks) { stock ->
                    StockQuoteCard(
                        stock = stock,
                        isWatchlisted = true,
                        onCardClick = { viewModel.selectStock(stock) },
                        onWatchlistToggle = { viewModel.toggleWatchlist(stock.symbol) },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
