package com.banglarbhumi.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglarbhumi.pro.ui.components.PortalLoginDialog
import com.banglarbhumi.pro.ui.screens.*
import com.banglarbhumi.pro.ui.theme.*
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel

enum class NavigationTab(
    val titleEn: String,
    val titleBn: String,
    val icon: ImageVector
) {
    SEARCH("Search", "সন্ধান", Icons.Default.Search),
    MOUZA("Mouza", "মৌজা", Icons.Default.Map),
    CALCULATOR("Calculator", "ক্যালকুলেটর", Icons.Default.Calculate),
    SAVED("Saved", "সংরক্ষিত", Icons.Default.Bookmark),
    ACCOUNT("Account", "অ্যাকাউন্ট", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            BanglarBhumiProTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainAppContainer(viewModel: MainViewModel) {
    var currentTab by remember { mutableStateOf(NavigationTab.SEARCH) }
    val isBengali by viewModel.isBengali.collectAsState()
    val searchResult by viewModel.searchResult.collectAsState()
    val showPortalLoginDialog by viewModel.showPortalLoginDialog.collectAsState()

    BackHandler(enabled = searchResult != null || currentTab != NavigationTab.SEARCH) {
        if (searchResult != null) {
            viewModel.clearSearchResult()
        } else if (currentTab != NavigationTab.SEARCH) {
            currentTab = NavigationTab.SEARCH
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = if (isBengali) "বাংলারভূমি প্রো" else "BanglarBhumi Pro",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White,
                                lineHeight = 19.sp
                            )
                            Text(
                                text = "A SumanOnline Project",
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.5.sp,
                                color = Color.White.copy(alpha = 0.88f),
                                lineHeight = 12.sp
                            )
                        }
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.toggleLanguage() },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Emerald700
                        ) {
                            Text(
                                text = if (isBengali) "EN" else "বাংলা",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Emerald800,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (!WindowInsets.isImeVisible) {
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .height(56.dp)
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavigationTab.values().forEach { tab ->
                            val selected = currentTab == tab
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { currentTab = tab }
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selected) Emerald100 else Color.Transparent
                                ) {
                                    Box(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.titleEn,
                                            tint = if (selected) Emerald800 else Slate500,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isBengali) tab.titleBn else tab.titleEn,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) Emerald800 else Slate500
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .background(Slate100)
        ) {
            when (currentTab) {
                NavigationTab.SEARCH -> {
                    if (searchResult != null) {
                        if (searchResult!!.searchType == com.banglarbhumi.pro.data.model.SearchType.PLOT) {
                            PlotResultScreen(result = searchResult!!, viewModel = viewModel)
                        } else {
                            KhatianResultScreen(result = searchResult!!, viewModel = viewModel)
                        }
                    } else {
                        SearchScreen(viewModel = viewModel)
                    }
                }
                NavigationTab.MOUZA -> {
                    MouzaDirectoryScreen(
                        viewModel = viewModel,
                        onSelectMouza = {
                            currentTab = NavigationTab.SEARCH
                        }
                    )
                }
                NavigationTab.CALCULATOR -> {
                    LandCalculatorScreen(viewModel = viewModel)
                }
                NavigationTab.SAVED -> {
                    SavedRecordsScreen(
                        viewModel = viewModel,
                        onViewRecord = {
                            currentTab = NavigationTab.SEARCH
                        }
                    )
                }
                NavigationTab.ACCOUNT -> {
                    AccountScreen(
                        viewModel = viewModel,
                        onNavigateToSaved = {
                            currentTab = NavigationTab.SAVED
                        }
                    )
                }
            }
        }
    }

    if (showPortalLoginDialog) {
        PortalLoginDialog(
            viewModel = viewModel,
            onDismissRequest = { viewModel.closePortalLoginDialog() }
        )
    }
}
