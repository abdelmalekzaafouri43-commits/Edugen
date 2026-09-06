package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.tween
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      var currentTheme by remember { mutableStateOf(AppTheme.SAPPHIRE_COPILOT) }
      MyApplicationTheme(theme = currentTheme) {
        EduGenApp(currentTheme = currentTheme, onThemeSelected = { currentTheme = it })
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EduGenApp(currentTheme: AppTheme, onThemeSelected: (AppTheme) -> Unit) {
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()
  var selectedItem by remember { mutableStateOf("Home") }

  val navItems = listOf(
    NavigationItem("Home", Icons.Default.Home),
    NavigationItem("AI Agent", Icons.Default.SmartToy),
    NavigationItem("Templates", Icons.Default.Description),
    NavigationItem("History", Icons.Default.History),
    NavigationItem("Settings", Icons.Default.Settings)
  )

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.width(280.dp)
      ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "EduGen",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          ),
          modifier = Modifier.padding(24.dp)
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        navItems.forEach { item ->
          NavigationDrawerItem(
            label = { Text(item.title) },
            selected = item.title == selectedItem,
            onClick = {
              selectedItem = item.title
              scope.launch { drawerState.close() }
            },
            icon = { Icon(item.icon, contentDescription = null) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            colors = NavigationDrawerItemDefaults.colors(
              selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
              unselectedContainerColor = Color.Transparent,
              selectedTextColor = MaterialTheme.colorScheme.secondary,
              unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
              selectedIconColor = MaterialTheme.colorScheme.secondary,
              unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }
      }
    }
  ) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(selectedItem, fontWeight = FontWeight.Bold) },
          navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
              Icon(Icons.Default.Menu, contentDescription = "Open Sidebar")
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground
          )
        )
      },
      containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
      Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
        when (selectedItem) {
          "Home" -> DashboardContent(onNavigate = { selectedItem = it })
          "WorksheetGen" -> WorksheetGenContent()
          "PowerPointGen" -> PowerPointGenContent()
          "AI Agent" -> AIAgentContent()
          "Templates" -> TemplatesContent(onNavigate = { selectedItem = it })
          "Settings" -> SettingsContent(currentTheme, onThemeSelected)
          else -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              Text(
                text = "$selectedItem Screen",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun SettingsContent(currentTheme: AppTheme, onThemeSelected: (AppTheme) -> Unit) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Appearance",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Choose a color theme that fits your workflow.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    item {
      Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AppTheme.entries.forEach { theme ->
          ThemeSelectionCard(
            theme = theme,
            isSelected = theme == currentTheme,
            onClick = { onThemeSelected(theme) }
          )
        }
      }
    }
  }
}

@Composable
fun ThemeSelectionCard(theme: AppTheme, isSelected: Boolean, onClick: () -> Unit) {
  val glowBrush = remember(isSelected) {
    if (isSelected) {
      Brush.linearGradient(
        // We use static brand colors for the selection glow just to highlight the selection
        colors = listOf(SapphireBlue, VibrantCyan) 
      )
    } else {
      Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
    }
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        brush = if (isSelected) glowBrush else Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant)),
        shape = RoundedCornerShape(16.dp)
      )
      .clickable { onClick() },
    color = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(16.dp)
  ) {
    Row(
      modifier = Modifier.padding(20.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
         Icon(
           imageVector = Icons.Default.Palette,
           contentDescription = null,
           tint = MaterialTheme.colorScheme.primary
         )
      }
      Spacer(modifier = Modifier.width(16.dp))
      Text(
        text = theme.title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.weight(1f))
      if (isSelected) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = "Selected",
          tint = MaterialTheme.colorScheme.secondary
        )
      }
    }
  }
}

