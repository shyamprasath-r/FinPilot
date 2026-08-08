package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InvestmentHorizon
import com.example.model.RiskTolerance
import com.example.ui.theme.*
import com.example.viewmodel.FinPilotViewModel

@Composable
fun ProfileScreen(
    viewModel: FinPilotViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()

    var selectedRisk by remember(profile) { mutableStateOf(profile.riskTolerance) }
    var selectedHorizon by remember(profile) { mutableStateOf(profile.investmentHorizon) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinNavyDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "User Risk Profile & Preferences",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = FinTextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Account Overview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = FinNavyMedium)
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FinPrimaryBlue.copy(alpha = 0.2f),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = FinAccentGold)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = profile.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FinTextPrimary)
                    Text(text = profile.email, fontSize = 12.sp, color = FinTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Paper Balance: $${String.format("%,.2f", profile.paperBalance)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = FinAccentGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Risk Tolerance Selector
        Text(
            text = "Risk Profile Calibration",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FinTextPrimary
        )
        Text(
            text = "FinPilot AI dynamically customizes Opportunity and Risk signals based on this setting.",
            style = MaterialTheme.typography.bodySmall,
            color = FinTextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        RiskTolerance.entries.forEach { level ->
            Card(
                onClick = {
                    selectedRisk = level
                    viewModel.updateProfile(profile.copy(riskTolerance = level))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("risk_option_${level.name}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedRisk == level) FinCardBackground else FinNavyDark
                ),
                border = if (selectedRisk == level) ButtonDefaults.outlinedButtonBorder else null
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = level.name,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedRisk == level) FinAccentGold else FinTextPrimary
                        )
                        Text(
                            text = when(level) {
                                RiskTolerance.CONSERVATIVE -> "Prioritizes capital protection and lower drawdown."
                                RiskTolerance.MODERATE -> "Balances capital growth with controlled volatility."
                                RiskTolerance.AGGRESSIVE -> "Maximizes alpha growth opportunities and momentum."
                            },
                            fontSize = 12.sp,
                            color = FinTextSecondary
                        )
                    }
                    RadioButton(
                        selected = (selectedRisk == level),
                        onClick = null,
                        colors = RadioButtonDefaults.colors(selectedColor = FinAccentGold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Investment Horizon Selector
        Text(
            text = "Investment Time Horizon",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FinTextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        InvestmentHorizon.entries.forEach { horizon ->
            Card(
                onClick = {
                    selectedHorizon = horizon
                    viewModel.updateProfile(profile.copy(investmentHorizon = horizon))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedHorizon == horizon) FinCardBackground else FinNavyDark
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = when(horizon) {
                            InvestmentHorizon.SHORT_TERM -> "Short-Term (< 1 Year)"
                            InvestmentHorizon.MEDIUM_TERM -> "Medium-Term (1 - 5 Years)"
                            InvestmentHorizon.LONG_TERM -> "Long-Term (5+ Years)"
                        },
                        fontWeight = FontWeight.Bold,
                        color = if (selectedHorizon == horizon) FinPrimaryBlue else FinTextPrimary
                    )
                    RadioButton(
                        selected = (selectedHorizon == horizon),
                        onClick = null,
                        colors = RadioButtonDefaults.colors(selectedColor = FinPrimaryBlue)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reset Paper Balance Button
        Button(
            onClick = {
                viewModel.updateProfile(profile.copy(paperBalance = 100000.00))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("reset_balance_button"),
            colors = ButtonDefaults.buttonColors(containerColor = FinCardBackground),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = FinAccentGold)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reset Paper Balance to $100,000", color = FinTextPrimary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
