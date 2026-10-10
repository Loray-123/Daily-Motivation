
package com.dailymotivation.app

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton as MaterialTextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime


// ------------------------------------------------------------
// QUOTES
// ------------------------------------------------------------

data class Quote(
    val text: String,
    val author: String
)

val quotes = listOf(
    Quote("Believe you can and you're halfway there.", "Theodore Roosevelt"),
    Quote("The only way to do great work is to love what you do.", "Steve Jobs"),
    Quote("It always seems impossible until it's done.", "Nelson Mandela"),
    Quote("You are never too old to set another goal or to dream a new dream.", "C.S. Lewis"),
    Quote("Start where you are. Use what you have. Do what you can.", "Arthur Ashe"),
    Quote("Success is the sum of small efforts, repeated day in and day out.", "Robert Collier"),
    Quote("Don't watch the clock; do what it does. Keep going.", "Sam Levenson"),
    Quote("The future depends on what you do today.", "Mahatma Gandhi"),
    Quote("You don't have to be perfect to make progress.", "Unknown"),
    Quote("Every day is another chance to get better.", "Unknown"),
    Quote("Your only limit is the one you set for yourself.", "Unknown"),
    Quote("Small steps every day lead to big changes.", "Unknown"),
    Quote("You are capable of more than you know.", "Unknown"),
    Quote("Keep going. Your future self will thank you.", "Unknown"),
    Quote("Difficult roads often lead to beautiful destinations.", "Unknown")
)


// ------------------------------------------------------------
// BACKGROUND HELPERS
// ------------------------------------------------------------

fun getBackgroundImages(context: Context): List<String> {
    return try {
        context.assets.list("backgrounds")
            ?.filter { fileName ->
                fileName.endsWith(".jpg", true) ||
                        fileName.endsWith(".jpeg", true) ||
                        fileName.endsWith(".png", true) ||
                        fileName.endsWith(".webp", true)
            }
            ?.sorted()
            ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}


// ------------------------------------------------------------
// CURRENT DATE
// ------------------------------------------------------------

@Composable
fun rememberCurrentDate(): LocalDate {

    var currentDate by remember {
        mutableStateOf(LocalDate.now())
    }

    LaunchedEffect(Unit) {

        while (true) {

            val now = LocalDateTime.now()

            val nextMidnight =
                now.toLocalDate()
                    .plusDays(1)
                    .atStartOfDay()

            val milliseconds =
                Duration.between(
                    now,
                    nextMidnight
                )
                    .toMillis()
                    .coerceAtLeast(1000L)

            delay(milliseconds)

            currentDate = LocalDate.now()
        }
    }

    return currentDate
}


// ------------------------------------------------------------
// ASSET IMAGE
// ------------------------------------------------------------

@Composable
fun AssetImage(
    fileName: String,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    var imageBitmap by remember(fileName) {
        mutableStateOf(
            null as androidx.compose.ui.graphics.ImageBitmap?
        )
    }

    LaunchedEffect(fileName) {

        val loadedBitmap =
            withContext(Dispatchers.IO) {

                try {

                    val bounds =
                        BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }

                    context.assets.open(
                        "backgrounds/$fileName"
                    ).use { input ->

                        BitmapFactory.decodeStream(
                            input,
                            null,
                            bounds
                        )
                    }

                    val options =
                        BitmapFactory.Options().apply {

                            inSampleSize = 1

                            inPreferredConfig =
                                android.graphics.Bitmap.Config.RGB_565

                            inMutable = false
                        }

                    context.assets.open(
                        "backgrounds/$fileName"
                    ).use { input ->

                        BitmapFactory.decodeStream(
                            input,
                            null,
                            options
                        )
                    }

                } catch (e: Exception) {

                    null
                }
            }

        imageBitmap =
            loadedBitmap?.asImageBitmap()
    }

    imageBitmap?.let { bitmap ->

        Image(
            bitmap = bitmap,
            contentDescription =
                "Motivational background",
            modifier = modifier,
            contentScale =
                ContentScale.Crop
        )
    }
}


// ------------------------------------------------------------
// SHARED BUTTON
// ------------------------------------------------------------

@Composable
fun TextButton(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
    onClick: () -> Unit
) {

    Text(
        text = text,
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                },
                onClick = onClick
            )
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            ),
        fontSize = fontSize,
        fontWeight = FontWeight.Medium,
        color = Color.White,
        textAlign = TextAlign.Center
    )
}


// ------------------------------------------------------------
// FAVORITE HEART ANIMATION
// ------------------------------------------------------------

