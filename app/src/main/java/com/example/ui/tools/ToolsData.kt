package com.example.ui.tools

import java.util.Calendar

// --- DATA STRUCTURES & STATIC DATABASES ---

object ToolsData {

    // 1. Postal Codes Database
    data class PostalCodeInfo(
        val city: String,
        val code: String,
        val district: String,
        val province: String
    )

    val POSTAL_CODES = listOf(
        PostalCodeInfo("Colombo 01 (Fort)", "00100", "Colombo", "Western"),
        PostalCodeInfo("Colombo 02 (Slave Island)", "00200", "Colombo", "Western"),
        PostalCodeInfo("Colombo 03 (Colpetty)", "00300", "Colombo", "Western"),
        PostalCodeInfo("Colombo 04 (Bambalapitiya)", "00400", "Colombo", "Western"),
        PostalCodeInfo("Colombo 07 (Cinnamon Gardens)", "00700", "Colombo", "Western"),
        PostalCodeInfo("Dehiwala", "10350", "Colombo", "Western"),
        PostalCodeInfo("Mount Lavinia", "10370", "Colombo", "Western"),
        PostalCodeInfo("Nugegoda", "10250", "Colombo", "Western"),
        PostalCodeInfo("Kotte", "10100", "Colombo", "Western"),
        PostalCodeInfo("Malabe", "10115", "Colombo", "Western"),
        PostalCodeInfo("Kandy", "20000", "Kandy", "Central"),
        PostalCodeInfo("Peradeniya", "20400", "Kandy", "Central"),
        PostalCodeInfo("Gampola", "20500", "Kandy", "Central"),
        PostalCodeInfo("Katugastota", "20800", "Kandy", "Central"),
        PostalCodeInfo("Galle", "80000", "Galle", "Southern"),
        PostalCodeInfo("Hikkaduwa", "80240", "Galle", "Southern"),
        PostalCodeInfo("Matara", "81000", "Matara", "Southern"),
        PostalCodeInfo("Jaffna", "40000", "Jaffna", "Northern"),
        PostalCodeInfo("Negombo", "11500", "Gampaha", "Western"),
        PostalCodeInfo("Gampaha", "11000", "Gampaha", "Western"),
        PostalCodeInfo("Kurunegala", "60000", "Kurunegala", "North Western"),
        PostalCodeInfo("Anuradhapura", "50000", "Anuradhapura", "North Central"),
        PostalCodeInfo("Polonnaruwa", "51000", "Polonnaruwa", "North Central"),
        PostalCodeInfo("Ratnapura", "70000", "Ratnapura", "Sabaragamuwa"),
        PostalCodeInfo("Kegalle", "71000", "Kegalle", "Sabaragamuwa"),
        PostalCodeInfo("Badulla", "90000", "Badulla", "Uva"),
        PostalCodeInfo("Bandarawela", "90100", "Badulla", "Uva"),
        PostalCodeInfo("Ella", "90090", "Badulla", "Uva"),
        PostalCodeInfo("Nuwara Eliya", "22200", "Nuwara Eliya", "Central"),
        PostalCodeInfo("Kalutara", "12000", "Kalutara", "Western"),
        PostalCodeInfo("Panadura", "12500", "Kalutara", "Western"),
        PostalCodeInfo("Trincomalee", "31000", "Trincomalee", "Eastern"),
        PostalCodeInfo("Batticaloa", "30000", "Batticaloa", "Eastern"),
        PostalCodeInfo("Ampara", "32000", "Ampara", "Eastern"),
        PostalCodeInfo("Matale", "21000", "Matale", "Central"),
        PostalCodeInfo("Hambantota", "82000", "Hambantota", "Southern"),
        PostalCodeInfo("Tangalle", "82200", "Hambantota", "Southern"),
        PostalCodeInfo("Chilaw", "61000", "Puttalam", "North Western"),
        PostalCodeInfo("Puttalam", "61300", "Puttalam", "North Western"),
        PostalCodeInfo("Mannar", "41000", "Mannar", "Northern"),
        PostalCodeInfo("Vavuniya", "43000", "Vavuniya", "Northern")
    )

    // 2. Local fallback collections of beautiful and emotional Sinhala Captions (ලංකා Captions)
    data class CaptionTemplate(
        val text: String,
        val singlish: String
    )

