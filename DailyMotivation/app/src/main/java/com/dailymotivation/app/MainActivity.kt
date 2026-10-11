
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

// 365 attributed quotations. The original first eight entries remain in place
// so previously saved favorites keep their existing index references.
// Additional quotations were selected from a curated, source-verified corpus.
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
    Quote("Do not be afraid of Christ! He takes nothing away, and he gives you everything.", "Pope Benedict XVI"),
    Quote("The believer has his stand on the Rock; for this reason he cannot be overthrown by the dashing of the billows.", "St. John Chrysostom"),
    Quote("Love finds nothing hard; no task is difficult to the eager. Think of all that Jacob bore for Rachel, the wife who had been promised to him. And they seemed unto him but a few days for the love he had to her.", "St. Jerome"),
    Quote("Open wide the doors for Christ.", "St. John Paul II"),
    Quote("Charity is the form of the virtues.", "St. Thomas Aquinas"),
    Quote("We are an Easter People and Alleluia is our song!", "St. John Paul II"),
    Quote("Humility and the fear of God are above all virtues.", "Abba John the Dwarf"),
    Quote("For all things in the eye of God are second to love.", "St. John Chrysostom"),
    Quote("True love does not demand a reward, but it deserves one.", "St. Bernard of Clairvaux"),
    Quote("Let us always meet each other with a smile, for the smile is the beginning of love.", "St. Teresa of Calcutta"),
    Quote("Let humility be always at work, like the bee at the honeycomb, or all will be lost.", "St. Teresa of Ávila"),
    Quote("Love does not set them all to work suddenly, or equally, at all times and everywhere.", "St. Francis de Sales"),
    Quote("He knew that the praise of God stands on its strongest ground when it stands on nothing.", "G. K. Chesterton"),
    Quote("All the virtues assist the mind in the pursuit of Divine Love, but above all does pure prayer.", "St. Maximus the Confessor"),
    Quote("The act and work of love—is strong as death, and emulation and importunity last as long as hell.", "St. John of the Cross"),
    Quote("The one who has hope lives differently; the one who hopes has been granted the gift of a new life.", "Pope Benedict XVI"),
    Quote("My children, the three acts of faith, hope and charity contain all the happiness of man upon the earth.", "St. John Vianney"),
    Quote("For faith is to believe what thou yet seest not; and the reward of faith is, to see what thou believest.", "St. Augustine of Hippo"),
    Quote("The three mystical virtues which Christianity has not adopted, but invented, are faith, hope, and charity.", "G. K. Chesterton"),
    Quote("Love, therefore, is the foremost of all excellent achievements and the first of the commandments of the law.", "St. Gregory of Nyssa"),
    Quote("The first thing in the study of science is to seek God; then the honesty of life with the work of innocence.", "St. Isidore of Seville"),
    Quote("Charity can always grow greater in itself, because it is a participation in uncreated love and unlimited love.", "Fr. Reginald Garrigou-Lagrange, O.P."),
    Quote("The supreme wisdom is this, to work at the exclusion of all self-seeking, and to be ready to do the will of God.", "Thomas à Kempis"),
    Quote("Behave as if you were a stranger, and wherever you are, do not expect your words to have an influence and you will be at peace.", "Abba Poemen"),
    Quote("The love which is based on this communication, is charity: wherefore it is evident that charity is the friendship of man for God.", "St. Thomas Aquinas"),
    Quote("Reputation, after all, is but a signboard giving notice where virtue dwells, and virtue itself is always and everywhere preferable.", "St. Francis de Sales"),
    Quote("No virtue, my daughter, can have life in itself except through charity and humility, which is the foster-mother and nurse of charity.", "St. Catherine of Siena"),
    Quote("O God! the happiness Thou promisest us is such that the eyes of man cannot see it, his ears cannot hear it, nor his heart conceive it.", "St. John Vianney"),
    Quote("Love the holy scriptures, and wisdom will love you. Love wisdom, and it will keep you safe. Honour wisdom, and it will embrace you round about.", "St. Jerome"),
    Quote("There are some who pass through life as through a tunnel, without ever understanding the splendour, the security and the warmth of the sun of faith.", "St. Josemaría Escrivá"),
    Quote("Love is the light—and in the end, the only light—that can always illuminate a world grown dim and give us the courage needed to keep living and working.", "Pope Benedict XVI"),
    Quote("God is the foundation of hope: not any god, but the God who has a human face and who has loved us to the end, each one of us and humanity in its entirety.", "Pope Benedict XVI"),
    Quote("From all, learn gladly what you do not know, because humility can make common to you what nature has made proper to each. You will be wiser than all, if you are willing to learn from all.", "Hugh of St. Victor"),
    Quote("For the beginning is faith, and the end is love. Now these two, being inseparably connected together, are of God, while all other things which are requisite for a holy life follow after them.", "St. Ignatius of Antioch"),
    Quote("Let nothing disturb thee, nothing affright thee; all things are passing; God never changeth; patient endurance attaineth to all things; who God possesseth in nothing is wanting; alone God sufficeth.", "St. Teresa of Ávila"),
    Quote("Everyone who is wise according to God is blessed. The blessed life is the cognition of divinity. The cognition of divinity is the virtue of good work. The virtue of good work is the fruit of eternity.", "St. Isidore of Seville"),
    Quote("I do not congratulate that believer in Christ whose faith can be shaken by Celsus — who no longer shares the common life of men, but has long since departed — or by any apparent plausibility of argument.", "Origen of Alexandria"),
    Quote("When through love the mind is ravished by Divine knowledge and in going outside of creatures has a perception of Divine Transcendence, then it comes in consternation to a realization of its own lowliness.", "St. Maximus the Confessor"),
    Quote("Only in this friendship are the doors of life opened wide. Only in this friendship is the great potential of human existence truly revealed. Only in this friendship do we experience beauty and liberation.", "Pope Benedict XVI"),
    Quote("No one there hears Him calling, Come unto me all you that labour. They scorn to learn of Him, because He is meek and lowly of heart; for You have hid those things from the wise and prudent, and hast revealed them unto babes.", "St. Augustine of Hippo"),
    Quote("The beginning of discipline is humility: first, that he hold no knowledge, no scripture as vile; second, that he not be ashamed to learn from anyone; third, that when he shall have attained knowledge, he not contemn the others.", "Hugh of St. Victor"),
    Quote("Accordingly, two cities have been formed by two loves: the earthly by the love of self, even to the contempt of God; the heavenly by the love of God, even to the contempt of self. The former, in a word, glories in itself, the latter in the Lord.", "St. Augustine of Hippo"),
    Quote("If then we wish to enjoy pleasure, above all things else let us shun wickedness, and follow after virtue; since it is not in the nature of things for one to have a share thereof on any other terms, even if we were mounted upon the king's throne itself.", "St. John Chrysostom"),
    Quote("Seek Him in faith and love, without seeking to satisfy thyself in aught, or to understand more than is expedient for thee to know; for faith and love are the two guides of the blind; they will lead thee, by a way thou knowest not, to the secret chamber of God.", "St. John of the Cross"),
    Quote("Family, become what you are.", "St. John Paul II"),
    Quote("The motive for loving God is God Himself.", "St. Bernard of Clairvaux"),
    Quote("Without faith it is impossible to be saved.", "St. John of Damascus"),
    Quote("Christianity is the only frame for pagan freedom.", "G. K. Chesterton"),
    Quote("In the heart of the Church, my Mother, I will be Love.", "St. Thérèse of Lisieux"),
    Quote("At the close of life you will be examined as to your love.", "St. John of the Cross"),
    Quote("Blessed is the man who has learnt to love all men equally.", "St. Maximus the Confessor"),
    Quote("The glory of a good man is the testimony of a good conscience.", "Thomas à Kempis"),
    Quote("Go and sit in your cell and your cell will teach you everything.", "Abba Moses"),
    Quote("The whole world cannot afford thee peace, but the peace of God can.", "Thomas à Kempis"),
    Quote("Mysticism keeps men sane; when you destroy mystery you create morbidity.", "G. K. Chesterton"),
    Quote("Let us then not be ashamed of the Cross of our Saviour, but rather glory in it.", "St. Cyril of Jerusalem"),
    Quote("God wills only our good; God loves us more than anybody else can or does love us.", "St. Alphonsus Liguori"),
    Quote("It is not power, but love that redeems us! This is God's sign: he himself is love.", "Pope Benedict XVI"),
    Quote("For the glory of God is a living man; and the life of man consists in beholding God.", "St. Irenaeus of Lyons"),
    Quote("I do not seek to understand that I may believe, but I believe in order to understand.", "St. Anselm of Canterbury"),
    Quote("So then we ought to fear God only, and despise the demons, and be in no fear of them.", "St. Athanasius"),
    Quote("The reason for loving God is God Himself; the measure, is to love Him without measure.", "St. Bernard of Clairvaux"),
    Quote("We say that the Resurrection is the reconstitution of our nature in its original form.", "St. Gregory of Nyssa"),
    Quote("Despise not the grace because it is freely given, but receive and treasure it devoutly.", "St. Cyril of Jerusalem"),
    Quote("The time which you lend to God is not lost: he will return it to you with large interest.", "St. Basil the Great"),
    Quote("Too late loved I Thee, O Thou Beauty of ancient days, yet ever new! too late I loved Thee!", "St. Augustine of Hippo"),
    Quote("This first great truth now remains established, that a good death depends upon a good life.", "St. Robert Bellarmine"),
    Quote("Love is a great thing, a good above all others, which alone maketh every heavy burden light.", "Thomas à Kempis"),
    Quote("Truth is always a quarry hard to hunt, and therefore we must look everywhere for its tracks.", "St. Basil the Great"),
    Quote("He who hath greater knowledge, and not the fear of God, is the more severe judge of himself.", "Thomas à Kempis"),
    Quote("Love is a good disposition of the soul by which one prefers no being to the knowledge of God.", "St. Maximus the Confessor"),
    Quote("This is why I said at the beginning that the measure of our love to God is to love immeasurably.", "St. Bernard of Clairvaux"),
    Quote("Of all things to be sought, the first is wisdom, in which the form of the perfect good consists.", "Hugh of St. Victor"),
    Quote("As you have entered upon a good and most glorious path, run with reverence the race of godliness.", "St. Cyril of Jerusalem"),
    Quote("Wherefore virtue has need at our hands of willingness alone, since it is in us and is formed from us.", "St. Athanasius"),
    Quote("God has created all things for good; all things for their greatest good; everything for its own good.", "St. John Henry Newman"),
    Quote("Love begins at home, and it is not how much we do, but how much love we put in the action that we do.", "St. Teresa of Calcutta"),
    Quote("My sins run out behind me, and I do not see them, and today I am coming to judge the errors of another.", "Abba Moses"),
    Quote("Until I enter into thy joy, O Lord, who art the Three and the One God, blessed for ever and ever. Amen.", "St. Anselm of Canterbury"),
    Quote("For to suffer without making excuses is a habit of great perfection, and very edifying and meritorious.", "St. Teresa of Ávila"),
    Quote("Our Lord does not care so much for the importance of our works as for the love with which they are done.", "St. Teresa of Ávila"),
    Quote("Do not be afraid that the Lord Who has called us to drink of this spring will allow you to die of thirst.", "St. Teresa of Ávila"),
    Quote("On this hungering step, the soul is nourished upon love; for, even as is its hunger, so is its abundance.", "St. John of the Cross"),
    Quote("Love desires secrecy; yea, though lovers may have nothing secret to say, yet they love to say it secretly.", "St. Francis de Sales"),
    Quote("Learn all things; afterward you will see that nothing is superfluous. Constricted knowledge is not pleasant.", "Hugh of St. Victor"),
    Quote("The whole point of St. Francis of Assisi is that he certainly was ascetical and he certainly was not gloomy.", "G. K. Chesterton"),
    Quote("The world was a shock, but it was not merely shocking; existence was a surprise, but it was a pleasant surprise.", "G. K. Chesterton"),
    Quote("Love unites us to God; love covers a multitude of sins; love beareth all things, is long-suffering in all things.", "St. Clement of Rome"),
    Quote("The madman is not the man who has lost his reason. The madman is the man who has lost everything except his reason.", "G. K. Chesterton"),
    Quote("Take therefore first, as an indestructible foundation, the Cross, and build upon it the other articles of the faith.", "St. Cyril of Jerusalem"),
    Quote("Faith is a habit of the mind, whereby eternal life is begun in us, making the intellect assent to what is non-apparent.", "St. Thomas Aquinas"),
    Quote("Every fortune is good fortune; for it either rewards, disciplines, amends, or punishes, and so is either useful or just.", "St. Boethius"),
    Quote("The highest solace in life is the study of wisdom, and he who finds it is fortunate, and he who possesses it is blessed.", "Hugh of St. Victor"),
    Quote("I write the way I do because (not though) I am a Catholic. This is a fact and nothing covers it like the bald statement.", "Flannery O'Connor"),
    Quote("Believing is an act of the intellect assenting to the Divine truth at the command of the will moved by the grace of God.", "St. Thomas Aquinas"),
    Quote("Love the one good in which are all goods, and it sufficeth. Desire the simple good which is every good, and it is enough.", "St. Anselm of Canterbury"),
    Quote("It behoved that there should be sin; but all shall be well, and all shall be well, and all manner of thing shall be well.", "Julian of Norwich"),
    Quote("We have all known the long loneliness and we have learned that the only solution is love and that love comes with community.", "Servant of God Dorothy Day"),
    Quote("In this night of reconcilement let no man be wroth or gloomy! in this night that stills all, none that threatens or disturbs!", "St. Ephrem the Syrian"),
    Quote("Two things indeed are what repair the divine similitude in man, that is, the speculation of truth and the exercise of virtue.", "Hugh of St. Victor"),
    Quote("All true and living devotion presupposes the love of God; and indeed it is neither more nor less than a very real love of God.", "St. Francis de Sales"),
    Quote("O Thou Good Omnipotent, who so carest for every one of us as if You cared for him only, and so for all as if they were but one!", "St. Augustine of Hippo"),
    Quote("Christ is the morning star, who promises and reveals to the saints the eternal light of life, when the night of the world is past.", "St. Bede the Venerable"),
    Quote("Virginity is really the whole offering of soul and body to be consumed in the fire of love and changed into the flame of its glory.", "Caryll Houselander"),
    Quote("For piety is an insatiable thing; and it argues a childish mind to imagine from just the beginnings that you have obtained the whole.", "St. John Chrysostom"),
    Quote("The contemplative life is to cling with our whole mind to the love of God and our neighbor, and to desire nothing beside our Creator.", "St. Thomas Aquinas"),
    Quote("Virtue is a good quality of the mind, by which we live righteously, of which no one can make bad use, which God works in us, without us.", "St. Thomas Aquinas"),
    Quote("The greatest challenge of the day is: how to bring about a revolution of the heart, a revolution which has to start with each one of us?", "Servant of God Dorothy Day"),
    Quote("In the evening of our life, we shall be judged by love, namely, by the sincerity of our love for God, for our own soul, for our neighbor.", "Fr. Reginald Garrigou-Lagrange, O.P."),
    Quote("Oh, night that guided me, Oh, night more lovely than the dawn, Oh, night that joined Beloved with lover, Lover transformed in the Beloved!", "St. John of the Cross"),
    Quote("Christian, acknowledge thy dignity, and becoming a partner in the Divine nature, refuse to return to the old baseness by degenerate conduct.", "St. Leo the Great"),
    Quote("The word of God makes us change our concept of realism: the realist is the one who recognizes in the word of God the foundation of all things.", "Pope Benedict XVI"),
    Quote("For nothing is more frigid than a teacher who shows his philosophy only in words: this is to act the part not of a teacher, but of a hypocrite.", "St. John Chrysostom"),
    Quote("Therefore I maintain, my daughters, that we should fix our eyes on Christ our only good, and on His saints; there we shall learn true humility.", "St. Teresa of Ávila"),
    Quote("What doth it profit thee to enter into deep discussion concerning the Holy Trinity, if thou lack humility, and be thus displeasing to the Trinity?", "Thomas à Kempis"),
    Quote("Man is called to a fullness of life which far exceeds the dimensions of his earthly existence, because it consists in sharing the very life of God.", "St. John Paul II"),
    Quote("Faith is an eye that enlightens every conscience, and imparts understanding; for the Prophet says, And if you believe not, you shall not understand.", "St. Cyril of Jerusalem"),
    Quote("The active life is the innocence of good works, the contemplative the speculation of supernal things; that one is common to many, this indeed to few.", "St. Isidore of Seville"),
    Quote("Hence, there is no doubt that there exists a being, than which nothing greater can be conceived, and it exists both in the understanding and in reality.", "St. Anselm of Canterbury"),
    Quote("For He who is true God is also true man: and in this union there is no lie, since the humility of manhood and the loftiness of the Godhead both meet there.", "St. Leo the Great"),
    Quote("When the body therefore fasts from food, let the mind fast from vices, and pass judgment upon all earthly cares and desires according to the law of its King.", "St. Leo the Great"),
    Quote("Therefore, about to build, first set the foundation of history, then, through typological signification, raise the fabric of the mind into the citadel of faith.", "Hugh of St. Victor"),
    Quote("For ourselves, let us but obey God's voice in our hearts, and I will venture to say we shall have no doubts practically formidable about the truth of Scripture.", "St. John Henry Newman"),
    Quote("Beneficially, therefore, did the divine Mercy provide that it should instruct us to hold by faith even those truths that the human reason is able to investigate.", "St. Thomas Aquinas"),
    Quote("The presence of Christ's true body and blood in this sacrament cannot be detected by sense, nor understanding, but by faith alone, which rests upon Divine authority.", "St. Thomas Aquinas"),
    Quote("For thus we have been enriched by His poverty, the human nature having been brought up in Him to a God-befitting dignity, and established on thrones beyond all things.", "St. Cyril of Alexandria"),
    Quote("Prayer is nothing else but a conversation in which the soul amorously entertains herself with God concerning his most amiable goodness, to unite and join herself thereto.", "St. Francis de Sales"),
    Quote("To begin is easy; to persevere is sanctity. Let your perseverance not be a blind consequence of the first impulse, the work of inertia: let it be a reflective perseverance.", "St. Josemaría Escrivá"),
    Quote("He is wise, then, who knows how to keep silent. Lastly, the Wisdom of God said: The Lord has given to me the tongue of learning, that I should know when it is good to speak.", "St. Ambrose of Milan"),
    Quote("With you is perfect rest, and life unchanging. He who enters into you enters into the joy of his Lord, and shall have no fear, and shall do excellently in the most Excellent.", "St. Augustine of Hippo"),
    Quote("It belongs to the wise man to meditate especially on the truth belonging to the first principle and to teach it to others, so it belongs to him to refute the opposing falsehood.", "St. Thomas Aquinas"),
    Quote("We are Christians and Catholics not because we worship a key, but because we have passed a door; and felt the wind that is the trumpet of liberty blow over the land of the living.", "G. K. Chesterton"),
    Quote("Whoever believes that Aristotle was a god, must also believe that he never erred. But if one believe that Aristotle was a man, then doubtless he was liable to error just as we are.", "St. Albert the Great"),
    Quote("Let him never cease from prayer who has once begun it, be his life ever so wicked; for prayer is the way to amend it, and without prayer such amendment will be much more difficult.", "St. Teresa of Ávila"),
    Quote("Admit that God deserves to be loved very much, yea, boundlessly, because He loved us first, He infinite and we nothing, loved us, miserable sinners, with a love so great and so free.", "St. Bernard of Clairvaux"),
    Quote("And I comforted him, saying, 'On that scaffold whatever God wills shall happen. For know that we are not placed in our own power, but in that of God.' And he departed from me in sorrow.", "St. Perpetua"),
    Quote("As often as this life's idle show tries to charm you; as often as you see in the world some vain pomp, transport yourself in mind to Paradise, essay to be now what you will be hereafter.", "St. Jerome"),
    Quote("Do not be afraid to go out on the streets and into public places, like the first Apostles who preached Christ and the Good News of salvation in the squares of cities, towns and villages.", "St. John Paul II"),
    Quote("COME, then, let us extol the Peace Divine, and Source of conciliation, by hymns of peace! For this it is which unifies all, and engenders, and effects the agreement and fellowship of all.", "(Pseudo-Dionysius)"),
    Quote("By the Ark of the Covenant we understand the grace of contemplation. An ark is a secret place for gold and silver; the grace of contemplation lays hold of the treasury of celestial wisdom.", "Richard of St. Victor"),
    Quote("Among the most holy Sacraments of Christ's church the most excellent is the most divine Sacrament of the Eucharist, the Sacrament of Grace, the Sacrament of charity, the Sacrament of love.", "St. Lawrence of Brindisi"),
    Quote("He has reached the citadel of no small intelligence who knows that he cannot penetrate the secrets of God. Then, however, we rightly recognize God, when we deny that we know him perfectly.", "St. Isidore of Seville"),
    Quote("If thou wouldst know how these things are done, ask grace, not learning; desire, not intellect; the groaning of prayer, not the diligence of reading; the spouse, not the master; God, not man.", "St. Bonaventure"),
    Quote("For peace is a good so great, that even in this earthly and mortal life there is no word we hear with such pleasure, nothing we desire with such zest, or find to be more thoroughly gratifying.", "St. Augustine of Hippo"),
    Quote("On this step the soul now walks so anxiously that it seeks the Beloved in all things. When it eats, when it sleeps, when it watches, when it does aught soever, all its care is about the Beloved.", "St. John of the Cross"),
    Quote("Poverty, to those who bear it wisely, is a great possession, a treasure that cannot be taken away; the stoutest of staves; a way of gain that cannot be thwarted; a lodging that is safe from snares.", "St. John Chrysostom"),
    Quote("And so, Lord, do thou, who dost give understanding to faith, give me, so far as thou knowest it to be profitable, to understand that thou art as we believe; and that thou art that which we believe.", "St. Anselm of Canterbury"),
    Quote("Burst forth into a fountain of abundance and overflow with mystical knowledge, until they who now think you contemptible because of Eve's transgression are stirred up by the flood of your irrigation.", "St. Hildegard of Bingen"),
    Quote("Teach me, Lord, to do Your will. Teach me to live worthily and humbly in Your sight, for You are my wisdom Who know me truly, and Who knew me even before the world was made and before I was born into it.", "Thomas à Kempis"),
    Quote("Now let us sing the Eternal Life, from which comes the self-existing Life, and every life; and from which, to all things however partaking of life, is distributed the power to live appropriately to each.", "(Pseudo-Dionysius)"),
    Quote("This Good is celebrated by the sacred theologians, both as beautiful and as Beauty, and as Love, and as Beloved; and all the other Divine Names which beseem the beautifying and highly-favoured comeliness.", "(Pseudo-Dionysius)"),
    Quote("O Lord and Master of my life, give me not the spirit of sloth, of despondency, of ambition, or of idle talking; but bestow on me, Thy servant, the spirit of chastity, of humility, of patience, and of love.", "St. Ephrem the Syrian"),
    Quote("I think that for those living in community obedience is a greater virtue than chastity, however perfect. Chastity carries within it the danger of pride, but obedience has within it the promise of humility.", "Amma Syncletica"),
    Quote("He who is the Imposer of the burden is Himself the Aider in its execution: and lest the weak recipient should fall beneath the greatness of the grace, He who conferred the dignity will also give the power.", "St. Leo the Great"),
    Quote("Nothing was so necessary for raising our hope as to show us how deeply God loved us. And what could afford us a stronger proof of this than that the Son of God should become a partner with us of human nature?", "St. Thomas Aquinas"),
    Quote("A habit of mind is formed which lasts through life, of which the attributes are, freedom, equitableness, calmness, moderation, and wisdom; or what in a former Discourse I have ventured to call a philosophical habit.", "St. John Henry Newman"),
    Quote("The one who sees a trace of hatred in his own heart through any fault at all toward any man whoever he may be makes himself completely foreign to the love of God, because love for God in no way admits hatred for man.", "St. Maximus the Confessor"),
    Quote("Charity in truth, to which Jesus Christ bore witness by his earthly life and especially by his death and resurrection, is the principal driving force behind the authentic development of every person and of all humanity.", "Pope Benedict XVI"),
    Quote("I think that the Church is the only thing that is going to make the terrible world we are coming to endurable; the only thing that makes the Church endurable is that it is somehow the body of Christ and that on this we are fed.", "Flannery O'Connor"),
    Quote("Of all the practices of the spiritual life purity of heart stands highest, and rightly, for it is the end and reward of all our labours, and is found only with those who live truly according to the spirit and as good religious.", "St. Albert the Great"),
    Quote("Love attracts love; mine darts towards Thee, and would fain make the abyss brim over, but alas! it is not even as a dewdrop in the ocean. To love Thee as Thou lovest me, I must make Thy Love mine own. Thus alone can I find rest.", "St. Thérèse of Lisieux"),
    Quote("You shall love God with all your heart, and with all your soul, and with all your mind: so that you are to concentrate all your thoughts, your whole life and your whole intelligence upon Him from whom you derive all that you bring.", "St. Augustine of Hippo"),
    Quote("For your conduct as a king is based upon the knowledge which by orthodox preaching you have obtained of your God and Creator, whereby you believe and worship Him, and as far as man is able, pay Him the sincere devotion of your mind.", "St. Bede the Venerable"),
    Quote("It behooves therefore those who have been deemed worthy to see and to hear such things, and who have enjoyed so great a gift, to display also a life worthy of the doctrines, that they may enjoy also the good things which are (laid up) there.", "St. John Chrysostom"),
    Quote("I am love and Mercy Itself. There is no misery that could be a match for My mercy, neither will misery exhaust it, because as it is being granted—it increases. The soul that trusts in My mercy is most fortunate, because I Myself take care of it.", "St. Faustina Kowalska"),
    Quote("The world was not conceived by chance and without reason, but for an useful end and for the great advantage of all beings, since it is really the school where reasonable souls exercise themselves, the training ground where they learn to know God.", "St. Basil the Great"),
    Quote("For as God shed forth of the Holy Spirit, so also the love of God is shed abroad in our hearts through the Holy Spirit; in order that we may understand that the Holy Spirit is not a work, Who is the dispenser and plenteous Fount of the divine love.", "St. Ambrose of Milan"),
    Quote("Thou hast found him to be a being which is the highest of all beings, a being than which nothing better can be conceived; that this being is life itself, light, wisdom, goodness, eternal blessedness and blessed eternity; and that it is every where and always.", "St. Anselm of Canterbury"),
    Quote("Rest is in Him alone.", "St. Bernard of Clairvaux"),
    Quote("Prayer is nothing else than union with God.", "St. John Vianney"),
    Quote("Prayer is the raising up of the mind to God.", "St. Thomas Aquinas"),
    Quote("For He was made man that we might be made God.", "St. Athanasius"),
    Quote("Grace does not destroy nature, but perfects it.", "St. Thomas Aquinas"),
    Quote("I will spend my Heaven in doing good upon earth.", "St. Thérèse of Lisieux"),
    Quote("She is Europe and all our past. She is returning.", "Hilaire Belloc"),
    Quote("The divine words grow with the one who reads them.", "St. Gregory the Great"),
    Quote("What is wrong is that we do not ask what is right.", "G. K. Chesterton"),
    Quote("Ignorance of the Scriptures is ignorance of Christ.", "St. Jerome"),
    Quote("A humanism which excludes God is an inhuman humanism.", "Pope Benedict XVI"),
    Quote("O mystery of piety! O sign of unity! O bond of charity!", "St. Augustine of Hippo"),
    Quote("The Deity, therefore, is ineffable and incomprehensible.", "St. John of Damascus"),
    Quote("More easily through fasting does prayer penetrate heaven.", "St. Isidore of Seville"),
    Quote("For if we too live as though dying daily, we shall not sin.", "St. Athanasius"),
    Quote("I always took delight in learning, or teaching, or writing.", "St. Bede the Venerable"),
    Quote("Fix your eyes on the Crucified One, and all will seem easy.", "St. Teresa of Ávila"),
    Quote("If you don't deny yourself you will never be a soul of prayer.", "St. Josemaría Escrivá"),
    Quote("If you are what you ought to be, you will set fire to all Italy.", "St. Catherine of Siena"),
    Quote("We needed an Incarnate God, a God put to death, that we might live.", "St. Gregory of Nazianzus"),
    Quote("One man inflamed with zeal is sufficient to reform a whole community!", "St. John Chrysostom"),
    Quote("He who longs always after God, he sees Him: for God is in all things.", "St. John of Damascus"),
    Quote("I know and I have experienced that 'the Kingdom of God is within us.'", "St. Thérèse of Lisieux"),
    Quote("He who prays is certainly saved; he who prays not is certainly damned.", "St. Alphonsus Liguori"),
    Quote("Illumination is the greatest and most magnificent of the Gifts of God.", "St. Gregory of Nazianzus"),
    Quote("The greatest glory we can give to God is to do his will in everything.", "St. Alphonsus Liguori"),
    Quote("Believe me, my daughter, there is no way to God save through this door.", "St. Francis de Sales"),
    Quote("I am wont to visit My elect in two ways—by temptation and by consolation.", "Thomas à Kempis"),
    Quote("The life of a monk ought at all times to have about it a Lenten character.", "St. Benedict of Nursia"),
    Quote("God always was and always is, and always will be; or rather, God always Is.", "St. Gregory of Nazianzus"),
    Quote("For where can it be well with us without Him, and where ill if He be present?", "St. Bernard of Clairvaux"),
    Quote("Action is worth nothing without prayer: prayer grows in value with sacrifice.", "St. Josemaría Escrivá"),
    Quote("Thou madest us for Thyself, and our heart is restless, until it repose in Thee.", "St. Augustine of Hippo"),
    Quote("He can no longer have God for his Father, who has not the Church for his mother.", "St. Cyprian of Carthage"),
    Quote("Eternity is the possession of endless life whole and perfect at a single moment.", "St. Boethius"),
    Quote("THE REDEEMER OF MAN, Jesus Christ, is the centre of the universe and of history.", "St. John Paul II"),
    Quote("Goodness extends Its goodness by the very fact of Its existence unto all things.", "(Pseudo-Dionysius)"),
    Quote("A candelabrum is a holder for lights; discretion is the lamp of the inner person.", "Richard of St. Victor"),
    Quote("Hell took a body, and met God face to face. It took earth, and encountered Heaven.", "St. John Chrysostom"),
    Quote("St. Thomas did not reconcile Christ to Aristotle; he reconciled Aristotle to Christ.", "G. K. Chesterton"),
    Quote("I believe we shall never learn to know ourselves except by endeavouring to know God.", "St. Teresa of Ávila"),
    Quote("Prayer is an uprising of the mind to God or a petitioning of God for what is fitting.", "St. John of Damascus"),
    Quote("Hearken, O my son, to the precepts of thy master, and incline the ear of thine heart.", "St. Benedict of Nursia"),
    Quote("the New Testament lies hidden in the Old and the Old Testament is unveiled in the New.", "St. Augustine of Hippo"),
    Quote("Following Christ is thus the essential and primordial foundation of Christian morality.", "St. John Paul II"),
    Quote("A man is born natural and physical. In baptism he becomes supernatural and theological.", "St. Lawrence of Brindisi"),
    Quote("Do you fast? Give me proof of it by your works. If you see a poor man, take pity on him.", "St. John Chrysostom"),
    Quote("There is nothing so good as thanksgiving; even as there is nothing worse than blasphemy.", "St. John Chrysostom"),
    Quote("There is nothing more pleasing we can offer God than to say to him: 'Possess thyself of us.'", "St. Louis de Montfort"),
    Quote("We preach not one advent only of Christ, but a second also, far more glorious than the former.", "St. Cyril of Jerusalem"),
    Quote("Final and perfect happiness can consist in nothing else than the vision of the Divine Essence.", "St. Thomas Aquinas"),
    Quote("How blessed is he who reaches the fourth degree of love, wherein one loves himself only in God!", "St. Bernard of Clairvaux"),
    Quote("For alms deliver from all sin and from death, and will not suffer the soul to go into darkness.", "St. Robert Bellarmine"),
    Quote("To the hard of hearing you shout, and for the almost-blind you draw large and startling figures.", "Flannery O'Connor"),
    Quote("Man is created to praise, reverence, and serve God our Lord, and by this means to save his soul.", "St. Ignatius of Loyola"),
    Quote("Therefore as long as a thing has being, God must be present to it, according to its mode of being.", "St. Thomas Aquinas"),
    Quote("What the soul is to the human body, the Holy Spirit is to the Body of Christ, which is the Church.", "St. Augustine of Hippo"),
    Quote("Such is its power, that it can raise us at once to heaven, if only we approach it with a sober mind.", "St. John Chrysostom"),
    Quote("To-day Godhead sealed itself upon Manhood, that so with the Godhead's stamp Manhood might be adorned.", "St. Ephrem the Syrian"),
    Quote("For the wilderness is the mother of quiet; it is a calm and a harbor, delivering us from all turmoils.", "St. John Chrysostom"),
    Quote("Why, then, ye children of mortality, seek ye from without that happiness whose seat is only within us?", "St. Boethius"),
    Quote("He does not cease to be God because He becomes man, nor fail to be man because He remains for ever God.", "St. Hilary of Poitiers"),
    Quote("Blessed art thou, my Lord, my God, and most beloved lover of my soul, who art one God in three persons.", "St. Bridget of Sweden"),
    Quote("The Christian ideal has not been tried and found wanting. It has been found difficult; and left untried.", "G. K. Chesterton"),
    Quote("There we shall rest and see, see and love, love and praise. This is what shall be in the end without end.", "St. Augustine of Hippo"),
    Quote("But our very Life descended hither, and bore our death, and slew it, out of the abundance of His own life.", "St. Augustine of Hippo"),
    Quote("For that which He has not assumed He has not healed; but that which is united to His Godhead is also saved.", "St. Gregory of Nazianzus"),
    Quote("For by the Word of God being made man has come about the destruction of death and the resurrection of life.", "St. Athanasius"),
    Quote("Since then He Himself declared and said of the Bread, 'This is My Body,' who shall dare to doubt any longer?", "St. Cyril of Jerusalem"),
    Quote("Do not then go beyond yourself to seek for evil, and imagine that there is an original nature of wickedness.", "St. Basil the Great"),
    Quote("The sixth, to consider how He suffers all this for my sins, etc.; and what I ought to do and suffer for Him.", "St. Ignatius of Loyola"),
    Quote("All things, then, seek the good; indeed, you may express the fact by defining good as that which all desire.", "St. Boethius"),
    Quote("Take, Lord, and receive all my liberty, my memory, my intellect, and all my will—all that I have and possess.", "St. Ignatius of Loyola"),
    Quote("This is part of the infinite goodness of God, that He should allow evil to exist, and out of it produce good.", "St. Thomas Aquinas"),
    Quote("Therefore the last and perfect happiness, which we await in the life to come, consists entirely in contemplation.", "St. Thomas Aquinas"),
    Quote("When God thus encounters no resistance, He does His own will in the soul, drawing it to Himself and into Himself.", "Johannes Tauler"),
    Quote("Oh, what a sure path is the way of Thy Passion, along the way of truth, up to the very pinnacle of all perfection.", "Bl. Henry Suso"),
    Quote("Before the blessing of the heavenly words another nature is spoken of, after the consecration the Body is signified.", "St. Ambrose of Milan"),
    Quote("To-day was born the Child, and His name was called Wonder! For a wonder it is that God as a Babe should show Himself.", "St. Ephrem the Syrian"),
    Quote("There is no such thing on earth as an uninteresting subject; the only thing that can exist is an uninterested person.", "G. K. Chesterton"),
    Quote("Those who love God are always happy, because their whole happiness is to fulfill, even in adversity, the will of God.", "St. Alphonsus Liguori"),
    Quote("He gave us Baptism, not that we should receive and depart, but that we should show the fruits of it in our after life.", "St. John Chrysostom"),
    Quote("Eighty and six years have I served Him, and He never did me any injury: how then can I blaspheme my King and my Saviour?", "St. Polycarp of Smyrna"),
    Quote("God therefore is both Creator and Provider, and His creative and preserving and providing power is simply His good-will.", "St. John of Damascus"),
    Quote("Though I am poor, Lord, I give thee all I possess; but when I say I give thee my will, I have nothing left to give thee.", "St. Alphonsus Liguori"),
    Quote("Therefore it is necessary to arrive at a first mover, put in motion by no other; and this everyone understands to be God.", "St. Thomas Aquinas"),
    Quote("I am the wheat of God, and let me be ground by the teeth of the wild beasts, that I may be found the pure bread of Christ.", "St. Ignatius of Antioch"),
    Quote("Nothing is worse than envy and malice, nothing more mischievous than vainglory; it is wont to mar ten thousand good things.", "St. John Chrysostom"),
    Quote("The Holy Spirit is light and strength. He teaches us to distinguish between truth and falsehood, and between good and evil.", "St. John Vianney"),
    Quote("Yesterday I was crucified with Him; today I am glorified with Him. Yesterday I died with Him; today I am quickened with Him.", "St. Gregory of Nazianzus"),
    Quote("It follows that if there is any grace in the water, it is not of the nature of the water, but of the presence of the Spirit.", "St. Basil the Great"),
    Quote("When can the upright man be alone, since he is always with God? When is he left forsaken who is never separated from Christ?", "St. Ambrose of Milan"),
    Quote("Thus in the whole and perfect nature of true man was true God born, complete in what was His own, complete in what was ours.", "St. Leo the Great"),
    Quote("Maker of earth and sky, from age to age / Who rul'st the world by reason; at whose word / Time issues from Eternity's abyss.", "St. Boethius"),
    Quote("And by thought it is that He creates, and thought is the basis of the work, the Word filling it and the Spirit perfecting it.", "St. John of Damascus"),
    Quote("Raise your soul above the earth; draw from its natural conformation the rule of your conduct; fix your conversation in heaven.", "St. Basil the Great"),
    Quote("The circle returns upon itself and is bound. The cross opens its arms to the four winds; it is a signpost for free travellers.", "G. K. Chesterton"),
    Quote("I know, O Lord God Almighty, that I owe You, as the chief duty of my life, the devotion of all my words and thoughts to Yourself.", "St. Hilary of Poitiers"),
    Quote("This is the end and aim of prayer, my daughters; this is the reason of the spiritual marriage whose children are always good works.", "St. Teresa of Ávila"),
    Quote("Remember how important it is for you to have understood this truth—that the Lord is within us and that we should be there with Him.", "St. Teresa of Ávila"),
    Quote("Unto this Darkness which is beyond Light we pray that we may come, and may attain unto vision through the loss of sight and knowledge.", "(Pseudo-Dionysius)"),
    Quote("Praised be my Lord God, with all His creatures, and especially our brother the Sun, who brings us the day, and who brings us the light.", "St. Francis of Assisi"),
    Quote("What art thou, except that which, as the highest of all beings, alone exists through itself, and creates all other things from nothing?", "St. Anselm of Canterbury"),
    Quote("By the simplex and supernatural nature of all beautiful things, all beauty, and everything beautiful, pre-existed uniquely as to Cause.", "(Pseudo-Dionysius)"),
    Quote("For we ought to think of God even more often than we draw our breath; and if the expression is permissible, we ought to do nothing else.", "St. Gregory of Nazianzus"),
    Quote("God is in all things; not, indeed, as part of their essence, nor as an accident, but as an agent is present to that upon which it works.", "St. Thomas Aquinas"),
    Quote("Take my advice, my friend, and be slow to do evil, but swift to your salvation; for readiness to evil and tardiness to good are equally bad.", "St. Gregory of Nazianzus"),
    Quote("The splendour of truth shines forth in all the works of the Creator and, in a special way, in man, created in the image and likeness of God.", "St. John Paul II"),
    Quote("Whenever we cover our brother's sin, God will cover ours; whenever we tell people about our brother's guilt, God will do the same about ours.", "Abba Poemen"),
    Quote("How I wish your bearing and conversation were such that, on seeing or hearing you, people would say: This man reads the life of Jesus Christ.", "St. Josemaría Escrivá"),
    Quote("Prayer is of the heart, not of the lips. For God does not attend to the words of the supplicant, but looks upon the heart of the one praying.", "St. Isidore of Seville"),
    Quote("God has created me to do Him some definite service; He has committed some work to me which He has not committed to another. I have my mission.", "St. John Henry Newman"),
    Quote("The Creation proclaims outright the Creator; for the very heavens, as the Prophet says, declare the glory of God with their unutterable words.", "St. Gregory of Nyssa"),
    Quote("By the tabernacle of the covenant we understand the state of perfection. Where perfection of the soul is, there also is the habitation of God.", "Richard of St. Victor"),
    Quote("It is a matter of necessity that every Church should agree with this Church, on account of its preeminent authority [potiorem principalitatem].", "St. Irenaeus of Lyons"),
    Quote("This is the summit of perfection and to it we should always aspire; this should be the goal of all our works, desires, meditations and prayers.", "St. Alphonsus Liguori"),
    Quote("It is the conscience of the offerer that sanctifies the sacrifice when it is pure, and thus moves God to accept [the offering] as from a friend.", "St. Irenaeus of Lyons"),
    Quote("I remained, lost in oblivion; My face I reclined on the Beloved. All ceased and I abandoned myself, Leaving my cares forgotten among the lilies.", "St. John of the Cross"),
    Quote("And the Word was made flesh and tabernacled in us (and we saw His glory, the glory as of the Only-Begotten of the Father) full of grace and truth.", "St. Cyril of Alexandria"),
    Quote("We are not real social workers. We may be doing social work in the eyes of the people, but we are really contemplatives in the heart of the world.", "St. Teresa of Calcutta"),
    Quote("The primary object of contemplation is, in fact, God Himself and His infinite perfections, especially His goodness, His wisdom, and His providence.", "Fr. Reginald Garrigou-Lagrange, O.P."),
    Quote("The one who loves God cannot help but love also every man as himself even though he is displeased by the passions of those who are not yet purified.", "St. Maximus the Confessor"),
    Quote("The Spirit of Truth Himself makes the house of His glory shine with the brightness of His light, and will have nothing dark nor lukewarm in His temple.", "St. Leo the Great"),
    Quote("The world, which promises small and passing things, is served with great eagerness: I promise great and eternal things and the hearts of men grow dull.", "Thomas à Kempis"),
    Quote("O Helmsman of that ark, be my pilot on the dry land! To it Thou gavest rest in the haven of a mountain; to me give Thou rest also in the haven of my walls!", "St. Ephrem the Syrian"),
    Quote("She is not only human; she is humanity. The one thing that she did and does is the one thing that we all have to do, namely, to bear Christ into the world.", "Caryll Houselander"),
    Quote("After that we must surely say with the Saints, De Maria nunquam satis; we have not yet praised, exalted, honoured, loved, and served Mary as we ought to do.", "St. Louis de Montfort"),
    Quote("Since men become happy by the acquisition of happiness, while happiness is very Godship, it is manifest that they become happy by the acquisition of Godship.", "St. Boethius"),
    Quote("Our life and our death are with our neighbour. If we gain our brother, we have gained our God; but if we scandalize our brother, we have sinned against Christ.", "Abba Anthony the Great"),
    Quote("Delicate is he still to whom the fatherland is sweet; strong already, he for whom every soil is a fatherland; but perfect, he for whom the whole world is exile.", "Hugh of St. Victor"),
    Quote("Perfection is founded entirely on the love of God: 'Charity is the bond of perfection;' and perfect love of God means the complete union of our will with God's.", "St. Alphonsus Liguori"),
    Quote("An understanding of this doctrine is so necessary that without it no one can study metaphysics, and ignorance of it gives rise to many errors in other sciences.", "Thomas Cajetan (Cardinal de Vio)"),
    Quote("Whosoever praises God for His essential goodness, and not merely because of the benefits He has bestowed, does really love God for God’s sake, and not selfishly.", "St. Bernard of Clairvaux"),
    Quote("All progress proceeds from reading and meditation. For the things we do not know, we learn by reading; but the things we have learned, we preserve by meditations.", "St. Isidore of Seville"),
    Quote("A man is justified by grace rather than by his own excellence: for grace is to every one the source of righteousness, the source of good and the fountain of merit.", "St. Leo the Great"),
    Quote("Hence this is not a formal, but a substantial conversion; nor is it a kind of natural movement: but, with a name of its own, it can be called \"transubstantiation.\"", "St. Thomas Aquinas"),
    Quote("Do you know why he is the temple of God? Because in him it pleased the Father that all fullness should dwell; in him the fullness of the Godhead dwelt corporately.", "St. Lawrence of Brindisi"),
    Quote("To attain heaven is to reach our ultimate and supernatural goal, to see God as He sees Himself, to love Him as He loves Himself, to possess Him unfailingly forever.", "Fr. Reginald Garrigou-Lagrange, O.P."),
    Quote("Glory to the Hidden One, Whose Son was made manifest! Glory to that Living One, Whose Son was made to die! Glory to that Great One, Whose Son descended and was small!", "St. Ephrem the Syrian"),
    Quote("I know that angels visit the canonical hours and the congregations of the brethren. What if they do not find me among the brethren? May they not say, 'Where is Bede?'", "St. Bede the Venerable"),
    Quote("The interior life, or the life of the soul with God, well deserves to be called the one thing necessary, since by it we tend to our last end and assure our salvation.", "Fr. Reginald Garrigou-Lagrange, O.P."),
    Quote("There are two things, if I may speak summarily, which have been divinely handed down to us concerning the Virgin: that She was the Spouse of God and Mother of Christ.", "St. Lawrence of Brindisi"),
    Quote("He makes himself the hungry one – the naked one – the homeless one – the sick one – the one in prison – the lonely one – the unwanted one – and he says: You did it to me.", "St. Teresa of Calcutta"),
    Quote("Where hast Thou hidden Thyself, And abandoned me to my sorrow, O my Beloved! Thou hast fled like the hart, Having wounded me. I ran after Thee, crying; but Thou wert gone.", "St. John of the Cross"),
    Quote("I assert, first, that the natural law is not only indicative of bad and good, but also contains its own proper prohibition of what is bad and prescription of what is good.", "Francisco Suarez, SJ"),
    Quote("Would you do honour to Christ's body? Neglect Him not when naked; do not while here you honour Him with silken garments, neglect Him perishing without of cold and nakedness.", "St. John Chrysostom"),
    Quote("All mortal creatures in those anxious aims which find employment in so many varied pursuits, though they take many paths, yet strive to reach one goal—the goal of happiness.", "St. Boethius"),
    Quote("And on the day called Sunday, all who live in cities or in the country gather together to one place, and the memoirs of the apostles or the writings of the prophets are read.", "St. Justin Martyr"),
    Quote("If you hoard it, you won't keep it; if you scatter, you won't lose. For (says the scripture), 'He has dispersed, he has given to the poor; his righteousness endures forever.'", "St. Basil the Great"),
    Quote("Therefore, the natural law includes God's will, because without his will there is no legislation. Then, too, the obligation that pertains to the natural law is a true obligation.", "Francisco Suarez, SJ"),
    Quote("In that contemplation, therefore, God will be all in all; because nothing else but Himself will be required, but it will be sufficient to be enlightened by and to enjoy Him alone.", "St. Augustine of Hippo"),
    Quote("By this light the blessed are made “deiform”—i.e. like to God, according to the saying: “When He shall appear we shall be like to Him, and we shall see Him as He is” (1 John 3:2).", "St. Thomas Aquinas"),
    Quote("In the distribution of alms and care of the poor, let our Christian fast-times be fat and abound; and let each bestow on the weak and destitute those dainties which he denies himself.", "St. Leo the Great"),
    Quote("By no means then is it permitted to speak, or even to think, anything, concerning the superessential and hidden Deity, beyond those things divinely revealed to us in the sacred Oracles.", "(Pseudo-Dionysius)"),
    Quote("It was the first day of a new creation, with a new heaven and a new earth; and in a semblance of the gardener God walked again in the garden, in the cool not of the evening but the dawn.", "G. K. Chesterton"),
    Quote("Let us look steadfastly to the blood of Christ, and see how precious that blood is to God which, having been shed for our salvation, has set the grace of repentance before the whole world.", "St. Clement of Rome"),
    Quote("All the pleasures of the world, and all the kingdoms of this earth, shall profit me nothing. It is better for me to die on behalf of Jesus Christ, than to reign over all the ends of the earth.", "St. Ignatius of Antioch"),
    Quote("For this reason man was brought into the world last after the creation, not being rejected to the last as worthless, but as one whom it behooved to be king over his subjects at his very birth.", "St. Gregory of Nyssa"),
    Quote("O chaste and holy love! O sweet and gracious affection! O pure and cleansed purpose, thoroughly washed and purged from any admixture of selfishness, and sweetened by contact with the divine will!", "St. Bernard of Clairvaux"),
    Quote("The highest perfection, therefore, of man in this life lies in this: that he is so united to God that his soul with all its powers and faculties becomes recollected in Him and is one spirit with Him.", "St. Albert the Great"),
    Quote("Change of place does not effect any drawing nearer unto God, but wherever you may be, God will come to you, if the chambers of your soul be found of such a sort that He can dwell in you and walk in you.", "St. Gregory of Nyssa"),
    Quote("Nay, Lord, Your kingdom come with all speed — the prayer of Christians the confusion of the heathen, the exultation of angels, for the sake of which we suffer, nay, rather, for the sake of which we pray!", "Tertullian"),
    Quote("So it behoves us, if we would have Christ for a frequent guest, to fill our hearts with faithful meditations on the mercy He showed in dying for us, and on His mighty power in rising again from the dead.", "St. Bernard of Clairvaux"),
    Quote("The common belief of all mankind agrees that God, the supreme of all things, is good. For since nothing can be imagined better than God, how can we doubt Him to be good than whom there is nothing better?", "St. Boethius"),
    Quote("A person enters into the first tabernacle when he returns to himself. A person enters into the second tabernacle when he goes beyond himself. When going beyond himself surely a person is elevated to God.", "Richard of St. Victor"),
    Quote("To save our soul, one thing alone is necessary: to hear the word of God and to live by it. Therein lies the best part, which will not be taken away from a faithful soul even though it should lose everything else.", "Fr. Reginald Garrigou-Lagrange, O.P."),
    Quote("Conformity signifies that we join our wills to the will of God. Uniformity means more—it means that we make one will of God's will and ours, so that we will only what God wills; that God's will alone, is our will.", "St. Alphonsus Liguori"),
    Quote("No indeed, my child, the devotion which is true hinders nothing, but on the contrary it perfects everything; and that which runs counter to the rightful vocation of any one is, you may be sure, a spurious devotion.", "St. Francis de Sales"),
    Quote("God, of Thy Goodness, give me Thyself: for Thou art enough to me, and I may nothing ask that is less that may be full worship to Thee; and if I ask anything that is less, ever me wanteth,—but only in Thee I have all.", "Julian of Norwich"),
    Quote("They abstain from the Eucharist and from prayer, because they confess not the Eucharist to be the flesh of our Saviour Jesus Christ, which suffered for our sins, and which the Father, of His goodness, raised up again.", "St. Ignatius of Antioch"),
    Quote("Since beatitude is nothing else than the fruition of the highest good, and the highest good is above us, none can be made blessed unless he ascend above himself, not by the ascent of his body but by that of his heart.", "St. Bonaventure"),
    Quote("The Babe that I carry carries me, says Mary, and He has lowered His wings, and taken and placed me between His pinions, and mounted into the air; and a promise has been given me that height and depth shall be my Son's.", "St. Ephrem the Syrian"),
    Quote("To be saved, we must know, love and serve God. Oh, what a beautiful life! How good, how great a thing it is to know, to love and serve God! We have nothing else to do in this world. All that we do besides is lost time.", "St. John Vianney"),
    Quote("When we say: Your kingdom come, which shall certainly come whether we wish it or not, we do by these words stir up our own desires for that kingdom, that it may come to us, and that we may be found worthy to reign in it.", "St. Augustine of Hippo"),
    Quote("There is one Physician who is possessed both of flesh and spirit; both made and not made; God existing in flesh; true life in death; both of Mary and of God; first passible and then impassible — even Jesus Christ our Lord.", "St. Ignatius of Antioch"),
    Quote("My God, I choose everything, I will not be a Saint by halves, I am not afraid of suffering for Thee, I only fear one thing, and that is to do my own will. Accept the offering of my will, for I choose all that Thou willest.", "St. Thérèse of Lisieux"),
    Quote("This, I say, is the end of all perfection—that the soul may become so purified from every earthly longing, and so raised to spiritual things, that at last the whole life and the desires of the heart form one unbroken prayer.", "St. Albert the Great"),
    Quote("The spouse of Christ cannot be adulterous; she is uncorrupted and pure. She knows one home; she guards with chaste modesty the sanctity of one couch. She keeps us for God. She appoints the sons whom she has born for the kingdom.", "St. Cyprian of Carthage"),
    Quote("For this is the cause why we be not all in ease of heart and soul: that we seek here rest in those things that are so little, wherein is no rest, and know not our God that is All-mighty, All-wise, All-good. For He is the Very Rest.", "Julian of Norwich"),
    Quote("For God is good, or rather is essentially the source of goodness: nor could one that is good be niggardly of anything: whence, grudging existence to none, He has made all things out of nothing by His own Word, Jesus Christ our Lord.", "St. Athanasius"),
    Quote("Meditation is frequent thinking with counsel, which prudently investigates the cause and origin, the mode and utility of each thing. Meditation takes its beginning from reading, yet is constrained by no rules or precepts of reading.", "Hugh of St. Victor"),
    Quote("Turn thee with thy whole heart unto the Lord, and forsake this wretched world, and thy soul shall find rest. Learn to despise outward things and to give thyself to things inward, and thou shalt see the kingdom of God come within thee.", "Thomas à Kempis"),
    Quote("In such wise man, animal and carnal by nature, and loving only himself, begins to love God by reason of that very self-love; since he learns that in God he can accomplish all things that are good, and that without God he can do nothing.", "St. Bernard of Clairvaux"),
    Quote("Behold, assiduous meditation on My Passion makes out of a simple man a master of high knowledge; truly it is a living book in which everything is to be found. How right blessed is that man who has it ever before his eyes and studies it!", "Bl. Henry Suso"),
    Quote("As water extinguishes fire, so almsgiving quenches sin. Here also it is shown and proved, that as in the layer of saving water the fire of Gehenna is extinguished, so by almsgiving and works of righteousness the flame of sins is subdued.", "St. Cyprian of Carthage"),
    Quote("All our perfection consists in being conformed, united, and consecrated to Jesus Christ; and therefore the most perfect of all devotions is, without any doubt, that which most perfectly conforms, unites and consecrates us to Jesus Christ.", "St. Louis de Montfort"),
    Quote("In studying nature we have not to inquire how God the Creator may, as He freely wills, use His creatures to work miracles and thereby show forth His power: we have rather to inquire what Nature with its immanent causes can naturally bring to pass.", "St. Albert the Great"),
    Quote("The interior life of a just man who tends toward God and who already lives by Him is indeed the one thing necessary. To be a saint, neither intellectual culture nor great exterior activity is a requisite; it suffices that we live profoundly by God.", "Fr. Reginald Garrigou-Lagrange, O.P.")
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
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "“${quote.text}”\n\n— ${quote.author}\n\nShared from Daily Motivation"
                                        )
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
                                } else {
                                    val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "“${quote.text}”\n\n— ${quote.author}\n\nDaily Motivation"
                                        )
                                    }
                                    context.startActivity(
                                        Intent.createChooser(fallbackIntent, "Share Motivation")
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
