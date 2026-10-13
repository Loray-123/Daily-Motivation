
package com.dailymotivation.app

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
    Quote("Every day is a new beginning.", "Unknown"),
    Quote("Small steps every day add up to big change.", "Unknown"),
    Quote("You don’t need to be perfect to make progress.", "Unknown"),
    Quote("A little progress is still progress.", "Unknown"),
    Quote("Start before you feel ready.", "Unknown"),
    Quote("You can always begin again.", "Unknown"),
    Quote("Be proud of how far you have come.", "Unknown"),
    Quote("Focus on what you can control.", "Unknown"),
    Quote("One good choice can change your day.", "Unknown"),
    Quote("Give yourself time to grow.", "Unknown"),
    Quote("You are allowed to learn as you go.", "Unknown"),
    Quote("Keep going, even if it is slow.", "Unknown"),
    Quote("You can do hard things one step at a time.", "Unknown"),
    Quote("Rest is part of making progress.", "Unknown"),
    Quote("Your effort matters, even when no one sees it.", "Unknown"),
    Quote("You do not have to do everything today.", "Unknown"),
    Quote("Choose progress over perfection.", "Unknown"),
    Quote("Be patient with yourself.", "Unknown"),
    Quote("You are stronger than one bad day.", "Unknown"),
    Quote("A fresh start can begin at any time.", "Unknown"),
    Quote("Make room for what makes you happy.", "Unknown"),
    Quote("You can change your direction at any time.", "Unknown"),
    Quote("Do one thing today that your future self will thank you for.", "Unknown"),
    Quote("Your pace is still a pace.", "Unknown"),
    Quote("Keep the promises you make to yourself.", "Unknown"),
    Quote("You deserve the same kindness you give others.", "Unknown"),
    Quote("You can be a work in progress and still be proud.", "Unknown"),
    Quote("Take the next small step.", "Unknown"),
    Quote("You have made it through hard days before.", "Unknown"),
    Quote("Let today be good enough.", "Unknown"),
    Quote("You do not need permission to try again.", "Unknown"),
    Quote("A kind word can make a big difference.", "Unknown"),
    Quote("You can learn something from every setback.", "Unknown"),
    Quote("Give yourself credit for trying.", "Unknown"),
    Quote("Your best can look different every day.", "Unknown"),
    Quote("Make today count in your own way.", "Unknown"),
    Quote("You are not behind; you are on your own path.", "Unknown"),
    Quote("One step forward is better than standing still.", "Unknown"),
    Quote("You can start small and still dream big.", "Unknown"),
    Quote("Let your actions speak for your goals.", "Unknown"),
    Quote("There is strength in asking for help.", "Unknown"),
    Quote("You do not have to have it all figured out.", "Unknown"),
    Quote("Make time for the things that matter.", "Unknown"),
    Quote("Your next chapter can be different.", "Unknown"),
    Quote("You can be nervous and still be brave.", "Unknown"),
    Quote("Keep what helps you grow. Let go of what holds you back.", "Unknown"),
    Quote("You are allowed to take a break.", "Unknown"),
    Quote("A better day can start with one small choice.", "Unknown"),
    Quote("Do what you can today, and leave the rest for tomorrow.", "Unknown"),
    Quote("Your value is not measured by how much you do.", "Unknown"),
    Quote("You can make progress without rushing.", "Unknown"),
    Quote("Be willing to begin again.", "Unknown"),
    Quote("You have more choices than you think.", "Unknown"),
    Quote("Celebrate the small wins.", "Unknown"),
    Quote("You can take things one day at a time.", "Unknown"),
    Quote("Make your own kind of progress.", "Unknown"),
    Quote("A setback is not the end of your story.", "Unknown"),
    Quote("You can grow at your own pace.", "Unknown"),
    Quote("The way forward starts with one step.", "Unknown"),
    Quote("You do not have to feel confident to begin.", "Unknown"),
    Quote("Be gentle with yourself while you learn.", "Unknown"),
    Quote("You can make today a little better.", "Unknown"),
    Quote("Keep showing up for yourself.", "Unknown"),
    Quote("You are allowed to change your mind and try a new way.", "Unknown"),
    Quote("Even a small effort is worth making.", "Unknown"),
    Quote("You can choose to begin again tomorrow.", "Unknown"),
    Quote("Let your goals guide you, not scare you.", "Unknown"),
    Quote("The next step does not have to be a big one.", "Unknown"),
    Quote("You can make a difference in ordinary ways.", "Unknown"),
    Quote("Keep moving toward what matters to you.", "Unknown"),
    Quote("You are learning, even when progress feels slow.", "Unknown"),
    Quote("Today is another chance to try.", "Unknown"),
    Quote("Do not give up on yourself.", "Unknown"),
    Quote("You can build a good life one day at a time.", "Unknown"),
    Quote("Give your dreams a little time and effort.", "Unknown"),
    Quote("A calm mind can take the next step.", "Unknown"),
    Quote("You do not need to compare your journey with anyone else’s.", "Unknown"),
    Quote("Choose one thing and start there.", "Unknown"),
    Quote("You can be proud of the effort, not just the result.", "Unknown"),
    Quote("Keep going; your effort is adding up.", "Unknown"),
    Quote("You are allowed to take the long way.", "Unknown"),
    Quote("The smallest start is better than no start.", "Unknown"),
    Quote("You can make room for joy today.", "Unknown"),
    Quote("Every new skill begins with practice.", "Unknown"),
    Quote("Do not let one mistake define you.", "Unknown"),
    Quote("You can take a deep breath and try again.", "Unknown"),
    Quote("Keep your goals simple and take the next step.", "Unknown"),
    Quote("You can do something meaningful with an ordinary day.", "Unknown"),
    Quote("You are more than your hardest moment.", "Unknown"),
    Quote("A little courage can take you a long way.", "Unknown"),
    Quote("You can move forward without having every answer.", "Unknown"),
    Quote("Make the next choice a helpful one.", "Unknown"),
    Quote("You have the right to grow and change.", "Unknown"),
    Quote("You can begin with what you have today.", "Unknown"),
    Quote("Progress is built one choice at a time.", "Unknown"),
    Quote("Do not overlook how far you have come.", "Unknown"),
    Quote("Give yourself a chance.", "Unknown"),
    Quote("Keep making room for better days.", "Unknown"),
    Quote("You can be proud of trying again.", "Unknown"),
    Quote("One day at a time is enough.", "Unknown"),
    Quote("Your future is shaped by the steps you take today.", "Unknown"),
    Quote("You can make a fresh start without changing everything.", "Unknown"),
    Quote("Keep going in a way that works for you.", "Unknown"),
    Quote("You do not need to do it all to do something good.", "Unknown"),
    Quote("Take care of yourself as you work toward your goals.", "Unknown"),
    Quote("Your next step is more important than your last mistake.", "Unknown"),
    Quote("Keep going until you are proud of yourself.", "Unknown"),
    Quote("Every effort gives you a chance to learn.", "Unknown"),
    Quote("Make today a little brighter than yesterday.", "Unknown"),
    Quote("You do not need a perfect plan to begin.", "Unknown"),
    Quote("Small changes can lead to wonderful things.", "Unknown"),
    Quote("Believe in the value of your effort.", "Unknown"),
    Quote("Give yourself room to get better.", "Unknown"),
    Quote("Every try teaches you something useful.", "Unknown"),
    Quote("Let your courage be bigger than your doubt.", "Unknown"),
    Quote("Good things grow with time and care.", "Unknown"),
    Quote("You can make progress without seeing it yet.", "Unknown"),
    Quote("Begin with one thing you can do.", "Unknown"),
    Quote("Your effort today can open a new door.", "Unknown"),
    Quote("Do not let fear make every decision.", "Unknown"),
    Quote("Keep learning and keep moving.", "Unknown"),
    Quote("Give your goals a fair chance.", "Unknown"),
    Quote("You can find a way forward one step at a time.", "Unknown"),
    Quote("Be proud of the work no one sees.", "Unknown"),
    Quote("Each day gives you another chance to grow.", "Unknown"),
    Quote("Let mistakes teach you, not stop you.", "Unknown"),
    Quote("You are allowed to move at your own speed.", "Unknown"),
    Quote("Start small, then keep building.", "Unknown"),
    Quote("Your choices can shape a better tomorrow.", "Unknown"),
    Quote("Make room for new possibilities.", "Unknown"),
    Quote("Keep trying different ways until something works.", "Unknown"),
    Quote("You can be proud before you reach the finish line.", "Unknown"),
    Quote("Let your actions build your confidence.", "Unknown"),
    Quote("One brave choice can change your direction.", "Unknown"),
    Quote("Do not confuse a slow day with no progress.", "Unknown"),
    Quote("Your story is still being written.", "Unknown"),
    Quote("Take the chance to surprise yourself.", "Unknown"),
    Quote("Focus on the next useful thing.", "Unknown"),
    Quote("Be open to a better way.", "Unknown"),
    Quote("Every little win deserves to count.", "Unknown"),
    Quote("Your growth does not need an audience.", "Unknown"),
    Quote("Keep your attention on what matters.", "Unknown"),
    Quote("Try again with what you know now.", "Unknown"),
    Quote("Hard days do not erase your progress.", "Unknown"),
    Quote("Give yourself credit for showing up.", "Unknown"),
    Quote("Let patience help you keep going.", "Unknown"),
    Quote("You can make a fresh choice right now.", "Unknown"),
    Quote("Make your goals fit the life you want.", "Unknown"),
    Quote("Every step can teach you something.", "Unknown"),
    Quote("Keep your hopes alive through the hard parts.", "Unknown"),
    Quote("Let today be a step, not a test.", "Unknown"),
    Quote("Your best effort is worth giving.", "Unknown"),
    Quote("Do not wait for confidence to take action.", "Unknown"),
    Quote("You can learn your way through uncertainty.", "Unknown"),
    Quote("Make progress in a way you can sustain.", "Unknown"),
    Quote("Keep choosing what helps you grow.", "Unknown"),
    Quote("You are capable of learning something new.", "Unknown"),
    Quote("Let a small win build your momentum.", "Unknown"),
    Quote("Keep the door open for change.", "Unknown"),
    Quote("Your future can start with today's decision.", "Unknown"),
    Quote("Be curious about what you can become.", "Unknown"),
    Quote("Take the pressure off and take one step.", "Unknown"),
    Quote("Make time for the person you want to be.", "Unknown"),
    Quote("Your effort is never wasted when you learn.", "Unknown"),
    Quote("Choose a direction and begin.", "Unknown"),
    Quote("Keep building the life you want.", "Unknown"),
    Quote("Do not let one tough moment define the day.", "Unknown"),
    Quote("Give your ideas a chance to work.", "Unknown"),
    Quote("You can take a new path without starting over.", "Unknown"),
    Quote("Let your progress be enough for today.", "Unknown"),
    Quote("Keep showing up, even in small ways.", "Unknown"),
    Quote("Trust yourself to learn along the way.", "Unknown"),
    Quote("Make your next move a positive one.", "Unknown"),
    Quote("Your dreams deserve steady effort.", "Unknown"),
    Quote("Be willing to be a beginner.", "Unknown"),
    Quote("Each attempt brings you more experience.", "Unknown"),
    Quote("Keep your mind open and your feet moving.", "Unknown"),
    Quote("Give yourself the time that growth takes.", "Unknown"),
    Quote("You can turn a hard lesson into a wise choice.", "Unknown"),
    Quote("Let your goals pull you forward.", "Unknown"),
    Quote("Choose the step you can take today.", "Unknown"),
    Quote("Your quiet progress still counts.", "Unknown"),
    Quote("Be proud of your willingness to try.", "Unknown"),
    Quote("Keep looking for what is possible.", "Unknown"),
    Quote("Your life can improve one habit at a time.", "Unknown"),
    Quote("Do not quit just because it feels unfamiliar.", "Unknown"),
    Quote("Give yourself another opportunity.", "Unknown"),
    Quote("Make a little space for hope.", "Unknown"),
    Quote("Keep going at a pace you can manage.", "Unknown"),
    Quote("Your courage can grow with practice.", "Unknown"),
    Quote("Let each new day bring a new attempt.", "Unknown"),
    Quote("Take action before doubt gets too loud.", "Unknown"),
    Quote("Small wins can lead to big confidence.", "Unknown"),
    Quote("You can change your approach and keep your goal.", "Unknown"),
    Quote("Learn from yesterday and use today well.", "Unknown"),
    Quote("Keep your focus on what you can do next.", "Unknown"),
    Quote("Your determination can outlast a difficult moment.", "Unknown"),
    Quote("Do something today that moves you forward.", "Unknown"),
    Quote("You are allowed to improve gradually.", "Unknown"),
    Quote("Every new beginning starts somewhere.", "Unknown"),
    Quote("Let your effort match what matters to you.", "Unknown"),
    Quote("Be patient while your hard work takes root.", "Unknown"),
    Quote("Keep believing that change is possible.", "Unknown"),
    Quote("Your next chapter can start with one choice.", "Unknown"),
    Quote("Do not make one setback your whole story.", "Unknown"),
    Quote("Give yourself permission to keep learning.", "Unknown"),
    Quote("Make the most of the step in front of you.", "Unknown"),
    Quote("Your goals are worth consistent effort.", "Unknown"),
    Quote("Keep taking chances on your own growth.", "Unknown"),
    Quote("Progress begins when you decide to try.", "Unknown"),
    Quote("You can handle more than you expect to learn.", "Unknown"),
    Quote("Choose steady effort over waiting for perfect timing.", "Unknown"),
    Quote("Let your confidence grow from what you do.", "Unknown"),
    Quote("Make today useful, even if it is not perfect.", "Unknown"),
    Quote("You can do more with practice than with worry.", "Unknown"),
    Quote("Keep making choices your future self will appreciate.", "Unknown"),
    Quote("Let each challenge show you what to practice.", "Unknown"),
    Quote("One thoughtful step can start a good change.", "Unknown"),
    Quote("Do not underestimate the power of beginning.", "Unknown"),
    Quote("You can keep moving without knowing every answer.", "Unknown"),
    Quote("Give your plans time to become real.", "Unknown"),
    Quote("Keep your energy for what you can influence.", "Unknown"),
    Quote("Every day is a chance to practice courage.", "Unknown"),
    Quote("Be willing to take the first awkward step.", "Unknown"),
    Quote("Let your hard work speak over your doubts.", "Unknown"),
    Quote("You can find strength in steady habits.", "Unknown"),
    Quote("Keep your standards high and your self-talk kind.", "Unknown"),
    Quote("Do not rush the parts that need time.", "Unknown"),
    Quote("Make a choice that supports your peace.", "Unknown"),
    Quote("You are growing through what you are learning.", "Unknown"),
    Quote("Let your next action be simple and clear.", "Unknown"),
    Quote("Keep working toward what feels meaningful.", "Unknown"),
    Quote("Your patience is part of your progress.", "Unknown"),
    Quote("Take the lesson and leave the shame.", "Unknown"),
    Quote("Make your effort something you can repeat.", "Unknown"),
    Quote("You can build confidence by keeping small promises.", "Unknown"),
    Quote("Do not give up before you have tried a new way.", "Unknown"),
    Quote("Let your goals remind you why you started.", "Unknown"),
    Quote("Your next opportunity may begin with practice.", "Unknown"),
    Quote("Choose to keep going a little longer.", "Unknown"),
    Quote("Be proud of the courage it takes to change.", "Unknown"),
    Quote("Keep your focus on growth, not comparison.", "Unknown"),
    Quote("You can make a useful start with limited time.", "Unknown"),
    Quote("Every day you try, you build experience.", "Unknown"),
    Quote("Let a difficult moment be temporary.", "Unknown"),
    Quote("Take one step that makes tomorrow easier.", "Unknown"),
    Quote("Your choices today can create more options later.", "Unknown"),
    Quote("Give yourself space to find your rhythm.", "Unknown"),
    Quote("Keep the good lessons and release the old limits.", "Unknown"),
    Quote("You can be hopeful and realistic at the same time.", "Unknown"),
    Quote("Make room for the things that restore you.", "Unknown"),
    Quote("Do not let a slow start stop you.", "Unknown"),
    Quote("Your progress can be quiet and still be real.", "Unknown"),
    Quote("Keep practicing what you want to improve.", "Unknown"),
    Quote("One good habit can support many goals.", "Unknown"),
    Quote("Let your next try benefit from your last lesson.", "Unknown"),
    Quote("Take pride in the effort you can control.", "Unknown"),
    Quote("You can move forward without rushing the result.", "Unknown"),
    Quote("Keep your plans flexible and your purpose clear.", "Unknown"),
    Quote("Do not overlook the value of consistency.", "Unknown"),
    Quote("Give your attention to the things you can change.", "Unknown"),
    Quote("Each day is another chance to build trust in yourself.", "Unknown"),
    Quote("Let action turn your ideas into progress.", "Unknown"),
    Quote("Keep going even when the reward is not immediate.", "Unknown"),
    Quote("You can start again without judging your past self.", "Unknown"),
    Quote("Make the next few minutes count.", "Unknown"),
    Quote("Be brave enough to try something unfamiliar.", "Unknown"),
    Quote("Your effort can make a difference over time.", "Unknown"),
    Quote("Choose to learn instead of giving up.", "Unknown"),
    Quote("Keep taking steps that match your values.", "Unknown"),
    Quote("You do not need to earn the right to rest.", "Unknown"),
    Quote("Let yourself enjoy how far you have come.", "Unknown"),
    Quote("Your best days are not the only days that matter.", "Unknown"),
    Quote("Keep moving toward a life that feels like yours.", "Unknown"),
    Quote("Make a plan that leaves room for real life.", "Unknown"),
    Quote("You can be proud of progress that others cannot see.", "Unknown"),
    Quote("Let your mistakes become useful information.", "Unknown"),
    Quote("Take one step, then decide on the next.", "Unknown"),
    Quote("Your goals can grow as you grow.", "Unknown"),
    Quote("Keep your mind on possibilities, not just obstacles.", "Unknown"),
    Quote("Small acts of courage build over time.", "Unknown"),
    Quote("Give your future a little help today.", "Unknown"),
    Quote("You can choose a more helpful thought.", "Unknown"),
    Quote("Let your work be steady rather than hurried.", "Unknown"),
    Quote("Keep looking for the next open door.", "Unknown"),
    Quote("Your willingness to learn is a strength.", "Unknown"),
    Quote("Make a little progress and build from there.", "Unknown"),
    Quote("You can take care of yourself and still be ambitious.", "Unknown"),
    Quote("Do not compare your beginning to someone else's middle.", "Unknown"),
    Quote("Let the next step be enough for now.", "Unknown"),
    Quote("Keep giving yourself reasons to believe in your ability.", "Unknown"),
    Quote("Your patience can protect your progress.", "Unknown"),
    Quote("Make today a chance to practice, not to prove.", "Unknown"),
    Quote("You can build something good from where you are.", "Unknown"),
    Quote("Keep going; you are gaining experience.", "Unknown"),
    Quote("Let your actions be guided by your hopes.", "Unknown"),
    Quote("One small improvement is a real improvement.", "Unknown"),
    Quote("You can move through uncertainty with care.", "Unknown"),
    Quote("Give your dreams regular attention.", "Unknown"),
    Quote("Keep choosing effort over excuses.", "Unknown"),
    Quote("Your next try can be different.", "Unknown"),
    Quote("Let the things you learn make you stronger.", "Unknown"),
    Quote("Be proud of the person you are becoming.", "Unknown"),
    Quote("Make progress with the tools you have.", "Unknown"),
    Quote("You can keep your goal and change your plan.", "Unknown"),
    Quote("Do not let a difficult beginning decide the ending.", "Unknown"),
    Quote("Every step you take gives you new information.", "Unknown"),
    Quote("Keep making choices that support your well-being.", "Unknown"),
    Quote("Your growth can happen in ordinary moments.", "Unknown"),
    Quote("Try to make the next choice a little wiser.", "Unknown"),
    Quote("Let yourself be new at something.", "Unknown"),
    Quote("You can do meaningful work without doing everything.", "Unknown"),
    Quote("Keep your eyes on the direction, not the distance.", "Unknown"),
    Quote("Make space for improvement instead of perfection.", "Unknown"),
    Quote("Your effort is a reason to respect yourself.", "Unknown"),
    Quote("Be steady, even when you cannot be fast.", "Unknown"),
    Quote("One good decision can lead to another.", "Unknown"),
    Quote("Keep practicing until the hard part feels easier.", "Unknown"),
    Quote("You can make a change without having all the answers.", "Unknown"),
    Quote("Let your progress encourage you to continue.", "Unknown"),
    Quote("Choose what moves you forward, not what holds you back.", "Unknown"),
    Quote("Give yourself credit for every honest attempt.", "Unknown"),
    Quote("Keep making time for what you want to build.", "Unknown"),
    Quote("Your next step can be simple and still matter.", "Unknown"),
    Quote("Do not let doubt speak louder than experience.", "Unknown"),
    Quote("Take the opportunity to begin where you are.", "Unknown"),
    Quote("Let today be a little better by your own measure.", "Unknown"),
    Quote("You can find a new solution with a calm mind.", "Unknown"),
    Quote("Keep your promises small enough to keep.", "Unknown"),
    Quote("Your effort today is part of your future.", "Unknown"),
    Quote("Try once more with a kinder approach.", "Unknown"),
    Quote("You do not need to be the loudest person in the room to matter.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Let your actions remind you what you are capable of.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Your opinion of yourself deserves a voice, too.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can be learning and still be impressive.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Stop measuring your worth with someone else’s ruler.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You are allowed to take up space and be heard.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Confidence grows each time you keep a promise to yourself.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You have something valuable to bring to the table.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Being different does not make you less worthy.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You do not have to prove your value every day.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Let yourself be a beginner without feeling small.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Your progress is evidence that you can grow.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can feel nervous and still trust your ability.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Speak to yourself like someone whose future matters.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Your strengths count, even when they come naturally.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You are more than the parts you wish were different.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Give yourself credit for the things you handle quietly.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You can be proud without being perfect.", "Unknown", "Confidence and Self-Esteem"),
    Quote("One awkward moment does not define who you are.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Your ideas deserve the chance to be heard.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You do not need everyone’s approval to move forward.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Take up the space your dreams require.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Let confidence be built by practice, not perfection.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You are allowed to believe good things about yourself.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Your past does not set the limit of your potential.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Remember the times you thought you could not, then did.", "Unknown", "Confidence and Self-Esteem"),
    Quote("Walk into today knowing you have learned a lot already.", "Unknown", "Confidence and Self-Esteem"),
    Quote("You deserve care on ordinary days, not only hard ones.", "Unknown", "Self-Love"),
    Quote("Rest is a way to respect your own limits.", "Unknown", "Self-Love"),
    Quote("You can love yourself while still working to grow.", "Unknown", "Self-Love"),
    Quote("Give yourself the patience you offer other people.", "Unknown", "Self-Love"),
    Quote("You do not have to earn the right to rest.", "Unknown", "Self-Love"),
    Quote("Your needs matter, even when someone else needs you.", "Unknown", "Self-Love"),
    Quote("Make room in your life for the person you are becoming.", "Unknown", "Self-Love"),
    Quote("Treat your mistakes as moments to learn, not reasons to punish yourself.", "Unknown", "Self-Love"),
    Quote("You are allowed to outgrow what once felt familiar.", "Unknown", "Self-Love"),
    Quote("Be on your own side while you figure things out.", "Unknown", "Self-Love"),
    Quote("Taking a break does not erase your progress.", "Unknown", "Self-Love"),
    Quote("You can say no and still be a caring person.", "Unknown", "Self-Love"),
    Quote("Your worth stays with you on unproductive days.", "Unknown", "Self-Love"),
    Quote("Give yourself permission to move at a human pace.", "Unknown", "Self-Love"),
    Quote("Care for yourself before you run completely empty.", "Unknown", "Self-Love"),
    Quote("You are allowed to change your mind when you learn more.", "Unknown", "Self-Love"),
    Quote("Make your inner voice a safe place to land.", "Unknown", "Self-Love"),
    Quote("Celebrate the small ways you show up for yourself.", "Unknown", "Self-Love"),
    Quote("You do not need to compare your healing to anyone else’s.", "Unknown", "Self-Love"),
    Quote("Let your boundaries protect the life you are building.", "Unknown", "Self-Love"),
    Quote("You can begin again without being cruel about yesterday.", "Unknown", "Self-Love"),
    Quote("Your body deserves respect at every stage of your journey.", "Unknown", "Self-Love"),
    Quote("Choose what supports you, not only what impresses others.", "Unknown", "Self-Love"),
    Quote("You can be a work in progress and still be loved.", "Unknown", "Self-Love"),
    Quote("Forgive yourself for not knowing then what you know now.", "Unknown", "Self-Love"),
    Quote("Your feelings deserve attention, not judgment.", "Unknown", "Self-Love"),
    Quote("Be gentle with the parts of you still learning to feel safe.", "Unknown", "Self-Love"),
    Quote("Start with one useful thing, then build from there.", "Unknown", "Motivation and Productivity"),
    Quote("Small actions turn good intentions into real change.", "Unknown", "Motivation and Productivity"),
    Quote("You do not need a perfect plan to take the first step.", "Unknown", "Motivation and Productivity"),
    Quote("Give your best attention to the next right task.", "Unknown", "Motivation and Productivity"),
    Quote("Progress often begins before motivation shows up.", "Unknown", "Motivation and Productivity"),
    Quote("Make the task smaller until starting feels possible.", "Unknown", "Motivation and Productivity"),
    Quote("Ten focused minutes can move you forward.", "Unknown", "Motivation and Productivity"),
    Quote("Consistency is built by returning, not by never slipping.", "Unknown", "Motivation and Productivity"),
    Quote("Finish one thing before you add five more.", "Unknown", "Motivation and Productivity"),
    Quote("Your future benefits from the effort you make today.", "Unknown", "Motivation and Productivity"),
    Quote("Use the time you have instead of waiting for more.", "Unknown", "Motivation and Productivity"),
    Quote("Done and improving beats perfect and unfinished.", "Unknown", "Motivation and Productivity"),
    Quote("Let your priorities decide where your energy goes.", "Unknown", "Motivation and Productivity"),
    Quote("Begin where you are, with what is already available.", "Unknown", "Motivation and Productivity"),
    Quote("Every finished step gives the next one a place to start.", "Unknown", "Motivation and Productivity"),
    Quote("Keep your goal clear and your next step simple.", "Unknown", "Motivation and Productivity"),
    Quote("Make a little progress before you check for applause.", "Unknown", "Motivation and Productivity"),
    Quote("Protect time for the things you say matter.", "Unknown", "Motivation and Productivity"),
    Quote("Momentum grows when you make starting a habit.", "Unknown", "Motivation and Productivity"),
    Quote("Choose the next action, not the whole mountain.", "Unknown", "Motivation and Productivity"),
    Quote("Work steadily; you do not have to rush to be effective.", "Unknown", "Motivation and Productivity"),
    Quote("Make today useful in one small, specific way.", "Unknown", "Motivation and Productivity"),
    Quote("Plan enough to begin, then learn as you go.", "Unknown", "Motivation and Productivity"),
    Quote("Keep showing up even when the work feels ordinary.", "Unknown", "Motivation and Productivity"),
    Quote("Your daily choices are shaping more than you can see.", "Unknown", "Motivation and Productivity"),
    Quote("Focus on what you can finish, not everything you could do.", "Unknown", "Motivation and Productivity"),
    Quote("Take action now; adjust your approach as you learn.", "Unknown", "Motivation and Productivity"),
    Quote("A hard chapter is not the whole story.", "Unknown", "Overcoming Challenges"),
    Quote("You can take a pause without giving up.", "Unknown", "Overcoming Challenges"),
    Quote("Some progress is simply choosing to try again.", "Unknown", "Overcoming Challenges"),
    Quote("One setback cannot erase all the steps you have taken.", "Unknown", "Overcoming Challenges"),
    Quote("You have survived days you once thought would break you.", "Unknown", "Overcoming Challenges"),
    Quote("Let a difficult moment be a moment, not your whole identity.", "Unknown", "Overcoming Challenges"),
    Quote("Ask for help; you were never meant to carry everything alone.", "Unknown", "Overcoming Challenges"),
    Quote("Slow progress is still a way through.", "Unknown", "Overcoming Challenges"),
    Quote("You can change your approach without abandoning your goal.", "Unknown", "Overcoming Challenges"),
    Quote("Even when the path changes, you can keep moving forward.", "Unknown", "Overcoming Challenges"),
    Quote("Give yourself time to find your footing again.", "Unknown", "Overcoming Challenges"),
    Quote("You do not have to solve everything today.", "Unknown", "Overcoming Challenges"),
    Quote("Hard things become more manageable one step at a time.", "Unknown", "Overcoming Challenges"),
    Quote("A mistake can teach you without defining you.", "Unknown", "Overcoming Challenges"),
    Quote("Let the next choice be wiser than the last one.", "Unknown", "Overcoming Challenges"),
    Quote("You can be tired and still have hope.", "Unknown", "Overcoming Challenges"),
    Quote("Keep going gently when going boldly feels impossible.", "Unknown", "Overcoming Challenges"),
    Quote("You are allowed to rebuild at your own pace.", "Unknown", "Overcoming Challenges"),
    Quote("An unexpected ending can make room for a new beginning.", "Unknown", "Overcoming Challenges"),
    Quote("Do not mistake a difficult season for a failed life.", "Unknown", "Overcoming Challenges"),
    Quote("Rest, regroup, and return when you are ready.", "Unknown", "Overcoming Challenges"),
    Quote("Your courage can be quiet and still be real.", "Unknown", "Overcoming Challenges"),
    Quote("Uncertainty does not mean you have no way forward.", "Unknown", "Overcoming Challenges"),
    Quote("You can learn from what happened without living there.", "Unknown", "Overcoming Challenges"),
    Quote("Even a tiny step can interrupt a standstill.", "Unknown", "Overcoming Challenges"),
    Quote("Let today be about finding the next foothold.", "Unknown", "Overcoming Challenges"),
    Quote("You do not need to feel fearless to face what comes.", "Unknown", "Overcoming Challenges"),
    Quote("Notice one good thing before the day gets busy.", "Unknown", "Happiness and Positivity"),
    Quote("Joy can live in small, ordinary moments.", "Unknown", "Happiness and Positivity"),
    Quote("You are allowed to enjoy life before everything is perfect.", "Unknown", "Happiness and Positivity"),
    Quote("A kind word can brighten two days at once.", "Unknown", "Happiness and Positivity"),
    Quote("Make space for the things that help you feel alive.", "Unknown", "Happiness and Positivity"),
    Quote("Look for what is working, not only what needs fixing.", "Unknown", "Happiness and Positivity"),
    Quote("Let a simple pleasure be enough for today.", "Unknown", "Happiness and Positivity"),
    Quote("Hope can begin as a very small possibility.", "Unknown", "Happiness and Positivity"),
    Quote("Collect moments that make you feel glad to be here.", "Unknown", "Happiness and Positivity"),
    Quote("Your day does not need to be extraordinary to be good.", "Unknown", "Happiness and Positivity"),
    Quote("Share your light without measuring how far it travels.", "Unknown", "Happiness and Positivity"),
    Quote("Gratitude can make room for joy beside the hard things.", "Unknown", "Happiness and Positivity"),
    Quote("Choose a thought that helps you take the next step.", "Unknown", "Happiness and Positivity"),
    Quote("Give yourself permission to laugh in the middle of life.", "Unknown", "Happiness and Positivity"),
    Quote("Peace can be found in doing one thing at a time.", "Unknown", "Happiness and Positivity"),
    Quote("Let the good moments count, even when they are brief.", "Unknown", "Happiness and Positivity"),
    Quote("Bring a little warmth wherever you can.", "Unknown", "Happiness and Positivity"),
    Quote("Today still holds something you have not noticed yet.", "Unknown", "Happiness and Positivity"),
    Quote("Be present for the life happening around you.", "Unknown", "Happiness and Positivity"),
    Quote("Make room for wonder, even in familiar places.", "Unknown", "Happiness and Positivity"),
    Quote("Your kindness can make an ordinary day feel lighter.", "Unknown", "Happiness and Positivity"),
    Quote("Let yourself celebrate without waiting for a milestone.", "Unknown", "Happiness and Positivity"),
    Quote("A fresh morning is a chance to try a new approach.", "Unknown", "Happiness and Positivity"),
    Quote("Find beauty in the small things you usually pass by.", "Unknown", "Happiness and Positivity"),
    Quote("Good things can grow from simple, steady care.", "Unknown", "Happiness and Positivity"),
    Quote("Choose to notice progress as well as problems.", "Unknown", "Happiness and Positivity"),
    Quote("You can welcome happiness without needing to explain it.", "Unknown", "Happiness and Positivity")
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
                                if (showFavoritesSwipeHint) {
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
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        modifier = Modifier.padding(end = 10.dp),
                                        style = favoritesShadowStyle,
                                        text = "${favoriteQuote.text}",
                                        fontSize = 20.sp,
                                        lineHeight = 29.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White,
                                        textAlign = TextAlign.Start
                                    )
                                }

                                TextButton(
                                    text = "×",
                                    modifier = Modifier.width(28.dp),
                                    fontSize = 16.sp,
                                    onClick = {
                                        quoteToRemove = favoriteIndex
                                    }
                                )
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