    val CAPTION_TEMPLATES = mapOf(
        "Love" to listOf(
            CaptionTemplate("හීනයක් තරමටම ලස්සන ඔයා මගේ ජීවිතේම වෙලා...", "Heenayak tharamatama lassana oya mage jeewithema wela..."),
            CaptionTemplate("ඔය ඇස් මුණගැසුණු දවසේ ඉඳන් මගේ ලෝකයම වෙනස් වුණා.", "Oya as munagasunu dawase indan mage lokayama wenas una."),
            CaptionTemplate("හුස්මක් තරමටම දැනෙන හැඟීමක් ඔයා...", "Husmak tharamatama danena hangeemak oya..."),
            CaptionTemplate("සිනහවකින් හදවත සුවපත් කරන්න හැක්කේ ඔයාට විතරයි.", "Sinahawakin hadawatha suwapath karanna hakke oyata vitharayi.")
        ),
        "Friendship" to listOf(
            CaptionTemplate("මුළු ලෝකයම දාලා ගියත්, දාලා නොයන එකම රැකවරණය යාලුකමයි.", "Mulu lokayama dala giyath, dala noyana ekama rakawaranaya yalukamayi."),
            CaptionTemplate("හිනාවෙන්න විතරක් නෙවෙයි, අඬන්නත් උරහිසක් දෙන එකා තමයි නියම යාළුවා.", "Hinawenna vitharak neweyi, andannath urahisak dena eka thamayi niyama yaluwa."),
            CaptionTemplate("කාලය වෙනස් වුණත් වෙනස් නොවන අපේ බොක්කවල් ටික!", "Kalaya wenas unath wenas nowana ape bokkawal tika!"),
            CaptionTemplate("දුකේදිත් සැපේදිත් එකටම ඉන්න එකාලා...", "Dukedith sapedith ekatama inna ekala...")
        ),
        "Motivation" to listOf(
            CaptionTemplate("වැටෙන හැම වතාවකම නැගිටින්න, මොකද ඔයාගේ කතාව ඉවර වෙන්නෙ ඔයා ජයග්‍රහණය කළාමයි.", "Watenna hama watawakama nagitinna, mokada oyage kathawa iwara wenne oya jayagrahanya kalamayi."),
            CaptionTemplate("කවුරු නැතත් තමන්ට තමන් ඉන්නවා කියන විශ්වාසය අතහරින්න එපා.", "Kawuru nathath thamanta thaman innawa kiyana vishwasaya athaharanna epa."),
            CaptionTemplate("කටුක මාවත් අවසානයේ සුන්දර ගමනාන්තයන් බලා සිටී.", "Katuka mawath awasanaye sundara gamananthayan bala sitee."),
            CaptionTemplate("අද අමාරුයි, හෙට තවත් අමාරුයි, හැබැයි අනිද්දා අනිවාර්යයෙන්ම ලස්සනයි!", "Ada amaruyi, heta thawath amaruyi, habayi anidda aniwarayenma lassanayi!")
        ),
        "Mother" to listOf(
            CaptionTemplate("මුළු ලෝකයේම ආදරය එකතු කළත් මගේ අම්මගේ ආදරයට සමාන කළ නොහැක.", "Mulu lokayema adaraya ekathu kalath mage ammage adarayata samana kala nohaeka."),
            CaptionTemplate("ජීවිතයේ හැම තැනකදීම මගේ හෙවනැල්ල වුණ මගේ ආදරණීය අම්මා.", "Jeewithey hama thanakadima mage hewanalla una mage adaraneeya amma."),
            CaptionTemplate("අම්මා... ඒ අකුරු තුන තරම් වටිනා කිසිවක් මේ විශ්වයේ නැත.", "Amma... E akuru thuna tharam watina kisiwak me wishwaye natha."),
            CaptionTemplate("මගේ ජීවිතයේ පලමු දේවදූතයා, අම්මා...", "Mage jeewithey palamu dewadoothaya, amma...")
        ),
        "Funny" to listOf(
            CaptionTemplate("කාඩ් එක වැටුණත් සරීර සෞඛ්‍ය උපරිමයි බ්‍රෝ!", "Card eka watunath sareera saukhya uparimayi bro!"),
            CaptionTemplate("ජීවිතේ කියන්නේ හරියට නලා පදිනවා වගේ වැඩක්, සද්දෙ විතරයි වැඩක් නෑ.", "Jeewithe kiyanne hariyata nala padinawa wage wadak, sadde vitharayi wadak naa."),
            CaptionTemplate("අපිත් එක්ක හිනාවෙලා, පිටිපස්සෙන් අපිටම කපන උදර මිතුරන්!", "Apith ekka hinawela, pitipassen apitama kapana udara mithuran!"),
            CaptionTemplate("කාලය රන් හා සමානයි, ඒත් අපිට කෝ සල්ලි?", "Kalaya ran ha samanayi, eth apita ko salli?")
        ),
        "Life" to listOf(
            CaptionTemplate("සොබාදහම අපට උගන්වන්නේ නිහඬව ශක්තිමත් වීමටයි.", "Sobadahama apara ugannanne nihada wa shakthimath weemata yi."),
            CaptionTemplate("සතුට කියන්නේ ලැබෙන දේ නෙවෙයි, තියෙන දේ විඳින එකටයි.", "Sathuta kiyanne labena de neweyi, thiyena de windina ekata yi."),
            CaptionTemplate("ජීවිතය කෙටියි, ඒ නිසා සැහැල්ලුවෙන් සතුටින් ඉන්න පුරුදු වෙන්න.", "Jeewithe ketiyi, e nisa saehalluwen sathutin inna purudu wenna.")
        )
    )