@Composable
fun FavoriteHeartButton(
    isSaved: Boolean,
    onClick: () -> Unit
) {

    var isAnimating by remember {
        mutableStateOf(false)
    }

    val heartScale =
        remember {
            Animatable(1f)
        }

    val ringScale =
        remember {
            Animatable(0.5f)
        }

    val ringAlpha =
        remember {
            Animatable(0f)
        }

    LaunchedEffect(isAnimating) {

        if (isAnimating) {

            heartScale.snapTo(1f)
            ringScale.snapTo(0.5f)
            ringAlpha.snapTo(0.65f)

            heartScale.animateTo(
                targetValue = 1.35f,
                animationSpec = tween(
                    durationMillis = 180
                )
            )

            heartScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 280
                )
            )

            isAnimating = false
        }
    }

    LaunchedEffect(isAnimating) {

        if (isAnimating) {

            ringScale.animateTo(
                targetValue = 2.0f,
                animationSpec = tween(
                    durationMillis = 500
                )
            )
        }
    }

    LaunchedEffect(isAnimating) {

        if (isAnimating) {

            ringAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 500
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .width(230.dp)
            .animateContentSize(),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        if (!isSaved) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .padding(start = 24.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start
            ) {

                Text(
                    text = "↶",
                    fontSize = 25.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    modifier = Modifier.graphicsLayer {
                        rotationZ = -20f
                    }
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = "Click to save",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Start
                )
            }

        } else {

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            )
        }

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(55.dp),
                contentAlignment =
                    Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp)
                        .scale(ringScale.value)
                        .alpha(ringAlpha.value)
                        .background(
                            Color.White.copy(
                                alpha = 0.45f
                            )
                        )
                )

                Text(
                    text =
                        if (isSaved) "♥" else "♡",
                    fontSize = 27.sp,
                    color = Color.White,
                    modifier = Modifier
                        .scale(heartScale.value)
                        .clickable(
                            indication = null,
                            interactionSource =
                                remember {
                                    MutableInteractionSource()
                                }
                        ) {

                            if (!isSaved) {
                                isAnimating = true
                            }

                            onClick()
                        }
                        .padding(8.dp),
                    textAlign =
                        TextAlign.Center
                )
            }

            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )
        }
    }
}

// ------------------------------------------------------------
// MAIN ACTIVITY
// ------------------------------------------------------------

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContent {
            DailyMotivationApp()
        }
    }
}


// ------------------------------------------------------------
// APP ROOT
// ------------------------------------------------------------

@Composable
fun DailyMotivationApp() {

    val context = LocalContext.current

    val today = rememberCurrentDate()

    val backgroundImages =
        remember {
            getBackgroundImages(context)
        }

    if (backgroundImages.isEmpty()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    "No background images found.",
                color = Color.White
            )
        }

        return
    }

    val backgroundIndex =
        Math.floorMod(
            today.toEpochDay(),
            backgroundImages.size.toLong()
        ).toInt()

    val backgroundImage =
        backgroundImages[backgroundIndex]

    var showIntro by remember {
        mutableStateOf(true)
    }

    AnimatedContent(
        targetState = showIntro,
        transitionSpec = {

            fadeIn(
                animationSpec = tween(
                    durationMillis = 900
                )
            ) togetherWith
                    fadeOut(
                        animationSpec = tween(
                            durationMillis = 900
                        )
                    )
        },
        label = "introTransition"
    ) { introVisible ->

        if (introVisible) {

            IntroScreen(
                backgroundImage = backgroundImage
            )

            LaunchedEffect(Unit) {

                delay(2200L)

                showIntro = false
            }

        } else {

            MainAndFavorites()
        }
    }
}


// ------------------------------------------------------------
// INTRO SCREEN
// ------------------------------------------------------------

@Composable
fun IntroScreen(
    backgroundImage: String
) {

    var targetScale by remember {
        mutableStateOf(1.0f)
    }

    LaunchedEffect(Unit) {
        targetScale = 1.20f
    }

    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(
            durationMillis = 2200,
            easing = LinearOutSlowInEasing
        ),
        label = "introZoom"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        ) {

            AssetImage(
                fileName = backgroundImage,
                modifier =
                    Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(
                        alpha = 0.42f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = "D A I L Y",
                fontSize = 16.sp,
                letterSpacing = 5.sp,
                fontWeight =
                    FontWeight.Medium,
                color = Color.White
            )

            Text(
                text = "Motivation",
                fontSize = 52.sp,
                fontFamily =
                    FontFamily.Cursive,
                fontWeight =
                    FontWeight.Normal,
                color = Color.White
            )

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(1.dp)
                    .background(
                        Color.White
                    )
            )

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            Text(
                text =
                    "A little inspiration for every day.",
                fontSize = 15.sp,
                fontWeight =
                    FontWeight.Normal,
                color =
                    Color.White.copy(
                        alpha = 0.90f
                    ),
                textAlign =
                    TextAlign.Center
            )
        }
    }
}