@Composable
fun DashboardContent(onNavigate: (String) -> Unit) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "What would you like to create today?",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Generate professional worksheets and slides instantly.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    item {
      GlowingCard(
        title = "Generate Worksheet",
        description = "Create customizable worksheets for students. Choose difficulty, topic, and formatting.",
        icon = Icons.Default.Description,
        onClick = { onNavigate("WorksheetGen") }
      )
    }

    item {
      GlowingCard(
        title = "Generate PowerPoint",
        description = "Instantly build beautiful slide decks for your lesson plans with AI assistance.",
        icon = Icons.Default.Slideshow,
        onClick = { onNavigate("PowerPointGen") }
      )
    }
    
    item {
      Spacer(modifier = Modifier.height(16.dp))
      GradientButton(
        text = "Try AI Assistant",
        icon = Icons.Default.AutoAwesome,
        onClick = { onNavigate("AI Agent") }
      )
    }
  }
}

@Composable
fun GlowingCard(
  title: String,
  description: String,
  icon: ImageVector,
  onClick: () -> Unit
) {
  val secondaryColor = MaterialTheme.colorScheme.secondary
  val tertiaryColor = MaterialTheme.colorScheme.tertiary
  val glowBrush = remember(secondaryColor, tertiaryColor) {
    Brush.linearGradient(
      colors = listOf(secondaryColor, tertiaryColor)
    )
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .border(
        width = 2.dp,
        brush = glowBrush,
        shape = RoundedCornerShape(20.dp)
      )
      .clickable { onClick() },
    color = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(20.dp)
  ) {
    Row(
      modifier = Modifier.padding(24.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(56.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.size(32.dp)
        )
      }
      
      Spacer(modifier = Modifier.width(20.dp))
      
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = description,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 20.sp
        )
      }
    }
  }
}

@Composable
fun GradientButton(
  text: String,
  icon: ImageVector? = null,
  onClick: () -> Unit
) {
  val primaryColor = MaterialTheme.colorScheme.primary
  val tertiaryColor = MaterialTheme.colorScheme.tertiary
  val buttonBrush = remember(primaryColor, tertiaryColor) {
    Brush.horizontalGradient(
      colors = listOf(primaryColor, tertiaryColor)
    )
  }

  Button(
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .height(60.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = Color.Transparent
    ),
    contentPadding = PaddingValues(0.dp),
    shape = RoundedCornerShape(16.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(buttonBrush)
        .padding(horizontal = 24.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
        }
        Text(
          text = text,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
      }
    }
  }
}

data class NavigationItem(val title: String, val icon: ImageVector)