    // 3. Astrology Tables (Lagnaya Sinhala name mapping & predictions)
    data class ZodiacInfo(
        val name: String,
        val sinhalaName: String,
        val element: String,
        val luckyColor: String,
        val luckyNumber: String,
        val auspiciousHours: String,
        val rahuKalam: String,
        val prediction: String
    )

    val ZODIAC_LIST = listOf(
        ZodiacInfo("Aries", "මේෂ (Mesha)", "Gini (Fire)", "Rathu (Red)", "9, 1", "Heta 08:15 AM - 09:30 AM", "Adha 03:00 PM - 04:30 PM", "අද ඔබට රැකියාවේ උසස්වීම් හෝ නව අවස්ථා ලැබිය හැකිය. මුදල් ගනුදෙනු වලදී ප්‍රවේශම් වන්න. පවුලේ සතුට උපරිමයි."),
        ZodiacInfo("Taurus", "වෘෂභ (Vrushabha)", "Pola (Earth)", "Kola (Green)", "6, 2", "Heta 10:30 AM - 11:45 AM", "Adha 12:00 PM - 01:30 PM", "පැරණි හිතවතෙකු හමුවීමෙන් සිතට සතුටක් දැනේ. නව ව්‍යාපාර අරමුණු සාර්ථක වේ. සෞඛ්‍යය ගැන සැලකිලිමත් වන්න."),
        ZodiacInfo("Gemini", "මිථුන (Mithuna)", "Sulanga (Air)", "Kaha (Yellow)", "5, 3", "Heta 06:00 AM - 07:15 AM", "Adha 07:30 AM - 09:00 AM", "සන්නිවේදන කටයුතු වලින් විශාල වාසි සැලසේ. නිර්මාණශීලී අදහස් ක්‍රියාත්මක කිරීමට සුදුසුම දවසක් දායක වේ."),
        ZodiacInfo("Cancer", "කටක (Kataka)", "Jalaya (Water)", "Sudu (White)", "2, 7", "Heta 11:15 AM - 12:30 PM", "Adha 01:30 PM - 03:00 PM", "ගෘහ ජීවිතය සාමකාමී වේ. මවගෙන් හෝ පිය පාර්ශවයෙන් උදව් උපකාර ලැබේ. අධ්‍යාපන කටයුතු වල නියැලෙන අයට ඉතා සුබයි."),
        ZodiacInfo("Leo", "සිංහ (Simha)", "Gini (Fire)", "Ran (Gold / Orange)", "1, 5", "Heta 02:00 PM - 03:15 PM", "Adha 03:00 PM - 04:30 PM", "සමාජයේ කැපී පෙනෙන චරිතයක් බවට පත්වේ. නායකත්ව ගුණාංග විදහා දැක්වීමට හැකි වේ. හදිසි ධන ලාභ පවතී."),
        ZodiacInfo("Virgo", "කන්‍යා (Kanya)", "Pola (Earth)", "Nila (Blue)", "5, 0", "Heta 09:00 AM - 10:15 AM", "Adha 10:30 AM - 12:00 PM", "ගැටළු සහගත ප්‍රශ්න රැසකට බුද්ධිමත්ව පිළිතුරු සෙවිය හැක. අසල්වැසියන් සමග සුහදතාව ඉහළ යයි."),
        ZodiacInfo("Libra", "තුලා (Thula)", "Sulanga (Air)", "Pink / Rose", "6, 8", "Heta 01:30 PM - 02:45 PM", "Adha 09:00 AM - 10:30 AM", "සහකරු හෝ සහකාරිය සමග සබඳතා ශක්තිමත් වේ. කලා නිර්මාණයන්හි නියුතු අයට සුපිරි ජයග්‍රහණ අත්වේ."),
        ZodiacInfo("Scorpio", "වෘශ්චික (Vrushchika)", "Jalaya (Water)", "Thaga (Copper / Crimson)", "9, 4", "Heta 07:45 AM - 09:00 AM", "Adha 01:30 PM - 03:00 PM", "හිතුවක්කාර තීරණ ගැනීමෙන් වලකින්න. විදේශ ගමන් අපේක්ෂා කළ අයට ශුභ ආරංචියක් ලැබේ. භක්තිමත් සිතුවිලි ඇතිවේ."),
        ZodiacInfo("Sagittarius", "ධනු (Dhanu)", "Gini (Fire)", "Ranthambili (Gold / Yellow)", "3, 1", "Heta 12:00 PM - 01:15 PM", "Adha 01:30 PM - 03:00 PM", "දිගුකාලීන ආයෝජන වලින් ලාභ ලැබේ. ආගමික කටයුතු වලට වැඩි නැඹුරුවක් දක්වයි. වාසනාව ලැබෙන දවසකි."),
        ZodiacInfo("Capricorn", "මකර (Makara)", "Pola (Earth)", "Kalu (Black / Slate)", "8, 4", "Heta 04:30 PM - 05:45 PM", "Adha 12:00 PM - 01:30 PM", "වගකීම් බහුල වන දවසකි. නමුත් ඔබගේ උත්සාහය නිසා සියලු බාධක ජයගත හැක. ඉවසීමෙන් කටයුතු කරන්න."),
        ZodiacInfo("Aquarius", "කුම්භ (Kumbha)", "Sulanga (Air)", "Dhumra (Grey / Cyan)", "8, 7", "Heta 03:15 PM - 04:30 PM", "Adha 01:30 PM - 03:00 PM", "මිතුරු හවුල් ව්‍යාපාර සාර්ථක වේ. අලුත් අදහස් වලට නිවසේ අයගේ සහයෝගය ලැබේ. ප්‍රීතිමත් සැඳෑවක් ගතවේ."),
        ZodiacInfo("Pisces", "මීන (Meena)", "Jalaya (Water)", "Kaha (Bright Yellow)", "3, 9", "Heta 08:30 AM - 09:45 AM", "Adha 10:30 AM - 12:00 PM", "සිතේ පැවති බර ගතිය පහව ගොස් සතුට ළඟාවේ. පුණ්‍ය කටයුත්තකට දායක විය හැක. පින්බර සබඳතා ඇතිවේ.")
    )

