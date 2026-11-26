package com.praxion.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

private val Ink = Color(0xFF050B10)
private val Panel = Color(0xFF0B1720)
private val Panel2 = Color(0xFF10232D)
private val Cyan = Color(0xFF63E8F2)
private val Mint = Color(0xFF53D8C4)
private val Muted = Color(0xFF8BA5AE)
private val White = Color(0xFFEAFBFF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(5, 11, 16)
        window.navigationBarColor = android.graphics.Color.rgb(5, 11, 16)
        setContent { PraxionApp() }
    }
}

private data class Persona(val name: String, val description: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
private val femaleModes = listOf(
    Persona("Close Companion", "Warm, encouraging everyday company", Icons.Rounded.FavoriteBorder),
    Persona("Best Friend", "Playful, honest and supportive", Icons.Rounded.Groups),
    Persona("Personal Assistant", "Organized, proactive and focused", Icons.Rounded.Assistant),
    Persona("Listener", "Patient space to talk things through", Icons.Rounded.Hearing),
    Persona("Study Partner", "Learn, revise and stay on track", Icons.Rounded.School),
    Persona("Creative Muse", "Ideas, stories and inspiration", Icons.Rounded.AutoAwesome)
)
private val maleModes = listOf(
    Persona("Close Companion", "Warm, encouraging everyday company", Icons.Rounded.FavoriteBorder),
    Persona("Best Friend", "Playful, honest and supportive", Icons.Rounded.Groups),
    Persona("Personal Assistant", "Organized, proactive and focused", Icons.Rounded.Assistant),
    Persona("Listener", "Patient space to talk things through", Icons.Rounded.Hearing),
    Persona("Study Partner", "Learn, revise and stay on track", Icons.Rounded.School),
    Persona("Gaming Buddy", "Gaming chat, tips and team energy", Icons.Rounded.SportsEsports)
)
private data class Look(val name: String, val detail: String, val hair: String)
private val looks = listOf(Look("Waves", "Long, flowing waves", "01"), Look("Silk", "Straight, sleek hairstyle", "02"), Look("Bob", "Short layered bob", "03"), Look("Aurora", "Soft shoulder-length cut", "04"), Look("Nova", "Modern textured style", "05"))

@Composable fun PraxionApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("praxion", Context.MODE_PRIVATE) }
    var page by remember { mutableStateOf("Home") }
    var gender by remember { mutableStateOf(prefs.getString("gender", "Female") ?: "Female") }
    var mode by remember { mutableStateOf(prefs.getString("mode", "Best Friend") ?: "Best Friend") }
    var look by remember { mutableIntStateOf(prefs.getInt("look", 0)) }
    var characterName by remember { mutableStateOf(prefs.getString("character", "Astra") ?: "Astra") }
    var messages by remember { mutableStateOf(listOf("Welcome to PRAXION. Your AI space is ready.")) }
    var draft by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf(prefs.getString("notes", "") ?: "") }
    var memory by remember { mutableStateOf(prefs.getString("memory", "") ?: "") }
    var endpoint by remember { mutableStateOf(prefs.getString("endpoint", "") ?: "") }
    var apiKey by remember { mutableStateOf(prefs.getString("api", "") ?: "") }
    var showLook by remember { mutableStateOf(false) }
    var showModes by remember { mutableStateOf(false) }
    MaterialTheme(colorScheme = darkColorScheme(primary = Cyan, secondary = Mint, background = Ink, surface = Panel, onBackground = White, onSurface = White)) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF07131B), Ink, Color(0xFF061018))))) {
            Column(Modifier.fillMaxSize()) {
                TopBar(page, characterName) { page = "Home" }
                AnimatedContent(page, modifier = Modifier.weight(1f), label = "page") { current ->
                    when (current) {
                        "Home" -> HomeScreen(characterName, gender, mode, look, onChat = { page = "Assistant" }, onMode = { showModes = true }, onLook = { showLook = true }, onOpen = { page = it })
                        "Assistant" -> ChatScreen(messages, draft, { draft = it }, { text -> if (text.isNotBlank()) { messages = messages + text + "I'm here. Connect your AI provider in Settings to enable live model responses."; draft = "" } })
                        "Characters" -> CharacterScreen(gender, { gender = it; prefs.edit().putString("gender", it).apply() }, characterName, { characterName = it; prefs.edit().putString("character", it).apply() }, mode, { showModes = true }, look, { showLook = true })
                        "Voice Studio" -> FeatureScreen("Voice Studio", "Give your assistant a voice", listOf("Voice selection", "Speech speed", "Pitch and tone", "Preview voice"), Icons.Rounded.GraphicEq, "Voice output needs a connected speech provider.")
                        "Memory" -> EditorScreen("Memory", "Things PRAXION can use as helpful context", memory, { memory = it; prefs.edit().putString("memory", it).apply() }, "Save memory")
                        "Notes" -> EditorScreen("Notes", "Quick thoughts, plans and ideas", notes, { notes = it; prefs.edit().putString("notes", it).apply() }, "Save notes")
                        "Skills" -> FeatureScreen("Skills", "Extend what your assistant can help with", listOf("Writing & summarizing", "Study and research", "Planning", "Creative ideas", "Daily productivity"), Icons.Rounded.Extension, "Skill actions can be connected to your backend.")
                        "Settings" -> SettingsScreen(endpoint, { endpoint = it }, apiKey, { apiKey = it }, { prefs.edit().putString("endpoint", endpoint).putString("api", apiKey).apply() })
                        else -> HomeScreen(characterName, gender, mode, look, onChat = { page = "Assistant" }, onMode = { showModes = true }, onLook = { showLook = true }, onOpen = { page = it })
                    }
                }
                if (page != "Assistant") BottomNav(page) { page = it }
            }
            if (showLook) LookDialog(look, { look = it }, { prefs.edit().putInt("look", look).apply(); showLook = false }, { showLook = false })
            if (showModes) ModeDialog(gender, mode, { mode = it; prefs.edit().putString("mode", it).apply() }, { showModes = false })
        }
    }
}

