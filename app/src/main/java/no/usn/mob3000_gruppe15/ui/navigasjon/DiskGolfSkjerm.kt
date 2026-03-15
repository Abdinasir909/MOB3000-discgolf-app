package no.usn.mob3000_gruppe15.ui.navigasjon

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.navArgument
import no.usn.mob3000_gruppe15.local.DataStoreManager
import no.usn.mob3000_gruppe15.ui.baner.BanedetaljerSkjerm
import no.usn.mob3000_gruppe15.ui.baner.VelgBaneinfoSkjerm
import no.usn.mob3000_gruppe15.ui.baner.BanerSkjerm
import no.usn.mob3000_gruppe15.ui.baner.BanerViewModel
import no.usn.mob3000_gruppe15.ui.baner.RedigerBane
import no.usn.mob3000_gruppe15.ui.baner.RedigerBaneinfoSkjerm
import no.usn.mob3000_gruppe15.ui.baner.RedigerHullPosisjonSkjerm
import no.usn.mob3000_gruppe15.ui.baner.VelgBaneForRedigeringSkjerm
import no.usn.mob3000_gruppe15.ui.baner.VelgBanePosisjon
import no.usn.mob3000_gruppe15.ui.baner.VelgPlasseringSkjerm
import no.usn.mob3000_gruppe15.ui.components.ScorekortLayout
import no.usn.mob3000_gruppe15.ui.hjem.HjemSkjerm
import no.usn.mob3000_gruppe15.ui.klubber.KlubbInfoSkjerm
import no.usn.mob3000_gruppe15.ui.klubber.KlubbSide
import no.usn.mob3000_gruppe15.ui.klubber.KlubberSkjerm
import no.usn.mob3000_gruppe15.ui.klubber.OpprettKlubbSkjerm
import no.usn.mob3000_gruppe15.ui.klubber.RedigerKlubb
import no.usn.mob3000_gruppe15.ui.login.GlemtPassordSkjerm
import no.usn.mob3000_gruppe15.ui.login.LoginSkjerm
import no.usn.mob3000_gruppe15.ui.login.OpprettBrukerSkjerm
import no.usn.mob3000_gruppe15.ui.meg.MegRedigerBrukerSkjerm
import no.usn.mob3000_gruppe15.ui.meg.MegSkjerm
import no.usn.mob3000_gruppe15.ui.meg.MegViewModel
import no.usn.mob3000_gruppe15.ui.notifikasjon.Notifikasjon
import no.usn.mob3000_gruppe15.ui.spill.RundeOppsettSkjerm
import no.usn.mob3000_gruppe15.ui.spill.SpillRundeSkjerm
import no.usn.mob3000_gruppe15.ui.spill.SpillViewModel
import no.usn.mob3000_gruppe15.ui.turnering.LagTurneringSkjerm
import no.usn.mob3000_gruppe15.ui.turnering.RedigerTurneringSkjerm
import no.usn.mob3000_gruppe15.ui.turnering.TurneringSkjerm
import no.usn.mob3000_gruppe15.ui.turnering.TurneringDetaljerSkjerm
import no.usn.mob3000_gruppe15.viewmodel.TurneringViewModel


// Startskjerm (Login)
const val LOGIN_ROUTE = "login"

// Startskjermer (etter innlogging)
enum class StartSkjermer(
    val label: String,
    val icon: ImageVector
) {
    Hjem("Hjem", Icons.Filled.Home),

    Klubber("Klubber", Icons.Filled.DateRange),
    Spill("Spill", Icons.Filled.Place),

    Turnering("Turnering", Icons.Filled.EmojiEvents),
    Meg("Meg", Icons.Filled.Person)
}

// Skjermer som har tilbakeknapp
enum class Tilbakeskjermer(
    val forrigeSkjermTittel: String,
    val medBottomBar: Boolean = true
) {
    Banedetaljer("Baner", true),
    MegRediger("Meg", true),
    RundeOppsett("Banedetaljer", false),
    SpillRunde("Avslutt", false),
    OpprettKlubb("Klubber", false),
    VelgBaneInfo("Hjem", false),
    VelgBanePosisjon("Velg Baneinfo", false),
    VelgPlassering("Velg Baneinfo", false),
    VelgBaneForRedigering("Hjem", false),
    RedigerBane("Velg bane", false),
    RedigerHullPosisjon("Rediger Bane", false),
    RedigerBaneinfo("Rediger Bane", false),
    TurneringDetaljer("Turnering", true),
    LagTurnering("Turneringer", true),
    RundeOppsummering("Meg", false),
    RedigerTurnering("Turneringer", true)
}

