package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.tools.GeminiClient
import com.example.ui.tools.ToolsData
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    MainAppContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

// --- APP BAR & CONTAINER DEFINITION ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(modifier: Modifier = Modifier) {
    var selectedToolId by rememberSaveable { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        if (selectedToolId == null) {
            // Main Dashboard View
            DashboardView(
                onToolSelect = { selectedToolId = it }
            )
        } else {
            // Individual Tool View
            ToolCoordinatorView(
                toolId = selectedToolId!!,
                onBack = { selectedToolId = null }
            )
        }
    }
}

// --- HELPER FUNCTION FOR IN-APP SHARING ---
fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share via Helasiri"))
}

// --- TOOL DATA CLASS REPRESENTING EACH COMPONENT ---
data class ToolItem(
    val id: String,
    val titleSinhala: String,
    val titleEnglish: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accentColor: Color,
    val isViralBadge: Boolean = false
)

// --- DASHBOARD LAYOUT COMPOSABLE ---
@Composable
fun DashboardView(
    onToolSelect: (String) -> Unit
) {
    val toolsList = remember {
        listOf(
            ToolItem("cash_counter", "සල්ලි ගණකය", "Cash Denomination Counter", "LKR notes and coins calculator with breakdown share", Icons.Default.Payments, Color(0xFF8D1B3D), true),
            ToolItem("singlish", "සිංග්ලිෂ් පරිවර්තකය", "Singlish to Sinhala Converter", "Phonetic keyboard transliteration helper (amma -> අම්මා)", Icons.Default.Translate, Color(0xFFE5B23D), true),
            ToolItem("ai_caption", "ලංකා AI Captions", "Sinhala & Singlish AI Caption", "Viral quotes generator for FB, Insta & TikTok", Icons.Default.AutoAwesome, Color(0xFF0E6E4F), true),
            ToolItem("electricity", "විදුලි බිල", "Domestic CEB Bill Estimator", "Estimate electricity expenses using CEB domestic tariffs", Icons.Default.Bolt, Color(0xFFE5B23D)),
            ToolItem("fuel_trip", "තෙල් & ගමන් වියදම", "Fuel Cost & Trip Calculator", "Trip sharing calculator using latest LKR fuel rates", Icons.Default.LocalGasStation, Color(0xFF2196F3)),
            ToolItem("postal_code", "තැපැල් කේත සෙවුම", "Lanka Postal Code Finder", "Quick search for 40+ major Sri Lankan towns and postcodes", Icons.Default.PinDrop, Color(0xFF9C27B0)),
            ToolItem("nic_analyzer", "NIC විශ්ලේෂකය", "Sri Lankan NIC Analyzer", "Extract age, DOB, gender & voter eligibility from NIC number", Icons.Default.ContactPage, Color(0xFF8D1B3D), true),
            ToolItem("astrology", "ලග්න පලාඵල සහ වර්ණ", "Zodiac & Auspicious Colors", "Lucky color of the day, auspicious hours and Rahu time", Icons.Default.WbSunny, Color(0xFFE5B23D)),
            ToolItem("currency", "විදේශ මුදල් හුවමාරු", "LKR Currency Exchange Converter", "Convert USD, GBP, EUR & AED with LKR index helper", Icons.Default.CurrencyExchange, Color(0xFF0E6E4F)),
            ToolItem("nrr_cricket", "ක්‍රිකට් NRR ගණකය", "Cricket Net Run Rate Calculator", "Net Run Rate (NRR) index helper to track Sri Lanka cricket matches", Icons.Default.EmojiEvents, Color(0xFF2196F3), true)
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    val filteredTools = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            toolsList
        } else {
            toolsList.filter {
                it.titleEnglish.contains(searchQuery, ignoreCase = true) ||
                        it.titleSinhala.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Aesthetic Sri Lankan Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "හෙළසිරි",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Helasiri Premium Tools Pack",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    // Patriotic accent - Sri Lankan flag colored small indicator
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(10.dp, 14.dp).background(Color(0xFF0E6E4F)))
                        Spacer(modifier = Modifier.width(3.dp))
                        Box(modifier = Modifier.size(10.dp, 14.dp).background(Color(0xFFE5B23D)))
                        Spacer(modifier = Modifier.width(3.dp))
                        Box(modifier = Modifier.size(10.dp, 14.dp).background(Color(0xFF8D1B3D)))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "ආයුබෝවන්! Sri Lanka's ultimate multi-utility hub. 10 viral tools compiled onto one beautiful modern package.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // Search Bar Section
        PaddingValues(horizontal = 16.dp).let {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("වැඩසටහනක් සොයන්න / Search tool...", fontSize = 14.sp) },
                prefix = { Icon(Icons.Default.Search, contentDescription = null, Modifier.size(20.dp).padding(end = 4.dp)) },
                suffix = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search", Modifier.size(18.dp))
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        // Grid List of Tools
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredTools.size) { index ->
                val tool = filteredTools[index]
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clickable { onToolSelect(tool.id) }
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Circular colored icon background
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(tool.accentColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = tool.icon,
                                    contentDescription = tool.titleEnglish,
                                    tint = tool.accentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = tool.titleSinhala,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tool.titleEnglish,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tool.description,
                                fontSize = 9.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 12.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Little viral flame flag to attract clicks
                        if (tool.isViralBadge) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE5B23D))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "VIRAL 🔥",
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1C1113)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- CONTEXT COOLDOWN STAGE MAP ---
@Composable
fun ToolCoordinatorView(
    toolId: String,
    onBack: () -> Unit
) {
    val title = when (toolId) {
        "cash_counter" -> "සල්ලි ਗණකය | Cash Counter"
        "singlish" -> "සිංග්ලිෂ් | Singlish converter"
        "ai_caption" -> "ලංකා AI Caption Generator"
        "electricity" -> "විදුලි බිල | Electricity Estimator"
        "fuel_trip" -> "තෙල් & ගමන් වියදම | Fuel & Trip"
        "postal_code" -> "තැපැල් කේත සෙවුම | Postal finder"
        "nic_analyzer" -> "NIC විශ්ලේෂක | NIC Analyzer"
        "astrology" -> "සුභ වර්ණ සහ පලාඵල | Astrology"
        "currency" -> "මුදල් හුවමාරු | Currency Track"
        "nrr_cricket" -> "ක්‍රිකට් NRR | Net Run Rate"
        else -> "Tool"
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Individual Tool Custom TopBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to dashboard",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(modifier = Modifier.weight(1f).padding(16.dp)) {
            when (toolId) {
                "cash_counter" -> ToolCashCounter()
                "singlish" -> ToolSinglishTransliteration()
                "ai_caption" -> ToolAIQuotes()
                "electricity" -> ToolElectricityEstimator()
                "fuel_trip" -> ToolFuelTrip()
                "postal_code" -> ToolPostalCodeFinder()
                "nic_analyzer" -> ToolNICAnalyzer()
                "astrology" -> ToolAstrology()
                "currency" -> ToolCurrencyConverter()
                "nrr_cricket" -> ToolCricketNRR()
            }
        }
    }
}