@Composable private fun TopBar(page: String, character: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text("PRAXION", color = White, fontSize = 20.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp); Text("INTELLIGENCE. IN ACTION.", color = Muted, fontSize = 8.sp, letterSpacing = 2.sp) }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clip(CircleShape).background(Panel).border(1.dp, Color(0xFF24404B), CircleShape).padding(horizontal = 11.dp, vertical = 7.dp)) { Box(Modifier.size(6.dp).background(Mint, CircleShape)); Spacer(Modifier.width(7.dp)); Text("SYSTEM READY", color = Mint, fontSize = 9.sp, letterSpacing = 1.sp) }
    }
}

@Composable private fun HomeScreen(character: String, gender: String, mode: String, look: Int, onChat: () -> Unit, onMode: () -> Unit, onLook: () -> Unit, onOpen: (String) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Spacer(Modifier.height(4.dp))
        Text("YOUR AI. YOUR SPACE.", color = Cyan, fontSize = 10.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
        Text("A smarter way\nto be you.", color = White, fontSize = 34.sp, lineHeight = 39.sp, fontWeight = FontWeight.Light)
        CharacterOrb(character, gender, Modifier.fillMaxWidth().height(205.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("ACTIVE PERSONA", mode, Icons.Rounded.Psychology, Modifier.weight(1f), onMode)
            StatCard("CHARACTER LOOK", looks[look].name, Icons.Rounded.Face, Modifier.weight(1f), onLook)
        }
        GlowButton("Start a conversation", Icons.Rounded.ArrowForward, onChat)
        Text("YOUR WORKSPACE", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
        val items = listOf(Triple("Characters", "Personalities and appearance", Icons.Rounded.Face), Triple("Voice Studio", "Voice and speech preferences", Icons.Rounded.GraphicEq), Triple("Memory", "Context that matters to you", Icons.Rounded.Memory), Triple("Skills", "Tools for everyday tasks", Icons.Rounded.Extension), Triple("Notes", "Capture a thought", Icons.Rounded.StickyNote2), Triple("Settings", "Configure your experience", Icons.Rounded.Settings))
        items.chunked(2).forEach { row -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { row.forEach { (title, sub, icon) -> WorkspaceCard(title, sub, icon, Modifier.weight(1f)) { onOpen(title) } }; if (row.size == 1) Spacer(Modifier.weight(1f)) } }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable private fun CharacterOrb(name: String, gender: String, modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "orb")
    val pulse by infinite.animateFloat(0.88f, 1.08f, infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pulse")
    Box(modifier.clip(RoundedCornerShape(28.dp)).background(Brush.radialGradient(listOf(Color(0xFF17434C), Color(0xFF0A202A), Panel, Ink))).border(1.dp, Color(0xFF245360), RoundedCornerShape(28.dp)), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) { for (i in 0..9) { val y = size.height * (0.48f + i * .055f); drawLine(Color(0x143DD6E4), Offset(0f, y), Offset(size.width, y), 1.dp.toPx()) } }
        Box(Modifier.size((112 * pulse).dp).background(Brush.radialGradient(listOf(Color(0xAA72F5F0), Color(0x5563E8F2), Color.Transparent)), CircleShape).border(1.dp, Color(0x7763E8F2), CircleShape), contentAlignment = Alignment.Center) { Icon(if (gender == "Female") Icons.Rounded.Face3 else Icons.Rounded.Face, null, tint = Cyan, modifier = Modifier.size(64.dp)) }
        Column(Modifier.align(Alignment.BottomCenter).padding(bottom = 18.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(name.uppercase(), color = White, fontSize = 12.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold); Text("HOLOGRAPHIC INTERFACE  ·  ONLINE", color = Muted, fontSize = 8.sp, letterSpacing = 1.5.sp) }
    }
}

@Composable private fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier.clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, Color(0xFF203C47))) { Column(Modifier.padding(14.dp)) { Icon(icon, null, tint = Cyan, modifier = Modifier.size(18.dp)); Spacer(Modifier.height(10.dp)); Text(label, color = Muted, fontSize = 8.sp, letterSpacing = 1.3.sp); Text(value, color = White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold) } }
}
@Composable private fun WorkspaceCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier.clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = Color(0xCC0B1820)), shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, Color(0xFF1C3540))) { Column(Modifier.padding(14.dp)) { Icon(icon, null, tint = Mint, modifier = Modifier.size(20.dp)); Spacer(Modifier.height(9.dp)); Text(title, color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold); Text(subtitle, color = Muted, fontSize = 10.sp, lineHeight = 14.sp) } }
}
@Composable private fun GlowButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF1C6470), Color(0xFF153A47)))).border(1.dp, Color(0xFF5BDCE8), RoundedCornerShape(16.dp)).clickable { onClick() }.padding(17.dp), verticalAlignment = Alignment.CenterVertically) { Text(text, color = White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f)); Icon(icon, null, tint = Cyan) }
}

