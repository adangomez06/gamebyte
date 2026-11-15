package com.cifpvg.gamebyte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- MODELO ----------
enum class Categoria(val titulo: String) {
    VIDEOJUEGOS("Videojuegos"),
    INFORMATICA("Informática"),
    ANIME("Anime")
}

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val categoria: Categoria,
    val emoji: String
)

// ---------- DATOS DE EJEMPLO ----------
val productosEjemplo = listOf(
    Producto(1, "The Legend of Zelda", "Aventura de mundo abierto para Switch", 59.99, 12, Categoria.VIDEOJUEGOS, "🎮"),
    Producto(2, "FIFA 26", "Simulador de fútbol para PS5", 69.90, 0, Categoria.VIDEOJUEGOS, "⚽"),
    Producto(3, "Teclado mecánico RGB", "Switches rojos, formato 75%", 79.95, 8, Categoria.INFORMATICA, "⌨️"),
    Producto(4, "Ratón gaming 16000 DPI", "Inalámbrico, 6 botones", 44.50, 20, Categoria.INFORMATICA, "🖱️"),
    Producto(5, "Auriculares 7.1", "Sonido envolvente con micrófono", 59.00, 5, Categoria.INFORMATICA, "🎧"),
    Producto(6, "Manga One Piece Vol. 100", "Edición en castellano", 8.95, 30, Categoria.ANIME, "📖"),
    Producto(7, "Figura Goku Ultra Instinto", "Figura de 25 cm en PVC", 34.90, 3, Categoria.ANIME, "🐉"),
    Producto(8, "Póster Attack on Titan", "Lámina 50x70 cm", 12.00, 15, Categoria.ANIME, "🖼️")
)

// ---------- ACTIVITY ----------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = Color(0xFF7C4DFF))) {
                CatalogoScreen()
            }
        }
    }
}

// ---------- PANTALLA CATÁLOGO ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen() {
    var categoriaSel by remember { mutableStateOf<Categoria?>(null) }
    var carrito by remember { mutableStateOf(listOf<Producto>()) }

    val filtrados = productosEjemplo.filter { p ->
        categoriaSel == null || p.categoria == categoriaSel
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GameByte", fontWeight = FontWeight.Bold) },
                actions = {
                    BadgedBox(
                        badge = { if (carrito.isNotEmpty()) Badge { Text("${carrito.size}") } },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text("🛒", fontSize = 24.sp)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = categoriaSel == null,
                        onClick = { categoriaSel = null },
                        label = { Text("Todo") }
                    )
                }
                items(Categoria.entries) { cat ->
                    FilterChip(
                        selected = categoriaSel == cat,
                        onClick = { categoriaSel = cat },
                        label = { Text(cat.titulo) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtrados, key = { it.id }) { producto ->
                    ProductoCard(
                        producto = producto,
                        onAnadir = { carrito = carrito + producto }
                    )
                }
            }
        }
    }
}

// ---------- TARJETA DE PRODUCTO ----------
@Composable
fun ProductoCard(producto: Producto, onAnadir: () -> Unit) {
    val disponible = producto.stock > 0

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(producto.emoji, fontSize = 36.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold)
                Text(
                    producto.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "%.2f €".format(producto.precio),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    if (disponible) "En stock (${producto.stock})" else "Agotado",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (disponible) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }

            Button(onClick = onAnadir, enabled = disponible) {
                Text("Añadir")
            }
        }
    }
}