data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun AIAgentContent() {
  var messageText by remember { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(false) }
  var latestAiResponse by remember { mutableStateOf<String?>(null) }
  val scope = rememberCoroutineScope()
  val messages = remember {
    mutableStateListOf(
      ChatMessage("Hello! I am your EduGen AI Agent. How can I help you plan your lesson today?", false)
    )
  }

  // Abstract Background to highlight the glass effect
  val primaryColor = MaterialTheme.colorScheme.primary
  val secondaryColor = MaterialTheme.colorScheme.secondary
  
  Box(modifier = Modifier.fillMaxSize()) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      drawCircle(
        color = primaryColor.copy(alpha = 0.25f),
        radius = size.width * 0.8f,
        center = androidx.compose.ui.geometry.Offset(0f, 0f)
      )
      drawCircle(
        color = secondaryColor.copy(alpha = 0.2f),
        radius = size.width * 0.6f,
        center = androidx.compose.ui.geometry.Offset(size.width, size.height)
      )
    }

    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // AI Agent Chat Area (Narrowed)
      Column(
        modifier = Modifier
          .weight(0.45f)
          .fillMaxHeight()
      ) {
        // Chat Messages Area
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentPadding = PaddingValues(bottom = 16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          items(messages.size) { index ->
            val msg = messages[index]
            ChatBubble(message = msg)
          }
        }

        // Glass Input Area
        GlassInputField(
          value = messageText,
          onValueChange = { messageText = it },
          onSend = {
            if (messageText.isNotBlank() && !isLoading) {
              val userMsg = messageText
              messages.add(ChatMessage(userMsg, true))
              messageText = ""
              isLoading = true
              
              scope.launch {
                  try {
                      val apiKey = BuildConfig.GEMINI_API_KEY
                      if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                          messages.add(ChatMessage("API Key is missing! Please configure it in the AI Studio Secrets panel.", false))
                          isLoading = false
                          return@launch
                      }
                      val request = GenerateContentRequest(
                          contents = listOf(Content(parts = listOf(Part(text = userMsg))))
                      )
                      val response = RetrofitClient.service.generateContent(apiKey, request)
                      val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I am not sure how to respond to that."
                      messages.add(ChatMessage(text, false))
                      latestAiResponse = text
                  } catch (e: Exception) {
                      messages.add(ChatMessage("Error communicating with AI: ${e.message}", false))
                  } finally {
                      isLoading = false
                  }
              }
            }
          },
          isLoading = isLoading
        )
      }

      // Worksheet Preview Area (New space on the right with fade-in animation)
      Surface(
        modifier = Modifier
          .weight(0.55f)
          .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
      ) {
        AnimatedVisibility(
          visible = true,
          enter = fadeIn(animationSpec = tween(600)),
          modifier = Modifier.fillMaxSize()
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(24.dp)
          ) {
            if (latestAiResponse == null) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  imageVector = Icons.Default.Description,
                  contentDescription = null,
                  modifier = Modifier.size(64.dp),
                  tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                  text = "Worksheet Preview",
                  style = MaterialTheme.typography.titleLarge,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "Ask the AI Agent to generate content to see it previewed here.",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            } else {
              Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Generated Worksheet",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                  )
                  IconButton(onClick = { /* Export or Copy */ }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.onSurface)
                  }
                }
                
                Surface(
                  modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                  color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                  shape = RoundedCornerShape(16.dp)
                ) {
                  LazyColumn(
                    modifier = Modifier
                      .fillMaxSize()
                      .padding(16.dp)
                  ) {
                    item {
                      Text(
                        text = latestAiResponse ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun ChatBubble(message: ChatMessage) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
  ) {
    if (message.isUser) {
      Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp),
        modifier = Modifier.widthIn(max = 280.dp)
      ) {
        Text(
          text = message.text,
          modifier = Modifier.padding(16.dp),
          color = MaterialTheme.colorScheme.onPrimary,
          style = MaterialTheme.typography.bodyLarge
        )
      }
    } else {
      Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
        shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp),
        modifier = Modifier
          .widthIn(max = 280.dp)
          .border(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.2f),
            shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
          )
      ) {
        Text(
          text = message.text,
          modifier = Modifier.padding(16.dp),
          color = MaterialTheme.colorScheme.onSurface,
          style = MaterialTheme.typography.bodyLarge
        )
      }
    }
  }
}

@Composable
fun GlassInputField(
  value: String,
  onValueChange: (String) -> Unit,
  onSend: () -> Unit,
  isLoading: Boolean = false
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .border(
        width = 1.dp,
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(24.dp)
      ),
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
    shape = RoundedCornerShape(24.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 16.dp, vertical = 12.dp),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
        decorationBox = { innerTextField ->
          if (value.isEmpty()) {
            Text("Ask the AI Agent...", color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          innerTextField()
        }
      )
      
      IconButton(
        onClick = { if (!isLoading) onSend() },
        modifier = Modifier
          .size(48.dp)
          .clip(RoundedCornerShape(50))
          .background(MaterialTheme.colorScheme.primary)
      ) {
        if (isLoading) {
          CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp
          )
        } else {
          Icon(
            imageVector = Icons.Default.Send,
            contentDescription = "Send",
            tint = MaterialTheme.colorScheme.onPrimary
          )
        }
      }
    }
  }
}