@Composable private fun BottomNav(page: String, onSelect: (String) -> Unit) {
    val nav = listOf("Home" to Icons.Rounded.Home, "Assistant" to Icons.Rounded.ChatBubbleOutline, "Characters" to Icons.Rounded.Face, "Settings" to Icons.Rounded.Settings)
    Row(Modifier.fillMaxWidth().background(Color(0xF9081219)).border(BorderStroke(1.dp, Color(0xFF1B3540))).padding(horizontal = 8.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceAround) { nav.forEach { (name, icon) -> Column(Modifier.clip(RoundedCornerShape(13.dp)).clickable { onSelect(name) }.padding(horizontal = 13.dp, vertical = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, null, tint = if (page == name) Cyan else Muted, modifier = Modifier.size(20.dp)); Text(name, color = if (page == name) Cyan else Muted, fontSize = 9.sp) } } }
}

@Composable private fun CharacterScreen(gender: String, onGender: (String) -> Unit, name: String, onName: (String) -> Unit, mode: String, onModes: () -> Unit, look: Int, onLook: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(17.dp)) {
        SectionTitle("CHARACTER LAB", "Build an assistant that feels like your own.")
        CharacterOrb(name, gender, Modifier.fillMaxWidth().height(190.dp))
        Text("CHARACTER PRESENTATION", color = Muted, fontSize = 10.sp, letterSpacing = 1.5.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { listOf("Female", "Male", "Neutral").forEach { option -> SelectChip(option, gender == option) { onGender(option) } } }
        Text("CHARACTER NAME", color = Muted, fontSize = 10.sp, letterSpacing = 1.5.sp)
        OutlinedInput(name, onName, "Enter a name")
        ActionRow("Interaction mode", mode, Icons.Rounded.Tune, onModes)
        ActionRow("Appearance / look", looks[look].name + " · " + looks[look].detail, Icons.Rounded.Face, onLook)
        Text("PERSONALITY IS SEPARATE FROM APPEARANCE", color = Muted, fontSize = 9.sp, letterSpacing = 1.sp)
    }
}

@Composable private fun SelectChip(text: String, selected: Boolean, onClick: () -> Unit) { Box(Modifier.clip(CircleShape).background(if (selected) Color(0xFF174650) else Panel).border(1.dp, if (selected) Cyan else Color(0xFF27434D), CircleShape).clickable { onClick() }.padding(horizontal = 18.dp, vertical = 11.dp)) { Text(text, color = if (selected) Cyan else Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) } }
@Composable private fun ActionRow(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) { Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Panel).border(1.dp, Color(0xFF203C47), RoundedCornerShape(16.dp)).clickable { onClick() }.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = Cyan); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, color = White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold); Text(value, color = Muted, fontSize = 10.sp) }; Icon(Icons.Rounded.ChevronRight, null, tint = Muted) } }

