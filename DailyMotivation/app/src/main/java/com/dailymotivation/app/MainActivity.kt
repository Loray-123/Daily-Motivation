
package com.dailymotivation.app

import com.brightdays.dailymotivation.R

import android.content.Context
import android.app.TimePickerDialog
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.style.TextDirection
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
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter


// ------------------------------------------------------------
// QUOTES
// ------------------------------------------------------------

fun categorizeQuote(text: String): String {
    val words = text.lowercase()
    return when {
        listOf("yourself", "self-worth", "self worth", "self-love", "self love", "rest", "forgive yourself", "be gentle", "your needs", "you deserve", "accept yourself", "own pace", "own path", "imperfect", "kind to yourself").any { it in words } ->
            "Self-Love"
        listOf("believe", "capable", "confidence", "courage", "worthy", "worth", "strength", "brave", "fear", "voice", "stand tall", "trust yourself", "you are enough", "your value", "potential", "proud", "doubt").any { it in words } ->
            "Confidence and Self-Esteem"
        listOf("hard day", "hard times", "setback", "struggle", "challenge", "obstacle", "storm", "fall", "failure", "mistake", "begin again", "keep going", "get back up", "through", "healing", "change", "uncertain", "difficult", "pain", "resilient").any { it in words } ->
            "Overcoming Challenges"
        listOf("happy", "happiness", "joy", "smile", "gratitude", "grateful", "positive", "sunshine", "beautiful", "wonder", "laugh", "peace", "hope", "kindness", "good day", "little things", "light", "love", "present moment").any { it in words } ->
            "Happiness and Positivity"
        else -> "Motivation and Productivity"
    }
}

data class Quote(
    val text: String,
    val author: String,
    val category: String,
    val id: String
)

