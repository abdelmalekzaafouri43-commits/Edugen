package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
      var currentTheme by remember { mutableStateOf(AppTheme.SAPPHIRE_DARK) }
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
          "Home" -> DashboardContent()
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
fun DashboardContent() {
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
        onClick = {}
      )
    }

    item {
      GlowingCard(
        title = "Generate PowerPoint",
        description = "Instantly build beautiful slide decks for your lesson plans with AI assistance.",
        icon = Icons.Default.Slideshow,
        onClick = {}
      )
    }
    
    item {
      Spacer(modifier = Modifier.height(16.dp))
      GradientButton(
        text = "Try AI Assistant",
        icon = Icons.Default.AutoAwesome,
        onClick = {}
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

@Preview(showBackground = true)
@Composable
fun EduGenAppPreview() {
  var currentTheme by remember { mutableStateOf(AppTheme.SAPPHIRE_DARK) }
  MyApplicationTheme(theme = currentTheme) {
    EduGenApp(currentTheme = currentTheme, onThemeSelected = {})
  }
}
