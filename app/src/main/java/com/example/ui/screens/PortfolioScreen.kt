package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.FinPilotViewModel

@Composable
fun PortfolioScreen(
    viewModel: FinPilotViewModel,
    modifier: Modifier = Modifier
) {
    val portfolio by viewModel.portfolio.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val stocks by viewModel.stocks.collectAsState()

    val totalHoldingsValue = portfolio.sumOf { it.totalValue }
    val totalCost = portfolio.sumOf { it.totalCost }
    val profitLoss = totalHoldingsValue - totalCost
    val profitLossPercent = if (totalCost > 0) (profitLoss / totalCost) * 100 else 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FinNavyDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Paper-Trading Portfolio",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FinTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Portfolio Net Worth Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("portfolio_detail_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FinNavyMedium)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Invested Asset Value", fontSize = 12.sp, color = FinTextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$${String.format("%,.2f", totalHoldingsValue)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = FinTextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Profit/Loss", fontSize = 11.sp, color = FinTextMuted)
                            Text(
                                text = "${if (profitLoss >= 0) "+" else ""}$${String.format("%.2f", profitLoss)} (${String.format("%.2f", profitLossPercent)}%)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (profitLoss >= 0) FinOpportunityGreen else FinRiskRed
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Buying Power", fontSize = 11.sp, color = FinTextMuted)
                            Text(
                                text = "$${String.format("%,.2f", profile.paperBalance)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FinAccentGold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Current Holdings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FinTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        items(portfolio) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FinCardBackground)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = item.symbol,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FinTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${item.shares} shares @ $${String.format("%.2f", item.avgPrice)}",
                            fontSize = 12.sp,
                            color = FinTextSecondary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${String.format("%,.2f", item.totalValue)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FinTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${if (item.profitLoss >= 0) "+" else ""}$${String.format("%.2f", item.profitLoss)} (${String.format("%.2f", item.profitLossPercent)}%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.profitLoss >= 0) FinOpportunityGreen else FinRiskRed
                        )
                    }
                }
            }
        }
    }
}