// Short, easy-to-understand motivational quotes without religious references.
// Each quote has a permanent ID. Keep its ID with the quote if the list is reordered.
val quotes = listOf(
    Quote("A routine becomes stronger when it fits the life you actually live. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_001"),
    Quote("Every new day gives you room to grow beyond yesterday's challenges. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_002"),
    Quote("Healthy relationships leave room for care, honesty, and healthy space. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_003"),
    Quote("Every small step forward is part of your progress. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_004"),
    Quote("Take the lessons that help you grow and let your confidence stay your own. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_005"),
    Quote("Self-respect means learning from your choices with patience and care. Your peace grows when you give your time to what matters most.", "Unknown", "Self-Love", id = "quote_006"),
    Quote("Every ending can make room for a beginning that fits who you are becoming. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_007"),
    Quote("Every choice can teach you something valuable for the road ahead. You deserve kindness, care, and joy every single day.", "Unknown", "Self-Love", id = "quote_008"),
    Quote("A short focused effort can break the spell of postponing. Put your energy into the meaningful steps you can take today.", "Unknown", "Motivation and Productivity", id = "quote_009"),
    Quote("New possibilities often begin just beyond what feels familiar. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_010"),
    Quote("There is room in your life for progress, gratitude, and happiness right now. Peace grows when you give your attention to the present moment.", "Unknown", "Happiness and Positivity", id = "quote_011"),
    Quote("Every new beginning is a chance to discover what you can do. Trust your values as you make choices that feel right for you.", "Unknown", "Confidence and Self-Esteem", id = "quote_012"),
    Quote("A short focused effort can break the spell of postponing. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_013"),
    Quote("New possibilities often begin just beyond what feels familiar. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_014"),
    Quote("Rest is a meaningful part of caring for yourself and growing well. You deserve kindness, care, and joy every single day.", "Unknown", "Self-Love", id = "quote_015"),
    Quote("A little action today can create the inspiration for tomorrow. Progress grows when your goals make room for balance and consistency.", "Unknown", "Motivation and Productivity", id = "quote_016"),
    Quote("Every new day gives you room to grow beyond yesterday's challenges. Give yourself a moment to notice the possibilities around you.", "Unknown", "Overcoming Challenges", id = "quote_017"),
    Quote("Hope grows through the small possibilities you choose to nurture. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_018"),
    Quote("A task often becomes less intimidating when you define its first step. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_019"),
    Quote("Ordinary moments often become meaningful only after we pay attention. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_020"),
    Quote("Every step you take through a challenge adds to your strength. The strength you build through each season becomes part of your story.", "Unknown", "Overcoming Challenges", id = "quote_021"),
    Quote("Take the lessons that help you grow and let your confidence stay your own. A willingness to learn is one of the surest ways to keep growing.", "Unknown", "Confidence and Self-Esteem", id = "quote_022"),
    Quote("You can make room for honest feelings while still welcoming moments of joy. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_023"),
    Quote("A routine becomes stronger when it fits the life you actually live. Each time you return to a habit, you strengthen the path forward.", "Unknown", "Motivation and Productivity", id = "quote_024"),
    Quote("Every fresh start carries the wisdom of everything you've learned. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_025"),
    Quote("A task often becomes less intimidating when you define its first step. Put your energy into the meaningful steps you can take today.", "Unknown", "Motivation and Productivity", id = "quote_026"),
    Quote("A new route can lead you toward the same meaningful destination. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_027"),
    Quote("Gratitude helps you notice the good that is already part of your day. Peace grows when you give your attention to the present moment.", "Unknown", "Happiness and Positivity", id = "quote_028"),
    Quote("A short focused effort can break the spell of postponing. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_029"),
    Quote("Your quiet confidence is just as powerful as the loudest voice in the room. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_030"),
    Quote("Rest is a gift you can give yourself as part of a balanced life. You deserve kindness, care, and joy every single day.", "Unknown", "Self-Love", id = "quote_031"),
    Quote("Quiet progress is still meaningful progress, even when no one else sees it. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_032"),
    Quote("Your worth is steady, and it does not depend on someone else's choice. Let each action remind you of what is possible when you believe in yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_033"),
    Quote("A short focused effort can break the spell of postponing. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_034"),
    Quote("You belong in every room where your hopes and talents take you. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_035"),
    Quote("Every choice can teach you something valuable for the road ahead. Every day brings another opportunity to grow with patience and compassion.", "Unknown", "Self-Love", id = "quote_036"),
    Quote("Gratitude helps you notice the good that is already part of your day. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_037"),
    Quote("New possibilities often begin just beyond what feels familiar. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_038"),
    Quote("Give your goals a place in your day, and watch your plans take shape. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_039"),
    Quote("Rest is a gift you can give yourself as part of a balanced life. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_040"),
    Quote("A fresh perspective can turn a challenge into a new opportunity. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_041"),
    Quote("A small comfort is still worth noticing. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_042"),
    Quote("Every small step forward is part of your progress. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_043"),
    Quote("Some peace comes from letting a moment be enough. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_044"),
    Quote("Preparation gives you confidence; taking the first step turns it into progress. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_045"),
    Quote("Your voice matters, and your perspective deserves to be heard. Let each action remind you of what is possible when you believe in yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_046"),
    Quote("There is room in your life for progress, gratitude, and happiness right now. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_047"),
    Quote("Every step you take through a challenge adds to your strength. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_048"),
    Quote("You can choose what supports your growth and make room for what matters. You deserve kindness, care, and joy every single day.", "Unknown", "Self-Love", id = "quote_049"),
    Quote("Every ending can make room for a beginning that fits who you are becoming. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_050"),
    Quote("Your own values and experience are important guides for your decisions. Trust your values as you make choices that feel right for you.", "Unknown", "Confidence and Self-Esteem", id = "quote_051"),
    Quote("A little action today can create the inspiration for tomorrow. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_052"),
    Quote("Every step you take through a challenge adds to your strength. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_053"),
    Quote("Your body deserves appreciation for all the ways it carries you through life. Your peace grows when you give your time to what matters most.", "Unknown", "Self-Love", id = "quote_054"),
    Quote("A small comfort is still worth noticing. Even an ordinary day can hold a moment worth treasuring.", "Unknown", "Happiness and Positivity", id = "quote_055"),
    Quote("Every fresh start carries the wisdom of everything you've learned. The strength you build through each season becomes part of your story.", "Unknown", "Overcoming Challenges", id = "quote_056"),
    Quote("Choose your priorities with care, and give each moment the attention it deserves. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_057"),
    Quote("You belong in every room where your hopes and talents take you. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_058"),
    Quote("You can choose what supports your growth and make room for what matters. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_059"),
    Quote("Joy can exist alongside worry without canceling it out. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_060"),
    Quote("A thoughtful pause can help you welcome the next step with clarity. The strength you build through each season becomes part of your story.", "Unknown", "Overcoming Challenges", id = "quote_061"),
    Quote("A routine becomes stronger when it fits the life you actually live. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_062"),
    Quote("A small comfort is still worth noticing. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_063"),
    Quote("Your worth is steady, and it does not depend on someone else's choice. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_064"),
    Quote("Your needs still matter when other people have needs too. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_065"),
    Quote("Joy can exist alongside worry without canceling it out. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_066"),
    Quote("Every ending can make room for a beginning that fits who you are becoming. The strength you build through each season becomes part of your story.", "Unknown", "Overcoming Challenges", id = "quote_067"),
    Quote("Ordinary moments often become meaningful only after we pay attention. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_068"),
    Quote("Rest is a gift you can give yourself as part of a balanced life. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_069"),
    Quote("A little action today can create the inspiration for tomorrow. Put your energy into the meaningful steps you can take today.", "Unknown", "Motivation and Productivity", id = "quote_070"),
    Quote("Healthy relationships leave room for care, honesty, and healthy space. You deserve kindness, care, and joy every single day.", "Unknown", "Self-Love", id = "quote_071"),
    Quote("You can make room for honest feelings while still welcoming moments of joy. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_072"),
    Quote("Your quiet confidence is just as powerful as the loudest voice in the room. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_073"),
    Quote("You do not need to make your pain useful before it can heal. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_074"),
    Quote("Consistency is easier when the next step is clear. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_075"),
    Quote("Every good day matters, whether memorable or quietly meaningful. Even an ordinary day can hold a moment worth treasuring.", "Unknown", "Happiness and Positivity", id = "quote_076"),
    Quote("Every step you take through a challenge adds to your strength. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_077"),
    Quote("A little action today can create the inspiration for tomorrow. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_078"),
    Quote("Your needs still matter when other people have needs too. Every day brings another opportunity to grow with patience and compassion.", "Unknown", "Self-Love", id = "quote_079"),
    Quote("Joy can exist alongside worry without canceling it out. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_080"),
    Quote("Consistency is easier when the next step is clear. Each time you return to a habit, you strengthen the path forward.", "Unknown", "Motivation and Productivity", id = "quote_081"),
    Quote("Your quiet confidence is just as powerful as the loudest voice in the room. Let each action remind you of what is possible when you believe in yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_082"),
    Quote("Every fresh start carries the wisdom of everything you've learned. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_083"),
    Quote("A little action today can create the inspiration for tomorrow. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_084"),
    Quote("Some peace comes from letting a moment be enough. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_085"),
    Quote("Consistency is easier when the next step is clear. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_086"),
    Quote("Your body deserves appreciation for all the ways it carries you through life. Every day brings another opportunity to grow with patience and compassion.", "Unknown", "Self-Love", id = "quote_087"),
    Quote("Understanding often grows with time; let each experience teach you at its own pace. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_088"),
    Quote("Joy can exist alongside worry without canceling it out. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_089"),
    Quote("New possibilities often begin just beyond what feels familiar. The strength you build through each season becomes part of your story.", "Unknown", "Overcoming Challenges", id = "quote_090"),
    Quote("Self-respect means learning from your choices with patience and care. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_091"),
    Quote("You can make room for honest feelings while still welcoming moments of joy. A gentle moment can shift the tone of your day and make room for joy.", "Unknown", "Happiness and Positivity", id = "quote_092"),
    Quote("Rest is a meaningful part of caring for yourself and growing well. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_093"),
    Quote("Your worth is steady, and it does not depend on someone else's choice. Trust your values as you make choices that feel right for you.", "Unknown", "Confidence and Self-Esteem", id = "quote_094"),
    Quote("Some peace comes from letting a moment be enough. Even an ordinary day can hold a moment worth treasuring.", "Unknown", "Happiness and Positivity", id = "quote_095"),
    Quote("Your worth is steady, and it does not depend on someone else's choice. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_096"),
    Quote("A short focused effort can break the spell of postponing. Each time you return to a habit, you strengthen the path forward.", "Unknown", "Motivation and Productivity", id = "quote_097"),
    Quote("Every good day matters, whether memorable or quietly meaningful. A gentle moment can shift the tone of your day and make room for joy.", "Unknown", "Happiness and Positivity", id = "quote_098"),
    Quote("Listening to your feelings can help you discover what you need. Every day brings another opportunity to grow with patience and compassion.", "Unknown", "Self-Love", id = "quote_099"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_100"),
    Quote("A new route can lead you toward the same meaningful destination. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_101"),
    Quote("Your body deserves appreciation for all the ways it carries you through life. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_102"),
    Quote("Joy belongs in your everyday life, not just at the end of a long to-do list. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_103"),
    Quote("You can trust yourself to make thoughtful choices, even as you keep learning. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_104"),
    Quote("There is room in your life for progress, gratitude, and happiness right now. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_105"),
    Quote("Listening to your feelings can help you discover what you need. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_106"),
    Quote("Joy belongs in your everyday life, not just at the end of a long to-do list. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_107"),
    Quote("You can stay true to yourself while allowing others their own perspective. Trust your values as you make choices that feel right for you.", "Unknown", "Confidence and Self-Esteem", id = "quote_108"),
    Quote("New possibilities often begin just beyond what feels familiar. Let what you have learned guide your next step with confidence and clarity.", "Unknown", "Overcoming Challenges", id = "quote_109"),
    Quote("Quiet progress is still meaningful progress, even when no one else sees it. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_110"),
    Quote("Every new day gives you room to grow beyond yesterday's challenges. The strength you build through each season becomes part of your story.", "Unknown", "Overcoming Challenges", id = "quote_111"),
    Quote("Healthy relationships leave room for care, honesty, and healthy space. Every day brings another opportunity to grow with patience and compassion.", "Unknown", "Self-Love", id = "quote_112"),
    Quote("A thoughtful pause can help you welcome the next step with clarity. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_113"),
    Quote("Joy can exist alongside worry without canceling it out. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_114"),
    Quote("Preparation gives you confidence; taking the first step turns it into progress. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_115"),
    Quote("Some peace comes from letting a moment be enough. A gentle moment can shift the tone of your day and make room for joy.", "Unknown", "Happiness and Positivity", id = "quote_116"),
    Quote("Your own values and experience are important guides for your decisions. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_117"),
    Quote("A task often becomes less intimidating when you define its first step. Progress grows when your goals make room for balance and consistency.", "Unknown", "Motivation and Productivity", id = "quote_118"),
    Quote("Joy belongs in your everyday life, not just at the end of a long to-do list. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_119"),
    Quote("A fresh perspective can turn a challenge into a new opportunity. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_120"),
    Quote("A short focused effort can break the spell of postponing. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_121"),
    Quote("Healthy relationships leave room for care, honesty, and healthy space. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_122"),
    Quote("Every fresh start carries the wisdom of everything you've learned. Let what you have learned guide your next step with confidence and clarity.", "Unknown", "Overcoming Challenges", id = "quote_123"),
    Quote("Listening to your feelings can help you discover what you need. Your peace grows when you give your time to what matters most.", "Unknown", "Self-Love", id = "quote_124"),
    Quote("You can trust yourself to make thoughtful choices, even as you keep learning. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_125"),
    Quote("Ordinary moments often become meaningful only after we pay attention. Peace grows when you give your attention to the present moment.", "Unknown", "Happiness and Positivity", id = "quote_126"),
    Quote("Give your goals a place in your day, and watch your plans take shape. Put your energy into the meaningful steps you can take today.", "Unknown", "Motivation and Productivity", id = "quote_127"),
    Quote("Gratitude helps you notice the good that is already part of your day. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_128"),
    Quote("You can trust yourself to make thoughtful choices, even as you keep learning. A willingness to learn is one of the surest ways to keep growing.", "Unknown", "Confidence and Self-Esteem", id = "quote_129"),
    Quote("A small comfort is still worth noticing. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_130"),
    Quote("Give your goals a place in your day, and watch your plans take shape. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_131"),
    Quote("Your voice matters, and your perspective deserves to be heard. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_132"),
    Quote("Every ending can make room for a beginning that fits who you are becoming. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_133"),
    Quote("Every choice can teach you something valuable for the road ahead. Your kindness is strongest when it is balanced with care for yourself.", "Unknown", "Self-Love", id = "quote_134"),
    Quote("Understanding often grows with time; let each experience teach you at its own pace. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_135"),
    Quote("Consistency is easier when the next step is clear. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_136"),
    Quote("You can stay true to yourself while allowing others their own perspective. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_137"),
    Quote("Preparation gives you confidence; taking the first step turns it into progress. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_138"),
    Quote("Take the lessons that help you grow and let your confidence stay your own. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_139"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_140"),
    Quote("Every good day matters, whether memorable or quietly meaningful. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_141"),
    Quote("A fresh perspective can turn a challenge into a new opportunity. Let what you have learned guide your next step with confidence and clarity.", "Unknown", "Overcoming Challenges", id = "quote_142"),
    Quote("You can trust yourself to make thoughtful choices, even as you keep learning. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_143"),
    Quote("Every step you take through a challenge adds to your strength. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_144"),
    Quote("Your quiet confidence is just as powerful as the loudest voice in the room. Trust your values as you make choices that feel right for you.", "Unknown", "Confidence and Self-Esteem", id = "quote_145"),
    Quote("Hope grows through the small possibilities you choose to nurture. Even an ordinary day can hold a moment worth treasuring.", "Unknown", "Happiness and Positivity", id = "quote_146"),
    Quote("Preparation gives you confidence; taking the first step turns it into progress. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_147"),
    Quote("You do not need to make your pain useful before it can heal. Your kindness is strongest when it is balanced with care for yourself.", "Unknown", "Self-Love", id = "quote_148"),
    Quote("You can trust yourself to make thoughtful choices, even as you keep learning. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_149"),
    Quote("A task often becomes less intimidating when you define its first step. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_150"),
    Quote("A thoughtful pause can help you welcome the next step with clarity. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_151"),
    Quote("You can make room for honest feelings while still welcoming moments of joy. Even an ordinary day can hold a moment worth treasuring.", "Unknown", "Happiness and Positivity", id = "quote_152"),
    Quote("Every small step forward is part of your progress. Give yourself a moment to notice the possibilities around you.", "Unknown", "Overcoming Challenges", id = "quote_153"),
    Quote("You can stay true to yourself while allowing others their own perspective. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_154"),
    Quote("Rest is a meaningful part of caring for yourself and growing well. Your kindness is strongest when it is balanced with care for yourself.", "Unknown", "Self-Love", id = "quote_155"),
    Quote("A fresh perspective can turn a challenge into a new opportunity. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_156"),
    Quote("Your needs still matter when other people have needs too. Your peace grows when you give your time to what matters most.", "Unknown", "Self-Love", id = "quote_157"),
    Quote("Every small step forward is part of your progress. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_158"),
    Quote("A routine becomes stronger when it fits the life you actually live. Progress grows when your goals make room for balance and consistency.", "Unknown", "Motivation and Productivity", id = "quote_159"),
    Quote("Every small step forward is part of your progress. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_160"),
    Quote("Every new beginning is a chance to discover what you can do. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_161"),
    Quote("Joy belongs in your everyday life, not just at the end of a long to-do list. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_162"),
    Quote("Every ending can make room for a beginning that fits who you are becoming. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_163"),
    Quote("You can stay true to yourself while allowing others their own perspective. Every accomplishment matters, whether it is celebrated by many or known only to you.", "Unknown", "Confidence and Self-Esteem", id = "quote_164"),
    Quote("Your body deserves appreciation for all the ways it carries you through life. You deserve kindness, care, and joy every single day.", "Unknown", "Self-Love", id = "quote_165"),
    Quote("Preparation gives you confidence; taking the first step turns it into progress. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_166"),
    Quote("Every mistake offers a chance to learn, adjust, and grow. Let each action remind you of what is possible when you believe in yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_167"),
    Quote("Gratitude helps you notice the good that is already part of your day. A gentle moment can shift the tone of your day and make room for joy.", "Unknown", "Happiness and Positivity", id = "quote_168"),
    Quote("A short focused effort can break the spell of postponing. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_169"),
    Quote("Your body deserves appreciation for all the ways it carries you through life. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_170"),
    Quote("Take the lessons that help you grow and let your confidence stay your own. Trust your values as you make choices that feel right for you.", "Unknown", "Confidence and Self-Esteem", id = "quote_171"),
    Quote("Every new day gives you room to grow beyond yesterday's challenges. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_172"),
    Quote("Every mistake offers a chance to learn, adjust, and grow. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_173"),
    Quote("A new route can lead you toward the same meaningful destination. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_174"),
    Quote("Every mistake offers a chance to learn, adjust, and grow. A willingness to learn is one of the surest ways to keep growing.", "Unknown", "Confidence and Self-Esteem", id = "quote_175"),
    Quote("You do not need to make your pain useful before it can heal. Every day brings another opportunity to grow with patience and compassion.", "Unknown", "Self-Love", id = "quote_176"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_177"),
    Quote("Every mistake offers a chance to learn, adjust, and grow. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_178"),
    Quote("Listening to your feelings can help you discover what you need. You deserve kindness, care, and joy every single day.", "Unknown", "Self-Love", id = "quote_179"),
    Quote("Choose your priorities with care, and give each moment the attention it deserves. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_180"),
    Quote("Every choice can teach you something valuable for the road ahead. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_181"),
    Quote("Every new day gives you room to grow beyond yesterday's challenges. Let what you have learned guide your next step with confidence and clarity.", "Unknown", "Overcoming Challenges", id = "quote_182"),
    Quote("Your voice matters, and your perspective deserves to be heard. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_183"),
    Quote("Self-respect means learning from your choices with patience and care. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_184"),
    Quote("Some peace comes from letting a moment be enough. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_185"),
    Quote("You can stay true to yourself while allowing others their own perspective. A willingness to learn is one of the surest ways to keep growing.", "Unknown", "Confidence and Self-Esteem", id = "quote_186"),
    Quote("You can choose what supports your growth and make room for what matters. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_187"),
    Quote("Every small step forward is part of your progress. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_188"),
    Quote("Every good day matters, whether memorable or quietly meaningful. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_189"),
    Quote("Take the lessons that help you grow and let your confidence stay your own. Every accomplishment matters, whether it is celebrated by many or known only to you.", "Unknown", "Confidence and Self-Esteem", id = "quote_190"),
    Quote("Hope grows through the small possibilities you choose to nurture. Peace grows when you give your attention to the present moment.", "Unknown", "Happiness and Positivity", id = "quote_191"),
    Quote("Understanding often grows with time; let each experience teach you at its own pace. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_192"),
    Quote("Take the lessons that help you grow and let your confidence stay your own. Each brave beginning gives your confidence another reason to grow.", "Unknown", "Confidence and Self-Esteem", id = "quote_193"),
    Quote("Joy can exist alongside worry without canceling it out. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_194"),
    Quote("Every fresh start carries the wisdom of everything you've learned. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_195"),
    Quote("You can make room for honest feelings while still welcoming moments of joy. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_196"),
    Quote("A fresh perspective can turn a challenge into a new opportunity. Give yourself a moment to notice the possibilities around you.", "Unknown", "Overcoming Challenges", id = "quote_197"),
    Quote("A little action today can create the inspiration for tomorrow. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_198"),
    Quote("A small comfort is still worth noticing. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_199"),
    Quote("You can stay true to yourself while allowing others their own perspective. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_200"),
    Quote("You can make room for honest feelings while still welcoming moments of joy. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_201"),
    Quote("Your own values and experience are important guides for your decisions. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_202"),
    Quote("Quiet progress is still meaningful progress, even when no one else sees it. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_203"),
    Quote("Every new day gives you room to grow beyond yesterday's challenges. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_204"),
    Quote("Hope grows through the small possibilities you choose to nurture. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_205"),
    Quote("Your own values and experience are important guides for your decisions. Each brave beginning gives your confidence another reason to grow.", "Unknown", "Confidence and Self-Esteem", id = "quote_206"),
    Quote("A short focused effort can break the spell of postponing. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_207"),
    Quote("You belong in every room where your hopes and talents take you. Let each action remind you of what is possible when you believe in yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_208"),
    Quote("You can choose what supports your growth and make room for what matters. One day never tells the whole story of a life filled with possibility.", "Unknown", "Self-Love", id = "quote_209"),
    Quote("Every ending can make room for a beginning that fits who you are becoming. Give yourself a moment to notice the possibilities around you.", "Unknown", "Overcoming Challenges", id = "quote_210"),
    Quote("Choose your priorities with care, and give each moment the attention it deserves. Each time you return to a habit, you strengthen the path forward.", "Unknown", "Motivation and Productivity", id = "quote_211"),
    Quote("Understanding often grows with time; let each experience teach you at its own pace. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_212"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Put your energy into the meaningful steps you can take today.", "Unknown", "Motivation and Productivity", id = "quote_213"),
    Quote("There is room in your life for progress, gratitude, and happiness right now. Even an ordinary day can hold a moment worth treasuring.", "Unknown", "Happiness and Positivity", id = "quote_214"),
    Quote("Every new beginning is a chance to discover what you can do. Each brave beginning gives your confidence another reason to grow.", "Unknown", "Confidence and Self-Esteem", id = "quote_215"),
    Quote("Rest is a gift you can give yourself as part of a balanced life. Every day brings another opportunity to grow with patience and compassion.", "Unknown", "Self-Love", id = "quote_216"),
    Quote("A new route can lead you toward the same meaningful destination. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_217"),
    Quote("Gratitude helps you notice the good that is already part of your day. Even an ordinary day can hold a moment worth treasuring.", "Unknown", "Happiness and Positivity", id = "quote_218"),
    Quote("Listening to your feelings can help you discover what you need. Your kindness is strongest when it is balanced with care for yourself.", "Unknown", "Self-Love", id = "quote_219"),
    Quote("Gratitude helps you notice the good that is already part of your day. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_220"),
    Quote("You can trust yourself to make thoughtful choices, even as you keep learning. Trust your values as you make choices that feel right for you.", "Unknown", "Confidence and Self-Esteem", id = "quote_221"),
    Quote("A routine becomes stronger when it fits the life you actually live. Put your energy into the meaningful steps you can take today.", "Unknown", "Motivation and Productivity", id = "quote_222"),
    Quote("Every good day matters, whether memorable or quietly meaningful. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_223"),
    Quote("Rest is a gift you can give yourself as part of a balanced life. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_224"),
    Quote("There is room in your life for progress, gratitude, and happiness right now. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_225"),
    Quote("Every small step forward is part of your progress. The strength you build through each season becomes part of your story.", "Unknown", "Overcoming Challenges", id = "quote_226"),
    Quote("You can stay true to yourself while allowing others their own perspective. Each brave beginning gives your confidence another reason to grow.", "Unknown", "Confidence and Self-Esteem", id = "quote_227"),
    Quote("A routine becomes stronger when it fits the life you actually live. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_228"),
    Quote("Every new beginning is a chance to discover what you can do. Every accomplishment matters, whether it is celebrated by many or known only to you.", "Unknown", "Confidence and Self-Esteem", id = "quote_229"),
    Quote("A small comfort is still worth noticing. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_230"),
    Quote("A short focused effort can break the spell of postponing. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_231"),
    Quote("You belong in every room where your hopes and talents take you. Every accomplishment matters, whether it is celebrated by many or known only to you.", "Unknown", "Confidence and Self-Esteem", id = "quote_232"),
    Quote("Give your goals a place in your day, and watch your plans take shape. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_233"),
    Quote("Every new beginning is a chance to discover what you can do. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_234"),
    Quote("Choose your priorities with care, and give each moment the attention it deserves. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_235"),
    Quote("New possibilities often begin just beyond what feels familiar. Give yourself a moment to notice the possibilities around you.", "Unknown", "Overcoming Challenges", id = "quote_236"),
    Quote("You can trust yourself to make thoughtful choices, even as you keep learning. Every accomplishment matters, whether it is celebrated by many or known only to you.", "Unknown", "Confidence and Self-Esteem", id = "quote_237"),
    Quote("Joy can exist alongside worry without canceling it out. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_238"),
    Quote("Take the lessons that help you grow and let your confidence stay your own. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_239"),
    Quote("Listening to your feelings can help you discover what you need. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_240"),
    Quote("A task often becomes less intimidating when you define its first step. Each time you return to a habit, you strengthen the path forward.", "Unknown", "Motivation and Productivity", id = "quote_241"),
    Quote("Healthy relationships leave room for care, honesty, and healthy space. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_242"),
    Quote("A task often becomes less intimidating when you define its first step. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_243"),
    Quote("A thoughtful pause can help you welcome the next step with clarity. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_244"),
    Quote("Joy belongs in your everyday life, not just at the end of a long to-do list. Even an ordinary day can hold a moment worth treasuring.", "Unknown", "Happiness and Positivity", id = "quote_245"),
    Quote("A thoughtful pause can help you welcome the next step with clarity. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_246"),
    Quote("Every good day matters, whether memorable or quietly meaningful. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_247"),
    Quote("Your body deserves appreciation for all the ways it carries you through life. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_248"),
    Quote("Every new beginning is a chance to discover what you can do. A willingness to learn is one of the surest ways to keep growing.", "Unknown", "Confidence and Self-Esteem", id = "quote_249"),
    Quote("Every new day gives you room to grow beyond yesterday's challenges. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_250"),
    Quote("Joy belongs in your everyday life, not just at the end of a long to-do list. A gentle moment can shift the tone of your day and make room for joy.", "Unknown", "Happiness and Positivity", id = "quote_251"),
    Quote("Your quiet confidence is just as powerful as the loudest voice in the room. A willingness to learn is one of the surest ways to keep growing.", "Unknown", "Confidence and Self-Esteem", id = "quote_252"),
    Quote("A task often becomes less intimidating when you define its first step. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_253"),
    Quote("Understanding often grows with time; let each experience teach you at its own pace. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_254"),
    Quote("Listening to your feelings can help you discover what you need. One day never tells the whole story of a life filled with possibility.", "Unknown", "Self-Love", id = "quote_255"),
    Quote("Give your goals a place in your day, and watch your plans take shape. Progress grows when your goals make room for balance and consistency.", "Unknown", "Motivation and Productivity", id = "quote_256"),
    Quote("Every ending can make room for a beginning that fits who you are becoming. Let what you have learned guide your next step with confidence and clarity.", "Unknown", "Overcoming Challenges", id = "quote_257"),
    Quote("Give your goals a place in your day, and watch your plans take shape. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_258"),
    Quote("You belong in every room where your hopes and talents take you. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_259"),
    Quote("Rest is a gift you can give yourself as part of a balanced life. One day never tells the whole story of a life filled with possibility.", "Unknown", "Self-Love", id = "quote_260"),
    Quote("Every new beginning is a chance to discover what you can do. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_261"),
    Quote("Consistency is easier when the next step is clear. Progress grows when your goals make room for balance and consistency.", "Unknown", "Motivation and Productivity", id = "quote_262"),
    Quote("Every good day matters, whether memorable or quietly meaningful. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_263"),
    Quote("Healthy relationships leave room for care, honesty, and healthy space. Your peace grows when you give your time to what matters most.", "Unknown", "Self-Love", id = "quote_264"),
    Quote("Your worth is steady, and it does not depend on someone else's choice. Each brave beginning gives your confidence another reason to grow.", "Unknown", "Confidence and Self-Esteem", id = "quote_265"),
    Quote("Every new day gives you room to grow beyond yesterday's challenges. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_266"),
    Quote("Your voice matters, and your perspective deserves to be heard. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_267"),
    Quote("You can make room for honest feelings while still welcoming moments of joy. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_268"),
    Quote("A thoughtful pause can help you welcome the next step with clarity. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_269"),
    Quote("Ordinary moments often become meaningful only after we pay attention. A gentle moment can shift the tone of your day and make room for joy.", "Unknown", "Happiness and Positivity", id = "quote_270"),
    Quote("Every fresh start carries the wisdom of everything you've learned. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_271"),
    Quote("Joy belongs in your everyday life, not just at the end of a long to-do list. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_272"),
    Quote("Every mistake offers a chance to learn, adjust, and grow. Every accomplishment matters, whether it is celebrated by many or known only to you.", "Unknown", "Confidence and Self-Esteem", id = "quote_273"),
    Quote("Preparation gives you confidence; taking the first step turns it into progress. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_274"),
    Quote("A new route can lead you toward the same meaningful destination. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_275"),
    Quote("You can choose what supports your growth and make room for what matters. Every day brings another opportunity to grow with patience and compassion.", "Unknown", "Self-Love", id = "quote_276"),
    Quote("A routine becomes stronger when it fits the life you actually live. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_277"),
    Quote("Every step you take through a challenge adds to your strength. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_278"),
    Quote("Ordinary moments often become meaningful only after we pay attention. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_279"),
    Quote("Your voice matters, and your perspective deserves to be heard. Trust your values as you make choices that feel right for you.", "Unknown", "Confidence and Self-Esteem", id = "quote_280"),
    Quote("Your body deserves appreciation for all the ways it carries you through life. One day never tells the whole story of a life filled with possibility.", "Unknown", "Self-Love", id = "quote_281"),
    Quote("Quiet progress is still meaningful progress, even when no one else sees it. Each time you return to a habit, you strengthen the path forward.", "Unknown", "Motivation and Productivity", id = "quote_282"),
    Quote("You do not need to make your pain useful before it can heal. You deserve kindness, care, and joy every single day.", "Unknown", "Self-Love", id = "quote_283"),
    Quote("Some peace comes from letting a moment be enough. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_284"),
    Quote("Every ending can make room for a beginning that fits who you are becoming. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_285"),
    Quote("Hope grows through the small possibilities you choose to nurture. A gentle moment can shift the tone of your day and make room for joy.", "Unknown", "Happiness and Positivity", id = "quote_286"),
    Quote("Your quiet confidence is just as powerful as the loudest voice in the room. Every accomplishment matters, whether it is celebrated by many or known only to you.", "Unknown", "Confidence and Self-Esteem", id = "quote_287"),
    Quote("Some peace comes from letting a moment be enough. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_288"),
    Quote("Rest is a meaningful part of caring for yourself and growing well. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_289"),
    Quote("Preparation gives you confidence; taking the first step turns it into progress. Progress grows when your goals make room for balance and consistency.", "Unknown", "Motivation and Productivity", id = "quote_290"),
    Quote("Your own values and experience are important guides for your decisions. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_291"),
    Quote("A routine becomes stronger when it fits the life you actually live. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_292"),
    Quote("You belong in every room where your hopes and talents take you. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_293"),
    Quote("Gratitude helps you notice the good that is already part of your day. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_294"),
    Quote("You can choose what supports your growth and make room for what matters. Your peace grows when you give your time to what matters most.", "Unknown", "Self-Love", id = "quote_295"),
    Quote("A little action today can create the inspiration for tomorrow. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_296"),
    Quote("A thoughtful pause can help you welcome the next step with clarity. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_297"),
    Quote("Joy belongs in your everyday life, not just at the end of a long to-do list. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_298"),
    Quote("Healthy relationships leave room for care, honesty, and healthy space. One day never tells the whole story of a life filled with possibility.", "Unknown", "Self-Love", id = "quote_299"),
    Quote("There is room in your life for progress, gratitude, and happiness right now. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_300"),
    Quote("Your own values and experience are important guides for your decisions. Let each action remind you of what is possible when you believe in yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_301"),
    Quote("Self-respect means learning from your choices with patience and care. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_302"),
    Quote("A routine becomes stronger when it fits the life you actually live. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_303"),
    Quote("Gratitude helps you notice the good that is already part of your day. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_304"),
    Quote("Every new beginning is a chance to discover what you can do. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_305"),
    Quote("A small comfort is still worth noticing. Peace grows when you give your attention to the present moment.", "Unknown", "Happiness and Positivity", id = "quote_306"),
    Quote("Quiet progress is still meaningful progress, even when no one else sees it. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_307"),
    Quote("You do not need to make your pain useful before it can heal. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_308"),
    Quote("Quiet progress is still meaningful progress, even when no one else sees it. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_309"),
    Quote("Some peace comes from letting a moment be enough. Peace grows when you give your attention to the present moment.", "Unknown", "Happiness and Positivity", id = "quote_310"),
    Quote("Your voice matters, and your perspective deserves to be heard. A willingness to learn is one of the surest ways to keep growing.", "Unknown", "Confidence and Self-Esteem", id = "quote_311"),
    Quote("Some peace comes from letting a moment be enough. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_312"),
    Quote("Your worth is steady, and it does not depend on someone else's choice. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_313"),
    Quote("A task often becomes less intimidating when you define its first step. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_314"),
    Quote("Hope grows through the small possibilities you choose to nurture. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_315"),
    Quote("Your worth is steady, and it does not depend on someone else's choice. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_316"),
    Quote("Rest is a gift you can give yourself as part of a balanced life. Your kindness is strongest when it is balanced with care for yourself.", "Unknown", "Self-Love", id = "quote_317"),
    Quote("A new route can lead you toward the same meaningful destination. Let what you have learned guide your next step with confidence and clarity.", "Unknown", "Overcoming Challenges", id = "quote_318"),
    Quote("Quiet progress is still meaningful progress, even when no one else sees it. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_319"),
    Quote("Your body deserves appreciation for all the ways it carries you through life. Your kindness is strongest when it is balanced with care for yourself.", "Unknown", "Self-Love", id = "quote_320"),
    Quote("A little action today can create the inspiration for tomorrow. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_321"),
    Quote("A small comfort is still worth noticing. A gentle moment can shift the tone of your day and make room for joy.", "Unknown", "Happiness and Positivity", id = "quote_322"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Each time you return to a habit, you strengthen the path forward.", "Unknown", "Motivation and Productivity", id = "quote_323"),
    Quote("You can stay true to yourself while allowing others their own perspective. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_324"),
    Quote("Give your goals a place in your day, and watch your plans take shape. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_325"),
    Quote("Healthy relationships leave room for care, honesty, and healthy space. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_326"),
    Quote("Consistency is easier when the next step is clear. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_327"),
    Quote("Rest is a gift you can give yourself as part of a balanced life. Your peace grows when you give your time to what matters most.", "Unknown", "Self-Love", id = "quote_328"),
    Quote("Every mistake offers a chance to learn, adjust, and grow. Trust your values as you make choices that feel right for you.", "Unknown", "Confidence and Self-Esteem", id = "quote_329"),
    Quote("Joy can exist alongside worry without canceling it out. Even an ordinary day can hold a moment worth treasuring.", "Unknown", "Happiness and Positivity", id = "quote_330"),
    Quote("Every fresh start carries the wisdom of everything you've learned. Give yourself a moment to notice the possibilities around you.", "Unknown", "Overcoming Challenges", id = "quote_331"),
    Quote("Some peace comes from letting a moment be enough. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_332"),
    Quote("Your voice matters, and your perspective deserves to be heard. Every accomplishment matters, whether it is celebrated by many or known only to you.", "Unknown", "Confidence and Self-Esteem", id = "quote_333"),
    Quote("You can choose what supports your growth and make room for what matters. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_334"),
    Quote("Your worth is steady, and it does not depend on someone else's choice. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_335"),
    Quote("Your needs still matter when other people have needs too. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_336"),
    Quote("Every small step forward is part of your progress. Let what you have learned guide your next step with confidence and clarity.", "Unknown", "Overcoming Challenges", id = "quote_337"),
    Quote("Your voice matters, and your perspective deserves to be heard. Each brave beginning gives your confidence another reason to grow.", "Unknown", "Confidence and Self-Esteem", id = "quote_338"),
    Quote("Your needs still matter when other people have needs too. Your kindness is strongest when it is balanced with care for yourself.", "Unknown", "Self-Love", id = "quote_339"),
    Quote("Every step you take through a challenge adds to your strength. Let what you have learned guide your next step with confidence and clarity.", "Unknown", "Overcoming Challenges", id = "quote_340"),
    Quote("Ordinary moments often become meaningful only after we pay attention. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_341"),
    Quote("A fresh perspective can turn a challenge into a new opportunity. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_342"),
    Quote("Gratitude helps you notice the good that is already part of your day. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_343"),
    Quote("Self-respect means learning from your choices with patience and care. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_344"),
    Quote("Every good day matters, whether memorable or quietly meaningful. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_345"),
    Quote("Understanding often grows with time; let each experience teach you at its own pace. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_346"),
    Quote("Consistency is easier when the next step is clear. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_347"),
    Quote("Every small step forward is part of your progress. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_348"),
    Quote("Consistency is easier when the next step is clear. Put your energy into the meaningful steps you can take today.", "Unknown", "Motivation and Productivity", id = "quote_349"),
    Quote("Your quiet confidence is just as powerful as the loudest voice in the room. Each brave beginning gives your confidence another reason to grow.", "Unknown", "Confidence and Self-Esteem", id = "quote_350"),
    Quote("Rest is a meaningful part of caring for yourself and growing well. One day never tells the whole story of a life filled with possibility.", "Unknown", "Self-Love", id = "quote_351"),
    Quote("Give your goals a place in your day, and watch your plans take shape. Each time you return to a habit, you strengthen the path forward.", "Unknown", "Motivation and Productivity", id = "quote_352"),
    Quote("New possibilities often begin just beyond what feels familiar. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_353"),
    Quote("Every mistake offers a chance to learn, adjust, and grow. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_354"),
    Quote("Understanding often grows with time; let each experience teach you at its own pace. Give yourself a moment to notice the possibilities around you.", "Unknown", "Overcoming Challenges", id = "quote_355"),
    Quote("Self-respect means learning from your choices with patience and care. One day never tells the whole story of a life filled with possibility.", "Unknown", "Self-Love", id = "quote_356"),
    Quote("Hope grows through the small possibilities you choose to nurture. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_357"),
    Quote("You can stay true to yourself while allowing others their own perspective. Let each action remind you of what is possible when you believe in yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_358"),
    Quote("A fresh perspective can turn a challenge into a new opportunity. The strength you build through each season becomes part of your story.", "Unknown", "Overcoming Challenges", id = "quote_359"),
    Quote("Rest is a meaningful part of caring for yourself and growing well. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_360"),
    Quote("A small comfort is still worth noticing. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_361"),
    Quote("Quiet progress is still meaningful progress, even when no one else sees it. Put your energy into the meaningful steps you can take today.", "Unknown", "Motivation and Productivity", id = "quote_362"),
    Quote("Every choice can teach you something valuable for the road ahead. One day never tells the whole story of a life filled with possibility.", "Unknown", "Self-Love", id = "quote_363"),
    Quote("A little action today can create the inspiration for tomorrow. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_364"),
    Quote("A thoughtful pause can help you welcome the next step with clarity. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_365"),
    Quote("You do not need to make your pain useful before it can heal. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_366"),
    Quote("Your quiet confidence is just as powerful as the loudest voice in the room. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_367"),
    Quote("Your needs still matter when other people have needs too. You deserve kindness, care, and joy every single day.", "Unknown", "Self-Love", id = "quote_368"),
    Quote("There is room in your life for progress, gratitude, and happiness right now. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_369"),
    Quote("Take the lessons that help you grow and let your confidence stay your own. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_370"),
    Quote("You do not need to make your pain useful before it can heal. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_371"),
    Quote("Quiet progress is still meaningful progress, even when no one else sees it. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_372"),
    Quote("Every ending can make room for a beginning that fits who you are becoming. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_373"),
    Quote("Your needs still matter when other people have needs too. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_374"),
    Quote("There is room in your life for progress, gratitude, and happiness right now. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_375"),
    Quote("You do not need to make your pain useful before it can heal. Your peace grows when you give your time to what matters most.", "Unknown", "Self-Love", id = "quote_376"),
    Quote("A short focused effort can break the spell of postponing. Progress grows when your goals make room for balance and consistency.", "Unknown", "Motivation and Productivity", id = "quote_377"),
    Quote("Every fresh start carries the wisdom of everything you've learned. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_378"),
    Quote("Joy can exist alongside worry without canceling it out. A gentle moment can shift the tone of your day and make room for joy.", "Unknown", "Happiness and Positivity", id = "quote_379"),
    Quote("Preparation gives you confidence; taking the first step turns it into progress. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_380"),
    Quote("Your voice matters, and your perspective deserves to be heard. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_381"),
    Quote("You do not need to make your pain useful before it can heal. One day never tells the whole story of a life filled with possibility.", "Unknown", "Self-Love", id = "quote_382"),
    Quote("Joy belongs in your everyday life, not just at the end of a long to-do list. Peace grows when you give your attention to the present moment.", "Unknown", "Happiness and Positivity", id = "quote_383"),
    Quote("Every mistake offers a chance to learn, adjust, and grow. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_384"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_385"),
    Quote("Every fresh start carries the wisdom of everything you've learned. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_386"),
    Quote("A task often becomes less intimidating when you define its first step. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_387"),
    Quote("Your worth is steady, and it does not depend on someone else's choice. A willingness to learn is one of the surest ways to keep growing.", "Unknown", "Confidence and Self-Esteem", id = "quote_388"),
    Quote("Rest is a meaningful part of caring for yourself and growing well. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_389"),
    Quote("Every new beginning is a chance to discover what you can do. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_390"),
    Quote("Give your goals a place in your day, and watch your plans take shape. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_391"),
    Quote("Rest is a meaningful part of caring for yourself and growing well. Every day brings another opportunity to grow with patience and compassion.", "Unknown", "Self-Love", id = "quote_392"),
    Quote("A new route can lead you toward the same meaningful destination. Give yourself a moment to notice the possibilities around you.", "Unknown", "Overcoming Challenges", id = "quote_393"),
    Quote("Every choice can teach you something valuable for the road ahead. Your peace grows when you give your time to what matters most.", "Unknown", "Self-Love", id = "quote_394"),
    Quote("Every new day gives you room to grow beyond yesterday's challenges. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_395"),
    Quote("Ordinary moments often become meaningful only after we pay attention. Even an ordinary day can hold a moment worth treasuring.", "Unknown", "Happiness and Positivity", id = "quote_396"),
    Quote("You can choose what supports your growth and make room for what matters. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_397"),
    Quote("You belong in every room where your hopes and talents take you. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_398"),
    Quote("Hope grows through the small possibilities you choose to nurture. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_399"),
    Quote("Your voice matters, and your perspective deserves to be heard. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_400"),
    Quote("Preparation gives you confidence; taking the first step turns it into progress. Each time you return to a habit, you strengthen the path forward.", "Unknown", "Motivation and Productivity", id = "quote_401"),
    Quote("You do not need to make your pain useful before it can heal. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_402"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Progress grows when your goals make room for balance and consistency.", "Unknown", "Motivation and Productivity", id = "quote_403"),
    Quote("Your own values and experience are important guides for your decisions. Every accomplishment matters, whether it is celebrated by many or known only to you.", "Unknown", "Confidence and Self-Esteem", id = "quote_404"),
    Quote("Preparation gives you confidence; taking the first step turns it into progress. Put your energy into the meaningful steps you can take today.", "Unknown", "Motivation and Productivity", id = "quote_405"),
    Quote("Hope grows through the small possibilities you choose to nurture. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_406"),
    Quote("A routine becomes stronger when it fits the life you actually live. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_407"),
    Quote("New possibilities often begin just beyond what feels familiar. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_408"),
    Quote("You can make room for honest feelings while still welcoming moments of joy. Peace grows when you give your attention to the present moment.", "Unknown", "Happiness and Positivity", id = "quote_409"),
    Quote("Choose your priorities with care, and give each moment the attention it deserves. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_410"),
    Quote("You can choose what supports your growth and make room for what matters. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_411"),
    Quote("Ordinary moments often become meaningful only after we pay attention. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_412"),
    Quote("Consistency is easier when the next step is clear. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_413"),
    Quote("Your own values and experience are important guides for your decisions. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_414"),
    Quote("Understanding often grows with time; let each experience teach you at its own pace. The strength you build through each season becomes part of your story.", "Unknown", "Overcoming Challenges", id = "quote_415"),
    Quote("Every choice can teach you something valuable for the road ahead. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_416"),
    Quote("Your quiet confidence is just as powerful as the loudest voice in the room. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_417"),
    Quote("A small comfort is still worth noticing. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_418"),
    Quote("A thoughtful pause can help you welcome the next step with clarity. Give yourself a moment to notice the possibilities around you.", "Unknown", "Overcoming Challenges", id = "quote_419"),
    Quote("You can choose what supports your growth and make room for what matters. Your kindness is strongest when it is balanced with care for yourself.", "Unknown", "Self-Love", id = "quote_420"),
    Quote("Choose your priorities with care, and give each moment the attention it deserves. Put your energy into the meaningful steps you can take today.", "Unknown", "Motivation and Productivity", id = "quote_421"),
    Quote("Every new day gives you room to grow beyond yesterday's challenges. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_422"),
    Quote("Your own values and experience are important guides for your decisions. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_423"),
    Quote("Listening to your feelings can help you discover what you need. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_424"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_425"),
    Quote("Every ending can make room for a beginning that fits who you are becoming. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_426"),
    Quote("Your needs still matter when other people have needs too. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_427"),
    Quote("Quiet progress is still meaningful progress, even when no one else sees it. Progress grows when your goals make room for balance and consistency.", "Unknown", "Motivation and Productivity", id = "quote_428"),
    Quote("Gratitude helps you notice the good that is already part of your day. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_429"),
    Quote("Choose your priorities with care, and give each moment the attention it deserves. Progress grows when your goals make room for balance and consistency.", "Unknown", "Motivation and Productivity", id = "quote_430"),
    Quote("Self-respect means learning from your choices with patience and care. Every day brings another opportunity to grow with patience and compassion.", "Unknown", "Self-Love", id = "quote_431"),
    Quote("Choose your priorities with care, and give each moment the attention it deserves. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_432"),
    Quote("A new route can lead you toward the same meaningful destination. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_433"),
    Quote("Self-respect means learning from your choices with patience and care. You deserve kindness, care, and joy every single day.", "Unknown", "Self-Love", id = "quote_434"),
    Quote("Your worth is steady, and it does not depend on someone else's choice. Every accomplishment matters, whether it is celebrated by many or known only to you.", "Unknown", "Confidence and Self-Esteem", id = "quote_435"),
    Quote("A new route can lead you toward the same meaningful destination. The strength you build through each season becomes part of your story.", "Unknown", "Overcoming Challenges", id = "quote_436"),
    Quote("Self-respect means learning from your choices with patience and care. Your kindness is strongest when it is balanced with care for yourself.", "Unknown", "Self-Love", id = "quote_437"),
    Quote("You belong in every room where your hopes and talents take you. Trust your values as you make choices that feel right for you.", "Unknown", "Confidence and Self-Esteem", id = "quote_438"),
    Quote("Listening to your feelings can help you discover what you need. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_439"),
    Quote("Every new beginning is a chance to discover what you can do. Let each action remind you of what is possible when you believe in yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_440"),
    Quote("A fresh perspective can turn a challenge into a new opportunity. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_441"),
    Quote("Every mistake offers a chance to learn, adjust, and grow. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_442"),
    Quote("A little action today can create the inspiration for tomorrow. Each time you return to a habit, you strengthen the path forward.", "Unknown", "Motivation and Productivity", id = "quote_443"),
    Quote("You can stay true to yourself while allowing others their own perspective. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_444"),
    Quote("Your body deserves appreciation for all the ways it carries you through life. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_445"),
    Quote("Every small step forward is part of your progress. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_446"),
    Quote("You can trust yourself to make thoughtful choices, even as you keep learning. Each brave beginning gives your confidence another reason to grow.", "Unknown", "Confidence and Self-Esteem", id = "quote_447"),
    Quote("Joy belongs in your everyday life, not just at the end of a long to-do list. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_448"),
    Quote("Every step you take through a challenge adds to your strength. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_449"),
    Quote("Rest is a meaningful part of caring for yourself and growing well. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_450"),
    Quote("A new route can lead you toward the same meaningful destination. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_451"),
    Quote("Choose your priorities with care, and give each moment the attention it deserves. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_452"),
    Quote("Every fresh start carries the wisdom of everything you've learned. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_453"),
    Quote("Every good day matters, whether memorable or quietly meaningful. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_454"),
    Quote("Your body deserves appreciation for all the ways it carries you through life. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_455"),
    Quote("There is room in your life for progress, gratitude, and happiness right now. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_456"),
    Quote("You belong in every room where your hopes and talents take you. A willingness to learn is one of the surest ways to keep growing.", "Unknown", "Confidence and Self-Esteem", id = "quote_457"),
    Quote("You can make room for honest feelings while still welcoming moments of joy. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_458"),
    Quote("A fresh perspective can turn a challenge into a new opportunity. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_459"),
    Quote("Take the lessons that help you grow and let your confidence stay your own. Let each action remind you of what is possible when you believe in yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_460"),
    Quote("Joy can exist alongside worry without canceling it out. Peace grows when you give your attention to the present moment.", "Unknown", "Happiness and Positivity", id = "quote_461"),
    Quote("Rest is a meaningful part of caring for yourself and growing well. Your peace grows when you give your time to what matters most.", "Unknown", "Self-Love", id = "quote_462"),
    Quote("New possibilities often begin just beyond what feels familiar. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_463"),
    Quote("Every choice can teach you something valuable for the road ahead. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_464"),
    Quote("Your own values and experience are important guides for your decisions. A willingness to learn is one of the surest ways to keep growing.", "Unknown", "Confidence and Self-Esteem", id = "quote_465"),
    Quote("You can make room for honest feelings while still welcoming moments of joy. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_466"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_467"),
    Quote("Understanding often grows with time; let each experience teach you at its own pace. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_468"),
    Quote("Healthy relationships leave room for care, honesty, and healthy space. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_469"),
    Quote("You can trust yourself to make thoughtful choices, even as you keep learning. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_470"),
    Quote("Rest is a gift you can give yourself as part of a balanced life. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_471"),
    Quote("Take the lessons that help you grow and let your confidence stay your own. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_472"),
    Quote("Ordinary moments often become meaningful only after we pay attention. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_473"),
    Quote("Your quiet confidence is just as powerful as the loudest voice in the room. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_474"),
    Quote("New possibilities often begin just beyond what feels familiar. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_475"),
    Quote("Choose your priorities with care, and give each moment the attention it deserves. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_476"),
    Quote("Every step you take through a challenge adds to your strength. Give yourself a moment to notice the possibilities around you.", "Unknown", "Overcoming Challenges", id = "quote_477"),
    Quote("Ordinary moments often become meaningful only after we pay attention. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_478"),
    Quote("Consistency is easier when the next step is clear. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_479"),
    Quote("You belong in every room where your hopes and talents take you. Each brave beginning gives your confidence another reason to grow.", "Unknown", "Confidence and Self-Esteem", id = "quote_480"),
    Quote("Every step you take through a challenge adds to your strength. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_481"),
    Quote("Every mistake offers a chance to learn, adjust, and grow. Each brave beginning gives your confidence another reason to grow.", "Unknown", "Confidence and Self-Esteem", id = "quote_482"),
    Quote("Rest is a gift you can give yourself as part of a balanced life. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_483"),
    Quote("Understanding often grows with time; let each experience teach you at its own pace. Let what you have learned guide your next step with confidence and clarity.", "Unknown", "Overcoming Challenges", id = "quote_484"),
    Quote("There is room in your life for progress, gratitude, and happiness right now. A gentle moment can shift the tone of your day and make room for joy.", "Unknown", "Happiness and Positivity", id = "quote_485"),
    Quote("A fresh perspective can turn a challenge into a new opportunity. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_486"),
    Quote("Hope grows through the small possibilities you choose to nurture. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_487"),
    Quote("Give your goals a place in your day, and watch your plans take shape. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_488"),
    Quote("You can trust yourself to make thoughtful choices, even as you keep learning. Let each action remind you of what is possible when you believe in yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_489"),
    Quote("Your needs still matter when other people have needs too. One day never tells the whole story of a life filled with possibility.", "Unknown", "Self-Love", id = "quote_490"),
    Quote("A thoughtful pause can help you welcome the next step with clarity. Let what you have learned guide your next step with confidence and clarity.", "Unknown", "Overcoming Challenges", id = "quote_491"),
    Quote("Every choice can teach you something valuable for the road ahead. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_492"),
    Quote("A task often becomes less intimidating when you define its first step. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_493"),
    Quote("Every good day matters, whether memorable or quietly meaningful. Peace grows when you give your attention to the present moment.", "Unknown", "Happiness and Positivity", id = "quote_494"),
    Quote("Listening to your feelings can help you discover what you need. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_495"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_496"),
    Quote("Your needs still matter when other people have needs too. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_497"),
    Quote("Self-respect means learning from your choices with patience and care. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_498"),
    Quote("Healthy relationships leave room for care, honesty, and healthy space. Your kindness is strongest when it is balanced with care for yourself.", "Unknown", "Self-Love", id = "quote_499"),
    Quote("Every choice can teach you something valuable for the road ahead. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_500")
)
// Favorites are stored as an ordered list so the most recently saved quote appears first.
// The set key remains for backwards compatibility with existing installations.
fun loadFavoriteIds(context: Context): Set<String> {
    val preferences = context.getSharedPreferences("daily_motivation", Context.MODE_PRIVATE)
    val savedIds = preferences.getStringSet("favorite_ids", emptySet())?.toSet() ?: emptySet()
    val orderedIds = linkedSetOf<String>()

    // Read both the previous delimiter and the current comma delimiter.
    preferences.getString("favorite_order", null)
        ?.split(",", "\\n")
        ?.filter { it.isNotBlank() && it in savedIds }
        ?.forEach { orderedIds.add(it) }

    // Preserve the existing favorites when installing this ordering fix.
    savedIds.forEach { orderedIds.add(it) }

    if (!preferences.getBoolean("favorites_ids_migrated", false)) {
        val legacyIndexes = preferences.getStringSet("favorites", emptySet()) ?: emptySet()
        legacyIndexes.forEach { indexString ->
            val index = indexString.toIntOrNull()
            if (index != null && index in quotes.indices) {
                orderedIds.add(quotes[index].id)
            }
        }
        preferences.edit()
            .putStringSet("favorite_ids", orderedIds)
            .putString("favorite_order", orderedIds.joinToString(","))
            .putBoolean("favorites_ids_migrated", true)
            .apply()
    } else if (orderedIds.size != savedIds.size ||
        preferences.getString("favorite_order", null) == null
    ) {
        preferences.edit()
            .putString("favorite_order", orderedIds.joinToString(","))
            .apply()
    }

    return orderedIds
}

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
        setShadowLayer(8f, 0f, 3f, AndroidColor.argb(145, 0, 0, 0))
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
        setShadowLayer(5f, 0f, 2f, AndroidColor.argb(115, 0, 0, 0))
    }
    canvas.drawText("♡", width / 2f, 350f, heartPaint)

    val quotePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = white
        textAlign = Paint.Align.CENTER
        textSize = 49f
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        setShadowLayer(7f, 0f, 2f, AndroidColor.argb(175, 0, 0, 0))
    }
    val maxTextWidth = 850f
    val quoteLines = wrapShareText("${quote.text}", quotePaint, maxTextWidth)
    val lineHeight = 68f
    val quoteBlockHeight = quoteLines.size * lineHeight
    var textY = (height * 0.52f) - quoteBlockHeight / 2f + lineHeight / 2f
    quoteLines.forEach { line ->
        canvas.drawText(line, width / 2f, textY, quotePaint)
        textY += lineHeight
    }

    val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.argb(225, 255, 255, 255)
        textAlign = Paint.Align.CENTER
        textSize = 34f
        typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    }

    canvas.drawText("Get Daily Motivation on Google Play", width / 2f, 1110f, footerPaint)

    val qrSize = 160
    val qrHints = mapOf(com.google.zxing.EncodeHintType.MARGIN to 1)
    val qrMatrix = QRCodeWriter().encode(
        "https://play.google.com/store/apps/details?id=com.brightdays.dailymotivation",
        BarcodeFormat.QR_CODE,
        qrSize,
        qrSize,
        qrHints
    )
    val qrBitmap = Bitmap.createBitmap(qrSize, qrSize, Bitmap.Config.ARGB_8888)
    for (x in 0 until qrSize) {
        for (y in 0 until qrSize) {
            qrBitmap.setPixel(
                x,
                y,
                if (qrMatrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE
            )
        }
    }
    val qrBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
    }
    canvas.drawRoundRect(451f, 1148f, 629f, 1326f, 22f, 22f, qrBackgroundPaint)
    canvas.drawBitmap(
        qrBitmap,
        null,
        Rect(460, 1157, 620, 1317),
        Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    )
    qrBitmap.recycle()

    val shareDirectory = java.io.File(context.cacheDir, "shared_quotes")
    if (!shareDirectory.exists()) shareDirectory.mkdirs()
    val imageFile = java.io.File(shareDirectory, "daily_motivation_quote_${java.util.UUID.randomUUID()}.png")
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
        textAlign = TextAlign.Center,
        style = androidx.compose.ui.text.TextStyle(
            shadow = androidx.compose.ui.graphics.Shadow(
                color = Color.Black.copy(alpha = 0.92f),
                offset = Offset(0f, 2f),
                blurRadius = 5f
            )
        )
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

                Box(
                    modifier = Modifier
                        .width(55.dp)
                        .height(55.dp)
                        .scale(heartScale.value)
                        .clickable(
                            indication = null,
                            interactionSource = remember {
                                MutableInteractionSource()
                            }
                        ) {
                            if (!isSaved) {
                                isAnimating = true
                            }
                            onClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSaved) "♥" else "♡",
                        fontSize = 34.sp,
                        lineHeight = 34.sp,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )
        }
    }
}

// ------------------------------------------------------------
// REMINDER ICON BUTTON
// ------------------------------------------------------------

@Composable
fun ReminderIconButton(
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
        ComposeCanvas(
            modifier = Modifier
                .width(29.dp)
                .height(29.dp)
        ) {
            val line = 1.7.dp.toPx()
            val color = Color.White.copy(alpha = 0.94f)
            val pageLeft = size.width * 0.08f
            val pageTop = size.height * 0.17f
            val pageWidth = size.width * 0.72f
            val pageHeight = size.height * 0.72f

            // Calendar page
            drawRoundRect(
                color = color,
                topLeft = Offset(pageLeft, pageTop),
                size = androidx.compose.ui.geometry.Size(pageWidth, pageHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.5.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = line)
            )

            // Calendar header and binding rings
            drawLine(
                color,
                Offset(pageLeft, size.height * 0.39f),
                Offset(pageLeft + pageWidth, size.height * 0.39f),
                strokeWidth = line
            )
            drawLine(
                color,
                Offset(size.width * 0.27f, size.height * 0.08f),
                Offset(size.width * 0.27f, size.height * 0.28f),
                strokeWidth = line
            )
            drawLine(
                color,
                Offset(size.width * 0.60f, size.height * 0.08f),
                Offset(size.width * 0.60f, size.height * 0.28f),
                strokeWidth = line
            )

            // Small calendar-day marks
            drawCircle(color, radius = line * 0.8f, center = Offset(size.width * 0.25f, size.height * 0.53f))
            drawCircle(color, radius = line * 0.8f, center = Offset(size.width * 0.48f, size.height * 0.53f))
            drawCircle(color, radius = line * 0.8f, center = Offset(size.width * 0.25f, size.height * 0.69f))

            // Clock overlay to make the daily reminder meaning clear
            val clockCenter = Offset(size.width * 0.72f, size.height * 0.73f)
            val clockRadius = 5.dp.toPx()
            drawCircle(
                color = color,
                radius = clockRadius,
                center = clockCenter,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = line)
            )
            drawLine(
                color,
                clockCenter,
                Offset(clockCenter.x, clockCenter.y - clockRadius * 0.52f),
                strokeWidth = line
            )
            drawLine(
                color,
                clockCenter,
                Offset(clockCenter.x + clockRadius * 0.42f, clockCenter.y + clockRadius * 0.20f),
                strokeWidth = line
            )
        }
    }
}
// ------------------------------------------------------------
// SHARE ICON BUTTON
// ------------------------------------------------------------

@Composable
fun ShareIconButton(
    onClick: () -> Unit,
    compact: Boolean = false
) {
    val buttonWidth = if (compact) 28.dp else 70.dp
    val buttonHeight = if (compact) 28.dp else 55.dp
    val iconSize = if (compact) 16.dp else 28.dp

    Box(
        modifier = Modifier
            .width(buttonWidth)
            .height(buttonHeight)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        ComposeCanvas(
            modifier = Modifier
                .width(iconSize)
                .height(iconSize)
        ) {
            val stroke = (if (compact) 1.4f else 1.8f).dp.toPx()
            val radius = (if (compact) 2.1f else 3.1f).dp.toPx()
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

    Box(modifier = Modifier.fillMaxSize()) {
        AssetImage(
            fileName = backgroundImage,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.36f))
        )

        AnimatedContent(
            targetState = showIntro,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 650,
                        delayMillis = 350
                    )
                ) togetherWith
                    fadeOut(
                        animationSpec = tween(
                            durationMillis = 350
                        )
                    )
            },
            label = "introTextTransition"
        ) { introVisible ->
            if (introVisible) {
                IntroScreen()

                LaunchedEffect(Unit) {
                    delay(900L)
                    showIntro = false
                }
            } else {
                MainAndFavorites()
            }
        }
    }
}