// ------------------------------------------------------------
// MAIN / FAVORITES NAVIGATION
// ------------------------------------------------------------

@Composable
fun MainAndFavorites() {

    val pagerState =
        androidx.compose.foundation.pager.rememberPagerState(
            initialPage = 0,
            pageCount = { 2 }
        )

    val coroutineScope =
        androidx.compose.runtime.rememberCoroutineScope()

    androidx.compose.foundation.pager.HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->

        when (page) {

            0 -> {
                HomeScreen(
                    onFavorites = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                )
            }

            1 -> {
                FavoritesScreen(
                    onHome = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                )
            }
        }
    }
}


// ------------------------------------------------------------
// HOME SCREEN
// ------------------------------------------------------------

@Composable
fun HomeScreen(
    onFavorites: () -> Unit
) {

    val context = LocalContext.current

    val today = rememberCurrentDate()

    val backgroundImages =
        remember {
            getBackgroundImages(context)
        }

    if (backgroundImages.isEmpty()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    "No background images found.",
                color = Color.White
            )
        }

        return
    }

    val quoteIndex =
        Math.floorMod(
            today.toEpochDay(),
            quotes.size.toLong()
        ).toInt()

    val quote = quotes[quoteIndex]

    val backgroundIndex =
        Math.floorMod(
            today.toEpochDay(),
            backgroundImages.size.toLong()
        ).toInt()

    val backgroundImage =
        backgroundImages[backgroundIndex]

    val preferences =
        context.getSharedPreferences(
            "daily_motivation",
            Context.MODE_PRIVATE
        )

    var favoriteSet by remember {

        mutableStateOf(
            preferences.getStringSet(
                "favorites",
                emptySet()
            ) ?: emptySet()
        )
    }

    val isSaved =
        favoriteSet.contains(
            quoteIndex.toString()
        )

    Surface(
        modifier =
            Modifier.fillMaxSize(),
        color = Color.Black
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                
        ) {

            AssetImage(
                fileName = backgroundImage,
                modifier =
                    Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(
                            alpha = 0.30f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 100.dp,
                        bottom = 25.dp
                    ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "D A I L Y",
                        fontSize = 15.sp,
                        letterSpacing = 4.sp,
                        fontWeight =
                            FontWeight.Medium,
                        color = Color.White
                    )

                    Text(
                        text = "Motivation",
                        fontSize = 49.sp,
                        fontFamily =
                            FontFamily.Cursive,
                        fontWeight =
                            FontWeight.Normal,
                        color = Color.White
                    )

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .width(58.dp)
                                .height(1.dp)
                                .background(
                                    Color.White.copy(
                                        alpha = 0.75f
                                    )
                                )
                        )

                        Text(
                            text = "♡",
                            fontSize = 29.sp,
                            color = Color.White,
                            modifier =
                                Modifier.padding(
                                    horizontal = 10.dp
                                )
                        )

                        Box(
                            modifier = Modifier
                                .width(58.dp)
                                .height(1.dp)
                                .background(
                                    Color.White.copy(
                                        alpha = 0.75f
                                    )
                                )
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.Center
                ) {

                    Text(
                        text =
                            "“${quote.text}”",
                        fontSize = 23.sp,
                        lineHeight = 32.sp,
                        fontWeight =
                            FontWeight.Medium,
                        textAlign =
                            TextAlign.Center,
                        color = Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )

                    Text(
                        text =
                            "— ${quote.author.uppercase()}",
                        fontSize = 11.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight =
                            FontWeight.Medium,
                        color =
                            Color.White.copy(
                                alpha = 0.94f
                            )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 35.dp
                        ),
                    horizontalArrangement =
                        Arrangement.SpaceEvenly,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    FavoriteHeartButton(
                        isSaved = isSaved,
                        onClick = {

                            val newFavorites =
                                favoriteSet
                                    .toMutableSet()

                            if (isSaved) {

                                newFavorites.remove(
                                    quoteIndex.toString()
                                )

                            } else {

                                newFavorites.add(
                                    quoteIndex.toString()
                                )
                            }

                            preferences.edit()
                                .putStringSet(
                                    "favorites",
                                    newFavorites
                                )
                                .apply()

                            favoriteSet =
                                newFavorites
                        }
                    )

                    TextButton(
                        text = "↗",
                        modifier =
                            Modifier.weight(1f),
                        fontSize = 26.sp,
                        onClick = {

                            val shareText =
                                "Today's Daily Motivation:\n\n" +
                                        "“${quote.text}”\n\n" +
                                        "— ${quote.author}"

                            val sendIntent =
                                Intent().apply {

                                    action =
                                        Intent.ACTION_SEND

                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        shareText
                                    )

                                    type =
                                        "text/plain"
                                }

                            context.startActivity(
                                Intent.createChooser(
                                    sendIntent,
                                    "Share Motivation"
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}


// ------------------------------------------------------------
// FAVORITES SCREEN
// ------------------------------------------------------------

@Composable
fun FavoritesScreen(
    onHome: () -> Unit
) {

    val context = LocalContext.current

    val today = rememberCurrentDate()

    val backgroundImages =
        remember {
            getBackgroundImages(context)
        }

    if (backgroundImages.isEmpty()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    "No background images found.",
                color = Color.White
            )
        }

        return
    }

    val backgroundIndex =
        Math.floorMod(
            today.toEpochDay(),
            backgroundImages.size.toLong()
        ).toInt()

    val backgroundImage =
        backgroundImages[backgroundIndex]

    val preferences =
        context.getSharedPreferences(
            "daily_motivation",
            Context.MODE_PRIVATE
        )

    var favoriteSet by remember {

        mutableStateOf(
            preferences.getStringSet(
                "favorites",
                emptySet()
            ) ?: emptySet()
        )
    }

    val favoriteQuotes =
        favoriteSet.mapNotNull { indexString ->

            val index =
                indexString.toIntOrNull()

            if (
                index != null &&
                index >= 0 &&
                index < quotes.size
            ) {

                index to quotes[index]

            } else {

                null
            }
        }

    var quoteToRemove by remember {
        mutableStateOf<Int?>(null)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        AssetImage(
            fileName = backgroundImage,
            modifier =
                Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(
                        alpha = 0.30f
                    )
                )
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 100.dp,
                        bottom = 25.dp
                    )
        ) {

            // Back button intentionally removed.
            // Swipe right to return to Home.

            Text(
                text = "My Favorites",
                fontSize = 34.sp,
                fontFamily =
                    FontFamily.Cursive,
                color = Color.White,
                modifier =
                    Modifier.fillMaxWidth(),
                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )

            if (favoriteQuotes.isEmpty()) {

                Column(
                    modifier =
                        Modifier.fillMaxSize(),
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.Center
                ) {

                    Text(
                        text = "♡",
                        fontSize = 60.sp,
                        color = Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "No saved quotes yet.",
                        fontSize = 20.sp,
                        fontWeight =
                            FontWeight.Medium,
                        color = Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "Save a quote and it will appear here.",
                        fontSize = 14.sp,
                        color =
                            Color.White.copy(
                                alpha = 0.85f
                            ),
                        textAlign =
                            TextAlign.Center
                    )
                }

            } else {

                LazyColumn(
                    modifier =
                        Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.spacedBy(24.dp),
                    contentPadding =
                        PaddingValues(
                            bottom = 20.dp
                        )
                ) {

                    items(
                        favoriteQuotes
                    ) { favoriteItem ->

                        val favoriteIndex =
                            favoriteItem.first

                        val favoriteQuote =
                            favoriteItem.second

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Column(
                                modifier =
                                    Modifier.weight(1f),
                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text =
                                        "“${favoriteQuote.text}”",
                                    fontSize = 20.sp,
                                    lineHeight = 29.sp,
                                    fontWeight =
                                        FontWeight.Medium,
                                    color = Color.White,
                                    textAlign =
                                        TextAlign.Center
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(12.dp)
                                )

                                Text(
                                    text =
                                        "— ${favoriteQuote.author.uppercase()}",
                                    fontSize = 11.sp,
                                    letterSpacing = 1.5.sp,
                                    fontWeight =
                                        FontWeight.Medium,
                                    color =
                                        Color.White.copy(
                                            alpha = 0.94f
                                        )
                                )
                            }

                            TextButton(
                                text = "×",
                                modifier =
                                    Modifier.width(45.dp),
                                fontSize = 25.sp,
                                onClick = {

                                    quoteToRemove =
                                        favoriteIndex
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    quoteToRemove?.let { index ->

        AlertDialog(
            onDismissRequest = {
                quoteToRemove = null
            },
            title = {

                Text(
                    text =
                        "Remove favorite?"
                )
            },
            text = {

                Text(
                    text =
                        "Are you sure you want to remove this quote from your favorites?"
                )
            },
            confirmButton = {

                MaterialTextButton(
                    onClick = {

                        val newFavorites =
                            favoriteSet
                                .toMutableSet()

                        newFavorites.remove(
                            index.toString()
                        )

                        preferences.edit()
                            .putStringSet(
                                "favorites",
                                newFavorites
                            )
                            .apply()

                        favoriteSet =
                            newFavorites

                        quoteToRemove = null
                    }
                ) {

                    Text(
                        text = "Remove"
                    )
                }
            },
            dismissButton = {

                MaterialTextButton(
                    onClick = {

                        quoteToRemove = null
                    }
                ) {

                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }
}