// TopAppBar med tilbakeknapp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun tilbakeKnappTopAppBar(
    skjermTittel: String?,
    kanNavigere: Boolean,
    navigateUp: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(skjermTittel ?: "ukjent tittel")
            }
        },
        navigationIcon = {
            if (kanNavigere) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Tilbake"
                    )
                }
            }
        }
    )
}



// TopBar med meny og notifikasjoner
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiskgolfTopBar(
    navController: NavController,
    onMenuClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = { Text("Discly") },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, contentDescription = "Meny")
            }
        },
        actions = {
            IconButton(onClick = {
                // Naviger til varslingssiden
                navController.navigate("notifications") {
                    launchSingleTop = true
                }
            }) {
                Icon(Icons.Filled.Notifications, contentDescription = "Notification")
            }
        }
    )
}


// Nederste navigasjonslinje
@Composable
fun DiskgolfNavBar(
    valgtSkjerm: StartSkjermer?,
    navController: NavController,
) {
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        StartSkjermer.entries.forEach { skjerm ->
            NavigationBarItem(
                selected = valgtSkjerm == skjerm,
                onClick = {
                    navController.navigate(skjerm.name) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                            inclusive = false
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = skjerm.icon,
                        contentDescription = skjerm.label
                    )
                },
                label = { Text(skjerm.label) }
            )
        }
    }
}