@Composable
fun WorksheetGenContent() {
  var topic by remember { mutableStateOf("") }
  var gradeLevel by remember { mutableStateOf("") }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier
        .widthIn(max = 600.dp)
        .fillMaxHeight()
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Generate Worksheet",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Configure parameters to generate a custom student worksheet.",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      item {
        GlassTextField(
          value = topic,
          onValueChange = { topic = it },
          placeholder = "Topic (e.g., Photosynthesis)"
        )
      }

      item {
        GlassTextField(
          value = gradeLevel,
          onValueChange = { gradeLevel = it },
          placeholder = "Grade Level (e.g., 8th Grade)"
        )
      }
      
      item {
          Spacer(modifier = Modifier.height(16.dp))
          GradientButton(
            text = "Generate Output",
            icon = Icons.Default.AutoAwesome,
            onClick = { /* TODO: Trigger generation */ }
          )
      }
    }
  }
}

@Composable
fun PowerPointGenContent() {
  var presentationTopic by remember { mutableStateOf("") }
  var audience by remember { mutableStateOf("") }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier
        .widthIn(max = 600.dp)
        .fillMaxHeight()
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Generate PowerPoint",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Instantly build beautiful slide decks with AI assistance.",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      item {
        GlassTextField(
          value = presentationTopic,
          onValueChange = { presentationTopic = it },
          placeholder = "Presentation Topic"
        )
      }

      item {
        GlassTextField(
          value = audience,
          onValueChange = { audience = it },
          placeholder = "Target Audience (e.g., Beginners)"
        )
      }
      
      item {
          Spacer(modifier = Modifier.height(16.dp))
          GradientButton(
            text = "Generate Slides",
            icon = Icons.Default.Slideshow,
            onClick = { /* TODO: Trigger generation */ }
          )
      }
    }
  }
}

@Composable
fun GlassTextField(
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp)
      ),
    color = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(16.dp)
  ) {
    androidx.compose.foundation.text.BasicTextField(
      value = value,
      onValueChange = onValueChange,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 16.dp),
      textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
      decorationBox = { innerTextField ->
        if (value.isEmpty()) {
          Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        innerTextField()
      }
    )
  }
}

data class TemplateItem(val title: String, val type: String, val icon: ImageVector, val targetRoute: String)

@Composable
fun TemplatesContent(onNavigate: (String) -> Unit) {
  val templates = listOf(
    TemplateItem("Math Quiz (Algebra)", "Worksheet", Icons.Default.Calculate, "WorksheetGen"),
    TemplateItem("History Timeline", "Presentation", Icons.Default.History, "PowerPointGen"),
    TemplateItem("Science Lab Report", "Worksheet", Icons.Default.Science, "WorksheetGen"),
    TemplateItem("Literature Review", "Presentation", Icons.Default.AutoStories, "PowerPointGen"),
    TemplateItem("Weekly Lesson Plan", "Worksheet", Icons.Default.CalendarToday, "WorksheetGen"),
    TemplateItem("Interactive Geography", "Presentation", Icons.Default.Public, "PowerPointGen")
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Templates Gallery",
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Select a starter template to quickly generate your content.",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(24.dp))

    LazyVerticalGrid(
      columns = GridCells.Adaptive(minSize = 150.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(bottom = 32.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(templates) { template ->
        TemplateCard(template, onClick = { onNavigate(template.targetRoute) })
      }
    }
  }
}

@Composable
fun TemplateCard(template: TemplateItem, onClick: () -> Unit) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(0.85f)
      .clip(RoundedCornerShape(16.dp))
      .border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        shape = RoundedCornerShape(16.dp)
      )
      .clickable { onClick() },
    color = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = template.icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.size(24.dp)
        )
      }
      
      Column {
        Text(
          text = template.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 2
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = template.type,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun EduGenAppPreview() {
  var currentTheme by remember { mutableStateOf(AppTheme.SAPPHIRE_COPILOT) }
  MyApplicationTheme(theme = currentTheme) {
    EduGenApp(currentTheme = currentTheme, onThemeSelected = {})
  }
}
