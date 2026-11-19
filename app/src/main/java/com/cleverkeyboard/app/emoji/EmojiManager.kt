package com.cleverkeyboard.app.emoji

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray

object EmojiManager {
    private const val PREFS_NAME = "clever_emoji_prefs"
    private const val KEY_RECENT_EMOJIS = "recent_emojis_json"
    private const val MAX_RECENT = 24

    val CATEGORIES = listOf(
        "🕒 Recent",
        "😀 Smileys",
        "🧑 People",
        "🐶 Animals",
        "🍔 Food",
        "✈️ Travel",
        "⚽ Activities",
        "💡 Objects",
        "🔣 Symbols",
        "🏁 Flags"
    )

    private val SMILEYS = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃",
        "😉", "😊", "😇", "🥰", "😍", "🤩", "😘", "😗", "😚", "😙",
        "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗", "🤭", "🤫", "🤔",
        "🤐", "🤨", "😐", "😑", "😶", "😏", "😒", "🙄", "😬", "🤥",
        "😌", "😔", "😪", "🤤", "😴", "😷", "🤒", "🤕", "🤢", "🤮",
        "🤧", "🥵", "🥶", "🥴", "😵", "🤯", "🤠", "🥳", "🥸", "😎",
        "🤓", "🧐", "😕", "😟", "🙁", "😮", "😯", "😲", "😳", "🥺",
        "😦", "😧", "😨", "😰", "😥", "😢", "😭", "😱", "😖", "😣",
        "😞", "😓", "😩", "😫", "🥱", "😤", "😡", "😠", "🤬", "😈",
        "👿", "💀", "☠️", "💩", "🤡", "👹", "👺", "👻", "👽", "👾",
        "🤖", "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾"
    )

    private val PEOPLE = listOf(
        "👋", "🤚", "🖐", "✋", "🖖", "👌", "🤌", "🤏", "✌️", "🤞",
        "🤟", "🤘", "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "👍",
        "👎", "✊", "👊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝",
        "🙏", "✍️", "💅", "🤳", "💪", "🦾", "🦿", "🦵", "🦶", "👂",
        "🦻", "👃", "🫀", "🫁", "🧠", "👀", "👁", "👅", "👄", "👶",
        "🧒", "👦", "👧", "🧑", "👱", "👨", "🧔", "👨‍🦰", "👨‍🦱", "👨‍🦳",
        "👨‍🦲", "👩", "👩‍🦰", "🧑‍🦰", "👩‍🦱", "🧑‍🦱", "👩‍🦳", "🧑‍🦳", "👩‍🦲", "🧑‍🦲",
        "👱‍♀️", "👱‍♂️", "🧓", "👴", "👵", "🙍", "🙎", "🙅", "🙆", "💁",
        "🙋", "🧏", "🙇", "🤦", "🤷", "👮", "🕵️", "💂", "🥷", "👷",
        "🤴", "👸", "👳", "👲", "🧕", "🤵", "👰", "🤰", "🤱", "👼"
    )

    private val ANIMALS = listOf(
        "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯",
        "🦁", "🐮", "🐷", "🐽", "🐸", "🐵", "🙈", "🙉", "🙊", "🐒",
        "🐔", "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉", "🦇",
        "🐺", "🐗", "🐴", "🦄", "🐝", "🪱", "🐛", "🦋", "🐌", "🐞",
        "🐜", "🪰", "🪲", "🪳", "🦟", "🦗", "🕷", "🕸", "🦂", "🐢",
        "🐍", "🦎", "🦖", "🦕", "🐙", "🦑", "🦐", "🦞", "🦀", "🐡",
        "🐠", "🐟", "🐬", "🐳", "🐋", "🦈", "🐊", "🐅", "🐆", "🦓",
        "🦍", "🦧", "🦣", "🐘", "🦛", "🦏", "🐪", "🐫", "🦒", "🦘",
        "🐃", "🐂", "🐄", "🐎", "🐖", "🐏", "🐑", "🦙", "🐐", "🦌",
        "🐕", "🐩", "🦮", "🐕‍🦺", "🐈", "🐈‍⬛", "🐓", "🦃", "🦚", "🦜",
        "🦢", "🦩", "🕊", "🐇", "🦝", "🦨", "🦡", "🦦", "🦥", "🐁"
    )

    private val FOOD = listOf(
        "🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐",
        "🍈", "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑",
        "🥦", "🥬", "🥒", "🌶", "🫑", "🌽", "🥕", "🫒", "🧄", "🧅",
        "🥔", "🍠", "🥐", "🥯", "🍞", "🥖", "🥨", "🧀", "🥚", "🍳",
        "🥞", "🧇", "🥓", "🥩", "🍗", "🍖", "🌭", "🍔", "🍟", "🍕",
        "🥪", "🥙", "🧆", "🌮", "🌯", "🥗", "🥘", "🍝", "🍜", "🍲",
        "🍛", "🍣", "🍱", "🥟", "🍤", "🍙", "🍚", "🍘", "🍦", "🍧",
        "🍨", "🍩", "🍪", "🎂", "🍰", "🧁", "🥧", "🍫", "🍬", "🍭",
        "🍮", "🍯", "🍼", "🥛", "☕️", "🫖", "🍵", "🧃", "🥤", "🧋",
        "🍶", "🍺", "🍻", "🥂", "🍷", "🥃", "🍸", "🍹", "🍾", "🍿"
    )

    private val TRAVEL = listOf(
        "🚗", "🚕", "🚙", "🚌", "🚎", "🏎", "🚓", "🚑", "🚒", "🚐",
        "🛻", "🚚", "🚛", "🚜", "🛴", "🚲", "🛵", "🏍", "🛺", "🚨",
        "🚍", "🚘", "🚖", "🚃", "🚋", "🚞", "🚝", "🚄", "🚅", "🚈",
        "🚂", "🚆", "🚇", "🚊", "🚉", "✈️", "🛫", "🛬", "🛩", "🚀",
        "🛸", "🚁", "🛶", "⛵️", "🚤", "🛥", "🛳", "⛴", "🚢", "⚓️",
        "⛽️", "🚧", "🚦", "🚥", "🗺", "🗿", "🗽", "🗼", "🏰", "🏯",
        "🏟", "🎡", "🎢", "🏖", "🏝", "🏜", "🌋", "⛰", "🏔", "🏕",
        "⛺️", "🛖", "🏠", "🏡", "🏢", "🏣", "🏥", "🏦", "🏨", "🏪",
        "🏫", "🏬", "🏭", "🏯", "🏰", "💒", "🗼", "🗽", "⛪️", "🕌"
    )

    private val ACTIVITIES = listOf(
        "⚽️", "🏀", "🏈", "⚾️", "🥎", "🎾", "🏐", "🏉", "🥏", "🎱",
        "🪀", "🏓", "🏸", "🏒", "🏑", "🥍", "🏏", "🪃", "🥅", "⛳️",
        "🪁", "🏹", "🎣", "🤿", "🥊", "🥋", "🎽", "🛹", "🛼", "⛸",
        "🎿", "⛷", "🏂", "🪂", "🏋️", "🤼", "🤸", "⛹️", "🤺", "🤾",
        "🏌️", "🏇", "🧘", "🏄", "🏊", "🤽", "🚣", "🧗", "🚵", "🚴",
        "🏆", "🥇", "🥈", "🥉", "🏅", "🎖", "🎫", "🎟", "🎪", "🤹",
        "🎭", "🎨", "🎬", "🎤", "🎧", "🎼", "🎹", "🥁", "🎷", "🎺",
        "🎸", "🎻", "🎲", "♟", "🎯", "🎳", "🎮", "🎰", "🧩", "🃏",
        "🪄", "🪆", "🪅", "🔮", "🧿", "🪡", "🧶", "🧵", "🎨", "🪕"
    )

    private val OBJECTS = listOf(
        "⌚️", "📱", "📲", "💻", "⌨️", "🖥", "🖨", "🖱", "🕹", "💾",
        "💿", "📀", "📷", "📸", "📹", "🎥", "📽", "📞", "☎️", "📟",
        "📺", "📻", "🎙", "⏱", "⏲", "⏰", "🕰", "⌛️", "⏳", "📡",
        "🔋", "🔌", "💡", "🔦", "🕯", "💸", "💵", "💴", "💶", "💷",
        "💰", "💳", "💎", "⚖️", "🪜", "🧰", "🔧", "🔨", "⛏", "⚙️",
        "🧱", "⛓", "🧲", "🔫", "💣", "🔪", "🗡", "⚔️", "🛡", "🔮",
        "📿", "🧿", "💊", "💉", "🩸", "🧬", "🧪", "🌡", "🧹", "🧺",
        "🧻", "🧼", "🪥", "🪒", "🔑", "🗝", "🚪", "🪑", "🛋", "🛏",
        "📦", "🏷", "✉️", "📩", "📨", "📧", "💌", "📮", "📜", "📄",
        "📊", "📈", "📉", "📅", "🗑", "📋", "📁", "📂", "📖", "📚"
    )

    private val SYMBOLS = listOf(
        "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
        "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "☮️",
        "✝️", "☪️", "🕉", "☸️", "✡️", "🔯", "🕎", "☯️", "☦️", "🛐",
        "♈️", "♉️", "♊️", "♋️", "♌️", "♍️", "♎️", "♏️", "♐️", "♑️",
        "♒️", "♓️", "🆔", "⚛️", "✴️", "🆚", "💮", "🉐", "㊙️", "㊗️",
        "🅰️", "🅱️", "🆎", "🅾️", "🆘", "❌", "⭕️", "🛑", "⛔️", "📛",
        "🚫", "💯", "💢", "♨️", "❗️", "❕", "❓", "❔", "‼️", "⁉️",
        "⚠️", "🔱", "⚜️", "🔰", "♻️", "✅", "🌐", "💠", "💤", "🏧",
        "0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣",
        "🔟", "🔢", "#️⃣", "*️⃣", "▶️", "⏸", "⏯", "⏹", "⏭", "⏮",
        "⏩", "⏪", "⏫", "⏬", "◀️", "🔼", "🔽", "➡️", "⬅️", "⬆️",
        "⬇️", "↗️", "↘️", "↙️", "↖️", "↕️", "↔️", "↪️", "↩️", "⤴️",
        "⤵️", "🔀", "🔁", "🔂", "🔄", "🔃", "🎵", "🎶", "➕", "➖",
        "➗", "✖️", "♾", "💲", "✔️", "☑️", "🔘", "🔴", "🟠", "🟡",
        "🟢", "🔵", "🟣", "⚫️", "⚪️", "🟤", "🔺", "🔻", "💬", "💭"
    )

    private val FLAGS = listOf(
        "🏁", "🚩", "🎌", "🏴", "🏳️", "🏳️‍🌈", "🏳️‍⚧️", "🏴‍☠️",
        "🇮🇳", "🇺🇸", "🇬🇧", "🇨🇦", "🇦🇺", "🇩🇪", "🇫🇷", "🇯🇵", "🇰🇷", "🇧🇷",
        "🇮🇹", "🇪🇸", "🇷🇺", "🇨🇳", "🇲🇽", "🇮🇩", "🇸🇦", "🇦🇪", "🇿🇦", "🇳🇬",
        "🇪🇬", "🇵🇰", "🇧🇩", "🇱🇰", "🇳🇵", "🇸🇬", "🇲🇾", "🇹🇭", "🇻🇳", "🇵🇭",
        "🇳🇿", "🇮🇪", "🇨🇭", "🇸🇪", "🇳🇴", "🇩🇰", "🇫🇮", "🇳🇱", "🇧🇪", "🇵🇹",
        "🇬🇷", "🇹🇷", "🇮🇱", "🇺🇦", "🇵🇱", "🇦🇹", "🇨🇿", "🇭🇺", "🇷🇴", "🇧🇬",
        "🇭🇷", "🇷🇸", "🇸🇰", "🇸🇮", "🇪🇪", "🇱🇻", "🇱🇹", "🇮🇸", "🇱🇺", "🇲🇹",
        "🇨🇾", "🇦🇱", "🇲🇰", "🇲🇪", "🇧🇦", "🇽🇰", "🇲🇩", "🇬🇪", "🇦🇲", "🇦🇿",
        "🇰🇿", "🇺🇿", "🇹🇲", "🇰🇬", "🇹🇯", "🇦🇫", "🇮🇷", "🇮🇶", "🇸🇾", "🇱🇧",
        "🇯🇴", "🇵🇸", "🇰🇼", "🇶🇦", "🇧🇭", "🇴🇲", "🇾🇪", "🇲🇳", "🇰🇵", "🇲🇲"
    )

    private val ALL_EMOJIS by lazy {
        (SMILEYS + PEOPLE + ANIMALS + FOOD + TRAVEL + ACTIVITIES + OBJECTS + SYMBOLS + FLAGS).distinct()
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getEmojisForCategory(context: Context, categoryIndex: Int): List<String> {
        return when (categoryIndex) {
            0 -> getRecentEmojis(context)
            1 -> SMILEYS
            2 -> PEOPLE
            3 -> ANIMALS
            4 -> FOOD
            5 -> TRAVEL
            6 -> ACTIVITIES
            7 -> OBJECTS
            8 -> SYMBOLS
            9 -> FLAGS
            else -> SMILEYS
        }
    }

    fun getRecentEmojis(context: Context): List<String> {
        val raw = getPrefs(context).getString(KEY_RECENT_EMOJIS, null)
        val list = mutableListOf<String>()
        if (!raw.isNullOrBlank()) {
            try {
                val array = JSONArray(raw)
                for (i in 0 until array.length()) {
                    list.add(array.getString(i))
                }
            } catch (_: Exception) {}
        }
        if (list.isEmpty()) {
            return listOf("😀", "😂", "❤️", "🔥", "👍", "🙏", "😍", "✨", "🎉", "💯", "🙌", "😊", "🥺", "😎", "🥳", "🍕")
        }
        return list
    }

    fun addRecentEmoji(context: Context, emoji: String) {
        val current = getRecentEmojis(context).toMutableList()
        current.remove(emoji)
        current.add(0, emoji)
        val trimmed = if (current.size > MAX_RECENT) current.take(MAX_RECENT) else current

        val array = JSONArray()
        for (e in trimmed) {
            array.put(e)
        }
        getPrefs(context).edit().putString(KEY_RECENT_EMOJIS, array.toString()).apply()
    }

    val SKIN_TONES = listOf(
        "",             // 0: Default yellow
        "\uD83C\uDFFB", // 1: Light
        "\uD83C\uDFFC", // 2: Medium-Light
        "\uD83C\uDFFD", // 3: Medium
        "\uD83C\uDFFE", // 4: Medium-Dark
        "\uD83C\uDFFF"  // 5: Dark
    )

    fun applySkinTone(emoji: String, toneIndex: Int): String {
        var clean = emoji
        for (st in SKIN_TONES) {
            if (st.isNotEmpty()) clean = clean.replace(st, "")
        }
        if (toneIndex <= 0 || toneIndex >= SKIN_TONES.size) return clean
        return clean + SKIN_TONES[toneIndex]
    }

    private val KEYWORD_MAP: Map<String, List<String>> = mapOf(
        "happy" to listOf("😀", "😃", "😄", "😁", "😆", "😊", "😇", "🥳", "🥰", "😍", "✨"),
        "smile" to listOf("😀", "😃", "😄", "😁", "😊", "🙂", "😉", "😇"),
        "love" to listOf("❤️", "💕", "😍", "🥰", "😘", "💖", "💗", "💓", "💞", "💘", "💌", "❣️", "🌹"),
        "heart" to listOf("❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔", "❣️", "💕", "💞"),
        "laugh" to listOf("😂", "🤣", "😆", "😅", "😹"),
        "lol" to listOf("😂", "🤣", "😆"),
        "haha" to listOf("😂", "🤣", "😆"),
        "sad" to listOf("😢", "😭", "🥺", "😞", "😔", "😿", "💔", "🙁", "☹️", "😓"),
        "cry" to listOf("😭", "😢", "🥺", "😿", "💧", "🌧️"),
        "fire" to listOf("🔥", "🧨", "💥", "🚒", "🌶️"),
        "hot" to listOf("🔥", "🥵", "🌶️", "☀️"),
        "dog" to listOf("🐶", "🐕", "🦮", "🐩", "🐕‍🦺", "🐾"),
        "cat" to listOf("🐱", "🐈", "🐈‍⬛", "😸", "😹", "😻", "😽"),
        "food" to listOf("🍕", "🍔", "🍟", "🌭", "🥪", "🌮", "🌯", "🥗", "🥘", "🍝", "🍜", "🍲", "🍛", "🍣"),
        "pizza" to listOf("🍕"),
        "burger" to listOf("🍔"),
        "party" to listOf("🥳", "🎉", "🎊", "🍾", "🍻", "🥂", "🎈", "🎂"),
        "car" to listOf("🚗", "🚕", "🚙", "🚌", "🏎️", "🚓", "🚐", "🛻"),
        "bike" to listOf("🚲", "🛵", "🏍️", "🛴"),
        "money" to listOf("💰", "💵", "💸", "💳", "🤑", "🪙", "💎"),
        "cash" to listOf("💵", "💸", "💰"),
        "sleep" to listOf("😴", "🥱", "💤", "🛌", "🌙", "✨"),
        "tired" to listOf("🥱", "😴", "😫", "😩"),
        "angry" to listOf("😡", "😠", "🤬", "👿", "😤", "💢"),
        "mad" to listOf("😡", "😠", "🤬"),
        "cool" to listOf("😎", "🕶️", "🤙", "🔥", "✨"),
        "star" to listOf("⭐", "🌟", "✨", "💫", "🌠"),
        "music" to listOf("🎵", "🎶", "🎧", "🎤", "🎼", "🎹", "🎸", "🥁"),
        "game" to listOf("🎮", "🕹️", "🎲", "♟️", "🎯", "👾"),
        "check" to listOf("✅", "✔️", "☑️", "👍"),
        "done" to listOf("✅", "✔️", "👍", "💯"),
        "yes" to listOf("👍", "✅", "👌", "🙌"),
        "no" to listOf("👎", "❌", "🙅", "🛑"),
        "cross" to listOf("❌", "✖️", "🛑", "⛔"),
        "tea" to listOf("🫖", "🍵", "☕"),
        "coffee" to listOf("☕", "🧋", "🥤"),
        "drink" to listOf("🍺", "🍻", "🍷", "🍸", "🍹", "🍾", "🥤", "🧃"),
        "beer" to listOf("🍺", "🍻"),
        "cake" to listOf("🎂", "🍰", "🧁"),
        "birthday" to listOf("🎂", "🎉", "🥳", "🎈", "🎁"),
        "flag" to listOf("🏁", "🚩", "🇮🇳", "🇺🇸", "🇬🇧", "🇨🇦", "🇦🇺"),
        "india" to listOf("🇮🇳", "🪷", "🕉️", "🏏", "🛺"),
        "water" to listOf("💧", "🌊", "💦", "🌧️", "🏊"),
        "sun" to listOf("☀️", "🌞", "🌅", "🌄"),
        "flower" to listOf("🌸", "🌹", "🌺", "🌻", "💐", "🌷", "🌼"),
        "rose" to listOf("🌹"),
        "pray" to listOf("🙏", "📿", "🛐", "✨"),
        "thanks" to listOf("🙏", "🙌", "✨", "❤️"),
        "ok" to listOf("👌", "👍", "✅", "🆗"),
        "thumbs" to listOf("👍", "👎"),
        "baby" to listOf("👶", "🍼", "🧸"),
        "work" to listOf("💼", "💻", "🖥️", "📁", "📊"),
        "phone" to listOf("📱", "📲", "📞", "☎️"),
        "laptop" to listOf("💻", "🖥️"),
        "book" to listOf("📖", "📚", "📕"),
        "time" to listOf("⏰", "⏱️", "⌛", "⌚")
    )

    fun searchEmojis(query: String): List<String> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return SMILEYS

        val results = mutableListOf<String>()

        // 1. Direct keyword match
        KEYWORD_MAP[q]?.let { results.addAll(it) }

        // 2. Prefix keyword match
        for ((k, list) in KEYWORD_MAP) {
            if (k != q && (k.startsWith(q) || q.startsWith(k))) {
                for (em in list) {
                    if (!results.contains(em)) results.add(em)
                }
            }
        }

        // 3. Fallback to broad emoji keywords filter
        val matches = ALL_EMOJIS.filter { emoji ->
            getEmojiKeyword(emoji).contains(q)
        }
        for (em in matches) {
            if (!results.contains(em)) results.add(em)
        }

        return if (results.isNotEmpty()) results else emptyList()
    }

    private fun getEmojiKeyword(emoji: String): String {
        return when (emoji) {
            "🔥" -> "fire lit flame hot burn heat trend popular"
            "🚒", "👨‍🚒" -> "fire truck firefighter fire rescue"
            "🧨", "💥" -> "fire cracker explosion burst blast"
            "🌶" -> "chili pepper hot spicy fire"
            "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
            "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟" -> "love heart romance affection red sweet"
            "😍", "🥰", "😘", "😗", "😚", "😙" -> "love kiss heart eyes affection cute romantic crush"
            "😀", "😃", "😄", "😁", "😆", "😊", "😇" -> "smile happy grin joy pleased positive"
            "😂", "🤣" -> "laugh tears cry funny lol rofl haha joy humor"
            "🥺", "😢", "😭", "😿" -> "cry sad tears unhappy weep sob please emotional"
            "😎" -> "cool sunglasses boss swag smart attitude"
            "🥳" -> "party celebrate birthday happy fun horn"
            "🎉", "🎊", "🍾" -> "party celebrate celebration congrats festival popper"
            "👍" -> "thumbs up like yes ok good agree super"
            "👎" -> "thumbs down dislike no bad disagree reject"
            "🙏" -> "pray please thank thanks namaste grateful blessing respect hope"
            "✨", "⭐", "🌟", "💫" -> "sparkle star shine magic clean bright glitter"
            "💯" -> "hundred 100 perfect score full agree"
            "👏", "🙌" -> "clap applause cheer praise hands celebrate"
            "🤝" -> "handshake deal agreement agree partner contract"
            "💪", "🦾" -> "muscle strong gym flex power fitness bicep"
            "🍕", "🍔", "🍟", "🌭", "🥪", "🌮", "🌯" -> "food fastfood burger pizza junk delicious eat hungry"
            "☕️", "🫖", "🍵", "🧋", "🥤" -> "coffee tea drink chai boba morning cafe beverage"
            "🍺", "🍻", "🥂", "🍷", "🥃", "🍸", "🍹" -> "beer drink alcohol wine party cheers pub bar"
            "🎂", "🍰", "🧁", "🍩", "🍫", "🍬", "🍭", "🍦", "🍧", "🍨" -> "cake sweet dessert chocolate birthday ice cream sugar bake"
            "🍎", "🍏", "🍌", "🍇", "🍓", "🍉", "🍒", "🍑", "🥭", "🍍" -> "fruit healthy apple banana strawberry mango food fresh"
            "🐶", "🐕", "🦮", "🐩" -> "dog puppy pet animal canine"
            "🐱", "🐈", "🐈‍⬛", "😸", "😹", "😺", "😻" -> "cat kitten pet animal feline meow"
            "🦁", "🐯", "🐅", "🐆" -> "lion tiger predator wild cat animal"
            "🐮", "🐂", "🐃", "🐄" -> "cow bull ox animal cattle milk"
            "🐷", "🐽", "🐖", "🐗" -> "pig piggy pork animal"
            "🐵", "🐒", "🦍", "🦧" -> "monkey ape gorilla chimpanzee animal"
            "🐔", "🐓", "🦃", "🦆", "🦅", "🦉", "🐧", "🐦" -> "bird chicken duck eagle owl penguin animal flying"
            "🐟", "🐠", "🐡", "🦈", "🐬", "🐳", "🐋" -> "fish sea ocean water shark whale dolphin aquatic animal"
            "⚽️", "🏀", "🏈", "⚾️", "🎾", "🏐", "🏏", "🏓", "🏸" -> "sports ball football basketball cricket tennis game match activity"
            "🏆", "🥇", "🥈", "🥉", "🏅", "🎖" -> "trophy winner medal first champion cup prize victory"
            "🚗", "🚕", "🚙", "🚌", "🏎", "🏍", "🛵", "🚲" -> "car travel drive taxi vehicle bike cycle auto transport"
            "✈️", "🛫", "🛬", "🚀", "🛸", "🚁" -> "plane flight travel airplane airport trip fly rocket helicopter space"
            "💰", "💵", "💸", "💳", "🤑", "🪙" -> "money cash rich dollar payment pay wealth coin bank"
            "😴", "🥱", "💤", "🤤" -> "sleep tired sleepy night bed rest snooze dream"
            "😡", "😠", "🤬", "👿", "😤" -> "angry mad rage furious evil hate curse swear"
            "🤔", "🧐", "🤨" -> "think thinking curious question why wonder ponder"
            "✅", "✔️", "☑️" -> "check tick done yes correct right verified complete"
            "❌", "🛑", "⛔️", "🚫" -> "cross cancel wrong stop error no false ban"
            "☀️", "🌙", "🌧", "⚡️", "🌈", "❄️", "⛅️" -> "sun weather moon rain storm rainbow summer cloud night lightning cold winter"
            "🌸", "🌹", "🌺", "🌻", "💐", "🌷", "🌼" -> "flower rose floral garden blossom nature spring bloom"
            "🇮🇳" -> "india flag indian bharat tiranga"
            "🇺🇸" -> "usa america flag american united states"
            "🇬🇧" -> "uk britain british england flag"
            "🧑", "👨", "👩", "👦", "👧", "👶" -> "person people man woman boy girl human baby child"
            "📱", "📲", "💻", "🖥", "⌚️" -> "phone mobile screen laptop computer tech watch device"
            "💡" -> "idea light bulb bright think smart innovation"
            "🔒", "🔑", "🗝" -> "lock key secret secure security privacy password"
            "🎁" -> "gift present surprise box birthday wrapped"
            "🎮", "🕹" -> "game gaming controller play gamer console video"
            else -> ""
        }
    }
}
