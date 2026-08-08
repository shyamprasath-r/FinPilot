package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AiInsightCard
import com.example.ui.components.SparklineCanvasChart
import com.example.ui.theme.*
import com.example.viewmodel.FinNavDestination
import com.example.viewmodel.FinPilotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailScreen(
    viewModel: FinPilotViewModel,
    modifier: Modifier = Modifier
) {
    val selectedStock by viewModel.selectedStock.collectAsState()
    val aiInsight by viewModel.selectedInsight.collectAsState()
    val isLoadingInsight by viewModel.isLoadingInsight.collectAsState()
    val watchlist by viewModel.watchlistSymbols.collectAsState()

    var showTradeDialog by remember { mutableStateOf(false) }
    var isBuyOrder by remember { mutableStateOf(true) }
    var tradeQuantity by remember { mutableStateOf("10") }

    val stock = selectedStock ?: return

    val isWatchlisted = watchlist.contains(stock.symbol)
    val tech = viewModel.getTechnicalIndicators(stock.symbol)
    val fund = viewModel.getFundamentalMetrics(stock.symbol)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stock.symbol,
                            fontWeight = FontWeight.Bold,
                            color = FinTextPrimary
                        )
                        Text(
                            text = stock.companyName,
                            fontSize = 11.sp,
                            color = FinTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(FinNavDestination.MARKET) },
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FinTextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleWatchlist(stock.symbol) }) {
                        Icon(
                            imageVector = if (isWatchlisted) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Watchlist",
                            tint = if (isWatchlisted) FinAccentGold else FinTextMuted
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FinNavyDark)
            )
        },
        bottomBar = {
            Surface(
                color = FinCardBackground,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            isBuyOrder = true
                            showTradeDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("paper_buy_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = FinOpportunityGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = FinNavyDark)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Paper Buy", color = FinNavyDark, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            isBuyOrder = false
                            showTradeDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("paper_sell_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = FinRiskRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = null, tint = FinTextPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Paper Sell", color = FinTextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = FinNavyDark
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))

                // Price Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "$${String.format("%.2f", stock.price)}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = FinTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${if (stock.change >= 0) "+" else ""}$${String.format("%.2f", stock.change)} (${String.format("%.2f", stock.changePercent)}%) Today",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (stock.change >= 0) FinOpportunityGreen else FinRiskRed
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Market Cap", fontSize = 11.sp, color = FinTextMuted)
                        Text(text = stock.marketCap, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FinTextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chart Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FinCardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Technical Price Action", style = MaterialTheme.typography.titleSmall, color = FinTextSecondary)
                            Text("7D Sparkline", fontSize = 11.sp, color = FinAccentGold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        SparklineCanvasChart(
                            data = stock.sparklineData,
                            isPositive = stock.change >= 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // FinPilot AI Reasoning Section
                if (isLoadingInsight) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = FinNavyMedium)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = FinAccentGold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("FinPilot AI Agent Analyzing Market Evidence...", color = FinTextPrimary)
                        }
                    }
                } else aiInsight?.let { insight ->
                    AiInsightCard(insight = insight)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Technical Analysis Metrics
                Text(
                    text = "Technical Indicators",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FinTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBox("RSI (14-Day)", tech.rsi.toString(), if (tech.rsi > 70) FinRiskRed else FinOpportunityGreen, Modifier.weight(1f))
                    MetricBox("MACD", tech.macd.toString(), FinAccentGold, Modifier.weight(1f))
                    MetricBox("50-Day SMA", "$${tech.sma50}", FinTextPrimary, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Fundamental Analysis Metrics
                Text(
                    text = "Fundamental Metrics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FinTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FinCardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Revenue Growth (YoY)", color = FinTextSecondary, fontSize = 13.sp)
                            Text(fund.revenueGrowth, fontWeight = FontWeight.Bold, color = FinOpportunityGreen, fontSize = 13.sp)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = FinSlate)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Net Profit Margin", color = FinTextSecondary, fontSize = 13.sp)
                            Text(fund.netProfitMargin, fontWeight = FontWeight.Bold, color = FinTextPrimary, fontSize = 13.sp)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = FinSlate)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("P/E Ratio / P/B Ratio", color = FinTextSecondary, fontSize = 13.sp)
                            Text("${fund.peRatio}x / ${fund.pbRatio}x", fontWeight = FontWeight.Bold, color = FinTextPrimary, fontSize = 13.sp)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = FinSlate)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Free Cash Flow", color = FinTextSecondary, fontSize = 13.sp)
                            Text(fund.freeCashFlow, fontWeight = FontWeight.Bold, color = FinAccentGold, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Paper Trading Execution Dialog
    if (showTradeDialog) {
        AlertDialog(
            onDismissRequest = { showTradeDialog = false },
            title = {
                Text(
                    text = if (isBuyOrder) "Execute Paper Buy Order" else "Execute Paper Sell Order",
                    fontWeight = FontWeight.Bold,
                    color = FinTextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "${stock.symbol} @ $${stock.price} per share",
                        color = FinTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tradeQuantity,
                        onValueChange = { tradeQuantity = it },
                        label = { Text("Number of Shares") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FinAccentGold,
                            unfocusedBorderColor = FinSlate,
                            focusedTextColor = FinTextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trade_quantity_input")
                    )
                    val qty = tradeQuantity.toDoubleOrNull() ?: 0.0
                    val estimatedTotal = qty * stock.price
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Estimated Order Total: $${String.format("%,.2f", estimatedTotal)}",
                        fontWeight = FontWeight.Bold,
                        color = FinAccentGold,
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = tradeQuantity.toDoubleOrNull() ?: 0.0
                        if (qty > 0) {
                            viewModel.executePaperTrade(
                                symbol = stock.symbol,
                                companyName = stock.companyName,
                                shares = qty,
                                price = stock.price,
                                isBuy = isBuyOrder
                            )
                        }
                        showTradeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBuyOrder) FinOpportunityGreen else FinRiskRed
                    ),
                    modifier = Modifier.testTag("confirm_trade_button")
                ) {
                    Text(
                        text = if (isBuyOrder) "Confirm Paper Buy" else "Confirm Paper Sell",
                        color = if (isBuyOrder) FinNavyDark else FinTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showTradeDialog = false }) {
                    Text("Cancel", color = FinTextMuted)
                }
            },
            containerColor = FinCardBackground
        )
    }
}

@Composable
fun MetricBox(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = FinCardBackground)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = label, fontSize = 10.sp, color = FinTextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}