@Composable private fun ModeDialog(gender: String, selected: String, onSelect: (String) -> Unit, onClose: () -> Unit) {
    val modes = if (gender == "Male") maleModes else femaleModes
    DialogFrame("CHOOSE INTERACTION MODE", "The same character can take on different helpful roles.", onClose) {
        Column(Modifier.heightIn(max = 440.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) { modes.forEach { persona -> Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(if (selected == persona.name) Color(0xFF123C46) else Panel).border(1.dp, if (selected == persona.name) Cyan else Color(0xFF1E3944), RoundedCornerShape(14.dp)).clickable { onSelect(persona.name); onClose() }.padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Icon(persona.icon, null, tint = Cyan); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(persona.name, color = White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold); Text(persona.description, color = Muted, fontSize = 10.sp) }; if (selected == persona.name) Icon(Icons.Rounded.CheckCircle, null, tint = Mint) } } }
    }
}

@Composable private fun LookDialog(selected: Int, onSelect: (Int) -> Unit, onKeep: () -> Unit, onCancel: () -> Unit) {
    DialogFrame("APPEARANCE STUDIO", "Choose a visual style. Voice, memory and personality stay the same.", onCancel) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf(maxOf(0, selected - 1), selected, minOf(looks.lastIndex, selected + 1)).distinct().forEach { index -> LookPreview(looks[index], index == selected, Modifier.weight(1f)) { onSelect(index) } } }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) { Text(looks[selected].hair, color = Cyan, fontSize = 9.sp, letterSpacing = 2.sp); Text(looks[selected].name, color = White, fontSize = 20.sp, fontWeight = FontWeight.Light) }
        Text(looks[selected].detail, color = Muted, fontSize = 11.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) { GlowButton("Keep this look", Icons.Rounded.Check, onKeep); TextButton(onClick = onCancel) { Text("Cancel", color = Muted) } }
        Text("SWIPE / TAP A STYLE TO PREVIEW", color = Muted, fontSize = 8.sp, letterSpacing = 1.5.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

@Composable private fun LookPreview(look: Look, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(modifier.clip(RoundedCornerShape(16.dp)).background(if (selected) Color(0xFF123A45) else Panel).border(1.dp, if (selected) Cyan else Color(0xFF1C3540), RoundedCornerShape(16.dp)).clickable { onClick() }.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.fillMaxWidth().height(112.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.size(if (selected) 88.dp else 76.dp).background(Brush.radialGradient(listOf(Color(0x995CEAF0), Color(0x332E8995), Color.Transparent)), CircleShape), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) { val w = size.width; val h = size.height; val hairColor = if (selected) Color(0xFF77F1EE) else Color(0xFF547D86); drawCircle(hairColor.copy(alpha = .28f), radius = w * .39f); drawCircle(hairColor, radius = w * .31f, style = Stroke(width = 2.dp.toPx())); drawArc(hairColor, 185f, 170f, false, topLeft = Offset(w*.17f,h*.08f), size = androidx.compose.ui.geometry.Size(w*.66f,h*.78f), style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)); drawLine(hairColor, Offset(w*.25f,h*.53f), Offset(w*.14f,h*.87f), 4.dp.toPx(), cap = StrokeCap.Round); drawLine(hairColor, Offset(w*.75f,h*.53f), Offset(w*.86f,h*.87f), 4.dp.toPx(), cap = StrokeCap.Round) }
                Icon(Icons.Rounded.Face3, null, tint = if (selected) Cyan else Muted, modifier = Modifier.size(36.dp))
            }
        }
        Text(look.hair, color = if (selected) Cyan else Muted, fontSize = 8.sp, letterSpacing = 1.sp)
        Text(look.name, color = if (selected) White else Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable private fun ChatScreen(messages: List<String>, draft: String, onDraft: (String) -> Unit, onSend: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth().padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(36.dp).background(Color(0xFF123E48), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Rounded.AutoAwesome, null, tint = Cyan) }; Spacer(Modifier.width(10.dp)); Column { Text("PRAXION ASSISTANT", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp); Text("Ready for your next idea", color = Mint, fontSize = 10.sp) } }
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { items(messages) { msg -> Box(Modifier.fillMaxWidth(), contentAlignment = if (msg.startsWith("You: ")) Alignment.CenterEnd else Alignment.CenterStart) { Text(if (msg.startsWith("You: ")) msg.removePrefix("You: ") else msg, color = White, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.widthIn(max = 300.dp).clip(RoundedCornerShape(16.dp)).background(if (msg.startsWith("You: ")) Color(0xFF16434D) else Panel).border(1.dp, Color(0xFF1F3B45), RoundedCornerShape(16.dp)).padding(13.dp)) } } }
        Row(Modifier.fillMaxWidth().padding(vertical = 10.dp).clip(RoundedCornerShape(18.dp)).background(Panel).border(1.dp, Color(0xFF2A4D58), RoundedCornerShape(18.dp)).padding(7.dp), verticalAlignment = Alignment.CenterVertically) { BasicTextField(draft, onDraft, Modifier.weight(1f).padding(horizontal = 10.dp), textStyle = androidx.compose.ui.text.TextStyle(color = White, fontSize = 13.sp), cursorBrush = SolidColor(Cyan), decorationBox = { inner -> Box { if (draft.isEmpty()) Text("Message PRAXION...", color = Muted, fontSize = 13.sp); inner() } }); IconButton(onClick = { onSend("You: $draft") }, enabled = draft.isNotBlank()) { Icon(Icons.Rounded.Send, null, tint = Cyan) } }
        Text("DEMO INTERFACE · CONNECT A MODEL IN SETTINGS FOR LIVE AI", color = Muted, fontSize = 8.sp, letterSpacing = .8.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp))
    }
}