// ==========================================
// TOOL 1: CASH DENOMINATION COUNTER
// ==========================================
@Composable
fun ToolCashCounter() {
    val denominations = remember { listOf(5000, 2000, 1000, 500, 100, 50, 20, 10, 5, 2, 1) }
    val counts = remember { mutableStateMapOf<Int, String>().apply { denominations.forEach { put(it, "0") } } }
    
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Compute totals
    val totalAmount = denominations.sumOf { k ->
        val countVal = counts[k]?.toLongOrNull() ?: 0L
        k.toLong() * countVal
    }
    val totalNoteCount = denominations.sumOf { counts[it]?.toIntOrNull() ?: 0 }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Cash Amount", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    Text("Rs. %,d.00".format(totalAmount), color = MaterialTheme.colorScheme.secondary, fontSize = 24.sp, fontWeight = FontWeight.Black)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Items", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    Text("$totalNoteCount Notes/Coins", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Action controls
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    denominations.forEach { counts[it] = "0" }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear All", fontSize = 12.sp)
            }

            Button(
                onClick = {
                    val sb = StringBuilder("💰 *Helasiri Cash Counter Breakdown* 💰\n\n")
                    denominations.forEach { d ->
                        val n = counts[d]?.toIntOrNull() ?: 0
                        if (n > 0) {
                            sb.append("Rs. %,d × %d = Rs. %,d\n".format(d, n, d * n))
                        }
                    }
                    sb.append("\n-------------------------\n")
                    sb.append("💵 *Grand Total: Rs. %,d.00*\n".format(totalAmount))
                    sb.append("Total Items counted: $totalNoteCount\n")
                    sb.append("Calculated via Helasiri App 🦁")

                    shareText(context, sb.toString())
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = Color(0xFF1C1113)),
                modifier = Modifier.weight(1.3f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share Breakdown", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // List of Denominations
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(denominations) { denom ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Title
                    Column(modifier = Modifier.width(80.dp)) {
                        Text(
                            text = "Rs. %,d".format(denom),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (denom >= 20) "Note" else "Coin",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Decrement Button
                    IconButton(
                        onClick = {
                            val current = counts[denom]?.toIntOrNull() ?: 0
                            if (current > 0) counts[denom] = (current - 1).toString()
                        },
                        modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Text("-", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    // Numeric Field
                    OutlinedTextField(
                        value = counts[denom] ?: "0",
                        onValueChange = { inputVal ->
                            val sanitized = inputVal.filter { it.isDigit() }
                            counts[denom] = if (sanitized.isEmpty()) "0" else sanitized
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 15.sp),
                        modifier = Modifier.weight(1f).height(48.dp).padding(horizontal = 8.dp)
                    )

                    // Increment Button
                    IconButton(
                        onClick = {
                            val current = counts[denom]?.toIntOrNull() ?: 0
                            counts[denom] = (current + 1).toString()
                        },
                        modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Text("+", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    // SubTotal
                    val sub = denom.toLong() * (counts[denom]?.toLongOrNull() ?: 0L)
                    Text(
                        text = "= Rs. %,d".format(sub),
                        modifier = Modifier.width(85.dp).padding(start = 6.dp),
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// ==========================================
// TOOL 2: SINGLISH TO SINHALA
// ==========================================
@Composable
fun ToolSinglishTransliteration() {
    var singlishText by rememberSaveable { mutableStateOf("") }
    val convertedSinhala = remember(singlishText) {
        ToolsData.transliterateSinglishToSinhala(singlishText)
    }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var isGuideExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = singlishText,
            onValueChange = { singlishText = it },
            label = { Text("මෙතන Singlish ටයිප් කරන්න (Type here)") },
            placeholder = { Text("उदा. amma, oya, kohomada, oyata lassanayi...") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Output Display
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                Text(
                    text = "Sinhala Output (සිංහල පරිවර්තනය):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (convertedSinhala.isNotEmpty()) {
                        Text(
                            text = convertedSinhala,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 26.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "ඔබ ටයිප් කරන Singlish වචන මෙතන සිංහල අකුරු බවට පත්වේ...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                if (convertedSinhala.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(convertedSinhala))
                                Toast.makeText(context, "Sinhala text copied!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { shareText(context, convertedSinhala) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = Color(0xFF1C1113)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share text", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Typing Guide Accordion
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().clickable { isGuideExpanded = !isGuideExpanded }
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⌨️ Singlish ටයිප් කරන හැටි (Easy Typing Guide)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Icon(
                        imageVector = if (isGuideExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isGuideExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• a = අ | aa = ආ | i = ඉ | e = එ \n" +
                                "• k = ක | g = ග | j = ජ | d = ද | p = ප\n" +
                                "• th = ත් | dh = ද් | ch = ච් | sh = ශ්\n" +
                                "• nda = න්ද | nch = ංච් | mb = ම්බ\n" +
                                "• amma -> අම්මා | oya -> ඔයා | kohomada -> කොහොමද",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// TOOL 3: MINDS-ON AI VIRAL CAPTION GENERATOR
// ==========================================
@Composable
fun ToolAIQuotes() {
    val categories = remember { listOf("Love", "Friendship", "Motivation", "Mother", "Funny", "Life") }
    var selectedCategory by remember { mutableStateOf("Motivation") }
    var isSinglish by remember { mutableStateOf(false) }
    var generatedCaption by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        Text("AI Caption විෂය තෝරන්න (Select Category):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(6.dp))

        // Grid-based category selection
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.height(84.dp)
        ) {
            items(categories.size) { idx ->
                val cat = categories[idx]
                val selected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                        .border(1.dp, if (selected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                        .clickable { selectedCategory = cat }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (cat) {
                            "Love" -> "❤️ Love"
                            "Friendship" -> "🤝 Friends"
                            "Motivation" -> "🔥 Advice"
                            "Mother" -> "👩 Amma"
                            "Funny" -> "🤣 Jokes"
                            else -> "🌱 Life"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Caption language style Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ටින්ටඩ් සිංග්ලිෂ් ආකෘතිය (Generate in Singlish?)", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurface)
            Switch(
                checked = isSinglish,
                onCheckedChange = { isSinglish = it },
                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.secondary)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Trigger Button
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    generatedCaption = GeminiClient.generateSinhalaCaption(selectedCategory, isSinglish)
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Caption හදනවා... (Connecting...)")
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("AI Caption එකක් සාදන්න ✨", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Generated Caption in a premium Frame
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(18.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (generatedCaption.isNotEmpty()) {
                    Text(
                        text = "“",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = generatedCaption,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    )
                    Text(
                        text = "”",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(generatedCaption))
                                Toast.makeText(context, "Caption Copied!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy caption", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { shareText(context, "$generatedCaption\n\nGenerated via Helasiri App 🦁") },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = Color(0xFF1C1113)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share Caption", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Icon(
                        Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "තේමාවක් තෝරා ඉහත බටන් එක ක්ලික් කරන්න. AI මගින් අති බිහිසුණු ලස්සන වදන් සහ උපසිරැසි සාදනු ඇත.",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

// ==========================================
// TOOL 4: DOMESTIC ELECTRICITY BILL ESTIMATOR
// ==========================================
@Composable
fun ToolElectricityEstimator() {
    var unitStr by rememberSaveable { mutableStateOf("") }
    val units = unitStr.toDoubleOrNull() ?: 0.0

    val billCost = remember(units) {
        ToolsData.calculateCEBBill(units)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = unitStr,
            onValueChange = { unitStr = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("භාවිතා කළ විදුලි ඒකක ගණන (Units consumed)") },
            placeholder = { Text("उदा. 75, 120, 200...") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Total Estimator Output Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Estimated Energy Cost", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                Text("Rs. %,.2f".format(billCost), color = MaterialTheme.colorScheme.secondary, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text("CEB Domestic Account Tariff Formula", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tariff Details & Breakdown explanation
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().weight(1f).border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            LazyColumn(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Text("💡 CEB බිල් සාදන ක්‍රමය (Tariff Calculation Criteria)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (units <= 60.0 && units > 0.0) {
                    item {
                        Text("• Low User Domestic Plan (<60 Units):", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("- First 30 units: Rs. 8.00 per unit", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("- 31-60 units: Rs. 20.00 per unit", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("- Fixed Charge: Rs. 150 (<30U) or Rs. 300 (31-60U)", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else if (units > 60.0) {
                    item {
                        Text("• Regular User Plan (>60 Units):", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("- First 60 units: Rs. 25.00 per unit", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("- 61-90 units: Rs. 30.00 per unit", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("- 91-120 units: Rs. 50.00 per unit", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("- 121-180 units: Rs. 75.00 per unit", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("- Above 180 units: Rs. 100.00 per unit", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        val fixedChg = when {
                            units <= 90.0 -> 600.0
                            units <= 120.0 -> 1000.0
                            units <= 180.0 -> 1500.0
                            else -> 2000.0
                        }
                        Text("- Fixed charge bracket applied: Rs. %,.0f".format(fixedChg), fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    item {
                        Text("විදුලි ඒකක ගණනක් ඇතුළු කළ විට විස්තරාත්මක ගණනය කිරීම් මෙහි දර්ශනය වේ.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

// ==========================================
// TOOL 5: FUEL COST & TRIP CALCULATOR
// ==========================================
@Composable
fun ToolFuelTrip() {
    var distanceStr by rememberSaveable { mutableStateOf("") }
    var efficiencyStr by rememberSaveable { mutableStateOf("15") }
    var sharingStr by rememberSaveable { mutableStateOf("1") }
    
    val fuelTypes = remember {
        listOf(
            Pair("Petrol (Octane 92)", 311.00),
            Pair("Petrol (Octane 95)", 377.00),
            Pair("Auto Diesel", 283.00),
            Pair("Super Diesel", 319.00),
            Pair("Kerosene (භූමිතෙල්)", 195.00)
        )
    }
    
    var selectedFuelIndex by remember { mutableStateOf(0) }

    val dist = distanceStr.toDoubleOrNull() ?: 0.0
    val eff = efficiencyStr.toDoubleOrNull() ?: 1.0
    val shares = sharingStr.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val fuelRate = fuelTypes[selectedFuelIndex].second

    val requiredLiters = if (eff > 0) dist / eff else 0.0
    val totalCost = requiredLiters * fuelRate
    val perPassengerCost = totalCost / shares

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            // Dropdown Fuel Select
            item {
                Text("තෙල් වර්ගය තෝරන්න (Select Fuel Type):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    fuelTypes.forEachIndexed { idx, pair ->
                        val isSelected = idx == selectedFuelIndex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                .border(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                                .clickable { selectedFuelIndex = idx }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = pair.first.replace("Petrol (", "").replace(")", ""),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Rs. ${pair.second.toInt()}",
                                    fontSize = 10.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Input Fields
            item {
                OutlinedTextField(
                    value = distanceStr,
                    onValueChange = { distanceStr = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("ගමන් දුර - KM (Distance of Trip)") },
                    placeholder = { Text("उदा. 120, 350...") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = efficiencyStr,
                    onValueChange = { efficiencyStr = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("රථයේ ඉන්ධන කාර්යක්ෂමතාව - KM/L (Fuel Efficiency)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = sharingStr,
                    onValueChange = { sharingStr = it.filter { it.isDigit() } },
                    label = { Text("සමග යන පිරිස (Passenger split count)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Output Calculations Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Required Fuel Volume:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("%,.2f Liters".format(requiredLiters), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Trip Fuel Cost:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Rs. %,.2f".format(totalCost), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Cost Per Passenger:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("Shared equally by $shares passengers", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("Rs. %,.2f".format(perPassengerCost), fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF0E6E4F))
                        }
                    }
                }
            }

            // Share Trip Cost Plan button
            if (totalCost > 0.0) {
                item {
                    Button(
                        onClick = {
                            val msg = "🚗 *Helasiri Trip Fuel Cost Plan* 🚗\n\n" +
                                    "🗺️ Distance: %.1f KM\n".format(dist) +
                                    "⛽ Fuel Type: ${fuelTypes[selectedFuelIndex].first}\n" +
                                    "💰 Market Price: Rs. ${fuelRate.toInt()}/L\n" +
                                    "🍶 Liters needed: %.2f L\n".format(requiredLiters) +
                                    "━━━━━━━━━━━━━━━━━━━━━━\n" +
                                    "💸 *Total Cost: Rs. %,.2f*\n".format(totalCost) +
                                    "👥 Passengers: $shares\n" +
                                    "👉 *Each Person Pays: Rs. %,.2f*\n".format(perPassengerCost) +
                                    "\nCalculated safely via Helasiri! 🦁"
                            shareText(context, msg)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Trip Cost Estimate with Friends")
                    }
                }
            }
        }
    }
}

// ==========================================
// TOOL 6: SRILANKAN POSTAL CODE FINDER
// ==========================================
@Composable
fun ToolPostalCodeFinder() {
    var query by remember { mutableStateOf("") }
    val baseList = remember { ToolsData.POSTAL_CODES }

    val filtered = remember(query) {
        if (query.isEmpty()) {
            baseList
        } else {
            baseList.filter {
                it.city.contains(query, ignoreCase = true) ||
                        it.code.contains(query, ignoreCase = true) ||
                        it.district.contains(query, ignoreCase = true)
            }
        }
    }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("නගරය, දිස්ත්‍රික්කය හෝ කේතය සොයන්න...") },
            prefix = { Icon(Icons.Default.Search, contentDescription = null, Modifier.size(20.dp)) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filtered) { info ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable {
                            clipboardManager.setText(AnnotatedString(info.code))
                            Toast.makeText(context, "${info.city} Postal Code (${info.code}) copied!", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(info.city, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${info.district} District | ${info.province} Province", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    // Large Copyable Postal Code Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = info.code,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy code", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }

            if (filtered.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("කිසිදු නගරයක් සොයාගත නොහැක", color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

// ==========================================
// TOOL 7: SRILANKAN NIC ANALYZER
// ==========================================
@Composable
fun ToolNICAnalyzer() {
    var nicStr by rememberSaveable { mutableStateOf("") }
    val result = remember(nicStr) {
        if (nicStr.trim().isEmpty()) {
            null
        } else {
            ToolsData.parseNIC(nicStr)
        }
    }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = nicStr,
            onValueChange = { nicStr = it },
            label = { Text("NIC අංකය ඇතුලත් කරන්න (Enter NIC)") },
            placeholder = { Text("उदा. 963251420V, 199632501420...") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (result != null) {
            if (result.error != null) {
                // Formatting Error
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(result.error, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp)
                    }
                }
            } else {
                // Analysis Details display
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(18.dp)) {
                        Text("NIC Extraction Summary:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))

                        val dataRows = listOf(
                            Pair("NIC Format Type:", result.format),
                            Pair("Date of Birth (උපන්දිනය):", result.dob),
                            Pair("Gender (ස්ත්‍රී/පුරුෂ භාවය):", result.gender),
                            Pair("Current Age (වයස):", "${result.age} Years Young"),
                            Pair("Voting Eligibility (ඡන්දය):", if (result.isVoter) "Eligible (ඡන්දය ප්‍රකාශ කළ හැක)" else "Under-age (නොහැක)")
                        )

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                            items(dataRows) { row ->
                                Column {
                                    Text(row.first, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                                    Text(row.second, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Divider(modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                }
                            }
                        }

                        // Share results
                        Button(
                            onClick = {
                                val msg = "🦁 *Helasiri NIC Analyzer Results* 🦁\n\n" +
                                        "💳 NIC: $nicStr\n" +
                                        "🗓️ DOB: ${result.dob}\n" +
                                        "🚻 Gender: ${result.gender}\n" +
                                        "🎂 Age: ${result.age} Years\n" +
                                        "🗳️ Voter status: ${if (result.isVoter) "Eligible Voter" else "Non-voter"}\n" +
                                        "ℹ️ Class: ${result.format}\n\n" +
                                        "Analyzed safely with Helasiri App!"
                                shareText(context, msg)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share NIC Analysis", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().weight(1f).border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.ContactPage, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ශ්‍රී ලංකා ජාතික හැඳුනුම්පත් අංකය ඇතුළු කරන්න (9-V හෝ 12 ඉලක්කම් ආකාරයෙන්). එවිට උපන් දිනය, වයස, ස්ත්‍රී/පුරුෂ භාවය ස්වයංක්‍රීයව විශ්ලේෂණය කරනු ඇත.",
                        textAlign = TextAlign.Center,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// TOOL 8: ASTROLOGISTIC (LAGNA) CALCULATOR
// ==========================================
@Composable
fun ToolAstrology() {
    var selectedId by remember { mutableStateOf(0) }
    val list = remember { ToolsData.ZODIAC_LIST }
    val active = list[selectedId]

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Text("ලග්නය තෝරන්න (Select Zodiac / Lagnaya):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))

        // Grid selection for Zodiac
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.height(132.dp)
        ) {
            items(list.size) { idx ->
                val iconSelected = idx == selectedId
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (iconSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                        .border(1.dp, if (iconSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        .clickable { selectedId = idx }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = list[idx].sinhalaName.substringBefore(" "),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (iconSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = list[idx].name,
                            fontSize = 9.sp,
                            color = if (iconSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Display Auspicious details Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Daily Lagnaya Forecast:", fontWeight = FontWeight.Black, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFFE5B23D)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text("DAILY LITHA 🌸", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1113))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                item {
                    Text("• Lucky Colors & Elements:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("🌈 Lucky Color: ${active.luckyColor}", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("💎 Elements (ධාතුව): ${active.element}", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                item {
                    Text("• Lucky Numbers of Today:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("🎯 Lucky digits: ${active.luckyNumber}", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                item {
                    Text("• Nakath / Auspicious Times (සුභ පැය):", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("⏱️ Auspicious hour: ${active.auspiciousHours}", fontSize = 10.5.sp, color = Color(0xFF0E6E4F), fontWeight = FontWeight.Bold)
                    Text("🚫 Rahu Kalam (රාහු කාලය): ${active.rahuKalam}", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }

                item {
                    Text("• Astrological Forecast (අනාවැකිය):", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text(active.prediction, fontSize = 12.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            val msg = "✨ *Helasiri Astrology Report* ✨\n\n" +
                                    "🌟 Lagnaya: ${active.sinhalaName}\n" +
                                    "🌈 Lucky Color: ${active.luckyColor}\n" +
                                    "🎯 Lucky digits: ${active.luckyNumber}\n" +
                                    "⏱️ Auspicious hours: ${active.auspiciousHours}\n" +
                                    "🚫 Rahu Kalam: ${active.rahuKalam}\n" +
                                    "━━━━━━━━━━━━━━━━━━━━━━\n" +
                                    "🔮 Predicts: ${active.prediction}\n\n" +
                                    "Check your daily nakath limits on Helasiri App!"
                            shareText(context, msg)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Daily Report", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// TOOL 9: LKR EXCHANGE RATE INDEX CONVERTER
// ==========================================
@Composable
fun ToolCurrencyConverter() {
    val currencies = remember {
        listOf(
            Triple("USD", "United States Dollar", 302.50),
            Triple("GBP", "Great Britain Pound", 385.20),
            Triple("EUR", "Euro Standard Network", 328.75),
            Triple("AED", "UAE Dirham", 82.35),
            Triple("AUD", "Australian Dollar", 201.10),
            Triple("CAD", "Canadian Dollar", 221.40),
            Triple("SAR", "Saudi Riyal", 80.60)
        )
    }

    var selectedIdx by remember { mutableStateOf(0) }
    val active = currencies[selectedIdx]

    var userCustomRateStr by remember { mutableStateOf(active.third.toString()) }
    var userAmountStr by remember { mutableStateOf("1") }

    val rate = userCustomRateStr.toDoubleOrNull() ?: active.third
    val amount = userAmountStr.toDoubleOrNull() ?: 1.0
    val lkrTotal = amount * rate

    // Automatically synchronize Custom Rate when currency changes
    LaunchedEffect(selectedIdx) {
        userCustomRateStr = currencies[selectedIdx].third.toString()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("මුදල් වර්ගය තෝරන්න (Select Foreign Currency):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))

        // Horizontal list select
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.height(68.dp)
        ) {
            items(currencies.size) { idx ->
                val isSelected = idx == selectedIdx
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                        .border(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        .clickable { selectedIdx = idx }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        currencies[idx].first,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Conversion fields
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                Text("Central Bank indicative index or Custom rate:", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                
                // Editable rate field
                OutlinedTextField(
                    value = userCustomRateStr,
                    onValueChange = { userCustomRateStr = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Rate per 1 ${active.first} in LKR") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Editable foreign amount
                OutlinedTextField(
                    value = userAmountStr,
                    onValueChange = { userAmountStr = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Amount in Foreign currency (${active.first})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Dynamic massive summary conversions card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Conversion output matches:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "%.2f %s ≈".format(amount, active.first),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Rs. %,.2f".format(lkrTotal),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0E6E4F)
                        )
                        Text(
                            "indicative exchange currency calculations in LKR",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// TOOL 10: CRICKET NET RUN RATE CALCULATOR
// ==========================================
@Composable
fun ToolCricketNRR() {
    // Sri Lanka Cricket Runs Scored & Faced
    var slRunsScored by rememberSaveable { mutableStateOf("") }
    var slOversFaced by rememberSaveable { mutableStateOf("") }
    var slOversFacedBalls by rememberSaveable { mutableStateOf("") }

    // Opponent Runs Conceded & Bowled
    var opponentRunsConceded by rememberSaveable { mutableStateOf("") }
    var slOversBowled by rememberSaveable { mutableStateOf("") }
    var slOversBowledBalls by rememberSaveable { mutableStateOf("") }

    var nrrResult by remember { mutableStateOf<Double?>(null) }
    var explanationText by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            // Batsman Section
            item {
                Text("🏏 ශ්‍රී ලංකා පිතිකරණය (SL Batting Stats):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    OutlinedTextField(
                        value = slRunsScored,
                        onValueChange = { slRunsScored = it.filter { c -> c.isDigit() } },
                        label = { Text("Runs Scored", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1.2f)
                    )
                    OutlinedTextField(
                        value = slOversFaced,
                        onValueChange = { slOversFaced = it.filter { c -> c.isDigit() } },
                        label = { Text("Overs Faced", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = slOversFacedBalls,
                        onValueChange = { slOversFacedBalls = it.filter { c -> c.isDigit() } },
                        label = { Text("Balls", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(0.8f)
                    )
                }
            }

            // Bowling Section
            item {
                Text("🥎 ශ්‍රී ලංකා පන්දු යැවීම (Opponent Batting Stats):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    OutlinedTextField(
                        value = opponentRunsConceded,
                        onValueChange = { opponentRunsConceded = it.filter { c -> c.isDigit() } },
                        label = { Text("Runs Conceded", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1.2f)
                    )
                    OutlinedTextField(
                        value = slOversBowled,
                        onValueChange = { slOversBowled = it.filter { c -> c.isDigit() } },
                        label = { Text("Overs Bowled", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = slOversBowledBalls,
                        onValueChange = { slOversBowledBalls = it.filter { c -> c.isDigit() } },
                        label = { Text("Balls", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(0.8f)
                    )
                }
            }

            // Trigger Actions Button
            item {
                Button(
                    onClick = {
                        val slRuns = slRunsScored.toDoubleOrNull() ?: 0.0
                        val slOversPart = slOversFaced.toDoubleOrNull() ?: 0.0
                        val slBallsPart = slOversFacedBalls.toDoubleOrNull() ?: 0.0
                        val slTotalOvers = slOversPart + (slBallsPart / 6.0)

                        val opRuns = opponentRunsConceded.toDoubleOrNull() ?: 0.0
                        val opOversPart = slOversBowled.toDoubleOrNull() ?: 0.0
                        val opBallsPart = slOversBowledBalls.toDoubleOrNull() ?: 0.0
                        val opTotalOvers = opOversPart + (opBallsPart / 6.0)

                        if (slTotalOvers > 0 && opTotalOvers > 0) {
                            val runsScoredRate = slRuns / slTotalOvers
                            val runsConcededRate = opRuns / opTotalOvers
                            val finalNRR = runsScoredRate - runsConcededRate
                            nrrResult = finalNRR

                            explanationText = "SL scored rate: %.3f runs per over. Conceded rate: %.3f runs per over.".format(runsScoredRate, runsConcededRate)
                        } else {
                            Toast.makeText(context, "Valid overs faced / bowled needed!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Sports, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Net Run Rate (NRR) ගණනය කරන්න", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // Calculations Display result
            nrrResult?.let { nrr ->
                item {
                    val sign = if (nrr >= 0) "+" else ""
                    val signText = if (nrr >= 0) "අපේ ලකුණු වේගය ඉහළයි! 🥳 🎉" else "ලකුණු අනුපාතය වැඩි කර ගත යුතුය! 🥺 💪"
                    val nrrColor = if (nrr >= 0) Color(0xFF0E6E4F) else MaterialTheme.colorScheme.error

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Current Net Run Rate (NRR):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "$sign%.3f".format(nrr),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = nrrColor
                            )
                            Text(signText, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = nrrColor, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(explanationText, fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    val msg = "🏏 *Helasiri Sri Lanka Matches NRR Calculation* 🦁\n\n" +
                                            "🇱🇰 SL Runs Scored: ${slRunsScored} in ${slOversFaced}.${slOversFacedBalls} Overs\n" +
                                            "🛡️ Conceded: ${opponentRunsConceded} in ${slOversBowled}.${slOversBowledBalls} Overs\n" +
                                            "━━━━━━━━━━━━━━━━━━━━━━\n" +
                                            "🔥 *Sri Lanka NRR: $sign%.3f*\n\n".format(nrr) +
                                            "Check tournaments standing on Helasiri App!"
                                    shareText(context, msg)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = Color(0xFF1C1113)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Lanka Fans NRR එක Share කරන්න", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