// ------------------------------------------------------------
// INTRO SCREEN
// ------------------------------------------------------------

@Composable
fun IntroScreen() {
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


// ------------------------------------------------------------
// MAIN / FAVORITES NAVIGATION
// ------------------------------------------------------------

@Composable
fun MainAndFavorites() {

    var showFavorites by remember { mutableStateOf(false) }
    var swipeDirection by remember { mutableStateOf(-1f) }

    AnimatedContent(
        modifier = Modifier.fillMaxSize(),
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

    var showReminderDialog by remember { mutableStateOf(false) }
    var showFavoritesSwipeHint by remember {
        mutableStateOf(!preferences.getBoolean("has_swiped_favorites", false))
    }
    var reminderEnabled by remember {
        mutableStateOf(preferences.getBoolean("reminder_enabled", true))
    }
    var reminderHour by remember {
        mutableStateOf(preferences.getInt("reminder_hour", 8).coerceIn(0, 23))
    }
    var reminderMinute by remember {
        mutableStateOf(preferences.getInt("reminder_minute", 0).coerceIn(0, 59))
    }

    var favoriteSet by remember {
        mutableStateOf(loadFavoriteIds(context))
    }

    val isSaved = favoriteSet.contains(quote.id)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
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
                                if (horizontalDrag > 0f && showFavoritesSwipeHint) {
                                    showFavoritesSwipeHint = false
                                    preferences.edit()
                                        .putBoolean("has_swiped_favorites", true)
                                        .apply()
                                }
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
                        color = Color.White,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.42f),
                                offset = Offset(0f, 1f),
                                blurRadius = 2f
                            )
                        )
                    )

                    Text(
                        text = "Motivation",
                        fontSize = 49.sp,
                        fontFamily =
                            FontFamily.Cursive,
                        fontWeight =
                            FontWeight.Normal,
                        color = Color.White,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.62f),
                                offset = Offset(0f, 2f),
                                blurRadius = 4f
                            )
                        )
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
                            "${quote.text}",
                        fontSize = 23.sp,
                        lineHeight = 32.sp,
                        fontWeight =
                            FontWeight.Medium,
                        textAlign =
                            TextAlign.Center,
                        color = Color.White,
                        style = androidx.compose.ui.text.TextStyle(
                            textDirection = TextDirection.Ltr,
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.96f),
                                offset = Offset(0f, 2f),
                                blurRadius = 5f
                            )
                        )
                    )

                }

                if (showFavoritesSwipeHint) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 0.dp, end = 0.dp, top = 4.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Swipe right to see your favorites →",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            style = androidx.compose.ui.text.TextStyle(
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black.copy(alpha = 0.90f),
                                    offset = Offset(0f, 2f),
                                    blurRadius = 5f
                                )
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 0.dp,
                            end = 0.dp,
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

                            val newFavorites = favoriteSet.toMutableSet()

                            if (isSaved) {
                                newFavorites.remove(quote.id)
                            } else {
                                // Put newly saved quotes at the top of the favorites list.
                                val reorderedFavorites = linkedSetOf(quote.id)
                                reorderedFavorites.addAll(newFavorites)
                                newFavorites.clear()
                                newFavorites.addAll(reorderedFavorites)
                            }

                            preferences.edit()
                                .putStringSet("favorite_ids", newFavorites)
                                .putString("favorite_order", newFavorites.joinToString(","))
                                .apply()

                            favoriteSet = newFavorites
                        }
                    )

                    ReminderIconButton(
                        onClick = { showReminderDialog = true }
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

    if (showReminderDialog) {
        val reminderTimeLabel = String.format(
            java.util.Locale.getDefault(),
            "%d:%02d %s",
            ((reminderHour + 11) % 12) + 1,
            reminderMinute,
            if (reminderHour < 12) "AM" else "PM"
        )

        AlertDialog(
            onDismissRequest = { showReminderDialog = false },
            title = { Text("Daily reminder") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Remind me every day")
                            Text(
                                text = if (reminderEnabled) "On" else "Off",
                                fontSize = 13.sp,
                                color = androidx.compose.ui.graphics.Color.Gray
                            )
                        }
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { enabled ->
                                reminderEnabled = enabled
                                preferences.edit()
                                    .putBoolean("reminder_enabled", enabled)
                                    .apply()
                                if (enabled) {
                                    DailyReminderScheduler.schedule(context)
                                    if (
                                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                        context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        (context as? ComponentActivity)?.requestPermissions(
                                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                                            1001
                                        )
                                    }
                                } else {
                                    DailyReminderScheduler.cancel(context)
                                }
                            }
                        )
                    }

                    MaterialTextButton(
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    reminderHour = hour
                                    reminderMinute = minute
                                    preferences.edit()
                                        .putInt("reminder_hour", hour)
                                        .putInt("reminder_minute", minute)
                                        .apply()
                                    if (reminderEnabled) {
                                        DailyReminderScheduler.schedule(context)
                                    }
                                },
                                reminderHour,
                                reminderMinute,
                                false
                            ).show()
                        }
                    ) {
                        Text("Reminder time: $reminderTimeLabel  ·  Change")
                    }
                }
            },
            confirmButton = {
                MaterialTextButton(onClick = { showReminderDialog = false }) {
                    Text("Done")
                }
            }
        )
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

    val shareScope = rememberCoroutineScope()

    val favoritesShadowStyle = androidx.compose.ui.text.TextStyle(
        shadow = androidx.compose.ui.graphics.Shadow(
            color = Color.Black.copy(alpha = 0.92f),
            offset = Offset(0f, 2f),
            blurRadius = 5f
        )
    )

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
                    style = favoritesShadowStyle,
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
        mutableStateOf(loadFavoriteIds(context))
    }

    // Always render from the persisted order, not from a Set's iteration order.
    val favoriteOrder = remember(favoriteSet) {
        val storedOrder = preferences.getString("favorite_order", null)
            ?.split(",", "\\n")
            ?.filter { it.isNotBlank() && it in favoriteSet }
            .orEmpty()
        (storedOrder + favoriteSet.filterNot { it in storedOrder }).distinct()
    }
    val favoriteQuotes = favoriteOrder.mapNotNull { quoteId ->
        quotes.firstOrNull { it.id == quoteId }?.let { quote -> quote.id to quote }
    }

    var quoteToRemove by remember {
        mutableStateOf<String?>(null)
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
                    style = favoritesShadowStyle,
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
                    style = favoritesShadowStyle,
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
                    style = favoritesShadowStyle,
                        text = "♡",
                        fontSize = 60.sp,
                        color = Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                    style = favoritesShadowStyle,
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
                    style = favoritesShadowStyle,
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

                        val favoriteId = favoriteItem.first

                        val favoriteQuote =
                            favoriteItem.second

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        modifier = Modifier.padding(end = 10.dp),
                                        style = favoritesShadowStyle.copy(
                                            textDirection = TextDirection.Ltr
                                        ),
                                        text = "${favoriteQuote.text}",
                                        fontSize = 20.sp,
                                        lineHeight = 29.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Column(
                                    modifier = Modifier.width(30.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    ShareIconButton(
                                        compact = true,
                                        onClick = {
                                            shareScope.launch {
                                                val shareUri = withContext(Dispatchers.IO) {
                                                    try {
                                                        createQuoteShareImage(
                                                            context = context,
                                                            backgroundImage = backgroundImage,
                                                            quote = favoriteQuote
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
                                                            "Daily Motivation saved quote",
                                                            shareUri
                                                        )
                                                    }
                                                    context.startActivity(
                                                        Intent.createChooser(sendIntent, "Share Saved Quote")
                                                    )
                                                }
                                            }
                                        }
                                    )

                                    TextButton(
                                        text = "×",
                                        modifier = Modifier.width(28.dp),
                                        fontSize = 16.sp,
                                        onClick = {
                                            quoteToRemove = favoriteId
                                        }
                                    )
                                }
                            }

                            if (favoriteId != favoriteQuotes.last().first) {
                                Spacer(modifier = Modifier.height(18.dp))
                                Box(
                                    modifier = Modifier
                                        .width(72.dp)
                                        .height(1.dp)
                                        .background(Color.White.copy(alpha = 0.65f))
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    quoteToRemove?.let { favoriteId ->

        AlertDialog(
            onDismissRequest = {
                quoteToRemove = null
            },
            title = {
                Text("Remove favorite?")
            },
            text = {
                Text("Are you sure you want to remove this quote from your favorites?")
            },
            confirmButton = {
                MaterialTextButton(
                    onClick = {
                        val newFavorites = favoriteSet.toMutableSet()
                        newFavorites.remove(favoriteId)

                        preferences.edit()
                            .putStringSet("favorite_ids", newFavorites)
                            .putString("favorite_order", newFavorites.joinToString(","))
                            .apply()

                        favoriteSet = newFavorites
                        quoteToRemove = null
                    }
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                MaterialTextButton(
                    onClick = {
                        quoteToRemove = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}