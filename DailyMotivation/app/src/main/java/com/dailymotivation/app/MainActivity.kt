
package com.dailymotivation.app

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import android.os.Bundle
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas as ComposeCanvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
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

// Short, easy-to-understand motivational quotes without religious references.
// The first eight entries stay in place to preserve existing favorite indexes.
val quotes = listOf(
    Quote("Believe you can and you're halfway there.", "Theodore Roosevelt"),
    Quote("The only way to do great work is to love what you do.", "Steve Jobs"),
    Quote("It always seems impossible until it's done.", "Nelson Mandela"),
    Quote("You are never too old to set another goal or to dream a new dream.", "C.S. Lewis"),
    Quote("Start where you are. Use what you have. Do what you can.", "Arthur Ashe"),
    Quote("Success is the sum of small efforts, repeated day in and day out.", "Robert Collier"),
    Quote("Don't watch the clock; do what it does. Keep going.", "Sam Levenson"),
    Quote("The future depends on what you do today.", "Mahatma Gandhi"),
    Quote("Nothing will work unless you do.", "Maya Angelou"),
    Quote("It always seems impossible until it is done.", "Nelson Mandela"),
    Quote("Act as if what you do makes a difference. It does.", "William James"),
    Quote("You miss 100% of the shots you don't take.", "Wayne Gretzky"),
    Quote("The secret of getting ahead is getting started.", "Mark Twain"),
    Quote("Great things are done by a series of small things brought together.", "Vincent van Gogh"),
    Quote("Well done is better than well said.", "Benjamin Franklin"),
    Quote("If you can dream it, you can do it.", "Walt Disney"),
    Quote("The best way out is always through.", "Robert Frost"),
    Quote("Do what you can, with what you have, where you are.", "Theodore Roosevelt"),
    Quote("It does not matter how slowly you go as long as you do not stop.", "Confucius"),
    Quote("Energy and persistence conquer all things.", "Benjamin Franklin"),
    Quote("Keep your face always toward the sunshine—and shadows will fall behind you.", "Walt Whitman"),
    Quote("The harder I work, the luckier I get.", "Samuel Goldwyn"),
    Quote("Make each day your masterpiece.", "John Wooden"),
    Quote("You are capable of more than you know.", "Gloria Steinem"),
    Quote("A journey of a thousand miles begins with a single step.", "Lao Tzu"),
    Quote("You don’t have to see the whole staircase, just take the first step.", "Martin Luther King Jr."),
    Quote("If there is no struggle, there is no progress.", "Frederick Douglass"),
    Quote("Dream big and dare to fail.", "Norman Vaughan"),
    Quote("Quality is not an act, it is a habit.", "Aristotle"),
    Quote("The only limit to our realization of tomorrow is our doubts of today.", "Franklin D. Roosevelt"),
    Quote("Turn your wounds into wisdom.", "Oprah Winfrey"),
    Quote("Try to be a rainbow in someone’s cloud.", "Maya Angelou"),
    Quote("Nothing is impossible; the word itself says “I’m possible!”", "Audrey Hepburn"),
    Quote("You must do the thing you think you cannot do.", "Eleanor Roosevelt"),
    Quote("Every day is a new beginning.", "Daily Motivation"),
    Quote("Small steps every day add up to big change.", "Daily Motivation"),
    Quote("You don’t need to be perfect to make progress.", "Daily Motivation"),
    Quote("A little progress is still progress.", "Daily Motivation"),
    Quote("Start before you feel ready.", "Daily Motivation"),
    Quote("You can always begin again.", "Daily Motivation"),
    Quote("Be proud of how far you have come.", "Daily Motivation"),
    Quote("Focus on what you can control.", "Daily Motivation"),
    Quote("One good choice can change your day.", "Daily Motivation"),
    Quote("Give yourself time to grow.", "Daily Motivation"),
    Quote("You are allowed to learn as you go.", "Daily Motivation"),
    Quote("Keep going, even if it is slow.", "Daily Motivation"),
    Quote("You can do hard things one step at a time.", "Daily Motivation"),
    Quote("Rest is part of making progress.", "Daily Motivation"),
    Quote("Your effort matters, even when no one sees it.", "Daily Motivation"),
    Quote("You do not have to do everything today.", "Daily Motivation"),
    Quote("Choose progress over perfection.", "Daily Motivation"),
    Quote("Be patient with yourself.", "Daily Motivation"),
    Quote("You are stronger than one bad day.", "Daily Motivation"),
    Quote("A fresh start can begin at any time.", "Daily Motivation"),
    Quote("Make room for what makes you happy.", "Daily Motivation"),
    Quote("You can change your direction at any time.", "Daily Motivation"),
    Quote("Do one thing today that your future self will thank you for.", "Daily Motivation"),
    Quote("Your pace is still a pace.", "Daily Motivation"),
    Quote("Keep the promises you make to yourself.", "Daily Motivation"),
    Quote("You deserve the same kindness you give others.", "Daily Motivation"),
    Quote("You can be a work in progress and still be proud.", "Daily Motivation"),
    Quote("Take the next small step.", "Daily Motivation"),
    Quote("You have made it through hard days before.", "Daily Motivation"),
    Quote("Let today be good enough.", "Daily Motivation"),
    Quote("You do not need permission to try again.", "Daily Motivation"),
    Quote("A kind word can make a big difference.", "Daily Motivation"),
    Quote("You can learn something from every setback.", "Daily Motivation"),
    Quote("Give yourself credit for trying.", "Daily Motivation"),
    Quote("Your best can look different every day.", "Daily Motivation"),
    Quote("Make today count in your own way.", "Daily Motivation"),
    Quote("You are not behind; you are on your own path.", "Daily Motivation"),
    Quote("One step forward is better than standing still.", "Daily Motivation"),
    Quote("You can start small and still dream big.", "Daily Motivation"),
    Quote("Let your actions speak for your goals.", "Daily Motivation"),
    Quote("There is strength in asking for help.", "Daily Motivation"),
    Quote("You do not have to have it all figured out.", "Daily Motivation"),
    Quote("Make time for the things that matter.", "Daily Motivation"),
    Quote("Your next chapter can be different.", "Daily Motivation"),
    Quote("You can be nervous and still be brave.", "Daily Motivation"),
    Quote("Keep what helps you grow. Let go of what holds you back.", "Daily Motivation"),
    Quote("You are allowed to take a break.", "Daily Motivation"),
    Quote("A better day can start with one small choice.", "Daily Motivation"),
    Quote("Do what you can today, and leave the rest for tomorrow.", "Daily Motivation"),
    Quote("Your value is not measured by how much you do.", "Daily Motivation"),
    Quote("You can make progress without rushing.", "Daily Motivation"),
    Quote("Be willing to begin again.", "Daily Motivation"),
    Quote("You have more choices than you think.", "Daily Motivation"),
    Quote("Celebrate the small wins.", "Daily Motivation"),
    Quote("You can take things one day at a time.", "Daily Motivation"),
    Quote("Make your own kind of progress.", "Daily Motivation"),
    Quote("A setback is not the end of your story.", "Daily Motivation"),
    Quote("You can grow at your own pace.", "Daily Motivation"),
    Quote("The way forward starts with one step.", "Daily Motivation"),
    Quote("You do not have to feel confident to begin.", "Daily Motivation"),
    Quote("Be gentle with yourself while you learn.", "Daily Motivation"),
    Quote("You can make today a little better.", "Daily Motivation"),
    Quote("Keep showing up for yourself.", "Daily Motivation"),
    Quote("You are allowed to change your mind and try a new way.", "Daily Motivation"),
    Quote("Even a small effort is worth making.", "Daily Motivation"),
    Quote("You can choose to begin again tomorrow.", "Daily Motivation"),
    Quote("Let your goals guide you, not scare you.", "Daily Motivation"),
    Quote("The next step does not have to be a big one.", "Daily Motivation"),
    Quote("You can make a difference in ordinary ways.", "Daily Motivation"),
    Quote("Keep moving toward what matters to you.", "Daily Motivation"),
    Quote("You are learning, even when progress feels slow.", "Daily Motivation"),
    Quote("Today is another chance to try.", "Daily Motivation"),
    Quote("Do not give up on yourself.", "Daily Motivation"),
    Quote("You can build a good life one day at a time.", "Daily Motivation"),
    Quote("Give your dreams a little time and effort.", "Daily Motivation"),
    Quote("A calm mind can take the next step.", "Daily Motivation"),
    Quote("You do not need to compare your journey with anyone else’s.", "Daily Motivation"),
    Quote("Choose one thing and start there.", "Daily Motivation"),
    Quote("You can be proud of the effort, not just the result.", "Daily Motivation"),
    Quote("Keep going; your effort is adding up.", "Daily Motivation"),
    Quote("You are allowed to take the long way.", "Daily Motivation"),
    Quote("The smallest start is better than no start.", "Daily Motivation"),
    Quote("You can make room for joy today.", "Daily Motivation"),
    Quote("Every new skill begins with practice.", "Daily Motivation"),
    Quote("Do not let one mistake define you.", "Daily Motivation"),
    Quote("You can take a deep breath and try again.", "Daily Motivation"),
    Quote("Keep your goals simple and take the next step.", "Daily Motivation"),
    Quote("You can do something meaningful with an ordinary day.", "Daily Motivation"),
    Quote("You are more than your hardest moment.", "Daily Motivation"),
    Quote("A little courage can take you a long way.", "Daily Motivation"),
    Quote("You can move forward without having every answer.", "Daily Motivation"),
    Quote("Make the next choice a helpful one.", "Daily Motivation"),
    Quote("You have the right to grow and change.", "Daily Motivation"),
    Quote("You can begin with what you have today.", "Daily Motivation"),
    Quote("Progress is built one choice at a time.", "Daily Motivation"),
    Quote("Do not overlook how far you have come.", "Daily Motivation"),
    Quote("Give yourself a chance.", "Daily Motivation"),
    Quote("Keep making room for better days.", "Daily Motivation"),
    Quote("You can be proud of trying again.", "Daily Motivation"),
    Quote("One day at a time is enough.", "Daily Motivation"),
    Quote("Your future is shaped by the steps you take today.", "Daily Motivation"),
    Quote("You can make a fresh start without changing everything.", "Daily Motivation"),
    Quote("Keep going in a way that works for you.", "Daily Motivation"),
    Quote("You do not need to do it all to do something good.", "Daily Motivation"),
    Quote("Take care of yourself as you work toward your goals.", "Daily Motivation")
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
// SHARE QUOTE IMAGE
// ------------------------------------------------------------

fun createQuoteShareImage(
    context: Context,
    backgroundImage: String,
    quote: Quote
): Uri {
    val width = 1080
    val height = 1350
    val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)

    val sourceBitmap = context.assets.open("backgrounds/$backgroundImage").use {
        BitmapFactory.decodeStream(it)
    } ?: throw IllegalStateException("Unable to load background image")

    val sourceRatio = sourceBitmap.width.toFloat() / sourceBitmap.height
    val targetRatio = width.toFloat() / height
    val sourceRect = if (sourceRatio > targetRatio) {
        val cropWidth = (sourceBitmap.height * targetRatio).toInt()
        val left = (sourceBitmap.width - cropWidth) / 2
        Rect(left, 0, left + cropWidth, sourceBitmap.height)
    } else {
        val cropHeight = (sourceBitmap.width / targetRatio).toInt()
        val top = (sourceBitmap.height - cropHeight) / 2
        Rect(0, top, sourceBitmap.width, top + cropHeight)
    }

    canvas.drawBitmap(
        sourceBitmap,
        sourceRect,
        Rect(0, 0, width, height),
        Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    )
    sourceBitmap.recycle()

    val shadePaint = Paint().apply {
        shader = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            intArrayOf(
                AndroidColor.argb(95, 0, 0, 0),
                AndroidColor.argb(125, 0, 0, 0),
                AndroidColor.argb(185, 0, 0, 0)
            ),
            floatArrayOf(0f, 0.48f, 1f),
            Shader.TileMode.CLAMP
        )
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), shadePaint)

    val white = AndroidColor.WHITE
    val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = white
        textAlign = Paint.Align.CENTER
        textSize = 35f
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }
    canvas.drawText("D A I L Y", width / 2f, 170f, brandPaint)

    val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = white
        textAlign = Paint.Align.CENTER
        textSize = 78f
        typeface = Typeface.create("cursive", Typeface.NORMAL)
        setShadowLayer(8f, 0f, 3f, AndroidColor.argb(130, 0, 0, 0))
    }
    canvas.drawText("Motivation", width / 2f, 250f, titlePaint)

    val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.argb(210, 255, 255, 255)
        strokeWidth = 2f
    }
    // Match the home screen's small heart framed by two fine lines.
    canvas.drawLine(365f, 330f, 455f, 330f, dividerPaint)
    canvas.drawLine(625f, 330f, 715f, 330f, dividerPaint)

    val heartPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = white
        textAlign = Paint.Align.CENTER
        textSize = 58f
        typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        setShadowLayer(5f, 0f, 2f, AndroidColor.argb(100, 0, 0, 0))
    }
    canvas.drawText("♡", width / 2f, 350f, heartPaint)

    val quotePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = white
        textAlign = Paint.Align.CENTER
        textSize = 49f
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        setShadowLayer(7f, 0f, 2f, AndroidColor.argb(160, 0, 0, 0))
    }
    val maxTextWidth = 850f
    val quoteLines = wrapShareText("“${quote.text}”", quotePaint, maxTextWidth)
    val lineHeight = 68f
    val quoteBlockHeight = quoteLines.size * lineHeight
    var textY = (height * 0.52f) - quoteBlockHeight / 2f + lineHeight / 2f
    quoteLines.forEach { line ->
        canvas.drawText(line, width / 2f, textY, quotePaint)
        textY += lineHeight
    }

    val authorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.argb(240, 255, 255, 255)
        textAlign = Paint.Align.CENTER
        textSize = 27f
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }
    canvas.drawText("— ${quote.author.uppercase()} —", width / 2f, textY + 58f, authorPaint)

    val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.argb(225, 255, 255, 255)
        textAlign = Paint.Align.CENTER
        textSize = 34f
        typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    }
    canvas.drawText("A little inspiration for every day.", width / 2f, 1220f, footerPaint)

    val appCreditPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.argb(200, 255, 255, 255)
        textAlign = Paint.Align.CENTER
        textSize = 28f
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }
    canvas.drawText("Shared from Daily Motivation", width / 2f, 1300f, appCreditPaint)

    val shareDirectory = java.io.File(context.cacheDir, "shared_quotes")
    if (!shareDirectory.exists()) shareDirectory.mkdirs()
    val imageFile = java.io.File(shareDirectory, "daily_motivation_quote.png")
    imageFile.outputStream().use { output ->
        outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
    }
    outputBitmap.recycle()

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