    // 4. Singlish-to-Sinhala Transliteration Engine
    fun transliterateSinglishToSinhala(input: String): String {
        if (input.isEmpty()) return ""
        var text = input.lowercase()

        // Replace specific common consonant clusters
        val multiMappings = listOf(
            "nch" to "ංච්", "tht" to "ත්ත", "nda" to "න්ද", "ndh" to "න්ධ", 
            "nga" to "ඟ", "nja" to "ඤ", "mb" to "ම්බ", "mbi" to "ම්බි", 
            "mbe" to "ම්බේ", "mba" to "ම්බා", "mbo" to "ම්බෝ", "mbu" to "ම්බු",
            "sh" to "ශ්", "ch" to "ච්", "th" to "ත්", "dh" to "ද්", "ph" to "ෆ්",
            "kh" to "ඛ්", "gh" to "ඝ්", "ch" to "ච්", "bh" to "භ්"
        )
        // Some vowels at start
        val vowelsStart = listOf(
            "aa" to "ආ", "ae" to "ඇ", "ee" to "ඊ", "oo" to "ඌ", "ei" to "ඒ", "ou" to "ඖ",
            "a" to "අ", "i" to "ඉ", "u" to "උ", "e" to "එ", "o" to "ඔ"
        )

        val consonants = mapOf(
            "k" to "ක", "g" to "ග", "c" to "ච", "j" to "ජ", "t" to "ට", "d" to "ඩ",
            "n" to "න", "t" to "ත", "d" to "ද", "p" to "ප", "b" to "බ", "m" to "ම",
            "y" to "ය", "r" to "ර", "l" to "ල", "w" to "ව", "v" to "ව", "s" to "ස",
            "h" to "හ"
        )

        // Custom vowel signs when appended to consonants (e.g., ka -> ක, kaa -> කා)
        val vowelSigns = listOf(
            "ae" to "ඇ", "aa" to "ා", "a" to "", "ee" to "ී", "e" to "ෙ", "oo" to "ූ",
            "o" to "ො", "uu" to "ු", "u" to "ු", "i" to "ි", "ei" to "ේ"
        )

        // Let's do a simple letter parsing state machine
        val out = StringBuilder()
        var i = 0
        while (i < text.length) {
            // First treat vowels at the very start of a word or after space
            val isStart = (i == 0 || text[i - 1] == ' ' || text[i - 1] == '\n')

            if (isStart) {
                var matchedVowel = false
                for ((sing, sin) in vowelsStart) {
                    if (text.startsWith(sing, i)) {
                        out.append(sin)
                        i += sing.length
                        matchedVowel = true
                        break
                    }
                }
                if (matchedVowel) continue
            }

            // Check multi consonant mapping
            var matchedMulti = false
            for ((sing, sin) in multiMappings) {
                if (text.startsWith(sing, i)) {
                    // Check if vowel follows
                    var vowelFollowsMapping = false
                    val nextIdx = i + sing.length
                    for ((vSing, vSin) in vowelSigns) {
                        if (text.startsWith(vSing, nextIdx)) {
                            // Strip "්" suffix from sin if mapping has it, then append vowel modifier
                            val baseSinCon = if (sin.endsWith("්")) sin.dropLast(1) else sin
                            out.append(baseSinCon).append(vSin)
                            i = nextIdx + vSing.length
                            vowelFollowsMapping = true
                            matchedMulti = true
                            break
                        }
                    }
                    if (!vowelFollowsMapping) {
                        out.append(sin)
                        i += sing.length
                        matchedMulti = true
                    }
                    break
                }
            }
            if (matchedMulti) continue

            // Normal Consonant Mapping
            val charStr = text[i].toString()
            if (consonants.containsKey(charStr)) {
                val sinCon = consonants[charStr]!!
                // Check if a vowel follows
                val nextIdx = i + 1
                var vowelFollowsLetter = false
                for ((vSing, vSin) in vowelSigns) {
                    if (text.startsWith(vSing, nextIdx)) {
                        out.append(sinCon).append(vSin)
                        i = nextIdx + vSing.length
                        vowelFollowsLetter = true
                        break
                    }
                }
                if (!vowelFollowsLetter) {
                    // Ends in silent consonant or followed by another consonant. Put Hal-Kereema "්"
                    out.append(sinCon).append("්")
                    i += 1
                }
            } else {
                // If it's none of above (space, numbers, punctuation, unmatched letters)
                out.append(text[i])
                i += 1
            }
        }

        // Quick post-processing cleaning
        return out.toString()
            .replace("්ා", "ා")
            .replace("්ි", "ි")
            .replace("්ී", "ී")
            .replace("්ු", "ු")
            .replace("්ූ", "ූ")
            .replace("්ෙ", "ෙ")
            .replace("්ො", "ො")
            .replace("්ේ", "ේ")
            .replace("න්ද්", "න්ද්")
            .replace("අා", "ආ")
            .replace("අැ", "ඇ")
            .replace("අි", "ඉ")
            .replace("අෙ", "එ")
            .replace("අො", "ඔ")
    }

