package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiInsight
import com.example.model.SignalType
import com.example.model.StockQuote
import com.example.ui.theme.*

@Composable
fun SignalBadge(
    signal: SignalType,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon, label) = when (signal) {
        SignalType.OPPORTUNITY -> Quadruple(
            FinOpportunityGreen.copy(alpha = 0.18f),
            FinOpportunityGreen,
            Icons.AutoMirrored.Filled.TrendingUp,
            "OPPORTUNITY"
        )
        SignalType.WATCH -> Quadruple(
            FinWatchGold.copy(alpha = 0.18f),
            FinWatchGold,
            Icons.Default.Visibility,
            "WATCH"
        )
        SignalType.RISK -> Quadruple(
            FinRiskRed.copy(alpha = 0.18f),
            FinRiskRed,
            Icons.AutoMirrored.Filled.TrendingDown,
            "RISK"
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = textColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun SparklineCanvasChart(
    data: List<Double>,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val lineColor = if (isPositive) FinOpportunityGreen else FinRiskRed

    Canvas(modifier = modifier) {
        val minVal = data.minOrNull() ?: 0.0
        val maxVal = data.maxOrNull() ?: 1.0
        val range = if (maxVal - minVal == 0.0) 1.0 else maxVal - minVal

        val width = size.width
        val height = size.height

        val stepX = width / (data.size - 1)

        val path = Path()
        val fillPath = Path()

        data.forEachIndexed { index, value ->
            val x = index * stepX
            val normalizedY = ((value - minVal) / range).toFloat()
            val y = height - (normalizedY * height)

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, height)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }

            if (index == data.size - 1) {
                fillPath.lineTo(x, height)
                fillPath.close()
            }
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.25f), Color.Transparent)
            )
        )

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
fun StockQuoteCard(
    stock: StockQuote,
    isWatchlisted: Boolean,
    onCardClick: () -> Unit,
    onWatchlistToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPositive = stock.change >= 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("stock_card_${stock.symbol}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FinCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1.2f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stock.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FinTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stock.sector,
                        fontSize = 10.sp,
                        color = FinTextMuted,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(FinSlate.copy(alpha = 0.5f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stock.companyName,
                    style = MaterialTheme.typography.bodySmall,
                    color = FinTextSecondary,
                    maxLines = 1
                )
            }

            // Sparkline chart preview
            SparklineCanvasChart(
                data = stock.sparklineData,
                isPositive = isPositive,
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .padding(horizontal = 8.dp)
            )

            // Price & Change
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1.1f)
            ) {
                Text(
                    text = "$${String.format("%.2f", stock.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FinTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = if (isPositive) FinOpportunityGreen else FinRiskRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${if (isPositive) "+" else ""}${String.format("%.2f", stock.changePercent)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPositive) FinOpportunityGreen else FinRiskRed
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onWatchlistToggle,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("watchlist_toggle_${stock.symbol}")
            ) {
                Icon(
                    imageVector = if (isWatchlisted) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Watchlist",
                    tint = if (isWatchlisted) FinAccentGold else FinTextMuted
                )
            }
        }
    }
}

@Composable
fun AiInsightCard(
    insight: AiInsight,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_insight_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FinNavyMedium.copy(alpha = 0.85f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(FinPrimaryBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "FinPilot AI",
                            tint = FinAccentGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FinPilot AI Intelligence",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FinTextPrimary
                    )
                }
                SignalBadge(signal = insight.signal)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Summary
            Text(
                text = insight.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = FinTextPrimary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Confidence & Time Horizon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(FinNavyDark.copy(alpha = 0.6f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Confidence", fontSize = 11.sp, color = FinTextMuted)
                    Text(
                        text = "${(insight.confidence * 100).toInt()}% Evidence Match",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FinAccentGold
                    )
                }
                Divider(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp),
                    color = FinSlate
                )
                Column {
                    Text(text = "Target Horizon", fontSize = 11.sp, color = FinTextMuted)
                    Text(
                        text = insight.timeHorizon,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FinTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Supporting Reasons
            Text(
                text = "Key Investment Drivers:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = FinAccentGold
            )
            Spacer(modifier = Modifier.height(6.dp))
            insight.reasons.forEach { reason ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = FinOpportunityGreen,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodySmall,
                        color = FinTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Risks
            Text(
                text = "Tail Risks & Risk Factors:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = FinRiskRed
            )
            Spacer(modifier = Modifier.height(6.dp))
            insight.risks.forEach { risk ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = FinRiskRed,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = risk,
                        style = MaterialTheme.typography.bodySmall,
                        color = FinTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = insight.disclaimer,
                fontSize = 10.sp,
                color = FinTextMuted,
                lineHeight = 14.sp
            )
        }
    }
}