fun wrapShareText(text: String, paint: Paint, maxWidth: Float): List<String> {
    val lines = mutableListOf<String>()
    var currentLine = ""
    text.split(" ").forEach { word ->
        val candidate = if (currentLine.isEmpty()) word else "$currentLine $word"
        if (currentLine.isNotEmpty() && paint.measureText(candidate) > maxWidth) {
            lines.add(currentLine)
            currentLine = word
        } else {
            currentLine = candidate
        }
    }
    if (currentLine.isNotEmpty()) lines.add(currentLine)
    return lines
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
            .width(70.dp)
            .animateContentSize(),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

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
                    fontSize = 34.sp,
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
// SHARE ICON BUTTON
// ------------------------------------------------------------

@Composable
fun ShareIconButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(70.dp)
            .height(55.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            contentAlignment = Alignment.Center
        ) {
            ComposeCanvas(
                modifier = Modifier
                    .width(28.dp)
                    .height(28.dp)
            ) {
                val stroke = 1.8.dp.toPx()
                val radius = 3.1.dp.toPx()
                val left = Offset(size.width * 0.23f, size.height * 0.50f)
                val topRight = Offset(size.width * 0.76f, size.height * 0.22f)
                val bottomRight = Offset(size.width * 0.76f, size.height * 0.78f)

                drawLine(Color.White.copy(alpha = 0.9f), left, topRight, strokeWidth = stroke)
                drawLine(Color.White.copy(alpha = 0.9f), left, bottomRight, strokeWidth = stroke)
                drawCircle(Color.White, radius, left)
                drawCircle(Color.White, radius, topRight)
                drawCircle(Color.White, radius, bottomRight)
            }
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

        DailyReminderScheduler.cancelTest(this)
        DailyReminderScheduler.schedule(this)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }

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

    var showFavorites by remember { mutableStateOf(false) }
    var swipeDirection by remember { mutableStateOf(-1f) }

    AnimatedContent(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        targetState = showFavorites,
        transitionSpec = {
            val enteringFrom = if (swipeDirection > 0f) -1 else 1
            val exitingTo = -enteringFrom
            (androidx.compose.animation.slideInHorizontally(
                animationSpec = tween(280),
                initialOffsetX = { fullWidth -> fullWidth * enteringFrom }
            ) + fadeIn(animationSpec = tween(180))) togetherWith
                (androidx.compose.animation.slideOutHorizontally(
                    animationSpec = tween(280),
                    targetOffsetX = { fullWidth -> fullWidth * exitingTo }
                ) + fadeOut(animationSpec = tween(180)))
        },
        label = "home_favorites_page"
    ) { favoritesPage ->
        if (favoritesPage) {
            FavoritesScreen(
                onHome = { direction ->
                    swipeDirection = direction
                    showFavorites = false
                }
            )
        } else {
            HomeScreen(
                onFavorites = { direction ->
                    swipeDirection = direction
                    showFavorites = true
                }
            )
        }
    }
}


