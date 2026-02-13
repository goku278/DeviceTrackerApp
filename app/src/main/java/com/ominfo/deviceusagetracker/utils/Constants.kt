import android.util.Log
import com.ominfo.deviceusagetracker.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Constants {

    private const val TAG = "Constants"

    // YouTube package names (comprehensive list)
    val YOUTUBE_PACKAGES = listOf(
        "com.google.android.youtube",           // Main YouTube app
        "com.google.android.youtube.tv",        // YouTube for Android TV
        "com.google.android.apps.youtube.music", // YouTube Music
        "com.google.android.youtube.googletv",   // YouTube for Google TV
        "com.google.android.youtube.kids",       // YouTube Kids
        "com.google.android.apps.youtube.creator", // YouTube Studio
        "com.google.android.youtube.lite",       // YouTube Go
        "com.vanced.android.youtube",            // YouTube Vanced
        "app.revanced.android.youtube",          // ReVanced
        "com.teamsmart.videomanager.youtube",    // NewPipe
        "org.schabi.newpipe",                    // NewPipe alternative
        "com.github.libretube",                   // LibreTube
        "com.kiwigrid.wingman.cloud",            // Some devices
        "com.google.android.gms"                  // Some YouTube services
    )

    // Social Media Apps
    val SOCIAL = listOf(
        "com.instagram.android",
        "com.facebook.katana",
        "com.facebook.orca", // Facebook Messenger
        "com.facebook.lite", // Facebook Lite
        "com.snapchat.android",
        "com.whatsapp",
        "com.whatsapp.w4b",  // WhatsApp Business
        "com.twitter.android",
        "com.google.android.apps.plus",
        "com.linkedin.android",
        "com.pinterest",
        "com.tumblr",
        "com.reddit.frontpage",
        "com.reddit.redditfun",
        "com.andrewshu.android.reddit", // Reddit is Fun
        "com.instagram.lite", // Instagram Lite
        "com.threads_app" // Threads
    )

    // Entertainment Apps
    val ENTERTAINMENT = YOUTUBE_PACKAGES + listOf(
        "com.netflix.mediaclient",
        "com.netflix.mediaclient.tv", // Netflix for TV
        "in.startv.hotstar",
        "com.amazon.avod.thirdpartyclient",
        "com.prime.video",
        "com.amazon.firetv.youtube",
        "com.spotify.music",
        "com.zhiliaoapp.musically", // TikTok
        "com.google.android.videos", // Google Play Movies
        "com.hulu.plus",
        "com.disney.disneyplus",
        "com.crunchyroid.crunchyroll",
        "com.twitch.tv",
        "com.mxtech.videoplayer.ad",
        "org.videolan.vlc",
        "com.imdb.mobile",
        "com.plexapp.android",
        "com.plexapp.mediaserver",
        "com.audible.application",
        "com.amazon.mp3", // Amazon Music
        "com.apple.android.music", // Apple Music
        "com.gaana", // Gaana
        "com.jio.media.jiobeats", // JioSaavn
        "com.spotify.lite" // Spotify Lite
    )

    // Education Apps
    val EDUCATION = listOf(
        "com.google.android.apps.classroom",
        "org.khanacademy.android",
        "com.duolingo",
        "com.quizlet.quizletandroid",
        "com.coursera.android",
        "com.udemy.android",
        "com.edx.mobile",
        "com.sololearn",
        "com.byjus.thelearningapp",
        "com.vedantu.courses",
        "com.udacity.android",
        "com.pluralsight",
        "com.linkedin.learning",
        "com.byjus.jee",
        "com.byjus.neet",
        "com.byjus.k3",
        "com.byjus.k4",
        "com.byjus.k5"
    )

    val ALL_CATEGORIES = listOf("Social", "Entertainment", "Education", "Others")

    // Category icons
    val CATEGORY_ICONS = mapOf(
        "Social" to R.drawable.ic_social,
        "Entertainment" to R.drawable.ic_entertainment,
        "Education" to R.drawable.ic_education,
        "Others" to R.drawable.ic_others
    )

    fun getCategory(pkg: String): String {
        // First check exact matches
        when {
            SOCIAL.any { it == pkg } -> {
                Log.d(TAG, "Exact match Social: $pkg")
                return "Social"
            }
            ENTERTAINMENT.any { it == pkg } -> {
                Log.d(TAG, "Exact match Entertainment: $pkg")
                return "Entertainment"
            }
            EDUCATION.any { it == pkg } -> {
                Log.d(TAG, "Exact match Education: $pkg")
                return "Education"
            }
        }

        // Check for partial matches (case insensitive)
        val lowerPkg = pkg.lowercase()

        // YouTube check (most specific first)
        if (lowerPkg.contains("youtube") ||
            lowerPkg.contains("youtu.be") ||
            lowerPkg.contains("yt") ||
            lowerPkg.contains("vanced") ||
            lowerPkg.contains("revanced") ||
            lowerPkg.contains("newpipe") ||
            lowerPkg.contains("libretube")) {
            Log.d(TAG, "Partial match YouTube: $pkg -> Entertainment")
            return "Entertainment"
        }

        // Other entertainment partial matches
        if (lowerPkg.contains("netflix") ||
            lowerPkg.contains("prime") ||
            lowerPkg.contains("hotstar") ||
            lowerPkg.contains("spotify") ||
            lowerPkg.contains("tiktok") ||
            lowerPkg.contains("twitch") ||
            lowerPkg.contains("disney") ||
            lowerPkg.contains("hulu") ||
            lowerPkg.contains("crunchyroll") ||
            lowerPkg.contains("vlc") ||
            lowerPkg.contains("mxplayer") ||
            lowerPkg.contains("gaana") ||
            lowerPkg.contains("jiosaavn") ||
            lowerPkg.contains("apple.music") ||
            lowerPkg.contains("amazon.mp3") ||
            lowerPkg.contains("plex") ||
            lowerPkg.contains("imdb") ||
            lowerPkg.contains("movie") ||
            lowerPkg.contains("video") ||
            lowerPkg.contains("music") ||
            lowerPkg.contains("stream")) {
            Log.d(TAG, "Partial match Entertainment: $pkg")
            return "Entertainment"
        }

        // Social media partial matches
        if (lowerPkg.contains("facebook") ||
            lowerPkg.contains("instagram") ||
            lowerPkg.contains("whatsapp") ||
            lowerPkg.contains("snapchat") ||
            lowerPkg.contains("twitter") ||
            lowerPkg.contains("tumblr") ||
            lowerPkg.contains("reddit") ||
            lowerPkg.contains("linkedin") ||
            lowerPkg.contains("pinterest") ||
            lowerPkg.contains("threads") ||
            lowerPkg.contains("discord") ||
            lowerPkg.contains("telegram") ||
            lowerPkg.contains("signal") ||
            lowerPkg.contains("wechat") ||
            lowerPkg.contains("line") ||
            lowerPkg.contains("viber") ||
            lowerPkg.contains("skype") ||
            lowerPkg.contains("zoom") ||
            lowerPkg.contains("meet") ||
            lowerPkg.contains("teams")) {
            Log.d(TAG, "Partial match Social: $pkg")
            return "Social"
        }

        // Education partial matches
        if (lowerPkg.contains("classroom") ||
            lowerPkg.contains("khan") ||
            lowerPkg.contains("duolingo") ||
            lowerPkg.contains("quizlet") ||
            lowerPkg.contains("coursera") ||
            lowerPkg.contains("udemy") ||
            lowerPkg.contains("edx") ||
            lowerPkg.contains("sololearn") ||
            lowerPkg.contains("byjus") ||
            lowerPkg.contains("vedantu") ||
            lowerPkg.contains("udacity") ||
            lowerPkg.contains("pluralsight") ||
            lowerPkg.contains("school") ||
            lowerPkg.contains("learn") ||
            lowerPkg.contains("edu") ||
            lowerPkg.contains("course") ||
            lowerPkg.contains("academy")) {
            Log.d(TAG, "Partial match Education: $pkg")
            return "Education"
        }

        Log.d(TAG, "No match - Others: $pkg")
        return "Others"
    }

    fun today(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    fun isNewDay(lastDate: String): Boolean {
        return today() != lastDate
    }
}