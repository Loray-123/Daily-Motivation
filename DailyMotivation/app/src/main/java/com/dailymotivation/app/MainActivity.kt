
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
    Quote("If your routine keeps failing, look at its design before blaming yourself. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_001"),
    Quote("You can admit that something hurts without letting it define everything. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_002"),
    Quote("You can care about someone and still need distance. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_003"),
    Quote("A slow recovery is still a recovery. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_004"),
    Quote("You can learn from criticism without accepting every judgment. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_005"),
    Quote("You can take responsibility without speaking to yourself cruelly. Protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love", id = "quote_006"),
    Quote("An ending can be painful even when it is the right one. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_007"),
    Quote("You can regret a choice without punishing yourself forever. You deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love", id = "quote_008"),
    Quote("A short focused effort can break the spell of postponing. Measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity", id = "quote_009"),
    Quote("Uncertainty is difficult, but it is not the same as danger. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_010"),
    Quote("Your life can contain unfinished things and genuine happiness. Peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity", id = "quote_011"),
    Quote("Being new at something does not make you less capable. You do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem", id = "quote_012"),
    Quote("A short focused effort can break the spell of postponing. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_013"),
    Quote("Uncertainty is difficult, but it is not the same as danger. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_014"),
    Quote("You do not have to reach exhaustion before you are allowed to rest. You deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love", id = "quote_015"),
    Quote("You do not need to feel inspired to begin. Progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity", id = "quote_016"),
    Quote("You can admit that something hurts without letting it define everything. Give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges", id = "quote_017"),
    Quote("Hope can be practical rather than certain. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_018"),
    Quote("A task often becomes less intimidating when you define its first step. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_019"),
    Quote("Ordinary moments often become meaningful only after we pay attention. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_020"),
    Quote("You can be tired of the struggle and still be making progress. Surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges", id = "quote_021"),
    Quote("You can learn from criticism without accepting every judgment. Being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem", id = "quote_022"),
    Quote("You do not have to force a bright side onto every disappointment. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_023"),
    Quote("If your routine keeps failing, look at its design before blaming yourself. Returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity", id = "quote_024"),
    Quote("Starting over does not erase what the first attempt taught you. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_025"),
    Quote("A task often becomes less intimidating when you define its first step. Measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity", id = "quote_026"),
    Quote("A setback can change your route without canceling your destination. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_027"),
    Quote("Gratitude is more honest when it leaves room for what is hard. Peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity", id = "quote_028"),
    Quote("A short focused effort can break the spell of postponing. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_029"),
    Quote("You do not have to be the most confident person in the room. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_030"),
    Quote("Rest does not have to be earned through suffering. You deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love", id = "quote_031"),
    Quote("Some progress is quiet enough to be mistaken for no progress. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_032"),
    Quote("Not being chosen by someone is not the same as being unworthy. Let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem", id = "quote_033"),
    Quote("A short focused effort can break the spell of postponing. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_034"),
    Quote("You are allowed to take up space before you feel ready. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_035"),
    Quote("You can regret a choice without punishing yourself forever. You can be healing and still have moments that feel familiar.", "Unknown", "Self-Love", id = "quote_036"),
    Quote("Gratitude is more honest when it leaves room for what is hard. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_037"),
    Quote("Uncertainty is difficult, but it is not the same as danger. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_038"),
    Quote("A goal needs room in your calendar, not only in your imagination. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_039"),
    Quote("Rest does not have to be earned through suffering. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_040"),
    Quote("Some problems need a different approach rather than more force. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_041"),
    Quote("A small comfort is still worth noticing. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_042"),
    Quote("A slow recovery is still a recovery. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_043"),
    Quote("Some peace comes from letting a moment be enough. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_044"),
    Quote("Preparation helps until it becomes a way to avoid starting. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_045"),
    Quote("Your voice does not need to be loud to deserve a place. Let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem", id = "quote_046"),
    Quote("Your life can contain unfinished things and genuine happiness. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_047"),
    Quote("You can be tired of the struggle and still be making progress. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_048"),
    Quote("You are allowed to change what you can no longer carry. You deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love", id = "quote_049"),
    Quote("An ending can be painful even when it is the right one. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_050"),
    Quote("Other people's opinions are only one part of the evidence. You do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem", id = "quote_051"),
    Quote("You do not need to feel inspired to begin. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_052"),
    Quote("You can be tired of the struggle and still be making progress. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_053"),
    Quote("Your body is part of your life, not a project you must finish. Protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love", id = "quote_054"),
    Quote("A small comfort is still worth noticing. An imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity", id = "quote_055"),
    Quote("Starting over does not erase what the first attempt taught you. Surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges", id = "quote_056"),
    Quote("You cannot give every task your best attention on the same day. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_057"),
    Quote("You are allowed to take up space before you feel ready. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_058"),
    Quote("You are allowed to change what you can no longer carry. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_059"),
    Quote("Joy can exist alongside worry without canceling it out. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_060"),
    Quote("You are allowed to pause before deciding what comes next. Surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges", id = "quote_061"),
    Quote("If your routine keeps failing, look at its design before blaming yourself. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_062"),
    Quote("A small comfort is still worth noticing. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_063"),
    Quote("Not being chosen by someone is not the same as being unworthy. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_064"),
    Quote("Your needs still matter when other people have needs too. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_065"),
    Quote("Joy can exist alongside worry without canceling it out. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_066"),
    Quote("An ending can be painful even when it is the right one. Surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges", id = "quote_067"),
    Quote("Ordinary moments often become meaningful only after we pay attention. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_068"),
    Quote("Rest does not have to be earned through suffering. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_069"),
    Quote("You do not need to feel inspired to begin. Measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity", id = "quote_070"),
    Quote("You can care about someone and still need distance. You deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love", id = "quote_071"),
    Quote("You do not have to force a bright side onto every disappointment. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_072"),
    Quote("You do not have to be the most confident person in the room. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_073"),
    Quote("You do not need to make your pain useful before it can heal. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_074"),
    Quote("Consistency is easier when the next step is clear. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_075"),
    Quote("Not every good day needs to be memorable to matter. An imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity", id = "quote_076"),
    Quote("You can be tired of the struggle and still be making progress. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_077"),
    Quote("You do not need to feel inspired to begin. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_078"),
    Quote("Your needs still matter when other people have needs too. You can be healing and still have moments that feel familiar.", "Unknown", "Self-Love", id = "quote_079"),
    Quote("Joy can exist alongside worry without canceling it out. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_080"),
    Quote("Consistency is easier when the next step is clear. Returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity", id = "quote_081"),
    Quote("You do not have to be the most confident person in the room. Let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem", id = "quote_082"),
    Quote("Starting over does not erase what the first attempt taught you. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_083"),
    Quote("You do not need to feel inspired to begin. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_084"),
    Quote("Some peace comes from letting a moment be enough. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_085"),
    Quote("Consistency is easier when the next step is clear. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_086"),
    Quote("Your body is part of your life, not a project you must finish. You can be healing and still have moments that feel familiar.", "Unknown", "Self-Love", id = "quote_087"),
    Quote("You do not have to understand the whole lesson while you are living it. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_088"),
    Quote("Joy can exist alongside worry without canceling it out. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_089"),
    Quote("Uncertainty is difficult, but it is not the same as danger. Surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges", id = "quote_090"),
    Quote("You can take responsibility without speaking to yourself cruelly. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_091"),
    Quote("You do not have to force a bright side onto every disappointment. A difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity", id = "quote_092"),
    Quote("You do not have to reach exhaustion before you are allowed to rest. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_093"),
    Quote("Not being chosen by someone is not the same as being unworthy. You do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem", id = "quote_094"),
    Quote("Some peace comes from letting a moment be enough. An imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity", id = "quote_095"),
    Quote("Not being chosen by someone is not the same as being unworthy. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_096"),
    Quote("A short focused effort can break the spell of postponing. Returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity", id = "quote_097"),
    Quote("Not every good day needs to be memorable to matter. A difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity", id = "quote_098"),
    Quote("Some feelings need room before they need solutions. You can be healing and still have moments that feel familiar.", "Unknown", "Self-Love", id = "quote_099"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_100"),
    Quote("A setback can change your route without canceling your destination. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_101"),
    Quote("Your body is part of your life, not a project you must finish. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_102"),
    Quote("You are allowed to enjoy something before you have earned a break. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_103"),
    Quote("You can feel uncertain and still make a sound decision. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_104"),
    Quote("Your life can contain unfinished things and genuine happiness. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_105"),
    Quote("Some feelings need room before they need solutions. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_106"),
    Quote("You are allowed to enjoy something before you have earned a break. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_107"),
    Quote("Being misunderstood can hurt without making you wrong. You do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem", id = "quote_108"),
    Quote("Uncertainty is difficult, but it is not the same as danger. Let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges", id = "quote_109"),
    Quote("Some progress is quiet enough to be mistaken for no progress. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_110"),
    Quote("You can admit that something hurts without letting it define everything. Surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges", id = "quote_111"),
    Quote("You can care about someone and still need distance. You can be healing and still have moments that feel familiar.", "Unknown", "Self-Love", id = "quote_112"),
    Quote("You are allowed to pause before deciding what comes next. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_113"),
    Quote("Joy can exist alongside worry without canceling it out. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_114"),
    Quote("Preparation helps until it becomes a way to avoid starting. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_115"),
    Quote("Some peace comes from letting a moment be enough. A difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity", id = "quote_116"),
    Quote("Other people's opinions are only one part of the evidence. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_117"),
    Quote("A task often becomes less intimidating when you define its first step. Progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity", id = "quote_118"),
    Quote("You are allowed to enjoy something before you have earned a break. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_119"),
    Quote("Some problems need a different approach rather than more force. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_120"),
    Quote("A short focused effort can break the spell of postponing. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_121"),
    Quote("You can care about someone and still need distance. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_122"),
    Quote("Starting over does not erase what the first attempt taught you. Let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges", id = "quote_123"),
    Quote("Some feelings need room before they need solutions. Protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love", id = "quote_124"),
    Quote("You can feel uncertain and still make a sound decision. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_125"),
    Quote("Ordinary moments often become meaningful only after we pay attention. Peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity", id = "quote_126"),
    Quote("A goal needs room in your calendar, not only in your imagination. Measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity", id = "quote_127"),
    Quote("Gratitude is more honest when it leaves room for what is hard. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_128"),
    Quote("You can feel uncertain and still make a sound decision. Being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem", id = "quote_129"),
    Quote("A small comfort is still worth noticing. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_130"),
    Quote("A goal needs room in your calendar, not only in your imagination. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_131"),
    Quote("Your voice does not need to be loud to deserve a place. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_132"),
    Quote("An ending can be painful even when it is the right one. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_133"),
    Quote("You can regret a choice without punishing yourself forever. You do not have to be endlessly available to be a good person.", "Unknown", "Self-Love", id = "quote_134"),
    Quote("You do not have to understand the whole lesson while you are living it. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_135"),
    Quote("Consistency is easier when the next step is clear. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_136"),
    Quote("Being misunderstood can hurt without making you wrong. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_137"),
    Quote("Preparation helps until it becomes a way to avoid starting. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_138"),
    Quote("You can learn from criticism without accepting every judgment. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_139"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_140"),
    Quote("Not every good day needs to be memorable to matter. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_141"),
    Quote("Some problems need a different approach rather than more force. Let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges", id = "quote_142"),
    Quote("You can feel uncertain and still make a sound decision. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_143"),
    Quote("You can be tired of the struggle and still be making progress. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_144"),
    Quote("You do not have to be the most confident person in the room. You do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem", id = "quote_145"),
    Quote("Hope can be practical rather than certain. An imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity", id = "quote_146"),
    Quote("Preparation helps until it becomes a way to avoid starting. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_147"),
    Quote("You do not need to make your pain useful before it can heal. You do not have to be endlessly available to be a good person.", "Unknown", "Self-Love", id = "quote_148"),
    Quote("You can feel uncertain and still make a sound decision. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_149"),
    Quote("A task often becomes less intimidating when you define its first step. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_150"),
    Quote("You are allowed to pause before deciding what comes next. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_151"),
    Quote("You do not have to force a bright side onto every disappointment. An imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity", id = "quote_152"),
    Quote("A slow recovery is still a recovery. Give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges", id = "quote_153"),
    Quote("Being misunderstood can hurt without making you wrong. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_154"),
    Quote("You do not have to reach exhaustion before you are allowed to rest. You do not have to be endlessly available to be a good person.", "Unknown", "Self-Love", id = "quote_155"),
    Quote("Some problems need a different approach rather than more force. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_156"),
    Quote("Your needs still matter when other people have needs too. Protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love", id = "quote_157"),
    Quote("A slow recovery is still a recovery. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_158"),
    Quote("If your routine keeps failing, look at its design before blaming yourself. Progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity", id = "quote_159"),
    Quote("A slow recovery is still a recovery. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_160"),
    Quote("Being new at something does not make you less capable. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_161"),
    Quote("You are allowed to enjoy something before you have earned a break. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_162"),
    Quote("An ending can be painful even when it is the right one. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_163"),
    Quote("Being misunderstood can hurt without making you wrong. A quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem", id = "quote_164"),
    Quote("Your body is part of your life, not a project you must finish. You deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love", id = "quote_165"),
    Quote("Preparation helps until it becomes a way to avoid starting. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_166"),
    Quote("A mistake can be real without becoming your identity. Let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem", id = "quote_167"),
    Quote("Gratitude is more honest when it leaves room for what is hard. A difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity", id = "quote_168"),
    Quote("A short focused effort can break the spell of postponing. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_169"),
    Quote("Your body is part of your life, not a project you must finish. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_170"),
    Quote("You can learn from criticism without accepting every judgment. You do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem", id = "quote_171"),
    Quote("You can admit that something hurts without letting it define everything. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_172"),
    Quote("A mistake can be real without becoming your identity. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_173"),
    Quote("A setback can change your route without canceling your destination. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_174"),
    Quote("A mistake can be real without becoming your identity. Being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem", id = "quote_175"),
    Quote("You do not need to make your pain useful before it can heal. You can be healing and still have moments that feel familiar.", "Unknown", "Self-Love", id = "quote_176"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_177"),
    Quote("A mistake can be real without becoming your identity. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_178"),
    Quote("Some feelings need room before they need solutions. You deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love", id = "quote_179"),
    Quote("You cannot give every task your best attention on the same day. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_180"),
    Quote("You can regret a choice without punishing yourself forever. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_181"),
    Quote("You can admit that something hurts without letting it define everything. Let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges", id = "quote_182"),
    Quote("Your voice does not need to be loud to deserve a place. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_183"),
    Quote("You can take responsibility without speaking to yourself cruelly. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_184"),
    Quote("Some peace comes from letting a moment be enough. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_185"),
    Quote("Being misunderstood can hurt without making you wrong. Being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem", id = "quote_186"),
    Quote("You are allowed to change what you can no longer carry. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_187"),
    Quote("A slow recovery is still a recovery. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_188"),
    Quote("Not every good day needs to be memorable to matter. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_189"),
    Quote("You can learn from criticism without accepting every judgment. A quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem", id = "quote_190"),
    Quote("Hope can be practical rather than certain. Peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity", id = "quote_191"),
    Quote("You do not have to understand the whole lesson while you are living it. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_192"),
    Quote("You can learn from criticism without accepting every judgment. Confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem", id = "quote_193"),
    Quote("Joy can exist alongside worry without canceling it out. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_194"),
    Quote("Starting over does not erase what the first attempt taught you. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_195"),
    Quote("You do not have to force a bright side onto every disappointment. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_196"),
    Quote("Some problems need a different approach rather than more force. Give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges", id = "quote_197"),
    Quote("You do not need to feel inspired to begin. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_198"),
    Quote("A small comfort is still worth noticing. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_199"),
    Quote("Being misunderstood can hurt without making you wrong. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_200"),
    Quote("You do not have to force a bright side onto every disappointment. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_201"),
    Quote("Other people's opinions are only one part of the evidence. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_202"),
    Quote("Some progress is quiet enough to be mistaken for no progress. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_203"),
    Quote("You can admit that something hurts without letting it define everything. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_204"),
    Quote("Hope can be practical rather than certain. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_205"),
    Quote("Other people's opinions are only one part of the evidence. Confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem", id = "quote_206"),
    Quote("A short focused effort can break the spell of postponing. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_207"),
    Quote("You are allowed to take up space before you feel ready. Let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem", id = "quote_208"),
    Quote("You are allowed to change what you can no longer carry. One difficult day is not a reliable summary of your life.", "Unknown", "Self-Love", id = "quote_209"),
    Quote("An ending can be painful even when it is the right one. Give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges", id = "quote_210"),
    Quote("You cannot give every task your best attention on the same day. Returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity", id = "quote_211"),
    Quote("You do not have to understand the whole lesson while you are living it. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_212"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity", id = "quote_213"),
    Quote("Your life can contain unfinished things and genuine happiness. An imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity", id = "quote_214"),
    Quote("Being new at something does not make you less capable. Confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem", id = "quote_215"),
    Quote("Rest does not have to be earned through suffering. You can be healing and still have moments that feel familiar.", "Unknown", "Self-Love", id = "quote_216"),
    Quote("A setback can change your route without canceling your destination. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_217"),
    Quote("Gratitude is more honest when it leaves room for what is hard. An imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity", id = "quote_218"),
    Quote("Some feelings need room before they need solutions. You do not have to be endlessly available to be a good person.", "Unknown", "Self-Love", id = "quote_219"),
    Quote("Gratitude is more honest when it leaves room for what is hard. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_220"),
    Quote("You can feel uncertain and still make a sound decision. You do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem", id = "quote_221"),
    Quote("If your routine keeps failing, look at its design before blaming yourself. Measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity", id = "quote_222"),
    Quote("Not every good day needs to be memorable to matter. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_223"),
    Quote("Rest does not have to be earned through suffering. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_224"),
    Quote("Your life can contain unfinished things and genuine happiness. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_225"),
    Quote("A slow recovery is still a recovery. Surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges", id = "quote_226"),
    Quote("Being misunderstood can hurt without making you wrong. Confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem", id = "quote_227"),
    Quote("If your routine keeps failing, look at its design before blaming yourself. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_228"),
    Quote("Being new at something does not make you less capable. A quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem", id = "quote_229"),
    Quote("A small comfort is still worth noticing. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_230"),
    Quote("A short focused effort can break the spell of postponing. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_231"),
    Quote("You are allowed to take up space before you feel ready. A quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem", id = "quote_232"),
    Quote("A goal needs room in your calendar, not only in your imagination. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_233"),
    Quote("Being new at something does not make you less capable. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_234"),
    Quote("You cannot give every task your best attention on the same day. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_235"),
    Quote("Uncertainty is difficult, but it is not the same as danger. Give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges", id = "quote_236"),
    Quote("You can feel uncertain and still make a sound decision. A quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem", id = "quote_237"),
    Quote("Joy can exist alongside worry without canceling it out. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_238"),
    Quote("You can learn from criticism without accepting every judgment. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_239"),
    Quote("Some feelings need room before they need solutions. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_240"),
    Quote("A task often becomes less intimidating when you define its first step. Returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity", id = "quote_241"),
    Quote("You can care about someone and still need distance. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_242"),
    Quote("A task often becomes less intimidating when you define its first step. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_243"),
    Quote("You are allowed to pause before deciding what comes next. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_244"),
    Quote("You are allowed to enjoy something before you have earned a break. An imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity", id = "quote_245"),
    Quote("You are allowed to pause before deciding what comes next. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_246"),
    Quote("Not every good day needs to be memorable to matter. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_247"),
    Quote("Your body is part of your life, not a project you must finish. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_248"),
    Quote("Being new at something does not make you less capable. Being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem", id = "quote_249"),
    Quote("You can admit that something hurts without letting it define everything. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_250"),
    Quote("You are allowed to enjoy something before you have earned a break. A difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity", id = "quote_251"),
    Quote("You do not have to be the most confident person in the room. Being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem", id = "quote_252"),
    Quote("A task often becomes less intimidating when you define its first step. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_253"),
    Quote("You do not have to understand the whole lesson while you are living it. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_254"),
    Quote("Some feelings need room before they need solutions. One difficult day is not a reliable summary of your life.", "Unknown", "Self-Love", id = "quote_255"),
    Quote("A goal needs room in your calendar, not only in your imagination. Progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity", id = "quote_256"),
    Quote("An ending can be painful even when it is the right one. Let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges", id = "quote_257"),
    Quote("A goal needs room in your calendar, not only in your imagination. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_258"),
    Quote("You are allowed to take up space before you feel ready. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_259"),
    Quote("Rest does not have to be earned through suffering. One difficult day is not a reliable summary of your life.", "Unknown", "Self-Love", id = "quote_260"),
    Quote("Being new at something does not make you less capable. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_261"),
    Quote("Consistency is easier when the next step is clear. Progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity", id = "quote_262"),
    Quote("Not every good day needs to be memorable to matter. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_263"),
    Quote("You can care about someone and still need distance. Protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love", id = "quote_264"),
    Quote("Not being chosen by someone is not the same as being unworthy. Confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem", id = "quote_265"),
    Quote("You can admit that something hurts without letting it define everything. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_266"),
    Quote("Your voice does not need to be loud to deserve a place. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_267"),
    Quote("You do not have to force a bright side onto every disappointment. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_268"),
    Quote("You are allowed to pause before deciding what comes next. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_269"),
    Quote("Ordinary moments often become meaningful only after we pay attention. A difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity", id = "quote_270"),
    Quote("Starting over does not erase what the first attempt taught you. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_271"),
    Quote("You are allowed to enjoy something before you have earned a break. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_272"),
    Quote("A mistake can be real without becoming your identity. A quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem", id = "quote_273"),
    Quote("Preparation helps until it becomes a way to avoid starting. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_274"),
    Quote("A setback can change your route without canceling your destination. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_275"),
    Quote("You are allowed to change what you can no longer carry. You can be healing and still have moments that feel familiar.", "Unknown", "Self-Love", id = "quote_276"),
    Quote("If your routine keeps failing, look at its design before blaming yourself. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_277"),
    Quote("You can be tired of the struggle and still be making progress. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_278"),
    Quote("Ordinary moments often become meaningful only after we pay attention. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_279"),
    Quote("Your voice does not need to be loud to deserve a place. You do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem", id = "quote_280"),
    Quote("Your body is part of your life, not a project you must finish. One difficult day is not a reliable summary of your life.", "Unknown", "Self-Love", id = "quote_281"),
    Quote("Some progress is quiet enough to be mistaken for no progress. Returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity", id = "quote_282"),
    Quote("You do not need to make your pain useful before it can heal. You deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love", id = "quote_283"),
    Quote("Some peace comes from letting a moment be enough. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_284"),
    Quote("An ending can be painful even when it is the right one. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_285"),
    Quote("Hope can be practical rather than certain. A difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity", id = "quote_286"),
    Quote("You do not have to be the most confident person in the room. A quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem", id = "quote_287"),
    Quote("Some peace comes from letting a moment be enough. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_288"),
    Quote("You do not have to reach exhaustion before you are allowed to rest. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_289"),
    Quote("Preparation helps until it becomes a way to avoid starting. Progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity", id = "quote_290"),
    Quote("Other people's opinions are only one part of the evidence. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_291"),
    Quote("If your routine keeps failing, look at its design before blaming yourself. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_292"),
    Quote("You are allowed to take up space before you feel ready. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_293"),
    Quote("Gratitude is more honest when it leaves room for what is hard. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_294"),
    Quote("You are allowed to change what you can no longer carry. Protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love", id = "quote_295"),
    Quote("You do not need to feel inspired to begin. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_296"),
    Quote("You are allowed to pause before deciding what comes next. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_297"),
    Quote("You are allowed to enjoy something before you have earned a break. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_298"),
    Quote("You can care about someone and still need distance. One difficult day is not a reliable summary of your life.", "Unknown", "Self-Love", id = "quote_299"),
    Quote("Your life can contain unfinished things and genuine happiness. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_300"),
    Quote("Other people's opinions are only one part of the evidence. Let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem", id = "quote_301"),
    Quote("You can take responsibility without speaking to yourself cruelly. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_302"),
    Quote("If your routine keeps failing, look at its design before blaming yourself. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_303"),
    Quote("Gratitude is more honest when it leaves room for what is hard. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_304"),
    Quote("Being new at something does not make you less capable. You can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem", id = "quote_305"),
    Quote("A small comfort is still worth noticing. Peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity", id = "quote_306"),
    Quote("Some progress is quiet enough to be mistaken for no progress. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_307"),
    Quote("You do not need to make your pain useful before it can heal. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_308"),
    Quote("Some progress is quiet enough to be mistaken for no progress. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_309"),
    Quote("Some peace comes from letting a moment be enough. Peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity", id = "quote_310"),
    Quote("Your voice does not need to be loud to deserve a place. Being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem", id = "quote_311"),
    Quote("Some peace comes from letting a moment be enough. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_312"),
    Quote("Not being chosen by someone is not the same as being unworthy. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_313"),
    Quote("A task often becomes less intimidating when you define its first step. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_314"),
    Quote("Hope can be practical rather than certain. Let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity", id = "quote_315"),
    Quote("Not being chosen by someone is not the same as being unworthy. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_316"),
    Quote("Rest does not have to be earned through suffering. You do not have to be endlessly available to be a good person.", "Unknown", "Self-Love", id = "quote_317"),
    Quote("A setback can change your route without canceling your destination. Let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges", id = "quote_318"),
    Quote("Some progress is quiet enough to be mistaken for no progress. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_319"),
    Quote("Your body is part of your life, not a project you must finish. You do not have to be endlessly available to be a good person.", "Unknown", "Self-Love", id = "quote_320"),
    Quote("You do not need to feel inspired to begin. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_321"),
    Quote("A small comfort is still worth noticing. A difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity", id = "quote_322"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity", id = "quote_323"),
    Quote("Being misunderstood can hurt without making you wrong. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_324"),
    Quote("A goal needs room in your calendar, not only in your imagination. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_325"),
    Quote("You can care about someone and still need distance. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_326"),
    Quote("Consistency is easier when the next step is clear. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_327"),
    Quote("Rest does not have to be earned through suffering. Protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love", id = "quote_328"),
    Quote("A mistake can be real without becoming your identity. You do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem", id = "quote_329"),
    Quote("Joy can exist alongside worry without canceling it out. An imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity", id = "quote_330"),
    Quote("Starting over does not erase what the first attempt taught you. Give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges", id = "quote_331"),
    Quote("Some peace comes from letting a moment be enough. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_332"),
    Quote("Your voice does not need to be loud to deserve a place. A quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem", id = "quote_333"),
    Quote("You are allowed to change what you can no longer carry. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_334"),
    Quote("Not being chosen by someone is not the same as being unworthy. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_335"),
    Quote("Your needs still matter when other people have needs too. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_336"),
    Quote("A slow recovery is still a recovery. Let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges", id = "quote_337"),
    Quote("Your voice does not need to be loud to deserve a place. Confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem", id = "quote_338"),
    Quote("Your needs still matter when other people have needs too. You do not have to be endlessly available to be a good person.", "Unknown", "Self-Love", id = "quote_339"),
    Quote("You can be tired of the struggle and still be making progress. Let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges", id = "quote_340"),
    Quote("Ordinary moments often become meaningful only after we pay attention. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_341"),
    Quote("Some problems need a different approach rather than more force. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_342"),
    Quote("Gratitude is more honest when it leaves room for what is hard. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_343"),
    Quote("You can take responsibility without speaking to yourself cruelly. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_344"),
    Quote("Not every good day needs to be memorable to matter. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_345"),
    Quote("You do not have to understand the whole lesson while you are living it. You are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges", id = "quote_346"),
    Quote("Consistency is easier when the next step is clear. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_347"),
    Quote("A slow recovery is still a recovery. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_348"),
    Quote("Consistency is easier when the next step is clear. Measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity", id = "quote_349"),
    Quote("You do not have to be the most confident person in the room. Confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem", id = "quote_350"),
    Quote("You do not have to reach exhaustion before you are allowed to rest. One difficult day is not a reliable summary of your life.", "Unknown", "Self-Love", id = "quote_351"),
    Quote("A goal needs room in your calendar, not only in your imagination. Returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity", id = "quote_352"),
    Quote("Uncertainty is difficult, but it is not the same as danger. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_353"),
    Quote("A mistake can be real without becoming your identity. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_354"),
    Quote("You do not have to understand the whole lesson while you are living it. Give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges", id = "quote_355"),
    Quote("You can take responsibility without speaking to yourself cruelly. One difficult day is not a reliable summary of your life.", "Unknown", "Self-Love", id = "quote_356"),
    Quote("Hope can be practical rather than certain. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_357"),
    Quote("Being misunderstood can hurt without making you wrong. Let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem", id = "quote_358"),
    Quote("Some problems need a different approach rather than more force. Surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges", id = "quote_359"),
    Quote("You do not have to reach exhaustion before you are allowed to rest. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_360"),
    Quote("A small comfort is still worth noticing. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_361"),
    Quote("Some progress is quiet enough to be mistaken for no progress. Measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity", id = "quote_362"),
    Quote("You can regret a choice without punishing yourself forever. One difficult day is not a reliable summary of your life.", "Unknown", "Self-Love", id = "quote_363"),
    Quote("You do not need to feel inspired to begin. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_364"),
    Quote("You are allowed to pause before deciding what comes next. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_365"),
    Quote("You do not need to make your pain useful before it can heal. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_366"),
    Quote("You do not have to be the most confident person in the room. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_367"),
    Quote("Your needs still matter when other people have needs too. You deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love", id = "quote_368"),
    Quote("Your life can contain unfinished things and genuine happiness. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_369"),
    Quote("You can learn from criticism without accepting every judgment. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_370"),
    Quote("You do not need to make your pain useful before it can heal. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_371"),
    Quote("Some progress is quiet enough to be mistaken for no progress. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_372"),
    Quote("An ending can be painful even when it is the right one. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_373"),
    Quote("Your needs still matter when other people have needs too. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_374"),
    Quote("Your life can contain unfinished things and genuine happiness. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_375"),
    Quote("You do not need to make your pain useful before it can heal. Protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love", id = "quote_376"),
    Quote("A short focused effort can break the spell of postponing. Progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity", id = "quote_377"),
    Quote("Starting over does not erase what the first attempt taught you. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_378"),
    Quote("Joy can exist alongside worry without canceling it out. A difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity", id = "quote_379"),
    Quote("Preparation helps until it becomes a way to avoid starting. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_380"),
    Quote("Your voice does not need to be loud to deserve a place. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_381"),
    Quote("You do not need to make your pain useful before it can heal. One difficult day is not a reliable summary of your life.", "Unknown", "Self-Love", id = "quote_382"),
    Quote("You are allowed to enjoy something before you have earned a break. Peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity", id = "quote_383"),
    Quote("A mistake can be real without becoming your identity. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_384"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_385"),
    Quote("Starting over does not erase what the first attempt taught you. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_386"),
    Quote("A task often becomes less intimidating when you define its first step. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_387"),
    Quote("Not being chosen by someone is not the same as being unworthy. Being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem", id = "quote_388"),
    Quote("You do not have to reach exhaustion before you are allowed to rest. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_389"),
    Quote("Being new at something does not make you less capable. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_390"),
    Quote("A goal needs room in your calendar, not only in your imagination. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_391"),
    Quote("You do not have to reach exhaustion before you are allowed to rest. You can be healing and still have moments that feel familiar.", "Unknown", "Self-Love", id = "quote_392"),
    Quote("A setback can change your route without canceling your destination. Give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges", id = "quote_393"),
    Quote("You can regret a choice without punishing yourself forever. Protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love", id = "quote_394"),
    Quote("You can admit that something hurts without letting it define everything. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_395"),
    Quote("Ordinary moments often become meaningful only after we pay attention. An imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity", id = "quote_396"),
    Quote("You are allowed to change what you can no longer carry. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_397"),
    Quote("You are allowed to take up space before you feel ready. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_398"),
    Quote("Hope can be practical rather than certain. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_399"),
    Quote("Your voice does not need to be loud to deserve a place. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_400"),
    Quote("Preparation helps until it becomes a way to avoid starting. Returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity", id = "quote_401"),
    Quote("You do not need to make your pain useful before it can heal. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_402"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity", id = "quote_403"),
    Quote("Other people's opinions are only one part of the evidence. A quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem", id = "quote_404"),
    Quote("Preparation helps until it becomes a way to avoid starting. Measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity", id = "quote_405"),
    Quote("Hope can be practical rather than certain. You can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity", id = "quote_406"),
    Quote("If your routine keeps failing, look at its design before blaming yourself. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_407"),
    Quote("Uncertainty is difficult, but it is not the same as danger. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_408"),
    Quote("You do not have to force a bright side onto every disappointment. Peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity", id = "quote_409"),
    Quote("You cannot give every task your best attention on the same day. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_410"),
    Quote("You are allowed to change what you can no longer carry. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_411"),
    Quote("Ordinary moments often become meaningful only after we pay attention. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_412"),
    Quote("Consistency is easier when the next step is clear. Let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity", id = "quote_413"),
    Quote("Other people's opinions are only one part of the evidence. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_414"),
    Quote("You do not have to understand the whole lesson while you are living it. Surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges", id = "quote_415"),
    Quote("You can regret a choice without punishing yourself forever. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_416"),
    Quote("You do not have to be the most confident person in the room. One person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem", id = "quote_417"),
    Quote("A small comfort is still worth noticing. Notice what made today a little more livable.", "Unknown", "Happiness and Positivity", id = "quote_418"),
    Quote("You are allowed to pause before deciding what comes next. Give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges", id = "quote_419"),
    Quote("You are allowed to change what you can no longer carry. You do not have to be endlessly available to be a good person.", "Unknown", "Self-Love", id = "quote_420"),
    Quote("You cannot give every task your best attention on the same day. Measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity", id = "quote_421"),
    Quote("You can admit that something hurts without letting it define everything. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_422"),
    Quote("Other people's opinions are only one part of the evidence. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_423"),
    Quote("Some feelings need room before they need solutions. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_424"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_425"),
    Quote("An ending can be painful even when it is the right one. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_426"),
    Quote("Your needs still matter when other people have needs too. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_427"),
    Quote("Some progress is quiet enough to be mistaken for no progress. Progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity", id = "quote_428"),
    Quote("Gratitude is more honest when it leaves room for what is hard. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_429"),
    Quote("You cannot give every task your best attention on the same day. Progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity", id = "quote_430"),
    Quote("You can take responsibility without speaking to yourself cruelly. You can be healing and still have moments that feel familiar.", "Unknown", "Self-Love", id = "quote_431"),
    Quote("You cannot give every task your best attention on the same day. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_432"),
    Quote("A setback can change your route without canceling your destination. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_433"),
    Quote("You can take responsibility without speaking to yourself cruelly. You deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love", id = "quote_434"),
    Quote("Not being chosen by someone is not the same as being unworthy. A quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem", id = "quote_435"),
    Quote("A setback can change your route without canceling your destination. Surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges", id = "quote_436"),
    Quote("You can take responsibility without speaking to yourself cruelly. You do not have to be endlessly available to be a good person.", "Unknown", "Self-Love", id = "quote_437"),
    Quote("You are allowed to take up space before you feel ready. You do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem", id = "quote_438"),
    Quote("Some feelings need room before they need solutions. Patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love", id = "quote_439"),
    Quote("Being new at something does not make you less capable. Let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem", id = "quote_440"),
    Quote("Some problems need a different approach rather than more force. You can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges", id = "quote_441"),
    Quote("A mistake can be real without becoming your identity. Self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem", id = "quote_442"),
    Quote("You do not need to feel inspired to begin. Returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity", id = "quote_443"),
    Quote("Being misunderstood can hurt without making you wrong. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_444"),
    Quote("Your body is part of your life, not a project you must finish. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_445"),
    Quote("A slow recovery is still a recovery. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_446"),
    Quote("You can feel uncertain and still make a sound decision. Confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem", id = "quote_447"),
    Quote("You are allowed to enjoy something before you have earned a break. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_448"),
    Quote("You can be tired of the struggle and still be making progress. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_449"),
    Quote("You do not have to reach exhaustion before you are allowed to rest. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_450"),
    Quote("A setback can change your route without canceling your destination. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_451"),
    Quote("You cannot give every task your best attention on the same day. Finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity", id = "quote_452"),
    Quote("Starting over does not erase what the first attempt taught you. Sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges", id = "quote_453"),
    Quote("Not every good day needs to be memorable to matter. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_454"),
    Quote("Your body is part of your life, not a project you must finish. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_455"),
    Quote("Your life can contain unfinished things and genuine happiness. Contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity", id = "quote_456"),
    Quote("You are allowed to take up space before you feel ready. Being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem", id = "quote_457"),
    Quote("You do not have to force a bright side onto every disappointment. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_458"),
    Quote("Some problems need a different approach rather than more force. One setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges", id = "quote_459"),
    Quote("You can learn from criticism without accepting every judgment. Let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem", id = "quote_460"),
    Quote("Joy can exist alongside worry without canceling it out. Peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity", id = "quote_461"),
    Quote("You do not have to reach exhaustion before you are allowed to rest. Protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love", id = "quote_462"),
    Quote("Uncertainty is difficult, but it is not the same as danger. Ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges", id = "quote_463"),
    Quote("You can regret a choice without punishing yourself forever. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_464"),
    Quote("Other people's opinions are only one part of the evidence. Being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem", id = "quote_465"),
    Quote("You do not have to force a bright side onto every disappointment. Make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity", id = "quote_466"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity", id = "quote_467"),
    Quote("You do not have to understand the whole lesson while you are living it. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_468"),
    Quote("You can care about someone and still need distance. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_469"),
    Quote("You can feel uncertain and still make a sound decision. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_470"),
    Quote("Rest does not have to be earned through suffering. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_471"),
    Quote("You can learn from criticism without accepting every judgment. You can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem", id = "quote_472"),
    Quote("Ordinary moments often become meaningful only after we pay attention. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_473"),
    Quote("You do not have to be the most confident person in the room. Trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem", id = "quote_474"),
    Quote("Uncertainty is difficult, but it is not the same as danger. Look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges", id = "quote_475"),
    Quote("You cannot give every task your best attention on the same day. Tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity", id = "quote_476"),
    Quote("You can be tired of the struggle and still be making progress. Give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges", id = "quote_477"),
    Quote("Ordinary moments often become meaningful only after we pay attention. Let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity", id = "quote_478"),
    Quote("Consistency is easier when the next step is clear. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_479"),
    Quote("You are allowed to take up space before you feel ready. Confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem", id = "quote_480"),
    Quote("You can be tired of the struggle and still be making progress. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_481"),
    Quote("A mistake can be real without becoming your identity. Confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem", id = "quote_482"),
    Quote("Rest does not have to be earned through suffering. A boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love", id = "quote_483"),
    Quote("You do not have to understand the whole lesson while you are living it. Let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges", id = "quote_484"),
    Quote("Your life can contain unfinished things and genuine happiness. A difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity", id = "quote_485"),
    Quote("Some problems need a different approach rather than more force. Changing direction can be a form of courage.", "Unknown", "Overcoming Challenges", id = "quote_486"),
    Quote("Hope can be practical rather than certain. Small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity", id = "quote_487"),
    Quote("A goal needs room in your calendar, not only in your imagination. You can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity", id = "quote_488"),
    Quote("You can feel uncertain and still make a sound decision. Let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem", id = "quote_489"),
    Quote("Your needs still matter when other people have needs too. One difficult day is not a reliable summary of your life.", "Unknown", "Self-Love", id = "quote_490"),
    Quote("You are allowed to pause before deciding what comes next. Let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges", id = "quote_491"),
    Quote("You can regret a choice without punishing yourself forever. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_492"),
    Quote("A task often becomes less intimidating when you define its first step. Choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity", id = "quote_493"),
    Quote("Not every good day needs to be memorable to matter. Peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity", id = "quote_494"),
    Quote("Some feelings need room before they need solutions. Kindness toward yourself can include making a hard change.", "Unknown", "Self-Love", id = "quote_495"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy. Give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity", id = "quote_496"),
    Quote("Your needs still matter when other people have needs too. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_497"),
    Quote("You can take responsibility without speaking to yourself cruelly. Changing your mind can be part of learning what you need.", "Unknown", "Self-Love", id = "quote_498"),
    Quote("You can care about someone and still need distance. You do not have to be endlessly available to be a good person.", "Unknown", "Self-Love", id = "quote_499"),
    Quote("You can regret a choice without punishing yourself forever. You can ask for help without surrendering your independence.", "Unknown", "Self-Love", id = "quote_500")
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
        "https://play.google.com/apps/testing/com.brightdays.dailymotivation",
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