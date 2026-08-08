package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SignalType
import com.example.model.StockQuote
import com.example.ui.components.SignalBadge
import com.example.ui.components.StockQuoteCard
import com.example.ui.theme.*
import com.example.viewmodel.FinNavDestination
import com.example.viewmodel.FinPilotViewModel

@Composable
fun DashboardScreen(
    viewModel: FinPilotViewModel,
    modifier: Modifier = Modifier
) {
    val stocks by viewModel.stocks.collectAsState()
    val portfolio by viewModel.portfolio.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val watchlist by viewModel.watchlistSymbols.collectAsState()
    val news by viewModel.news.collectAsState()

    val totalPortfolioValue = portfolio.sumOf { it.totalValue }
    val totalCost = portfolio.sumOf { it.totalCost }
    val netWorth = totalPortfolioValue + profile.paperBalance
    val totalProfitLoss = totalPortfolioValue - totalCost
    val totalProfitLossPercent = if (totalCost > 0) (totalProfitLoss / totalCost) * 100 else 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FinNavyDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // User Welcome Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello, ${profile.name.split(" ").first()}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = FinTextPrimary
                    )
                    Text(
                        text = "Risk Profile: ${profile.riskTolerance.name} | ${profile.investmentGoal}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FinTextSecondary
                    )
                }
                IconButton(
                    onClick = { viewModel.navigateTo(FinNavDestination.ALERTS) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(FinCardBackground)
                        .testTag("dashboard_alerts_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Alerts",
                        tint = FinAccentGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Net Worth & Paper Trading Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("portfolio_summary_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FinNavyMedium)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Total Paper Net Worth",
                        style = MaterialTheme.typography.labelMedium,
                        color = FinTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$${String.format("%,.2f", netWorth)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = FinTextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Unrealized P/L", fontSize = 11.sp, color = FinTextMuted)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${if (totalProfitLoss >= 0) "+" else ""}$${String.format("%.2f", totalProfitLoss)} (${String.format("%.2f", totalProfitLossPercent)}%)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (totalProfitLoss >= 0) FinOpportunityGreen else FinRiskRed
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Available Buying Power", fontSize = 11.sp, color = FinTextMuted)
                            Text(
                                text = "$${String.format("%,.2f", profile.paperBalance)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FinAccentGold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Signal Summary Counters
            Text(
                text = "Market Intelligence Signals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FinTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SignalSummaryCard(
                    signal = SignalType.OPPORTUNITY,
                    count = 2,
                    modifier = Modifier.weight(1f)
                )
                SignalSummaryCard(
                    signal = SignalType.WATCH,
                    count = 3,
                    modifier = Modifier.weight(1f)
                )
                SignalSummaryCard(
                    signal = SignalType.RISK,
                    count = 1,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Watchlist Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monitored Watchlist",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FinTextPrimary
                )
                TextButton(onClick = { viewModel.navigateTo(FinNavDestination.WATCHLIST) }) {
                    Text(text = "View All", color = FinPrimaryBlue)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Watchlist Stock Cards
        val watchlistedStocks = stocks.filter { watchlist.contains(it.symbol) }
        items(watchlistedStocks) { stock ->
            StockQuoteCard(
                stock = stock,
                isWatchlisted = true,
                onCardClick = { viewModel.selectStock(stock) },
                onWatchlistToggle = { viewModel.toggleWatchlist(stock.symbol) },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Financial News Highlights
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Market News Sentiment",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FinTextPrimary
                )
                TextButton(onClick = { viewModel.navigateTo(FinNavDestination.NEWS) }) {
                    Text(text = "Explore", color = FinPrimaryBlue)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            news.take(2).forEach { article ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.navigateTo(FinNavDestination.NEWS) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = FinCardBackground)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = article.source,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FinAccentGold
                            )
                            Text(
                                text = article.sentiment,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (article.sentiment == "POSITIVE") FinOpportunityGreen else FinWatchGold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (article.sentiment == "POSITIVE") FinOpportunityGreen.copy(alpha = 0.2f)
                                        else FinWatchGold.copy(alpha = 0.2f)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = FinTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SignalSummaryCard(
    signal: SignalType,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FinCardBackground)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            SignalBadge(signal = signal)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$count Assets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FinTextPrimary
            )
        }
    }
}
