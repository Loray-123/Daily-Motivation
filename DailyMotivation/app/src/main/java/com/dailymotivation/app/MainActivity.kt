
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
    val category: String = categorizeQuote(text)
)

// Short, easy-to-understand motivational quotes without religious references.
// The first eight entries stay in place to preserve existing favorite indexes.
val quotes = listOf(
    Quote("Being new at something does not make you less capable; let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being new at something does not make you less capable, and confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being new at something does not make you less capable—one person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being new at something does not make you less capable, which means self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being new at something does not make you less capable; remember, you can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being new at something does not make you less capable; a quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being new at something does not make you less capable, and you do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being new at something does not make you less capable—being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being new at something does not make you less capable, which means you can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being new at something does not make you less capable; remember, trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can feel uncertain and still make a sound decision; self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can feel uncertain and still make a sound decision, and you can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can feel uncertain and still make a sound decision—a quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can feel uncertain and still make a sound decision, which means you do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can feel uncertain and still make a sound decision; remember, being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can feel uncertain and still make a sound decision; you can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can feel uncertain and still make a sound decision, and trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can feel uncertain and still make a sound decision—let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can feel uncertain and still make a sound decision, which means confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can feel uncertain and still make a sound decision; remember, one person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Other people's opinions are only one part of the evidence; you do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Other people's opinions are only one part of the evidence, and being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Other people's opinions are only one part of the evidence—you can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Other people's opinions are only one part of the evidence, which means trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Other people's opinions are only one part of the evidence; remember, let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Other people's opinions are only one part of the evidence; confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Other people's opinions are only one part of the evidence, and one person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Other people's opinions are only one part of the evidence—self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Other people's opinions are only one part of the evidence, which means you can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Other people's opinions are only one part of the evidence; remember, a quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem"),
    Quote("A mistake can be real without becoming your identity; trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem"),
    Quote("A mistake can be real without becoming your identity, and let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem"),
    Quote("A mistake can be real without becoming your identity—confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem"),
    Quote("A mistake can be real without becoming your identity, which means one person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem"),
    Quote("A mistake can be real without becoming your identity; remember, self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem"),
    Quote("A mistake can be real without becoming your identity; you can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem"),
    Quote("A mistake can be real without becoming your identity, and a quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem"),
    Quote("A mistake can be real without becoming your identity—you do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem"),
    Quote("A mistake can be real without becoming your identity, which means being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem"),
    Quote("A mistake can be real without becoming your identity; remember, you can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You do not have to be the most confident person in the room; one person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You do not have to be the most confident person in the room, and self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You do not have to be the most confident person in the room—you can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You do not have to be the most confident person in the room, which means a quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You do not have to be the most confident person in the room; remember, you do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You do not have to be the most confident person in the room; being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You do not have to be the most confident person in the room, and you can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You do not have to be the most confident person in the room—trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You do not have to be the most confident person in the room, which means let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You do not have to be the most confident person in the room; remember, confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Your voice does not need to be loud to deserve a place; a quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Your voice does not need to be loud to deserve a place, and you do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Your voice does not need to be loud to deserve a place—being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Your voice does not need to be loud to deserve a place, which means you can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Your voice does not need to be loud to deserve a place; remember, trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Your voice does not need to be loud to deserve a place; let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Your voice does not need to be loud to deserve a place, and confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Your voice does not need to be loud to deserve a place—one person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Your voice does not need to be loud to deserve a place, which means self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Your voice does not need to be loud to deserve a place; remember, you can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You are allowed to take up space before you feel ready; you can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You are allowed to take up space before you feel ready, and trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You are allowed to take up space before you feel ready—let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You are allowed to take up space before you feel ready, which means confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You are allowed to take up space before you feel ready; remember, one person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You are allowed to take up space before you feel ready; self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You are allowed to take up space before you feel ready, and you can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You are allowed to take up space before you feel ready—a quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You are allowed to take up space before you feel ready, which means you do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You are allowed to take up space before you feel ready; remember, being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Not being chosen by someone is not the same as being unworthy; confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Not being chosen by someone is not the same as being unworthy, and one person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Not being chosen by someone is not the same as being unworthy—self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Not being chosen by someone is not the same as being unworthy, which means you can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Not being chosen by someone is not the same as being unworthy; remember, a quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Not being chosen by someone is not the same as being unworthy; you do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Not being chosen by someone is not the same as being unworthy, and being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Not being chosen by someone is not the same as being unworthy—you can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Not being chosen by someone is not the same as being unworthy, which means trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Not being chosen by someone is not the same as being unworthy; remember, let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can learn from criticism without accepting every judgment; you can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can learn from criticism without accepting every judgment, and a quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can learn from criticism without accepting every judgment—you do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can learn from criticism without accepting every judgment, which means being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can learn from criticism without accepting every judgment; remember, you can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can learn from criticism without accepting every judgment; trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can learn from criticism without accepting every judgment, and let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can learn from criticism without accepting every judgment—confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can learn from criticism without accepting every judgment, which means one person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can learn from criticism without accepting every judgment; remember, self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being misunderstood can hurt without making you wrong; being willing to learn is a strength, not an admission of failure.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being misunderstood can hurt without making you wrong, and you can outgrow an old role without apologizing for changing.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being misunderstood can hurt without making you wrong—trust is built by keeping small promises to yourself.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being misunderstood can hurt without making you wrong, which means let your next action tell a fuller story than your first doubt.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being misunderstood can hurt without making you wrong; remember, confidence is often built after you begin, not before.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being misunderstood can hurt without making you wrong; one person's limits for you do not define your own.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being misunderstood can hurt without making you wrong, and self-respect grows when you stop arguing against your own worth.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being misunderstood can hurt without making you wrong—you can be a work in progress and still be taken seriously.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being misunderstood can hurt without making you wrong, which means a quiet accomplishment counts even when nobody notices.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being misunderstood can hurt without making you wrong; remember, you do not need universal approval to make a thoughtful choice.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You do not have to reach exhaustion before you are allowed to rest; patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love"),
    Quote("You do not have to reach exhaustion before you are allowed to rest, and a boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love"),
    Quote("You do not have to reach exhaustion before you are allowed to rest—one difficult day is not a reliable summary of your life.", "Unknown", "Self-Love"),
    Quote("You do not have to reach exhaustion before you are allowed to rest, which means you can ask for help without surrendering your independence.", "Unknown", "Self-Love"),
    Quote("You do not have to reach exhaustion before you are allowed to rest; remember, protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love"),
    Quote("You do not have to reach exhaustion before you are allowed to rest; you deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love"),
    Quote("You do not have to reach exhaustion before you are allowed to rest, and changing your mind can be part of learning what you need.", "Unknown", "Self-Love"),
    Quote("You do not have to reach exhaustion before you are allowed to rest—you can be healing and still have moments that feel familiar.", "Unknown", "Self-Love"),
    Quote("You do not have to reach exhaustion before you are allowed to rest, which means kindness toward yourself can include making a hard change.", "Unknown", "Self-Love"),
    Quote("You do not have to reach exhaustion before you are allowed to rest; remember, you do not have to be endlessly available to be a good person.", "Unknown", "Self-Love"),
    Quote("You can regret a choice without punishing yourself forever; you can ask for help without surrendering your independence.", "Unknown", "Self-Love"),
    Quote("You can regret a choice without punishing yourself forever, and protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love"),
    Quote("You can regret a choice without punishing yourself forever—you deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love"),
    Quote("You can regret a choice without punishing yourself forever, which means changing your mind can be part of learning what you need.", "Unknown", "Self-Love"),
    Quote("You can regret a choice without punishing yourself forever; remember, you can be healing and still have moments that feel familiar.", "Unknown", "Self-Love"),
    Quote("You can regret a choice without punishing yourself forever; kindness toward yourself can include making a hard change.", "Unknown", "Self-Love"),
    Quote("You can regret a choice without punishing yourself forever, and you do not have to be endlessly available to be a good person.", "Unknown", "Self-Love"),
    Quote("You can regret a choice without punishing yourself forever—patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love"),
    Quote("You can regret a choice without punishing yourself forever, which means a boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love"),
    Quote("You can regret a choice without punishing yourself forever; remember, one difficult day is not a reliable summary of your life.", "Unknown", "Self-Love"),
    Quote("Your needs still matter when other people have needs too; changing your mind can be part of learning what you need.", "Unknown", "Self-Love"),
    Quote("Your needs still matter when other people have needs too, and you can be healing and still have moments that feel familiar.", "Unknown", "Self-Love"),
    Quote("Your needs still matter when other people have needs too—kindness toward yourself can include making a hard change.", "Unknown", "Self-Love"),
    Quote("Your needs still matter when other people have needs too, which means you do not have to be endlessly available to be a good person.", "Unknown", "Self-Love"),
    Quote("Your needs still matter when other people have needs too; remember, patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love"),
    Quote("Your needs still matter when other people have needs too; a boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love"),
    Quote("Your needs still matter when other people have needs too, and one difficult day is not a reliable summary of your life.", "Unknown", "Self-Love"),
    Quote("Your needs still matter when other people have needs too—you can ask for help without surrendering your independence.", "Unknown", "Self-Love"),
    Quote("Your needs still matter when other people have needs too, which means protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love"),
    Quote("Your needs still matter when other people have needs too; remember, you deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love"),
    Quote("Some feelings need room before they need solutions; you do not have to be endlessly available to be a good person.", "Unknown", "Self-Love"),
    Quote("Some feelings need room before they need solutions, and patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love"),
    Quote("Some feelings need room before they need solutions—a boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love"),
    Quote("Some feelings need room before they need solutions, which means one difficult day is not a reliable summary of your life.", "Unknown", "Self-Love"),
    Quote("Some feelings need room before they need solutions; remember, you can ask for help without surrendering your independence.", "Unknown", "Self-Love"),
    Quote("Some feelings need room before they need solutions; protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love"),
    Quote("Some feelings need room before they need solutions, and you deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love"),
    Quote("Some feelings need room before they need solutions—changing your mind can be part of learning what you need.", "Unknown", "Self-Love"),
    Quote("Some feelings need room before they need solutions, which means you can be healing and still have moments that feel familiar.", "Unknown", "Self-Love"),
    Quote("Some feelings need room before they need solutions; remember, kindness toward yourself can include making a hard change.", "Unknown", "Self-Love"),
    Quote("You can care about someone and still need distance; one difficult day is not a reliable summary of your life.", "Unknown", "Self-Love"),
    Quote("You can care about someone and still need distance, and you can ask for help without surrendering your independence.", "Unknown", "Self-Love"),
    Quote("You can care about someone and still need distance—protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love"),
    Quote("You can care about someone and still need distance, which means you deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love"),
    Quote("You can care about someone and still need distance; remember, changing your mind can be part of learning what you need.", "Unknown", "Self-Love"),
    Quote("You can care about someone and still need distance; you can be healing and still have moments that feel familiar.", "Unknown", "Self-Love"),
    Quote("You can care about someone and still need distance, and kindness toward yourself can include making a hard change.", "Unknown", "Self-Love"),
    Quote("You can care about someone and still need distance—you do not have to be endlessly available to be a good person.", "Unknown", "Self-Love"),
    Quote("You can care about someone and still need distance, which means patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love"),
    Quote("You can care about someone and still need distance; remember, a boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love"),
    Quote("Your body is part of your life, not a project you must finish; you deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love"),
    Quote("Your body is part of your life, not a project you must finish, and changing your mind can be part of learning what you need.", "Unknown", "Self-Love"),
    Quote("Your body is part of your life, not a project you must finish—you can be healing and still have moments that feel familiar.", "Unknown", "Self-Love"),
    Quote("Your body is part of your life, not a project you must finish, which means kindness toward yourself can include making a hard change.", "Unknown", "Self-Love"),
    Quote("Your body is part of your life, not a project you must finish; remember, you do not have to be endlessly available to be a good person.", "Unknown", "Self-Love"),
    Quote("Your body is part of your life, not a project you must finish; patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love"),
    Quote("Your body is part of your life, not a project you must finish, and a boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love"),
    Quote("Your body is part of your life, not a project you must finish—one difficult day is not a reliable summary of your life.", "Unknown", "Self-Love"),
    Quote("Your body is part of your life, not a project you must finish, which means you can ask for help without surrendering your independence.", "Unknown", "Self-Love"),
    Quote("Your body is part of your life, not a project you must finish; remember, protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love"),
    Quote("You are allowed to change what you can no longer carry; kindness toward yourself can include making a hard change.", "Unknown", "Self-Love"),
    Quote("You are allowed to change what you can no longer carry, and you do not have to be endlessly available to be a good person.", "Unknown", "Self-Love"),
    Quote("You are allowed to change what you can no longer carry—patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love"),
    Quote("You are allowed to change what you can no longer carry, which means a boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love"),
    Quote("You are allowed to change what you can no longer carry; remember, one difficult day is not a reliable summary of your life.", "Unknown", "Self-Love"),
    Quote("You are allowed to change what you can no longer carry; you can ask for help without surrendering your independence.", "Unknown", "Self-Love"),
    Quote("You are allowed to change what you can no longer carry, and protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love"),
    Quote("You are allowed to change what you can no longer carry—you deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love"),
    Quote("You are allowed to change what you can no longer carry, which means changing your mind can be part of learning what you need.", "Unknown", "Self-Love"),
    Quote("You are allowed to change what you can no longer carry; remember, you can be healing and still have moments that feel familiar.", "Unknown", "Self-Love"),
    Quote("Rest does not have to be earned through suffering; a boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love"),
    Quote("Rest does not have to be earned through suffering, and one difficult day is not a reliable summary of your life.", "Unknown", "Self-Love"),
    Quote("Rest does not have to be earned through suffering—you can ask for help without surrendering your independence.", "Unknown", "Self-Love"),
    Quote("Rest does not have to be earned through suffering, which means protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love"),
    Quote("Rest does not have to be earned through suffering; remember, you deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love"),
    Quote("Rest does not have to be earned through suffering; changing your mind can be part of learning what you need.", "Unknown", "Self-Love"),
    Quote("Rest does not have to be earned through suffering, and you can be healing and still have moments that feel familiar.", "Unknown", "Self-Love"),
    Quote("Rest does not have to be earned through suffering—kindness toward yourself can include making a hard change.", "Unknown", "Self-Love"),
    Quote("Rest does not have to be earned through suffering, which means you do not have to be endlessly available to be a good person.", "Unknown", "Self-Love"),
    Quote("Rest does not have to be earned through suffering; remember, patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love"),
    Quote("You can take responsibility without speaking to yourself cruelly; protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love"),
    Quote("You can take responsibility without speaking to yourself cruelly, and you deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love"),
    Quote("You can take responsibility without speaking to yourself cruelly—changing your mind can be part of learning what you need.", "Unknown", "Self-Love"),
    Quote("You can take responsibility without speaking to yourself cruelly, which means you can be healing and still have moments that feel familiar.", "Unknown", "Self-Love"),
    Quote("You can take responsibility without speaking to yourself cruelly; remember, kindness toward yourself can include making a hard change.", "Unknown", "Self-Love"),
    Quote("You can take responsibility without speaking to yourself cruelly; you do not have to be endlessly available to be a good person.", "Unknown", "Self-Love"),
    Quote("You can take responsibility without speaking to yourself cruelly, and patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love"),
    Quote("You can take responsibility without speaking to yourself cruelly—a boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love"),
    Quote("You can take responsibility without speaking to yourself cruelly, which means one difficult day is not a reliable summary of your life.", "Unknown", "Self-Love"),
    Quote("You can take responsibility without speaking to yourself cruelly; remember, you can ask for help without surrendering your independence.", "Unknown", "Self-Love"),
    Quote("You do not need to make your pain useful before it can heal; you can be healing and still have moments that feel familiar.", "Unknown", "Self-Love"),
    Quote("You do not need to make your pain useful before it can heal, and kindness toward yourself can include making a hard change.", "Unknown", "Self-Love"),
    Quote("You do not need to make your pain useful before it can heal—you do not have to be endlessly available to be a good person.", "Unknown", "Self-Love"),
    Quote("You do not need to make your pain useful before it can heal, which means patience with yourself can be honest without becoming harsh.", "Unknown", "Self-Love"),
    Quote("You do not need to make your pain useful before it can heal; remember, a boundary explains what you can sustain; it does not have to prove a point.", "Unknown", "Self-Love"),
    Quote("You do not need to make your pain useful before it can heal; one difficult day is not a reliable summary of your life.", "Unknown", "Self-Love"),
    Quote("You do not need to make your pain useful before it can heal, and you can ask for help without surrendering your independence.", "Unknown", "Self-Love"),
    Quote("You do not need to make your pain useful before it can heal—protecting your peace may disappoint people who expected unlimited access.", "Unknown", "Self-Love"),
    Quote("You do not need to make your pain useful before it can heal, which means you deserve care on ordinary days, not only during a crisis.", "Unknown", "Self-Love"),
    Quote("You do not need to make your pain useful before it can heal; remember, changing your mind can be part of learning what you need.", "Unknown", "Self-Love"),
    Quote("A task often becomes less intimidating when you define its first step; make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity"),
    Quote("A task often becomes less intimidating when you define its first step, and choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity"),
    Quote("A task often becomes less intimidating when you define its first step—let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity"),
    Quote("A task often becomes less intimidating when you define its first step, which means returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity"),
    Quote("A task often becomes less intimidating when you define its first step; remember, progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity"),
    Quote("A task often becomes less intimidating when you define its first step; finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity"),
    Quote("A task often becomes less intimidating when you define its first step, and give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity"),
    Quote("A task often becomes less intimidating when you define its first step—you can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity"),
    Quote("A task often becomes less intimidating when you define its first step, which means measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity"),
    Quote("A task often becomes less intimidating when you define its first step; remember, tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy; returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy, and progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy—finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy, which means give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy; remember, you can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy; measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy, and tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy—make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy, which means choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity"),
    Quote("A plan that fits an ordinary day is more useful than a perfect fantasy; remember, let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity"),
    Quote("You do not need to feel inspired to begin; give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity"),
    Quote("You do not need to feel inspired to begin, and you can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity"),
    Quote("You do not need to feel inspired to begin—measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity"),
    Quote("You do not need to feel inspired to begin, which means tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity"),
    Quote("You do not need to feel inspired to begin; remember, make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity"),
    Quote("You do not need to feel inspired to begin; choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity"),
    Quote("You do not need to feel inspired to begin, and let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity"),
    Quote("You do not need to feel inspired to begin—returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity"),
    Quote("You do not need to feel inspired to begin, which means progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity"),
    Quote("You do not need to feel inspired to begin; remember, finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity"),
    Quote("A short focused effort can break the spell of postponing; tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity"),
    Quote("A short focused effort can break the spell of postponing, and make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity"),
    Quote("A short focused effort can break the spell of postponing—choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity"),
    Quote("A short focused effort can break the spell of postponing, which means let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity"),
    Quote("A short focused effort can break the spell of postponing; remember, returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity"),
    Quote("A short focused effort can break the spell of postponing; progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity"),
    Quote("A short focused effort can break the spell of postponing, and finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity"),
    Quote("A short focused effort can break the spell of postponing—give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity"),
    Quote("A short focused effort can break the spell of postponing, which means you can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity"),
    Quote("A short focused effort can break the spell of postponing; remember, measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity"),
    Quote("If your routine keeps failing, look at its design before blaming yourself; let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity"),
    Quote("If your routine keeps failing, look at its design before blaming yourself, and returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity"),
    Quote("If your routine keeps failing, look at its design before blaming yourself—progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity"),
    Quote("If your routine keeps failing, look at its design before blaming yourself, which means finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity"),
    Quote("If your routine keeps failing, look at its design before blaming yourself; remember, give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity"),
    Quote("If your routine keeps failing, look at its design before blaming yourself; you can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity"),
    Quote("If your routine keeps failing, look at its design before blaming yourself, and measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity"),
    Quote("If your routine keeps failing, look at its design before blaming yourself—tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity"),
    Quote("If your routine keeps failing, look at its design before blaming yourself, which means make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity"),
    Quote("If your routine keeps failing, look at its design before blaming yourself; remember, choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity"),
    Quote("You cannot give every task your best attention on the same day; finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity"),
    Quote("You cannot give every task your best attention on the same day, and give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity"),
    Quote("You cannot give every task your best attention on the same day—you can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity"),
    Quote("You cannot give every task your best attention on the same day, which means measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity"),
    Quote("You cannot give every task your best attention on the same day; remember, tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity"),
    Quote("You cannot give every task your best attention on the same day; make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity"),
    Quote("You cannot give every task your best attention on the same day, and choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity"),
    Quote("You cannot give every task your best attention on the same day—let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity"),
    Quote("You cannot give every task your best attention on the same day, which means returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity"),
    Quote("You cannot give every task your best attention on the same day; remember, progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity"),
    Quote("Preparation helps until it becomes a way to avoid starting; measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity"),
    Quote("Preparation helps until it becomes a way to avoid starting, and tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity"),
    Quote("Preparation helps until it becomes a way to avoid starting—make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity"),
    Quote("Preparation helps until it becomes a way to avoid starting, which means choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity"),
    Quote("Preparation helps until it becomes a way to avoid starting; remember, let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity"),
    Quote("Preparation helps until it becomes a way to avoid starting; returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity"),
    Quote("Preparation helps until it becomes a way to avoid starting, and progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity"),
    Quote("Preparation helps until it becomes a way to avoid starting—finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity"),
    Quote("Preparation helps until it becomes a way to avoid starting, which means give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity"),
    Quote("Preparation helps until it becomes a way to avoid starting; remember, you can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity"),
    Quote("Consistency is easier when the next step is clear; choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity"),
    Quote("Consistency is easier when the next step is clear, and let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity"),
    Quote("Consistency is easier when the next step is clear—returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity"),
    Quote("Consistency is easier when the next step is clear, which means progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity"),
    Quote("Consistency is easier when the next step is clear; remember, finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity"),
    Quote("Consistency is easier when the next step is clear; give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity"),
    Quote("Consistency is easier when the next step is clear, and you can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity"),
    Quote("Consistency is easier when the next step is clear—measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity"),
    Quote("Consistency is easier when the next step is clear, which means tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity"),
    Quote("Consistency is easier when the next step is clear; remember, make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity"),
    Quote("Some progress is quiet enough to be mistaken for no progress; progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity"),
    Quote("Some progress is quiet enough to be mistaken for no progress, and finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity"),
    Quote("Some progress is quiet enough to be mistaken for no progress—give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity"),
    Quote("Some progress is quiet enough to be mistaken for no progress, which means you can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity"),
    Quote("Some progress is quiet enough to be mistaken for no progress; remember, measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity"),
    Quote("Some progress is quiet enough to be mistaken for no progress; tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity"),
    Quote("Some progress is quiet enough to be mistaken for no progress, and make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity"),
    Quote("Some progress is quiet enough to be mistaken for no progress—choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity"),
    Quote("Some progress is quiet enough to be mistaken for no progress, which means let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity"),
    Quote("Some progress is quiet enough to be mistaken for no progress; remember, returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity"),
    Quote("A goal needs room in your calendar, not only in your imagination; you can adjust the pace without abandoning the destination.", "Unknown", "Motivation and Productivity"),
    Quote("A goal needs room in your calendar, not only in your imagination, and measure the work you can influence, not every outcome you cannot.", "Unknown", "Motivation and Productivity"),
    Quote("A goal needs room in your calendar, not only in your imagination—tomorrow becomes easier when you leave yourself a clear next action.", "Unknown", "Motivation and Productivity"),
    Quote("A goal needs room in your calendar, not only in your imagination, which means make the beginning small enough to do while you are still unsure.", "Unknown", "Motivation and Productivity"),
    Quote("A goal needs room in your calendar, not only in your imagination; remember, choose what matters most before the urgent things fill the day.", "Unknown", "Motivation and Productivity"),
    Quote("A goal needs room in your calendar, not only in your imagination; let a useful imperfect attempt teach you what planning cannot.", "Unknown", "Motivation and Productivity"),
    Quote("A goal needs room in your calendar, not only in your imagination, and returning after a missed day is part of the habit.", "Unknown", "Motivation and Productivity"),
    Quote("A goal needs room in your calendar, not only in your imagination—progress is easier to repeat when the process respects your limits.", "Unknown", "Motivation and Productivity"),
    Quote("A goal needs room in your calendar, not only in your imagination, which means finishing one meaningful thing can matter more than touching ten.", "Unknown", "Motivation and Productivity"),
    Quote("A goal needs room in your calendar, not only in your imagination; remember, give your attention a job instead of asking it to resist everything.", "Unknown", "Motivation and Productivity"),
    Quote("A setback can change your route without canceling your destination; look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges"),
    Quote("A setback can change your route without canceling your destination, and changing direction can be a form of courage.", "Unknown", "Overcoming Challenges"),
    Quote("A setback can change your route without canceling your destination—surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges"),
    Quote("A setback can change your route without canceling your destination, which means let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges"),
    Quote("A setback can change your route without canceling your destination; remember, ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges"),
    Quote("A setback can change your route without canceling your destination; you can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges"),
    Quote("A setback can change your route without canceling your destination, and one setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges"),
    Quote("A setback can change your route without canceling your destination—sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges"),
    Quote("A setback can change your route without canceling your destination, which means give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges"),
    Quote("A setback can change your route without canceling your destination; remember, you are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges"),
    Quote("You can be tired of the struggle and still be making progress; let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges"),
    Quote("You can be tired of the struggle and still be making progress, and ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges"),
    Quote("You can be tired of the struggle and still be making progress—you can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges"),
    Quote("You can be tired of the struggle and still be making progress, which means one setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges"),
    Quote("You can be tired of the struggle and still be making progress; remember, sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges"),
    Quote("You can be tired of the struggle and still be making progress; give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges"),
    Quote("You can be tired of the struggle and still be making progress, and you are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges"),
    Quote("You can be tired of the struggle and still be making progress—look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges"),
    Quote("You can be tired of the struggle and still be making progress, which means changing direction can be a form of courage.", "Unknown", "Overcoming Challenges"),
    Quote("You can be tired of the struggle and still be making progress; remember, surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges"),
    Quote("Some problems need a different approach rather than more force; one setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges"),
    Quote("Some problems need a different approach rather than more force, and sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges"),
    Quote("Some problems need a different approach rather than more force—give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges"),
    Quote("Some problems need a different approach rather than more force, which means you are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges"),
    Quote("Some problems need a different approach rather than more force; remember, look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges"),
    Quote("Some problems need a different approach rather than more force; changing direction can be a form of courage.", "Unknown", "Overcoming Challenges"),
    Quote("Some problems need a different approach rather than more force, and surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges"),
    Quote("Some problems need a different approach rather than more force—let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges"),
    Quote("Some problems need a different approach rather than more force, which means ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges"),
    Quote("Some problems need a different approach rather than more force; remember, you can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges"),
    Quote("Starting over does not erase what the first attempt taught you; you are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges"),
    Quote("Starting over does not erase what the first attempt taught you, and look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges"),
    Quote("Starting over does not erase what the first attempt taught you—changing direction can be a form of courage.", "Unknown", "Overcoming Challenges"),
    Quote("Starting over does not erase what the first attempt taught you, which means surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges"),
    Quote("Starting over does not erase what the first attempt taught you; remember, let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges"),
    Quote("Starting over does not erase what the first attempt taught you; ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges"),
    Quote("Starting over does not erase what the first attempt taught you, and you can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges"),
    Quote("Starting over does not erase what the first attempt taught you—one setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges"),
    Quote("Starting over does not erase what the first attempt taught you, which means sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges"),
    Quote("Starting over does not erase what the first attempt taught you; remember, give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges"),
    Quote("Uncertainty is difficult, but it is not the same as danger; surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges"),
    Quote("Uncertainty is difficult, but it is not the same as danger, and let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges"),
    Quote("Uncertainty is difficult, but it is not the same as danger—ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges"),
    Quote("Uncertainty is difficult, but it is not the same as danger, which means you can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges"),
    Quote("Uncertainty is difficult, but it is not the same as danger; remember, one setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges"),
    Quote("Uncertainty is difficult, but it is not the same as danger; sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges"),
    Quote("Uncertainty is difficult, but it is not the same as danger, and give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges"),
    Quote("Uncertainty is difficult, but it is not the same as danger—you are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges"),
    Quote("Uncertainty is difficult, but it is not the same as danger, which means look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges"),
    Quote("Uncertainty is difficult, but it is not the same as danger; remember, changing direction can be a form of courage.", "Unknown", "Overcoming Challenges"),
    Quote("You can admit that something hurts without letting it define everything; you can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges"),
    Quote("You can admit that something hurts without letting it define everything, and one setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges"),
    Quote("You can admit that something hurts without letting it define everything—sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges"),
    Quote("You can admit that something hurts without letting it define everything, which means give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges"),
    Quote("You can admit that something hurts without letting it define everything; remember, you are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges"),
    Quote("You can admit that something hurts without letting it define everything; look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges"),
    Quote("You can admit that something hurts without letting it define everything, and changing direction can be a form of courage.", "Unknown", "Overcoming Challenges"),
    Quote("You can admit that something hurts without letting it define everything—surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges"),
    Quote("You can admit that something hurts without letting it define everything, which means let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges"),
    Quote("You can admit that something hurts without letting it define everything; remember, ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges"),
    Quote("An ending can be painful even when it is the right one; give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges"),
    Quote("An ending can be painful even when it is the right one, and you are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges"),
    Quote("An ending can be painful even when it is the right one—look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges"),
    Quote("An ending can be painful even when it is the right one, which means changing direction can be a form of courage.", "Unknown", "Overcoming Challenges"),
    Quote("An ending can be painful even when it is the right one; remember, surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges"),
    Quote("An ending can be painful even when it is the right one; let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges"),
    Quote("An ending can be painful even when it is the right one, and ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges"),
    Quote("An ending can be painful even when it is the right one—you can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges"),
    Quote("An ending can be painful even when it is the right one, which means one setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges"),
    Quote("An ending can be painful even when it is the right one; remember, sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges"),
    Quote("You are allowed to pause before deciding what comes next; changing direction can be a form of courage.", "Unknown", "Overcoming Challenges"),
    Quote("You are allowed to pause before deciding what comes next, and surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges"),
    Quote("You are allowed to pause before deciding what comes next—let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges"),
    Quote("You are allowed to pause before deciding what comes next, which means ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges"),
    Quote("You are allowed to pause before deciding what comes next; remember, you can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges"),
    Quote("You are allowed to pause before deciding what comes next; one setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges"),
    Quote("You are allowed to pause before deciding what comes next, and sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges"),
    Quote("You are allowed to pause before deciding what comes next—give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges"),
    Quote("You are allowed to pause before deciding what comes next, which means you are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges"),
    Quote("You are allowed to pause before deciding what comes next; remember, look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges"),
    Quote("A slow recovery is still a recovery; ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges"),
    Quote("A slow recovery is still a recovery, and you can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges"),
    Quote("A slow recovery is still a recovery—one setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges"),
    Quote("A slow recovery is still a recovery, which means sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges"),
    Quote("A slow recovery is still a recovery; remember, give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges"),
    Quote("A slow recovery is still a recovery; you are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges"),
    Quote("A slow recovery is still a recovery, and look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges"),
    Quote("A slow recovery is still a recovery—changing direction can be a form of courage.", "Unknown", "Overcoming Challenges"),
    Quote("A slow recovery is still a recovery, which means surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges"),
    Quote("A slow recovery is still a recovery; remember, let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges"),
    Quote("You do not have to understand the whole lesson while you are living it; sometimes strength is choosing a better support system.", "Unknown", "Overcoming Challenges"),
    Quote("You do not have to understand the whole lesson while you are living it, and give yourself time to see options that panic hides.", "Unknown", "Overcoming Challenges"),
    Quote("You do not have to understand the whole lesson while you are living it—you are not starting from nothing when you begin again.", "Unknown", "Overcoming Challenges"),
    Quote("You do not have to understand the whole lesson while you are living it, which means look for the next workable step, not a guarantee that everything will work.", "Unknown", "Overcoming Challenges"),
    Quote("You do not have to understand the whole lesson while you are living it; remember, changing direction can be a form of courage.", "Unknown", "Overcoming Challenges"),
    Quote("You do not have to understand the whole lesson while you are living it; surviving a hard season does not mean you must pretend it was easy.", "Unknown", "Overcoming Challenges"),
    Quote("You do not have to understand the whole lesson while you are living it, and let the facts guide your next move more than fear of repeating the past.", "Unknown", "Overcoming Challenges"),
    Quote("You do not have to understand the whole lesson while you are living it—ask what this situation requires now, not what it required last time.", "Unknown", "Overcoming Challenges"),
    Quote("You do not have to understand the whole lesson while you are living it, which means you can carry the lesson forward without carrying every hurt with it.", "Unknown", "Overcoming Challenges"),
    Quote("You do not have to understand the whole lesson while you are living it; remember, one setback is a chapter, not the entire account.", "Unknown", "Overcoming Challenges"),
    Quote("Not every good day needs to be memorable to matter; notice what made today a little more livable.", "Unknown", "Happiness and Positivity"),
    Quote("Not every good day needs to be memorable to matter, and a difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity"),
    Quote("Not every good day needs to be memorable to matter—let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity"),
    Quote("Not every good day needs to be memorable to matter, which means contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity"),
    Quote("Not every good day needs to be memorable to matter; remember, make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity"),
    Quote("Not every good day needs to be memorable to matter; small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity"),
    Quote("Not every good day needs to be memorable to matter, and you can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity"),
    Quote("Not every good day needs to be memorable to matter—an imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity"),
    Quote("Not every good day needs to be memorable to matter, which means peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity"),
    Quote("Not every good day needs to be memorable to matter; remember, let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity"),
    Quote("Joy can exist alongside worry without canceling it out; contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity"),
    Quote("Joy can exist alongside worry without canceling it out, and make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity"),
    Quote("Joy can exist alongside worry without canceling it out—small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity"),
    Quote("Joy can exist alongside worry without canceling it out, which means you can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity"),
    Quote("Joy can exist alongside worry without canceling it out; remember, an imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity"),
    Quote("Joy can exist alongside worry without canceling it out; peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity"),
    Quote("Joy can exist alongside worry without canceling it out, and let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity"),
    Quote("Joy can exist alongside worry without canceling it out—notice what made today a little more livable.", "Unknown", "Happiness and Positivity"),
    Quote("Joy can exist alongside worry without canceling it out, which means a difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity"),
    Quote("Joy can exist alongside worry without canceling it out; remember, let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity"),
    Quote("A small comfort is still worth noticing; you can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity"),
    Quote("A small comfort is still worth noticing, and an imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity"),
    Quote("A small comfort is still worth noticing—peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity"),
    Quote("A small comfort is still worth noticing, which means let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity"),
    Quote("A small comfort is still worth noticing; remember, notice what made today a little more livable.", "Unknown", "Happiness and Positivity"),
    Quote("A small comfort is still worth noticing; a difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity"),
    Quote("A small comfort is still worth noticing, and let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity"),
    Quote("A small comfort is still worth noticing—contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity"),
    Quote("A small comfort is still worth noticing, which means make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity"),
    Quote("A small comfort is still worth noticing; remember, small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity"),
    Quote("You do not have to force a bright side onto every disappointment; let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity"),
    Quote("You do not have to force a bright side onto every disappointment, and notice what made today a little more livable.", "Unknown", "Happiness and Positivity"),
    Quote("You do not have to force a bright side onto every disappointment—a difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity"),
    Quote("You do not have to force a bright side onto every disappointment, which means let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity"),
    Quote("You do not have to force a bright side onto every disappointment; remember, contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity"),
    Quote("You do not have to force a bright side onto every disappointment; make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity"),
    Quote("You do not have to force a bright side onto every disappointment, and small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity"),
    Quote("You do not have to force a bright side onto every disappointment—you can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity"),
    Quote("You do not have to force a bright side onto every disappointment, which means an imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity"),
    Quote("You do not have to force a bright side onto every disappointment; remember, peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity"),
    Quote("Ordinary moments often become meaningful only after we pay attention; let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity"),
    Quote("Ordinary moments often become meaningful only after we pay attention, and contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity"),
    Quote("Ordinary moments often become meaningful only after we pay attention—make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity"),
    Quote("Ordinary moments often become meaningful only after we pay attention, which means small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity"),
    Quote("Ordinary moments often become meaningful only after we pay attention; remember, you can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity"),
    Quote("Ordinary moments often become meaningful only after we pay attention; an imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity"),
    Quote("Ordinary moments often become meaningful only after we pay attention, and peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity"),
    Quote("Ordinary moments often become meaningful only after we pay attention—let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity"),
    Quote("Ordinary moments often become meaningful only after we pay attention, which means notice what made today a little more livable.", "Unknown", "Happiness and Positivity"),
    Quote("Ordinary moments often become meaningful only after we pay attention; remember, a difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity"),
    Quote("Gratitude is more honest when it leaves room for what is hard; small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity"),
    Quote("Gratitude is more honest when it leaves room for what is hard, and you can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity"),
    Quote("Gratitude is more honest when it leaves room for what is hard—an imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity"),
    Quote("Gratitude is more honest when it leaves room for what is hard, which means peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity"),
    Quote("Gratitude is more honest when it leaves room for what is hard; remember, let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity"),
    Quote("Gratitude is more honest when it leaves room for what is hard; notice what made today a little more livable.", "Unknown", "Happiness and Positivity"),
    Quote("Gratitude is more honest when it leaves room for what is hard, and a difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity"),
    Quote("Gratitude is more honest when it leaves room for what is hard—let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity"),
    Quote("Gratitude is more honest when it leaves room for what is hard, which means contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity"),
    Quote("Gratitude is more honest when it leaves room for what is hard; remember, make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity"),
    Quote("Your life can contain unfinished things and genuine happiness; peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity"),
    Quote("Your life can contain unfinished things and genuine happiness, and let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity"),
    Quote("Your life can contain unfinished things and genuine happiness—notice what made today a little more livable.", "Unknown", "Happiness and Positivity"),
    Quote("Your life can contain unfinished things and genuine happiness, which means a difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity"),
    Quote("Your life can contain unfinished things and genuine happiness; remember, let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity"),
    Quote("Your life can contain unfinished things and genuine happiness; contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity"),
    Quote("Your life can contain unfinished things and genuine happiness, and make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity"),
    Quote("Your life can contain unfinished things and genuine happiness—small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity"),
    Quote("Your life can contain unfinished things and genuine happiness, which means you can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity"),
    Quote("Your life can contain unfinished things and genuine happiness; remember, an imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity"),
    Quote("Some peace comes from letting a moment be enough; a difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity"),
    Quote("Some peace comes from letting a moment be enough, and let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity"),
    Quote("Some peace comes from letting a moment be enough—contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity"),
    Quote("Some peace comes from letting a moment be enough, which means make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity"),
    Quote("Some peace comes from letting a moment be enough; remember, small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity"),
    Quote("Some peace comes from letting a moment be enough; you can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity"),
    Quote("Some peace comes from letting a moment be enough, and an imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity"),
    Quote("Some peace comes from letting a moment be enough—peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity"),
    Quote("Some peace comes from letting a moment be enough, which means let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity"),
    Quote("Some peace comes from letting a moment be enough; remember, notice what made today a little more livable.", "Unknown", "Happiness and Positivity"),
    Quote("Hope can be practical rather than certain; make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity"),
    Quote("Hope can be practical rather than certain, and small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity"),
    Quote("Hope can be practical rather than certain—you can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity"),
    Quote("Hope can be practical rather than certain, which means an imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity"),
    Quote("Hope can be practical rather than certain; remember, peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity"),
    Quote("Hope can be practical rather than certain; let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity"),
    Quote("Hope can be practical rather than certain, and notice what made today a little more livable.", "Unknown", "Happiness and Positivity"),
    Quote("Hope can be practical rather than certain—a difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity"),
    Quote("Hope can be practical rather than certain, which means let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity"),
    Quote("Hope can be practical rather than certain; remember, contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity"),
    Quote("You are allowed to enjoy something before you have earned a break; an imperfect day may still hold something worth remembering.", "Unknown", "Happiness and Positivity"),
    Quote("You are allowed to enjoy something before you have earned a break, and peace is sometimes the decision not to rehearse every possible outcome.", "Unknown", "Happiness and Positivity"),
    Quote("You are allowed to enjoy something before you have earned a break—let yourself be surprised by what is still good.", "Unknown", "Happiness and Positivity"),
    Quote("You are allowed to enjoy something before you have earned a break, which means notice what made today a little more livable.", "Unknown", "Happiness and Positivity"),
    Quote("You are allowed to enjoy something before you have earned a break; remember, a difficult feeling does not have to take the whole day hostage.", "Unknown", "Happiness and Positivity"),
    Quote("You are allowed to enjoy something before you have earned a break; let good moments count without asking them to fix everything.", "Unknown", "Happiness and Positivity"),
    Quote("You are allowed to enjoy something before you have earned a break, and contentment can grow when comparison stops setting the price.", "Unknown", "Happiness and Positivity"),
    Quote("You are allowed to enjoy something before you have earned a break—make room for delight that does not need to be productive.", "Unknown", "Happiness and Positivity"),
    Quote("You are allowed to enjoy something before you have earned a break, which means small pleasures are not small when they help you feel present.", "Unknown", "Happiness and Positivity"),
    Quote("You are allowed to enjoy something before you have earned a break; remember, you can appreciate what is here while still working toward change.", "Unknown", "Happiness and Positivity")
)// ------------------------------------------------------------
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
                color = Color.Black.copy(alpha = 0.82f),
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
                        color = Color.White
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
                                color = Color.Black.copy(alpha = 0.55f),
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
                                color = Color.Black.copy(alpha = 0.85f),
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
                                    color = Color.Black.copy(alpha = 0.85f),
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
            color = Color.Black.copy(alpha = 0.82f),
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

                        val favoriteIndex =
                            favoriteItem.first

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
                                            quoteToRemove = favoriteIndex
                                        }
                                    )
                                }
                            }

                            if (favoriteIndex != favoriteQuotes.last().first) {
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

    quoteToRemove?.let { index ->

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
                        newFavorites.remove(index.toString())

                        preferences.edit()
                            .putStringSet("favorites", newFavorites)
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