    // 5. Domestic Electricity Bill Bracket Calculator
    fun calculateCEBBill(units: Double): Double {
        if (units <= 0.0) return 0.0
        var totalCost = 0.0

        if (units <= 60.0) {
            // Low user brackets
            if (units <= 30.0) {
                totalCost = (units * 8.0) + 150.0
            } else {
                // 31-60
                val block1 = 30 * 8.0
                val block2 = (units - 30.0) * 20.0
                totalCost = block1 + block2 + 300.0
            }
        } else {
            // High user brackets (> 60 units)
            if (units <= 90.0) {
                val block1 = 60 * 25.0
                val block2 = (units - 60.0) * 30.0
                totalCost = block1 + block2 + 600.0
            } else if (units <= 120.0) {
                val block1 = 60 * 25.0
                val block2 = 30 * 30.0
                val block3 = (units - 90.0) * 50.0
                totalCost = block1 + block2 + block3 + 1000.0
            } else if (units <= 180.0) {
                val block1 = 60 * 25.0
                val block2 = 30 * 30.0
                val block3 = 30 * 50.0
                val block4 = (units - 120.0) * 75.0
                totalCost = block1 + block2 + block3 + block4 + 1500.0
            } else {
                val block1 = 60 * 25.0
                val block2 = 30 * 30.0
                val block3 = 30 * 50.0
                val block4 = 60 * 75.0
                val block5 = (units - 180.0) * 100.0
                totalCost = block1 + block2 + block3 + block4 + block5 + 2000.0
            }
        }
        return totalCost
    }

