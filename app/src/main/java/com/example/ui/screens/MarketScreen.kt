package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.StockQuoteCard
import com.example.ui.theme.*
import com.example.viewmodel.FinPilotViewModel

@Composable
fun MarketScreen(
    viewModel: FinPilotViewModel,
    modifier: Modifier = Modifier
) {
    val stocks by viewModel.stocks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val watchlist by viewModel.watchlistSymbols.collectAsState()

    val filteredStocks = stocks.filter {
        it.symbol.contains(searchQuery, ignoreCase = true) ||
                it.companyName.contains(searchQuery, ignoreCase = true) ||
                it.sector.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinNavyDark)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Market Intelligence Directory",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = FinTextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("Search by symbol, company, or sector...", color = FinTextMuted) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = FinAccentGold) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("market_search_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FinCardBackground,
                unfocusedContainerColor = FinCardBackground,
                focusedBorderColor = FinPrimaryBlue,
                unfocusedBorderColor = FinSlate,
                focusedTextColor = FinTextPrimary,
                unfocusedTextColor = FinTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredStocks) { stock ->
                StockQuoteCard(
                    stock = stock,
                    isWatchlisted = watchlist.contains(stock.symbol),
                    onCardClick = { viewModel.selectStock(stock) },
                    onWatchlistToggle = { viewModel.toggleWatchlist(stock.symbol) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
