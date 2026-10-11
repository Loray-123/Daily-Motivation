
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

// Secular motivational quotations. Religious references have been excluded.
// The original first eight entries remain in place.
val quotes = listOf(
    Quote("Believe you can and you're halfway there.", "Theodore Roosevelt"),
    Quote("The only way to do great work is to love what you do.", "Steve Jobs"),
    Quote("It always seems impossible until it's done.", "Nelson Mandela"),
    Quote("You are never too old to set another goal or to dream a new dream.", "C.S. Lewis"),
    Quote("Start where you are. Use what you have. Do what you can.", "Arthur Ashe"),
    Quote("Success is the sum of small efforts, repeated day in and day out.", "Robert Collier"),
    Quote("Don't watch the clock; do what it does. Keep going.", "Sam Levenson"),
    Quote("The future depends on what you do today.", "Mahatma Gandhi"),
    Quote("You cannot love a thing without wanting to fight for it. You cannot fight without something to fight for. To love a thing without wishing to fight for it is not love at all; it is lust.", "G. K. Chesterton"),
    Quote("The believer has his stand on the Rock; for this reason he cannot be overthrown by the dashing of the billows.", "St. John Chrysostom"),
    Quote("Love finds nothing hard; no task is difficult to the eager. Think of all that Jacob bore for Rachel, the wife who had been promised to him. And they seemed unto him but a few days for the love he had to her.", "St. Jerome"),
    Quote("Charity is the form of the virtues.", "St. Thomas Aquinas"),
    Quote("True love does not demand a reward, but it deserves one.", "St. Bernard of Clairvaux"),
    Quote("Let us always meet each other with a smile, for the smile is the beginning of love.", "St. Teresa of Calcutta"),
    Quote("Let humility be always at work, like the bee at the honeycomb, or all will be lost.", "St. Teresa of Ávila"),
    Quote("Love does not set them all to work suddenly, or equally, at all times and everywhere.", "St. Francis de Sales"),
    Quote("Charity can always grow greater in itself, because it is a participation in uncreated love and unlimited love.", "Fr. Reginald Garrigou-Lagrange, O.P."),
    Quote("Behave as if you were a stranger, and wherever you are, do not expect your words to have an influence and you will be at peace.", "Abba Poemen"),
    Quote("Reputation, after all, is but a signboard giving notice where virtue dwells, and virtue itself is always and everywhere preferable.", "St. Francis de Sales"),
    Quote("No virtue, my daughter, can have life in itself except through charity and humility, which is the foster-mother and nurse of charity.", "St. Catherine of Siena"),
    Quote("From all, learn gladly what you do not know, because humility can make common to you what nature has made proper to each. You will be wiser than all, if you are willing to learn from all.", "Hugh of St. Victor"),
    Quote("No one there hears Him calling, Come unto me all you that labour. They scorn to learn of Him, because He is meek and lowly of heart; for You have hid those things from the wise and prudent, and hast revealed them unto babes.", "St. Augustine of Hippo"),
    Quote("If then we wish to enjoy pleasure, above all things else let us shun wickedness, and follow after virtue; since it is not in the nature of things for one to have a share thereof on any other terms, even if we were mounted upon the king's throne itself.", "St. John Chrysostom"),
    Quote("Family, become what you are.", "St. John Paul II"),
    Quote("Christianity is the only frame for pagan freedom.", "G. K. Chesterton"),
    Quote("The glory of a good man is the testimony of a good conscience.", "Thomas à Kempis"),
    Quote("Go and sit in your cell and your cell will teach you everything.", "Abba Moses"),
    Quote("Mysticism keeps men sane; when you destroy mystery you create morbidity.", "G. K. Chesterton"),
    Quote("I do not seek to understand that I may believe, but I believe in order to understand.", "St. Anselm of Canterbury"),
    Quote("Despise not the grace because it is freely given, but receive and treasure it devoutly.", "St. Cyril of Jerusalem"),
    Quote("Too late loved I Thee, O Thou Beauty of ancient days, yet ever new! too late I loved Thee!", "St. Augustine of Hippo"),
    Quote("This first great truth now remains established, that a good death depends upon a good life.", "St. Robert Bellarmine"),
    Quote("Love is a great thing, a good above all others, which alone maketh every heavy burden light.", "Thomas à Kempis"),
    Quote("Truth is always a quarry hard to hunt, and therefore we must look everywhere for its tracks.", "St. Basil the Great"),
    Quote("Of all things to be sought, the first is wisdom, in which the form of the perfect good consists.", "Hugh of St. Victor"),
    Quote("As you have entered upon a good and most glorious path, run with reverence the race of godliness.", "St. Cyril of Jerusalem"),
    Quote("Wherefore virtue has need at our hands of willingness alone, since it is in us and is formed from us.", "St. Athanasius"),
    Quote("Love begins at home, and it is not how much we do, but how much love we put in the action that we do.", "St. Teresa of Calcutta"),
    Quote("My sins run out behind me, and I do not see them, and today I am coming to judge the errors of another.", "Abba Moses"),
    Quote("For to suffer without making excuses is a habit of great perfection, and very edifying and meritorious.", "St. Teresa of Ávila"),
    Quote("Love desires secrecy; yea, though lovers may have nothing secret to say, yet they love to say it secretly.", "St. Francis de Sales"),
    Quote("Learn all things; afterward you will see that nothing is superfluous. Constricted knowledge is not pleasant.", "Hugh of St. Victor"),
    Quote("The whole point of St. Francis of Assisi is that he certainly was ascetical and he certainly was not gloomy.", "G. K. Chesterton"),
    Quote("The world was a shock, but it was not merely shocking; existence was a surprise, but it was a pleasant surprise.", "G. K. Chesterton"),
    Quote("The madman is not the man who has lost his reason. The madman is the man who has lost everything except his reason.", "G. K. Chesterton"),
    Quote("Every fortune is good fortune; for it either rewards, disciplines, amends, or punishes, and so is either useful or just.", "St. Boethius"),
    Quote("Love the one good in which are all goods, and it sufficeth. Desire the simple good which is every good, and it is enough.", "St. Anselm of Canterbury"),
    Quote("In this night of reconcilement let no man be wroth or gloomy! in this night that stills all, none that threatens or disturbs!", "St. Ephrem the Syrian"),
    Quote("O Thou Good Omnipotent, who so carest for every one of us as if You cared for him only, and so for all as if they were but one!", "St. Augustine of Hippo"),
    Quote("Virginity is really the whole offering of soul and body to be consumed in the fire of love and changed into the flame of its glory.", "Caryll Houselander"),
    Quote("For piety is an insatiable thing; and it argues a childish mind to imagine from just the beginnings that you have obtained the whole.", "St. John Chrysostom"),
    Quote("For nothing is more frigid than a teacher who shows his philosophy only in words: this is to act the part not of a teacher, but of a hypocrite.", "St. John Chrysostom"),
    Quote("The active life is the innocence of good works, the contemplative the speculation of supernal things; that one is common to many, this indeed to few.", "St. Isidore of Seville"),
    Quote("Hence, there is no doubt that there exists a being, than which nothing greater can be conceived, and it exists both in the understanding and in reality.", "St. Anselm of Canterbury"),
    Quote("When the body therefore fasts from food, let the mind fast from vices, and pass judgment upon all earthly cares and desires according to the law of its King.", "St. Leo the Great"),
    Quote("To begin is easy; to persevere is sanctity. Let your perseverance not be a blind consequence of the first impulse, the work of inertia: let it be a reflective perseverance.", "St. Josemaría Escrivá"),
    Quote("It belongs to the wise man to meditate especially on the truth belonging to the first principle and to teach it to others, so it belongs to him to refute the opposing falsehood.", "St. Thomas Aquinas"),
    Quote("As often as this life's idle show tries to charm you; as often as you see in the world some vain pomp, transport yourself in mind to Paradise, essay to be now what you will be hereafter.", "St. Jerome"),
    Quote("By the Ark of the Covenant we understand the grace of contemplation. An ark is a secret place for gold and silver; the grace of contemplation lays hold of the treasury of celestial wisdom.", "Richard of St. Victor"),
    Quote("For peace is a good so great, that even in this earthly and mortal life there is no word we hear with such pleasure, nothing we desire with such zest, or find to be more thoroughly gratifying.", "St. Augustine of Hippo"),
    Quote("Poverty, to those who bear it wisely, is a great possession, a treasure that cannot be taken away; the stoutest of staves; a way of gain that cannot be thwarted; a lodging that is safe from snares.", "St. John Chrysostom"),
    Quote("Burst forth into a fountain of abundance and overflow with mystical knowledge, until they who now think you contemptible because of Eve's transgression are stirred up by the flood of your irrigation.", "St. Hildegard of Bingen"),
    Quote("I think that for those living in community obedience is a greater virtue than chastity, however perfect. Chastity carries within it the danger of pride, but obedience has within it the promise of humility.", "Amma Syncletica"),
    Quote("He who is the Imposer of the burden is Himself the Aider in its execution: and lest the weak recipient should fall beneath the greatness of the grace, He who conferred the dignity will also give the power.", "St. Leo the Great"),
    Quote("A habit of mind is formed which lasts through life, of which the attributes are, freedom, equitableness, calmness, moderation, and wisdom; or what in a former Discourse I have ventured to call a philosophical habit.", "St. John Henry Newman"),
    Quote("Love attracts love; mine darts towards Thee, and would fain make the abyss brim over, but alas! it is not even as a dewdrop in the ocean. To love Thee as Thou lovest me, I must make Thy Love mine own. Thus alone can I find rest.", "St. Thérèse of Lisieux"),
    Quote("It behooves therefore those who have been deemed worthy to see and to hear such things, and who have enjoyed so great a gift, to display also a life worthy of the doctrines, that they may enjoy also the good things which are (laid up) there.", "St. John Chrysostom"),
    Quote("I am love and Mercy Itself. There is no misery that could be a match for My mercy, neither will misery exhaust it, because as it is being granted—it increases. The soul that trusts in My mercy is most fortunate, because I Myself take care of it.", "St. Faustina Kowalska"),
    Quote("Rest is in Him alone.", "St. Bernard of Clairvaux"),
    Quote("Grace does not destroy nature, but perfects it.", "St. Thomas Aquinas"),
    Quote("She is Europe and all our past. She is returning.", "Hilaire Belloc"),
    Quote("What is wrong is that we do not ask what is right.", "G. K. Chesterton"),
    Quote("O mystery of piety! O sign of unity! O bond of charity!", "St. Augustine of Hippo"),
    Quote("The Deity, therefore, is ineffable and incomprehensible.", "St. John of Damascus"),
    Quote("I always took delight in learning, or teaching, or writing.", "St. Bede the Venerable"),
    Quote("Fix your eyes on the Crucified One, and all will seem easy.", "St. Teresa of Ávila"),
    Quote("If you are what you ought to be, you will set fire to all Italy.", "St. Catherine of Siena"),
    Quote("One man inflamed with zeal is sufficient to reform a whole community!", "St. John Chrysostom"),
    Quote("He who prays is certainly saved; he who prays not is certainly damned.", "St. Alphonsus Liguori"),
    Quote("I am wont to visit My elect in two ways—by temptation and by consolation.", "Thomas à Kempis"),
    Quote("The life of a monk ought at all times to have about it a Lenten character.", "St. Benedict of Nursia"),
    Quote("For where can it be well with us without Him, and where ill if He be present?", "St. Bernard of Clairvaux"),
    Quote("Thou madest us for Thyself, and our heart is restless, until it repose in Thee.", "St. Augustine of Hippo"),
    Quote("Eternity is the possession of endless life whole and perfect at a single moment.", "St. Boethius"),
    Quote("Goodness extends Its goodness by the very fact of Its existence unto all things.", "(Pseudo-Dionysius)"),
    Quote("A candelabrum is a holder for lights; discretion is the lamp of the inner person.", "Richard of St. Victor"),
    Quote("Hearken, O my son, to the precepts of thy master, and incline the ear of thine heart.", "St. Benedict of Nursia"),
    Quote("the New Testament lies hidden in the Old and the Old Testament is unveiled in the New.", "St. Augustine of Hippo"),
    Quote("A man is born natural and physical. In baptism he becomes supernatural and theological.", "St. Lawrence of Brindisi"),
    Quote("Do you fast? Give me proof of it by your works. If you see a poor man, take pity on him.", "St. John Chrysostom"),
    Quote("There is nothing so good as thanksgiving; even as there is nothing worse than blasphemy.", "St. John Chrysostom"),
    Quote("To the hard of hearing you shout, and for the almost-blind you draw large and startling figures.", "Flannery O'Connor"),
    Quote("To-day Godhead sealed itself upon Manhood, that so with the Godhead's stamp Manhood might be adorned.", "St. Ephrem the Syrian"),
    Quote("For the wilderness is the mother of quiet; it is a calm and a harbor, delivering us from all turmoils.", "St. John Chrysostom"),
    Quote("Why, then, ye children of mortality, seek ye from without that happiness whose seat is only within us?", "St. Boethius"),
    Quote("There we shall rest and see, see and love, love and praise. This is what shall be in the end without end.", "St. Augustine of Hippo"),
    Quote("But our very Life descended hither, and bore our death, and slew it, out of the abundance of His own life.", "St. Augustine of Hippo"),
    Quote("For that which He has not assumed He has not healed; but that which is united to His Godhead is also saved.", "St. Gregory of Nazianzus"),
    Quote("Since then He Himself declared and said of the Bread, 'This is My Body,' who shall dare to doubt any longer?", "St. Cyril of Jerusalem"),
    Quote("Do not then go beyond yourself to seek for evil, and imagine that there is an original nature of wickedness.", "St. Basil the Great"),
    Quote("The sixth, to consider how He suffers all this for my sins, etc.; and what I ought to do and suffer for Him.", "St. Ignatius of Loyola"),
    Quote("All things, then, seek the good; indeed, you may express the fact by defining good as that which all desire.", "St. Boethius"),
    Quote("Therefore the last and perfect happiness, which we await in the life to come, consists entirely in contemplation.", "St. Thomas Aquinas"),
    Quote("Oh, what a sure path is the way of Thy Passion, along the way of truth, up to the very pinnacle of all perfection.", "Bl. Henry Suso"),
    Quote("There is no such thing on earth as an uninteresting subject; the only thing that can exist is an uninterested person.", "G. K. Chesterton"),
    Quote("He gave us Baptism, not that we should receive and depart, but that we should show the fruits of it in our after life.", "St. John Chrysostom"),
    Quote("Nothing is worse than envy and malice, nothing more mischievous than vainglory; it is wont to mar ten thousand good things.", "St. John Chrysostom"),
    Quote("Yesterday I was crucified with Him; today I am glorified with Him. Yesterday I died with Him; today I am quickened with Him.", "St. Gregory of Nazianzus"),
    Quote("It follows that if there is any grace in the water, it is not of the nature of the water, but of the presence of the Spirit.", "St. Basil the Great"),
    Quote("Maker of earth and sky, from age to age / Who rul'st the world by reason; at whose word / Time issues from Eternity's abyss.", "St. Boethius"),
    Quote("And by thought it is that He creates, and thought is the basis of the work, the Word filling it and the Spirit perfecting it.", "St. John of Damascus"),
    Quote("What art thou, except that which, as the highest of all beings, alone exists through itself, and creates all other things from nothing?", "St. Anselm of Canterbury"),
    Quote("By the simplex and supernatural nature of all beautiful things, all beauty, and everything beautiful, pre-existed uniquely as to Cause.", "(Pseudo-Dionysius)"),
    Quote("This is the summit of perfection and to it we should always aspire; this should be the goal of all our works, desires, meditations and prayers.", "St. Alphonsus Liguori"),
    Quote("And the Word was made flesh and tabernacled in us (and we saw His glory, the glory as of the Only-Begotten of the Father) full of grace and truth.", "St. Cyril of Alexandria"),
    Quote("We are not real social workers. We may be doing social work in the eyes of the people, but we are really contemplatives in the heart of the world.", "St. Teresa of Calcutta"),
    Quote("The Spirit of Truth Himself makes the house of His glory shine with the brightness of His light, and will have nothing dark nor lukewarm in His temple.", "St. Leo the Great"),
    Quote("The world, which promises small and passing things, is served with great eagerness: I promise great and eternal things and the hearts of men grow dull.", "Thomas à Kempis"),
    Quote("O Helmsman of that ark, be my pilot on the dry land! To it Thou gavest rest in the haven of a mountain; to me give Thou rest also in the haven of my walls!", "St. Ephrem the Syrian"),
    Quote("After that we must surely say with the Saints, De Maria nunquam satis; we have not yet praised, exalted, honoured, loved, and served Mary as we ought to do.", "St. Louis de Montfort"),
    Quote("Since men become happy by the acquisition of happiness, while happiness is very Godship, it is manifest that they become happy by the acquisition of Godship.", "St. Boethius"),
    Quote("Delicate is he still to whom the fatherland is sweet; strong already, he for whom every soil is a fatherland; but perfect, he for whom the whole world is exile.", "Hugh of St. Victor"),
    Quote("An understanding of this doctrine is so necessary that without it no one can study metaphysics, and ignorance of it gives rise to many errors in other sciences.", "Thomas Cajetan (Cardinal de Vio)"),
    Quote("All progress proceeds from reading and meditation. For the things we do not know, we learn by reading; but the things we have learned, we preserve by meditations.", "St. Isidore of Seville"),
    Quote("A man is justified by grace rather than by his own excellence: for grace is to every one the source of righteousness, the source of good and the fountain of merit.", "St. Leo the Great"),
    Quote("Hence this is not a formal, but a substantial conversion; nor is it a kind of natural movement: but, with a name of its own, it can be called \"transubstantiation.\"", "St. Thomas Aquinas"),
    Quote("Glory to the Hidden One, Whose Son was made manifest! Glory to that Living One, Whose Son was made to die! Glory to that Great One, Whose Son descended and was small!", "St. Ephrem the Syrian"),
    Quote("I know that angels visit the canonical hours and the congregations of the brethren. What if they do not find me among the brethren? May they not say, 'Where is Bede?'", "St. Bede the Venerable"),
    Quote("He makes himself the hungry one – the naked one – the homeless one – the sick one – the one in prison – the lonely one – the unwanted one – and he says: You did it to me.", "St. Teresa of Calcutta"),
    Quote("I assert, first, that the natural law is not only indicative of bad and good, but also contains its own proper prohibition of what is bad and prescription of what is good.", "Francisco Suarez, SJ"),
    Quote("All mortal creatures in those anxious aims which find employment in so many varied pursuits, though they take many paths, yet strive to reach one goal—the goal of happiness.", "St. Boethius"),
    Quote("And on the day called Sunday, all who live in cities or in the country gather together to one place, and the memoirs of the apostles or the writings of the prophets are read.", "St. Justin Martyr"),
    Quote("For this reason man was brought into the world last after the creation, not being rejected to the last as worthless, but as one whom it behooved to be king over his subjects at his very birth.", "St. Gregory of Nyssa"),
    Quote("No indeed, my child, the devotion which is true hinders nothing, but on the contrary it perfects everything; and that which runs counter to the rightful vocation of any one is, you may be sure, a spurious devotion.", "St. Francis de Sales"),
    Quote("The Babe that I carry carries me, says Mary, and He has lowered His wings, and taken and placed me between His pinions, and mounted into the air; and a promise has been given me that height and depth shall be my Son's.", "St. Ephrem the Syrian"),
    Quote("When we say: Your kingdom come, which shall certainly come whether we wish it or not, we do by these words stir up our own desires for that kingdom, that it may come to us, and that we may be found worthy to reign in it.", "St. Augustine of Hippo"),
    Quote("Meditation is frequent thinking with counsel, which prudently investigates the cause and origin, the mode and utility of each thing. Meditation takes its beginning from reading, yet is constrained by no rules or precepts of reading.", "Hugh of St. Victor"),
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
