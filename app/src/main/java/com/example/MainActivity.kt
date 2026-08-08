package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.FinCardBackground
import com.example.ui.theme.FinNavyDark
import com.example.ui.theme.FinPilotTheme
import com.example.ui.theme.FinPrimaryBlue
import com.example.ui.theme.FinTextMuted
import com.example.ui.theme.FinTextPrimary
import com.example.viewmodel.FinNavDestination
import com.example.viewmodel.FinPilotViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinPilotTheme {
                val viewModel: FinPilotViewModel = viewModel()
                FinPilotApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FinPilotApp(viewModel: FinPilotViewModel) {
    val currentDest by viewModel.currentDestination.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentDest != FinNavDestination.STOCK_DETAIL) {
                NavigationBar(
                    containerColor = FinCardBackground,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = (currentDest == FinNavDestination.DASHBOARD),
                        onClick = { viewModel.navigateTo(FinNavDestination.DASHBOARD) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FinPrimaryBlue,
                            selectedTextColor = FinPrimaryBlue,
                            unselectedIconColor = FinTextMuted,
                            unselectedTextColor = FinTextMuted,
                            indicatorColor = FinNavyDark
                        ),
                        modifier = Modifier.testTag("nav_dashboard")
                    )

                    NavigationBarItem(
                        selected = (currentDest == FinNavDestination.MARKET),
                        onClick = { viewModel.navigateTo(FinNavDestination.MARKET) },
                        icon = { Icon(Icons.Default.ShowChart, contentDescription = "Market") },
                        label = { Text("Market", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FinPrimaryBlue,
                            selectedTextColor = FinPrimaryBlue,
                            unselectedIconColor = FinTextMuted,
                            unselectedTextColor = FinTextMuted,
                            indicatorColor = FinNavyDark
                        ),
                        modifier = Modifier.testTag("nav_market")
                    )

                    NavigationBarItem(
                        selected = (currentDest == FinNavDestination.PORTFOLIO),
                        onClick = { viewModel.navigateTo(FinNavDestination.PORTFOLIO) },
                        icon = { Icon(Icons.Default.PieChart, contentDescription = "Portfolio") },
                        label = { Text("Portfolio", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FinPrimaryBlue,
                            selectedTextColor = FinPrimaryBlue,
                            unselectedIconColor = FinTextMuted,
                            unselectedTextColor = FinTextMuted,
                            indicatorColor = FinNavyDark
                        ),
                        modifier = Modifier.testTag("nav_portfolio")
                    )

                    NavigationBarItem(
                        selected = (currentDest == FinNavDestination.NEWS),
                        onClick = { viewModel.navigateTo(FinNavDestination.NEWS) },
                        icon = { Icon(Icons.Default.Newspaper, contentDescription = "News") },
                        label = { Text("News", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FinPrimaryBlue,
                            selectedTextColor = FinPrimaryBlue,
                            unselectedIconColor = FinTextMuted,
                            unselectedTextColor = FinTextMuted,
                            indicatorColor = FinNavyDark
                        ),
                        modifier = Modifier.testTag("nav_news")
                    )

                    NavigationBarItem(
                        selected = (currentDest == FinNavDestination.PROFILE),
                        onClick = { viewModel.navigateTo(FinNavDestination.PROFILE) },
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FinPrimaryBlue,
                            selectedTextColor = FinPrimaryBlue,
                            unselectedIconColor = FinTextMuted,
                            unselectedTextColor = FinTextMuted,
                            indicatorColor = FinNavyDark
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        },
        containerColor = FinNavyDark
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        when (currentDest) {
            FinNavDestination.ONBOARDING -> DashboardScreen(viewModel, screenModifier)
            FinNavDestination.DASHBOARD -> DashboardScreen(viewModel, screenModifier)
            FinNavDestination.MARKET -> MarketScreen(viewModel, screenModifier)
            FinNavDestination.PORTFOLIO -> PortfolioScreen(viewModel, screenModifier)
            FinNavDestination.WATCHLIST -> WatchlistScreen(viewModel, screenModifier)
            FinNavDestination.NEWS -> NewsScreen(viewModel, screenModifier)
            FinNavDestination.AI_INSIGHTS -> DashboardScreen(viewModel, screenModifier)
            FinNavDestination.ALERTS -> AlertsScreen(viewModel, screenModifier)
            FinNavDestination.PROFILE -> ProfileScreen(viewModel, screenModifier)
            FinNavDestination.STOCK_DETAIL -> StockDetailScreen(viewModel, screenModifier)
        }
    }
}
