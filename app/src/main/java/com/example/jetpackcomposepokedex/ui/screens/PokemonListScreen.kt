package com.example.jetpackcomposepokedex.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.jetpackcomposepokedex.R
import com.example.jetpackcomposepokedex.model.PokedexEntry
import com.example.jetpackcomposepokedex.ui.theme.JetpackComposePokedexTheme

@Composable
fun PokemonListScreen(
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltViewModel(),
    navController: NavController
) {
    val pokemonListResults by remember { viewModel.pokemonListResults }
    val endOfResults by remember { viewModel.endOfResults }
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }

    Surface(
        color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Image(
                painter = painterResource(R.drawable.pokemonlogo),
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally),
                contentDescription = null
            )
            SearchBar(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {

            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(top = 50.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = pokemonListResults) { pokemon ->
                    if (!endOfResults && !isLoading) {
                        viewModel.getPokemonsAndPaginate()
                    }
                    PokedexCard(pokedexEntry = pokemon, navController = navController)
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (loadError.isNotEmpty()) {
                    RetrySection(error = loadError) {
                        viewModel.getPokemonsAndPaginate()
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {}, // Takes a string and does something
) {
    var text by remember { mutableStateOf("") }

    Box(modifier = modifier) {
        TextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(text)
            },
            maxLines = 1,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, shape = CircleShape)
                .clip(shape = RoundedCornerShape(50.dp)),
            placeholder = {
                Text(
                    text = "Search",
                    fontFamily = FontFamily.Default,
                    color = Color.Gray
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun PokedexCard(
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltViewModel(),
    pokedexEntry: PokedexEntry,
    navController: NavController
) {
    // sets up default background colours to be passed in
    val defaultDominantColour = MaterialTheme.colorScheme.surface
    var dominantColor by remember { mutableStateOf(defaultDominantColour) }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { navController.navigate("detailScreen/${dominantColor}/${pokedexEntry.name}") },
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            dominantColor,
                            defaultDominantColour
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.Center),
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(pokedexEntry.imageUrl)
                        .build(),
                    onSuccess = {
                        viewModel.getDominantColour(it.result.drawable) { color ->
                            dominantColor = color
                        }
                    },
                    loading = {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .scale(0.5f)
                                .align(Alignment.Center),
                            color = Color.LightGray,
                            strokeWidth = 5.dp
                        )
                    },
                    error = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_broken_image),
                            contentDescription = null
                        )
                    },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            }
            Text(
                text = pokedexEntry.name,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun RetrySection(
    error: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = error,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(50.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp
            )
        ) {
            Text(text = "Try again")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    JetpackComposePokedexTheme {
        SearchBar(onSearch = { Log.i("myTag", "This is my message") })
    }
}

//@Preview(showBackground = true)
//@Composable
//fun RetryButtonPreview() {
//    JetpackComposePokedexTheme {
//        RetrySection(onClick = { })
//    }
//}