    // 6. National Identity Card (NIC) Parser
    data class NicDetails(
        val format: String,
        val dob: String,
        val gender: String,
        val age: Int,
        val isVoter: Boolean,
        val error: String? = null
    )

    fun parseNIC(nicStr: String): NicDetails {
        var clean = nicStr.trim().uppercase()
        if (clean.length != 9 && clean.length != 10 && clean.length != 12) {
            return NicDetails("", "", "", 0, false, "NIC අංකය වැරදියි (9 හෝ 12 ඉලක්කම් විය යුතුය)")
        }

        try {
            var year = 0
            var daysValue = 0
            var isVoterVal = false
            var formatVal = ""

            if (clean.length == 9 || (clean.length == 10 && (clean.endsWith("V") || clean.endsWith("X")))) {
                formatVal = "Old Format (පැරණි ක්‍රමය)"
                val yearPart = clean.substring(0, 2).toInt()
                year = 1900 + yearPart
                daysValue = clean.substring(2, 5).toInt()
                val lastChar = if (clean.length == 10) clean[9] else 'V'
                isVoterVal = lastChar == 'V'
            } else if (clean.length == 12) {
                formatVal = "New Format (නව ක්‍රමය)"
                year = clean.substring(0, 4).toInt()
                daysValue = clean.substring(4, 7).toInt()
                // Voter status is generally true for any citizen above age 18 in new format
                isVoterVal = (2026 - year) >= 18
            } else {
                return NicDetails("", "", "", 0, false, "වලංගු නොවන ආකෘතියකි.")
            }

            // Parse Gender
            val isFemale = daysValue > 500
            val genderVal = if (isFemale) "Female (ස්ත්‍රී)" else "Male (පුරුෂ)"
            var actualDays = if (isFemale) daysValue - 500 else daysValue

            // Validate days limits
            if (actualDays < 1 || actualDays > 366) {
                return NicDetails("", "", "", 0, false, "ඇතුලත් කල අංක වල දින වැරදියි (NIC error)")
            }

            // Convert days of year to exact calendar date
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.DAY_OF_YEAR, actualDays)
            val monthNames = listOf("ජනවාරි", "පෙබරවාරි", "මාර්තු", "අප්‍රේල්", "මැයි", "ජූනි", "ජූලි", "අගෝස්තු", "සැප්තැම්බර්", "ඔක්තෝබර්", "නොවැම්බර්", "දෙසැම්බර්")
            
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val monthIndex = calendar.get(Calendar.MONTH)
            val dobStr = "$year ${monthNames[monthIndex]} $dayOfMonth"

            val currentYear = 2026
            val ageVal = currentYear - year

            return NicDetails(formatVal, dobStr, genderVal, ageVal, isVoterVal, null)

        } catch (e: Exception) {
            return NicDetails("", "", "", 0, false, "විශ්ලේෂණය අපහසුයි. අංක නිවැරදිදැයි පරීක්ෂා කරන්න.")
        }
    }
}