// Hjelpefunksjon for meny-elementer
@Composable
fun DrawerItem(navn: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = navn,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// Hovedapp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiskgolfApp(
    modifier: Modifier = Modifier
) {
    val navController: NavHostController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination?.route
    var valgtSkjerm = StartSkjermer.entries.find { it.name === currentDestination }
    val erTilbakeskjerm = Tilbakeskjermer.entries.find { it.name === currentDestination }
    val medBottomBar = erTilbakeskjerm?.medBottomBar ?: true


    val turneringViewModel: TurneringViewModel = viewModel()


    //Lagre bruker innloggingsstatus (så bruker forblir innlogget)
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)
    val isLoggedIn by dataStoreManager.loginStatus.collectAsState(initial = false)

    val banerViewModel: BanerViewModel = viewModel() {
        BanerViewModel(context)
    }

    val spillViewModel: SpillViewModel = viewModel() {
        SpillViewModel(context)
    }

    val megViewModel: MegViewModel = viewModel() {
        MegViewModel(context)
    }


    // KI hjalp med skjermrotasjon.
    // (blir ikke kastet tilbake til 'hjem' når skjermen roteres)
    var harNavigertEtterLogin by remember { mutableStateOf(false) }
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && !harNavigertEtterLogin) {
            harNavigertEtterLogin = true
            navController.navigate(StartSkjermer.Hjem.name) {
                popUpTo(LOGIN_ROUTE) { inclusive = true }
            }
        }
    }

    // Hovedinnpakning med Drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp) // fast bredde
            ) {
                // Top bar i drawer med lukke-knapp
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Meny",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = { scope.launch { drawerState.close() } }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Lukk meny"
                        )
                    }
                }

                Divider() // linje under top bar

                // Menypunkter – holder knappestil men gjør ingenting
                Column(modifier = Modifier.padding(8.dp)) {
                    TextButton(
                        onClick = {
                            navController.navigate(Tilbakeskjermer.VelgBaneInfo.name)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Legg til Bane",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    TextButton(
                        onClick = {
                            navController.navigate(Tilbakeskjermer.VelgBaneForRedigering.name)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Rediger Bane",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    TextButton(
                        onClick =  {
                            navController.navigate(Tilbakeskjermer.OpprettKlubb.name)
                            scope.launch { drawerState.close() }  // Lukk menyen
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Legg til Klubb",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    TextButton(
                        onClick =  {
                            navController.navigate("redigerKlubb")
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Rediger klubb",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    TextButton(
                        onClick = {
                            navController.navigate(Tilbakeskjermer.LagTurnering.name)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Legg til Turnering",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }


                }
            }
        }
    ) {

        // Hovedskjerm med TopBar og NavBar
        Scaffold(
            topBar = {
                if (erTilbakeskjerm != null) {
                    tilbakeKnappTopAppBar(
                        skjermTittel = erTilbakeskjerm.forrigeSkjermTittel,
                        kanNavigere = navController.previousBackStackEntry != null,
                        navigateUp = { navController.navigateUp() },
                    )
                    valgtSkjerm = StartSkjermer.entries.find {
                        it.name === navController.previousBackStackEntry?.destination?.route
                    }
                } else {
                    DiskgolfTopBar(
                        navController = navController,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                }
            },
            bottomBar = {
                if (
                    currentDestination != LOGIN_ROUTE
                    && currentDestination != "opprett_bruker"
                    && currentDestination != "glemt-passord"
                    && medBottomBar
                ) {
                    DiskgolfNavBar(valgtSkjerm, navController)
                }
            }
        ) { innerPadding ->

            val banerUiState by banerViewModel.uiState.collectAsState()
            val spillUiState by spillViewModel.uiState.collectAsState()
            val valgtPoengkort = spillUiState.valgtPoengkort
            val valgtBane = banerUiState.valgtBane

            NavHost(
                navController = navController,
                startDestination = if (isLoggedIn) StartSkjermer.Hjem.name else LOGIN_ROUTE,
                modifier = Modifier.padding(innerPadding)
            ) {

                composable(LOGIN_ROUTE) {
                    LoginSkjerm(navController = navController)
                }
                composable("opprett_bruker") {
                    OpprettBrukerSkjerm(navController = navController)
                }
                composable("glemt-passord") {
                    GlemtPassordSkjerm(navController = navController)
                }
                composable(route = StartSkjermer.Hjem.name) {
                    HjemSkjerm(
                        navController = navController,
                        viewModel = megViewModel
                    )
                }
                composable(route = StartSkjermer.Spill.name) {
                    BanerSkjerm(
                        viewModel = banerViewModel,
                        onNavigerTilDetaljer = { bane ->
                            banerViewModel.velgBane(bane)
                            navController.navigate(Tilbakeskjermer.Banedetaljer.name)
                        })
                }

                composable(route = Tilbakeskjermer.Banedetaljer.name) {
                    if (valgtBane != null) {
                        BanedetaljerSkjerm(
                            bane = valgtBane,
                            onNavigerTilOppsett = {
                                navController.navigate(Tilbakeskjermer.RundeOppsett.name)
                                spillViewModel.leggTilRundeStarter()
                            },
                            viewModel = banerViewModel
                        )
                    }
                }
                composable(route = Tilbakeskjermer.VelgBaneInfo.name) {
                    VelgBaneinfoSkjerm(
                        viewModel = banerViewModel,
                        onNavigerVelgBanePosisjon = {
                            navController.navigate(Tilbakeskjermer.VelgBanePosisjon.name)
                        },
                        onNavigerVelgPlasseringSkjerm = {
                            navController.navigate(Tilbakeskjermer.VelgPlassering.name)
                        }
                    )
                }
                composable ( route = Tilbakeskjermer.VelgPlassering.name ) {
                    VelgPlasseringSkjerm(
                        viewModel = banerViewModel,
                        onNavigerBaner = {
                            navController.navigate(StartSkjermer.Spill.name)
                        }
                    )
                }
                composable(route = Tilbakeskjermer.VelgBanePosisjon.name) {
                    VelgBanePosisjon(
                        viewModel = banerViewModel,
                        onNavigerVelgBaneInfo = { navController.navigateUp() }
                    )
                }
                composable(route = Tilbakeskjermer.VelgBaneForRedigering.name) {
                    VelgBaneForRedigeringSkjerm(
                        viewModel = banerViewModel,
                        onNavigerTilRedigerBaneinfo = {
                            navController.navigate(Tilbakeskjermer.RedigerBane.name)
                        }
                    )
                }
                composable(route = Tilbakeskjermer.RedigerBane.name) {
                    RedigerBane(
                        onNavigerRedigerBaneInfo = {
                            navController.navigate(Tilbakeskjermer.RedigerBaneinfo.name)
                        },
                        onNavigerRedigerHullPosisjon = {
                            navController.navigate(Tilbakeskjermer.RedigerHullPosisjon.name)
                        },
                        viewModel = banerViewModel,
                        onNavigerTilbake = {
                            navController.navigate(StartSkjermer.Hjem.name)
                        }
                    )
                }
                composable(route = Tilbakeskjermer.RedigerBaneinfo.name) {
                    RedigerBaneinfoSkjerm(
                        viewModel = banerViewModel,
                        onNavigerTilbake = {
                            navController.navigate(StartSkjermer.Hjem.name)
                        }
                    )
                }
                composable(route = Tilbakeskjermer.RedigerHullPosisjon.name) {
                    RedigerHullPosisjonSkjerm(
                        viewModel = banerViewModel,
                        onNavigerTilbake = {
                            navController.navigate(StartSkjermer.Hjem.name)
                        }
                    )
                }
                composable(route = Tilbakeskjermer.RundeOppsett.name) {
                    if (valgtBane != null) {
                        RundeOppsettSkjerm(
                            bane = valgtBane,
                            onNavigerTilSpillRunde = {
                                spillViewModel.fyllAntallKast(valgtBane.antHull)
                                navController.navigate(Tilbakeskjermer.SpillRunde.name)
                            },
                            viewModel = spillViewModel
                        )
                    }
                }
                composable(route = Tilbakeskjermer.SpillRunde.name) {
                    if (valgtBane != null) {

                        SpillRundeSkjerm(
                            spillViewModel = spillViewModel,
                            bane = valgtBane,
                            onFerdigSpill = {
                                spillViewModel.lagreScorekort(valgtBane)
                                navController.popBackStack(
                                    StartSkjermer.Hjem.name,
                                    inclusive = false
                                )
                            }
                        )
                    }
                }
                composable(route = StartSkjermer.Klubber.name) {
                    KlubberSkjerm(navController)
                }
                composable(route = StartSkjermer.Meg.name) {
                    MegSkjerm(
                        navController,
                        spillViewModel = spillViewModel,
                        onNavigerRundeOppsummering = {
                            navController.navigate(Tilbakeskjermer.RundeOppsummering.name)
                        },
                        megViewModel = megViewModel
                    )
                }
                composable(route = Tilbakeskjermer.RundeOppsummering.name) {
                    if(valgtPoengkort != null) {
                        ScorekortLayout(
                            spillere = valgtPoengkort.spillere,
                            hullListe = valgtPoengkort.hullListe
                        )
                    }
                }
                composable(route = Tilbakeskjermer.MegRediger.name) {
                    MegRedigerBrukerSkjerm(
                        navController,
                        viewModel = megViewModel
                    )
                }
                composable("klubbInfo/{klubbId}") { backStackEntry ->
                    val klubbId = backStackEntry.arguments?.getString("klubbId")
                    KlubbInfoSkjerm(navController, klubbId)
                }
                composable(route = Tilbakeskjermer.OpprettKlubb.name) {
                    OpprettKlubbSkjerm(
                        navController = navController,
                        banerViewModel = banerViewModel
                    )
                }
                composable("klubbInfo/{klubbNavn}") { backStackEntry ->
                    val klubbNavn = backStackEntry.arguments?.getString("klubbNavn")
                    KlubbInfoSkjerm(navController, klubbNavn)
                }

                composable("klubbSide/{klubbNavn}") { backStackEntry ->
                    val klubbNavn = backStackEntry.arguments?.getString("klubbNavn")
                    KlubbSide(navController, klubbNavn)
                }
                composable("redigerKlubb") {
                    RedigerKlubb(
                        navController = navController,
                        banerViewModel = banerViewModel
                    )
                }
                composable(
                    route = "redigerKlubb/{klubbId}",
                    arguments = listOf(navArgument("klubbId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val klubbId = backStackEntry.arguments?.getString("klubbId")
                    RedigerKlubb(
                        navController = navController,
                        klubbId = klubbId,
                        banerViewModel = banerViewModel
                    )
                }


                composable("notifications") {
                    Column {
                        tilbakeKnappTopAppBar(
                            skjermTittel = "Notifikasjoner",
                            kanNavigere = navController.previousBackStackEntry != null,
                            navigateUp = { navController.navigateUp() }
                        )
                        Notifikasjon()
                    }
                }

                // Turnering routes
                composable(route = StartSkjermer.Turnering.name) {
                    TurneringSkjerm(
                        navController = navController,
                        turneringViewModel = turneringViewModel
                    )
                }

                // TurneringDetaljer
                composable(route = Tilbakeskjermer.TurneringDetaljer.name) {
                    val turneringUiState by turneringViewModel.uiState.collectAsState()
                    val valgtTurnering = turneringUiState.valgtTurnering

                    if (valgtTurnering != null) {
                        TurneringDetaljerSkjerm(
                            turnering = valgtTurnering,
                            onNavigerTilResultat = {
                                navController.navigate("turneringResultat/${valgtTurnering.id}")
                            }
                        )
                    } else {
                        // Fallback
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text("Turnering ikke valgt")
                                Button(onClick = { navController.navigateUp() }) {
                                    Text("Gå tilbake")
                                }
                            }
                        }
                    }
                }


                // Turnering Resultater (Bruker StringType for ID)
                composable(
                    route = "turneringResultat/{turneringId}",
                    arguments = listOf(navArgument("turneringId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val turneringId = backStackEntry.arguments?.getString("turneringId")
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Funksjonalitet kommer senere")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { navController.navigateUp() }
                        ) {
                            Text("Tilbake")
                        }
                    }
                }

                composable(Tilbakeskjermer.LagTurnering.name) {
                    LagTurneringSkjerm(
                        navController = navController,
                        onTilbake = { navController.navigateUp() }
                    )
                }


                composable(Tilbakeskjermer.RedigerTurnering.name) {
                    RedigerTurneringSkjerm(
                        navController = navController,
                        turneringViewModel = turneringViewModel,
                        onTilbake = { navController.navigateUp() }
                    )
                }

            }
        }
    }
}