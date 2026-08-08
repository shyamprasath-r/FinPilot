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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.FinPilotViewModel

@Composable
fun NewsScreen(
    viewModel: FinPilotViewModel,
    modifier: Modifier = Modifier
) {
    val newsList by viewModel.news.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinNavyDark)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Financial News & Sentiment Feed",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = FinTextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(newsList) { article ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FinCardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = article.source,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FinAccentGold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "• ${article.publishedAt}",
                                    fontSize = 11.sp,
                                    color = FinTextMuted
                                )
                            }

                            Text(
                                text = "${article.sentiment} (${(article.sentimentScore * 100).toInt()}%)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (article.sentiment == "POSITIVE") FinOpportunityGreen else FinWatchGold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (article.sentiment == "POSITIVE") FinOpportunityGreen.copy(alpha = 0.2f)
                                        else FinWatchGold.copy(alpha = 0.2f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FinTextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = article.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = FinTextSecondary,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Related Asset: ${article.relatedSymbol}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FinPrimaryBlue
                        )
                    }
                }
            }
        }
    }
}