@Composable private fun EditorScreen(title: String, subtitle: String, value: String, onValue: (String) -> Unit, button: String) { Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { SectionTitle(title.uppercase(), subtitle); OutlinedTextField(value, onValue, Modifier.fillMaxWidth().weight(1f), placeholder = { Text("Write here...", color = Muted) }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White, focusedBorderColor = Cyan, unfocusedBorderColor = Color(0xFF29434D), cursorColor = Cyan), shape = RoundedCornerShape(16.dp)); Text("Your changes are saved locally as you type.", color = Muted, fontSize = 10.sp) } }
@Composable private fun FeatureScreen(title: String, subtitle: String, features: List<String>, icon: androidx.compose.ui.graphics.vector.ImageVector, note: String) { Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { SectionTitle(title.uppercase(), subtitle); Box(Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(22.dp)).background(Brush.radialGradient(listOf(Color(0xFF164B56), Panel, Ink))), contentAlignment = Alignment.Center) { Icon(icon, null, tint = Cyan, modifier = Modifier.size(54.dp)) }; features.forEach { FeatureLine(it) }; Text(note, color = Muted, fontSize = 11.sp, lineHeight = 17.sp) } }

@Composable private fun SettingsScreen(endpoint: String, onEndpoint: (String) -> Unit, apiKey: String, onApi: (String) -> Unit, onSave: () -> Unit) { Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) { SectionTitle("SYSTEM SETTINGS", "Connect the AI service used by your PRAXION build."); Text("API BASE URL", color = Muted, fontSize = 10.sp, letterSpacing = 1.sp); OutlinedInput(endpoint, onEndpoint, "https://your-server.example/api"); Text("API KEY", color = Muted, fontSize = 10.sp, letterSpacing = 1.sp); OutlinedInput(apiKey, onApi, "Provider key (stored locally)", true); GlowButton("Save connection settings", Icons.Rounded.Save, onSave); Text("Security note: this prototype stores credentials in app preferences on this device. For production, use a secure backend and short-lived tokens; never ship a private provider key inside a public app.", color = Muted, fontSize = 10.sp, lineHeight = 16.sp); FeatureLine("App version · 1.0.0 · Android") } }
@Composable private fun OutlinedInput(value: String, onValue: (String) -> Unit, placeholder: String, secret: Boolean = false) { OutlinedTextField(value, onValue, Modifier.fillMaxWidth(), placeholder = { Text(placeholder, color = Muted) }, visualTransformation = if (secret) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White, focusedBorderColor = Cyan, unfocusedBorderColor = Color(0xFF29434D), cursorColor = Cyan), shape = RoundedCornerShape(14.dp), singleLine = true) }
@Composable private fun FeatureLine(text: String) { Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Panel).border(1.dp, Color(0xFF203C47), RoundedCornerShape(14.dp)).padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(7.dp).background(Mint, CircleShape)); Spacer(Modifier.width(12.dp)); Text(text, color = White, fontSize = 13.sp) } }
@Composable private fun SectionTitle(eyebrow: String, subtitle: String) { Column(verticalArrangement = Arrangement.spacedBy(5.dp)) { Text(eyebrow, color = Cyan, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold); Text(subtitle, color = Muted, fontSize = 12.sp, lineHeight = 17.sp) } }

@Composable private fun DialogFrame(title: String, subtitle: String, onClose: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onClose, properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)) { Column(Modifier.fillMaxWidth().padding(horizontal = 15.dp).clip(RoundedCornerShape(26.dp)).background(Brush.verticalGradient(listOf(Color(0xFF102A35), Color(0xFF07131B)))).border(1.dp, Color(0xFF32616D), RoundedCornerShape(26.dp)).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(title, color = Cyan, fontSize = 9.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold); Text(subtitle, color = White, fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.Light) }; IconButton(onClick = onClose) { Icon(Icons.Rounded.Close, null, tint = Muted) } }; content() } }
}