// ------------------------------------------------------------
// HOME SCREEN
// ------------------------------------------------------------

@Composable
fun HomeScreen(
    onFavorites: (Float) -> Unit
) {

    val context = LocalContext.current
    val shareScope = rememberCoroutineScope()

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

    val showFavoritesSwipeHint = remember {
        !preferences.getBoolean("has_seen_favorites_swipe_hint_v3", false)
    }

    LaunchedEffect(Unit) {
        if (showFavoritesSwipeHint) {
            preferences.edit()
                .putBoolean("has_seen_favorites_swipe_hint_v3", true)
                .apply()
        }
    }

    Surface(
        modifier =
            Modifier.fillMaxSize(),
        color = Color.Black
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(onFavorites) {
                    var horizontalDrag = 0f
                    detectHorizontalDragGestures(
                        onDragStart = {
                            horizontalDrag = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            horizontalDrag += dragAmount
                        },
                        onDragEnd = {
                            if (kotlin.math.abs(horizontalDrag) > 80f) {
                                onFavorites(if (horizontalDrag > 0f) 1f else -1f)
                            }
                            horizontalDrag = 0f
                        },
                        onDragCancel = {
                            horizontalDrag = 0f
                        }
                    )
                }
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

                if (showFavoritesSwipeHint) {
                    Text(
                        text = "Swipe right to see your favorites  →",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 12.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            bottom = 38.dp
                        ),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
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

                    ShareIconButton(
                        onClick = {
                            shareScope.launch {
                                val shareUri = withContext(Dispatchers.IO) {
                                    try {
                                        createQuoteShareImage(
                                            context = context,
                                            backgroundImage = backgroundImage,
                                            quote = quote
                                        )
                                    } catch (e: Exception) {
                                        null
                                    }
                                }

                                if (shareUri != null) {
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "image/png"
                                        putExtra(Intent.EXTRA_STREAM, shareUri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        clipData = android.content.ClipData.newUri(
                                            context.contentResolver,
                                            "Daily Motivation quote",
                                            shareUri
                                        )
                                    }
                                    context.startActivity(
                                        Intent.createChooser(sendIntent, "Share Quote Card")
                                    )
                                }
                            }
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
    onHome: (Float) -> Unit
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
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(onHome) {
                var horizontalDrag = 0f
                detectHorizontalDragGestures(
                    onDragStart = { horizontalDrag = 0f },
                    onHorizontalDrag = { _, dragAmount -> horizontalDrag += dragAmount },
                    onDragEnd = {
                        if (kotlin.math.abs(horizontalDrag) > 80f) {
                            onHome(if (horizontalDrag > 0f) 1f else -1f)
                        }
                        horizontalDrag = 0f
                    },
                    onDragCancel = { horizontalDrag = 0f }
                )
            }
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
                fontSize = 49.sp,
                fontFamily =
                    FontFamily.Cursive,
                fontWeight =
                    FontWeight.Normal,
                color = Color.White,
                modifier =
                    Modifier.fillMaxWidth(),
                textAlign =
                    TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(58.dp)
                        .height(1.dp)
                        .background(
                            Color.White.copy(alpha = 0.75f)
                        )
                )

                Text(
                    text = "♡",
                    fontSize = 29.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Box(
                    modifier = Modifier
                        .width(58.dp)
                        .height(1.dp)
                        .background(
                            Color.White.copy(alpha = 0.75f)
                        )
                )
            }

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
