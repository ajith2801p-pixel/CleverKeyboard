package com.cleverkeyboard.app.gif

data class GifItem(
    val id: String,
    val title: String,
    val gifUrl: String,
    val previewUrl: String,
    val tags: List<String>
)

object GifManager {
    val CATEGORIES = listOf(
        "🔥 Trending",
        "😂 Laugh",
        "❤️ Love",
        "🎉 Party",
        "👏 Applause",
        "👍 Yes",
        "💃 Dance",
        "😢 Sad",
        "😮 OMG",
        "🤯 Mind Blown",
        "😴 Sleep",
        "🙏 Thanks",
        "☕ Coffee / Tea",
        "🔥 Fire"
    )

    private val CURATED_GIFS = listOf(
        // Trending / Laugh
        GifItem("1", "Laughing Out Loud", "https://media.giphy.com/media/26n6Gx9moCgs1qx9K/giphy.gif", "https://media.giphy.com/media/26n6Gx9moCgs1qx9K/200w.gif", listOf("laugh", "lol", "funny", "haha", "trending")),
        GifItem("2", "Minion Laugh", "https://media.giphy.com/media/MOWPkhJx7Mzx6/giphy.gif", "https://media.giphy.com/media/MOWPkhJx7Mzx6/200w.gif", listOf("minion", "laugh", "happy", "trending")),
        GifItem("3", "Cat Vibing", "https://media.giphy.com/media/jpbnoe3UIa8TU8LM13/giphy.gif", "https://media.giphy.com/media/jpbnoe3UIa8TU8LM13/200w.gif", listOf("cat", "vibe", "music", "dance", "trending")),
        GifItem("4", "Leo Clapping", "https://media.giphy.com/media/kd9BlRovbPOykLBMqX/giphy.gif", "https://media.giphy.com/media/kd9BlRovbPOykLBMqX/200w.gif", listOf("pointing", "leo", "trending", "yes")),
        
        // Love / Hearts
        GifItem("5", "Heart Explosion", "https://media.giphy.com/media/26BRv0ThflsHCqDrG/giphy.gif", "https://media.giphy.com/media/26BRv0ThflsHCqDrG/200w.gif", listOf("love", "heart", "cute", "kiss")),
        GifItem("6", "Cute Bear Hug", "https://media.giphy.com/media/KL7xA3fLx7bna/giphy.gif", "https://media.giphy.com/media/KL7xA3fLx7bna/200w.gif", listOf("love", "hug", "bear", "cute", "romance")),
        GifItem("7", "Baby Sending Kiss", "https://media.giphy.com/media/l41lI4bYmcsPJX9Go/giphy.gif", "https://media.giphy.com/media/l41lI4bYmcsPJX9Go/200w.gif", listOf("kiss", "love", "baby", "sweet")),

        // Party / Celebration
        GifItem("8", "Confetti Celebration", "https://media.giphy.com/media/g9582DNuQppxC/giphy.gif", "https://media.giphy.com/media/g9582DNuQppxC/200w.gif", listOf("party", "celebrate", "cheers", "gatsby")),
        GifItem("9", "Happy Dance", "https://media.giphy.com/media/artj92V8o75VPL7AeQ/giphy.gif", "https://media.giphy.com/media/artj92V8o75VPL7AeQ/200w.gif", listOf("party", "dance", "happy", "excited")),
        GifItem("10", "Carlton Dance", "https://media.giphy.com/media/pa37AAGzKXoek/giphy.gif", "https://media.giphy.com/media/pa37AAGzKXoek/200w.gif", listOf("dance", "carlton", "funny", "party")),

        // Applause / Thumbs Up
        GifItem("11", "Office Clapping", "https://media.giphy.com/media/nbvFVPiEiJH6JOGIok/giphy.gif", "https://media.giphy.com/media/nbvFVPiEiJH6JOGIok/200w.gif", listOf("applause", "clap", "bravo", "yes")),
        GifItem("12", "Obama Thumbs Up", "https://media.giphy.com/media/3o7abKhOpu0NwenH3O/giphy.gif", "https://media.giphy.com/media/3o7abKhOpu0NwenH3O/200w.gif", listOf("thumbsup", "yes", "ok", "good")),
        GifItem("13", "Nodding Yes", "https://media.giphy.com/media/10Jpr9KSaXLchW/giphy.gif", "https://media.giphy.com/media/10Jpr9KSaXLchW/200w.gif", listOf("yes", "agree", "nod", "thumbsup")),

        // Mind Blown / Shocked
        GifItem("14", "Mind Blown Galaxy", "https://media.giphy.com/media/26ufdipQqU2lhNA4g/giphy.gif", "https://media.giphy.com/media/26ufdipQqU2lhNA4g/200w.gif", listOf("mindblown", "shocked", "omg", "wow")),
        GifItem("15", "Dramatic Chipmunk", "https://media.giphy.com/media/kKdgdegbqyYZa/giphy.gif", "https://media.giphy.com/media/kKdgdegbqyYZa/200w.gif", listOf("omg", "shocked", "drama", "look")),
        GifItem("16", "Steve Carell No", "https://media.giphy.com/media/12XMGIWtrHBl5e/giphy.gif", "https://media.giphy.com/media/12XMGIWtrHBl5e/200w.gif", listOf("no", "stop", "never", "hate")),

        // Sad / Crying
        GifItem("17", "Crying River", "https://media.giphy.com/media/L95W4wvxxB1G8/giphy.gif", "https://media.giphy.com/media/L95W4wvxxB1G8/200w.gif", listOf("sad", "cry", "tears", "emotional")),
        GifItem("18", "Puppy Eyes", "https://media.giphy.com/media/OPU6wzx8JrHna/giphy.gif", "https://media.giphy.com/media/OPU6wzx8JrHna/200w.gif", listOf("sad", "please", "cry", "puppy")),

        // Thanks / Morning / Food
        GifItem("19", "Thank You So Much", "https://media.giphy.com/media/BYoRqTmcgzHcL9TCy1/giphy.gif", "https://media.giphy.com/media/BYoRqTmcgzHcL9TCy1/200w.gif", listOf("thanks", "thank you", "grateful", "namaste")),
        GifItem("20", "Coffee Morning", "https://media.giphy.com/media/hPTZgtzfRIB5Nfb5rL/giphy.gif", "https://media.giphy.com/media/hPTZgtzfRIB5Nfb5rL/200w.gif", listOf("coffee", "tea", "morning", "drink")),
        GifItem("21", "Popcorn Eating", "https://media.giphy.com/media/gl0mkIZOW6Nwc/giphy.gif", "https://media.giphy.com/media/gl0mkIZOW6Nwc/200w.gif", listOf("popcorn", "eating", "drama", "watching")),
        GifItem("22", "Sleeping Snooze", "https://media.giphy.com/media/mguPrVJAnEHIY/giphy.gif", "https://media.giphy.com/media/mguPrVJAnEHIY/200w.gif", listOf("sleep", "tired", "bed", "goodnight"))
    )

    fun getGifsForCategory(category: String): List<GifItem> {
        val catClean = category.replace(Regex("[^a-zA-Z]"), "").lowercase().trim()
        if (catClean.contains("trending") || catClean.isBlank()) {
            return CURATED_GIFS
        }
        val matches = CURATED_GIFS.filter { gif ->
            gif.tags.any { tag -> tag.contains(catClean) || catClean.contains(tag) }
        }
        return if (matches.isNotEmpty()) matches else CURATED_GIFS
    }

    fun searchGifs(query: String): List<GifItem> {
        val q = query.lowercase().trim()
        if (q.isBlank()) return CURATED_GIFS

        val tokens = q.split(" ", "_", "-").filter { it.isNotBlank() }
        val results = CURATED_GIFS.filter { gif ->
            tokens.any { token ->
                gif.title.lowercase().contains(token) ||
                gif.tags.any { it.contains(token) }
            }
        }
        return if (results.isNotEmpty()) results else CURATED_GIFS.take(8)
    }
}
