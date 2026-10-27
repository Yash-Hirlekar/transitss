package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.TransitViewModel
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.signOut
import com.example.ui.components.TransitBottomBar
import com.example.ui.components.TransitTab
import com.example.ui.dashboard.CrowdReportingViewModel
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.optimizer.RouteOptimizerScreen
import com.example.ui.sos.SosScreen
import com.example.ui.timetable.TimetableScreen
import com.example.ui.timetable.TimetableViewModel
import com.example.ui.theme.TransitSaathiTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import androidx.credentials.CredentialManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TransitSaathiTheme {
                MainTransitApp()
            }
        }
    }
}

@Composable
fun MainTransitApp() {
    val context = LocalContext.current
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    // Listen for Auth changes
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null) {
        AuthScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
            }
        )
    } else {
        AuthenticatedAppContent(
            userDisplayName = currentUser?.displayName ?: "Commuter",
            onSignOut = {
                val credManager = CredentialManager.create(context)
                val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
                signOut(
                    context = context,
                    credentialManager = credManager,
                    onSignOutComplete = {
                        currentUser = null
                    },
                    scope = scope
                )
            }
        )
    }
}

@Composable
fun AuthenticatedAppContent(
    userDisplayName: String,
    onSignOut: () -> Unit,
    viewModel: TransitViewModel = viewModel(),
    crowdReportingViewModel: CrowdReportingViewModel = viewModel(),
    timetableViewModel: TimetableViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(TransitTab.DASHBOARD) }

    val locationState by viewModel.locationState.collectAsStateWithLifecycle()
    val recentReports by viewModel.recentReports.collectAsStateWithLifecycle()
    val predictions by viewModel.predictions.collectAsStateWithLifecycle()
    val isLoadingPredictions by viewModel.isLoadingPredictions.collectAsStateWithLifecycle()
    val isSubmittingReport by viewModel.isSubmittingReport.collectAsStateWithLifecycle()
    val pendingOfflineCount by viewModel.pendingOfflineCount.collectAsStateWithLifecycle()
    val crowdReportingUiState by crowdReportingViewModel.uiState.collectAsStateWithLifecycle()

    // Request Location Permissions
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        viewModel.locationTracker.updatePermissionStatus(fineGranted || coarseGranted)
        crowdReportingViewModel.locationTracker.updatePermissionStatus(fineGranted || coarseGranted)
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            viewModel.locationTracker.updatePermissionStatus(true)
            crowdReportingViewModel.locationTracker.updatePermissionStatus(true)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Handle Back Press: return to Dashboard if on other tabs
    BackHandler(enabled = selectedTab != TransitTab.DASHBOARD) {
        selectedTab = TransitTab.DASHBOARD
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            TransitBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                TransitTab.DASHBOARD -> {
                    DashboardScreen(
                        locationState = locationState,
                        isOnline = viewModel.isOnline(),
                        pendingOfflineCount = pendingOfflineCount,
                        recentReports = recentReports,
                        predictions = predictions,
                        isLoadingPredictions = isLoadingPredictions,
                        isSubmittingReport = isSubmittingReport,
                        onRefreshPredictions = { viewModel.refreshPredictions() },
                        onSubmitReport = { viewModel.submitCrowdReport(it) },
                        onSignOutClicked = onSignOut,
                        userDisplayName = userDisplayName
                    )
                }
                TransitTab.TIMETABLE -> {
                    TimetableScreen(
                        viewModel = timetableViewModel
                    )
                }
                TransitTab.ROUTE_OPTIMIZER -> {
                    RouteOptimizerScreen(
                        locationState = locationState
                    )
                }
                TransitTab.SOS -> {
                    SosScreen(
                        locationState = locationState,
                        onTriggerEmergency = { alert, onCreated ->
                            viewModel.triggerEmergency(alert, onCreated)
                        },
                        onCancelEmergency = { emergencyId ->
                            viewModel.cancelEmergency(emergencyId)
                        }
                    )
                }
            }
        }
    }
}
