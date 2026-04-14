package com.myapplication.matapp2.tourist_packages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageData {

    public static Map<String, List<TouristPackage>> getAllPackages() {
        Map<String, List<TouristPackage>> data = new HashMap<>();

        // ── JAIPUR ──────────────────────────────────────────────
        List<TouristPackage> jaipur = new ArrayList<>();
        jaipur.add(new TouristPackage(
            "Royal Jaipur Heritage Tour", "4.8 ★", "₹8,499", "3N / 4D",
            "Heritage • Forts • Royalty",
            "Explore Amber Fort, City Palace, Hawa Mahal & Jantar Mantar with camel safari.",
            "🏯",
            Arrays.asList(
                "✈  Return airfare from your city",
                "🏨  3 nights stay in 4-star heritage hotel",
                "🍽  Daily breakfast & dinner (Rajasthani cuisine)",
                "🐪  Camel safari at Amber Fort",
                "🎭  Traditional cultural dance performance",
                "🚌  AC transport for all sightseeing",
                "🎫  Entry tickets to all monuments",
                "📸  Professional photography at Amber Fort"
            ),
            Arrays.asList(
                "Day 1 | Arrival & check-in. Evening Hawa Mahal & local bazaar. Welcome dinner.",
                "Day 2 | Amber Fort camel safari, City Palace, Jantar Mantar & Jal Mahal photo stop.",
                "Day 3 | Nahargarh Fort, Albert Hall Museum, block printing workshop & Johari Bazaar.",
                "Day 4 | Breakfast, check-out & transfer to airport."
            )
        ));
        jaipur.add(new TouristPackage(
            "Pink City Luxury Package", "4.6 ★", "₹16,520", "4N / 5D",
            "Luxury • Shopping • Culture",
            "Luxury haveli stay with private tours, shopping & cultural experiences.",
            "🌸",
            Arrays.asList(
                "✈  Return airfare (Business class upgrade available)",
                "🏨  4 nights in luxury heritage haveli",
                "🍽  All meals — breakfast, lunch & dinner",
                "🛒  Guided shopping tour at Johari Bazaar",
                "🎨  Block printing & blue pottery workshop",
                "🚗  Private AC car for all transfers",
                "🎫  All monument entry tickets",
                "💆  Complimentary Ayurvedic spa session"
            ),
            Arrays.asList(
                "Day 1 | VIP arrival & haveli check-in. Rooftop welcome dinner with city views.",
                "Day 2 | Exclusive Amber Fort private tour, City Palace & Jantar Mantar.",
                "Day 3 | Nahargarh sunrise, Jal Mahal, block printing workshop & bazaar shopping.",
                "Day 4 | Albert Hall Museum, Birla Mandir & spa session at the haveli.",
                "Day 5 | Breakfast & check-out. Chauffeur transfer to airport."
            )
        ));
        jaipur.add(new TouristPackage(
            "Rajasthan Wildlife & Forts", "4.5 ★", "₹11,200", "5N / 6D",
            "Wildlife • Adventure • Forts",
            "Tiger safari at Ranthambore combined with Jaipur forts & stepwells.",
            "🐯",
            Arrays.asList(
                "🚂  Train tickets (Jaipur ↔ Ranthambore)",
                "🏨  5 nights (2 wildlife resort + 3 heritage hotel)",
                "🍽  Daily breakfast & dinner",
                "🐯  2× Ranthambore tiger safari (jeep)",
                "🏯  Amber Fort sound & light show entry",
                "🌊  Chand Baori stepwell guided visit",
                "🚌  AC transport throughout",
                "🎫  All tickets & forest permits"
            ),
            Arrays.asList(
                "Day 1 | Arrive Jaipur. Check-in & local market walk.",
                "Day 2 | Amber Fort & City Palace. Evening Nahargarh Fort sunset.",
                "Day 3 | Chand Baori stepwell & Abhaneri temples. Transfer to Ranthambore.",
                "Day 4 | Morning & afternoon jeep safaris in Ranthambore Tiger Reserve.",
                "Day 5 | Optional safari, return to Jaipur. Amber Fort sound & light show.",
                "Day 6 | Breakfast & departure."
            )
        ));
        data.put("jaipur", jaipur);

        // ── VARANASI ─────────────────────────────────────────────
        List<TouristPackage> varanasi = new ArrayList<>();
        varanasi.add(new TouristPackage(
            "Ganga Aarti Spiritual Package", "4.9 ★", "₹6,800", "2N / 3D",
            "Spiritual • Ganges • Ghats",
            "Deeply spiritual — Ganga Aarti, sunrise boat rides & temple darshans.",
            "🪔",
            Arrays.asList(
                "✈  Return airfare",
                "🏨  2 nights ghat-view hotel",
                "🍽  Daily breakfast & prasad thali",
                "🚣  Sunrise boat ride on the Ganges",
                "🪔  Ganga Aarti front-row seating",
                "🛕  Kashi Vishwanath Temple darshan",
                "🏛  Sarnath excursion with guide",
                "🚌  AC transport for all transfers"
            ),
            Arrays.asList(
                "Day 1 | Arrival, check-in & evening Ganga Aarti at Dashashwamedh Ghat.",
                "Day 2 | Pre-dawn sunrise boat ride. Kashi Vishwanath darshan. Afternoon Sarnath.",
                "Day 3 | Morning meditation on ghat, breakfast & departure."
            )
        ));
        varanasi.add(new TouristPackage(
            "Heritage Varanasi Deep Dive", "4.7 ★", "₹9,500", "3N / 4D",
            "Heritage • Culture • Food",
            "Walk all 88 ghats, silk weaving factory & guided street food tour.",
            "🕌",
            Arrays.asList(
                "✈  Return airfare",
                "🏨  3 nights heritage guesthouse",
                "🍽  Daily breakfast + street food tour dinner",
                "🚣  Morning & evening boat rides",
                "🧵  Benaras silk weaving factory visit",
                "🎵  Classical Banarasi music evening",
                "🍜  Guided street food tour (chaat, lassi, kachori)",
                "🚌  AC transport & professional guide"
            ),
            Arrays.asList(
                "Day 1 | Arrival & check-in. Evening Ganga Aarti & intro ghat walk.",
                "Day 2 | Full ghat walk Assi to Manikarnika. Afternoon silk weaving factory.",
                "Day 3 | Ramnagar Fort, Durga Temple & classical music show. Street food tour.",
                "Day 4 | Sunrise boat ride, morning meditation & departure."
            )
        ));
        varanasi.add(new TouristPackage(
            "Varanasi Pilgrimage Tour", "4.8 ★", "₹12,000", "4N / 5D",
            "Pilgrimage • Temples • Peace",
            "Complete temple circuit, daily aarti, meditation & spiritual immersion.",
            "🙏",
            Arrays.asList(
                "✈  Return airfare",
                "🏨  4 nights spiritual retreat guesthouse",
                "🍽  All vegetarian sattvic meals included",
                "🛕  Darshan: Kashi Vishwanath, Sankat Mochan & Annapurna",
                "🪔  Evening aarti every night at the ghat",
                "🧘  Daily guided meditation session (1 hr)",
                "🚣  Morning boat ride every day",
                "📿  Rudraksha & prasad gift kit",
                "🚌  All transfers included"
            ),
            Arrays.asList(
                "Day 1 | Arrival, pooja ceremony & evening Ganga Aarti.",
                "Day 2 | Kashi Vishwanath & Annapurna Temple darshan. Afternoon meditation.",
                "Day 3 | Sankat Mochan, Tulsi Manas Temple & ghat walk. Evening aarti.",
                "Day 4 | Sarnath Buddhist sites & local artisan visit. Night aarti.",
                "Day 5 | Final sunrise boat ride, breakfast & departure."
            )
        ));
        data.put("varanasi", varanasi);

        // ── AGRA ──────────────────────────────────────────────────
        List<TouristPackage> agra = new ArrayList<>();
        agra.add(new TouristPackage(
            "Taj Mahal Sunrise Special", "4.9 ★", "₹7,200", "2N / 3D",
            "Taj Mahal • UNESCO • Wonder",
            "Sunrise entry to Taj Mahal, Agra Fort & Mehtab Bagh sunset view.",
            "🕍",
            Arrays.asList(
                "✈  Return airfare",
                "🏨  2 nights 5-star hotel near Taj Mahal",
                "🍽  Daily breakfast & dinner",
                "🌅  Exclusive sunrise Taj Mahal entry",
                "🏯  Agra Fort guided tour",
                "💎  Itmad-ud-Daulah (Baby Taj) visit",
                "🌿  Mehtab Bagh sunset Taj view",
                "🚌  AC transport & licensed guide",
                "🎫  All monument entry tickets"
            ),
            Arrays.asList(
                "Day 1 | Arrival & check-in. Evening Mehtab Bagh for Taj sunset view.",
                "Day 2 | 4:30 AM pickup for sunrise Taj Mahal. Agra Fort & Itmad-ud-Daulah.",
                "Day 3 | Marble inlay craft demo, local bazaar & departure transfer."
            )
        ));
        agra.add(new TouristPackage(
            "Mughal Splendour Package", "4.7 ★", "₹10,800", "3N / 4D",
            "Mughal • History • Art",
            "Comprehensive Mughal heritage with Fatehpur Sikri & cooking class.",
            "👑",
            Arrays.asList(
                "✈  Return airfare",
                "🏨  3 nights heritage hotel",
                "🍽  All meals — breakfast, lunch & dinner",
                "🌅  Sunrise Taj Mahal entry",
                "🏯  Agra Fort & Fatehpur Sikri full day",
                "💎  Marble inlay craft workshop",
                "👨‍🍳  Mughal cuisine cooking class",
                "🚌  AC car & licensed guide",
                "🎫  All entry tickets"
            ),
            Arrays.asList(
                "Day 1 | Arrival, check-in & Mehtab Bagh sunset. Mughal welcome dinner.",
                "Day 2 | Sunrise Taj Mahal. Agra Fort & Itmad-ud-Daulah afternoon.",
                "Day 3 | Full day Fatehpur Sikri. Evening marble inlay & cooking class.",
                "Day 4 | Morning bazaar visit, breakfast & departure."
            )
        ));
        agra.add(new TouristPackage(
            "Golden Triangle – Agra Leg", "4.6 ★", "₹14,500", "4N / 5D",
            "Photography • Culture • History",
            "Photography-focused Agra with professional guide & Akbar's Tomb.",
            "📸",
            Arrays.asList(
                "✈  Return airfare",
                "🏨  4 nights 5-star hotel",
                "🍽  Daily breakfast & select dinners",
                "📷  Professional photography guide at Taj",
                "🌅  Sunrise & sunset Taj photo sessions",
                "🏯  Agra Fort & Sikandra (Akbar's Tomb)",
                "🛒  Guided Kinari Bazaar shopping tour",
                "🚗  Private AC car throughout",
                "🎫  All tickets & permits"
            ),
            Arrays.asList(
                "Day 1 | Arrival & check-in. Briefing with photography guide. Mehtab Bagh sunset.",
                "Day 2 | Golden hour sunrise Taj shoot. Agra Fort photography walk.",
                "Day 3 | Itmad-ud-Daulah & Sikandra (Akbar's Tomb). Evening Kinari Bazaar.",
                "Day 4 | Final Taj golden hour session. Marble craft demo & free time.",
                "Day 5 | Breakfast & departure transfer."
            )
        ));
        data.put("agra", agra);

        // ── CHENNAI ───────────────────────────────────────────────
        List<TouristPackage> chennai = new ArrayList<>();
        chennai.add(new TouristPackage(
            "Temple City Cultural Tour", "4.7 ★", "₹7,800", "3N / 4D",
            "Temples • Culture • Dravidian",
            "Dravidian temples, Santhome Basilica, Marina Beach & classical dance.",
            "🏛",
            Arrays.asList(
                "✈  Return airfare",
                "🏨  3 nights 4-star hotel",
                "🍽  Daily breakfast & one special South Indian thali",
                "🛕  Kapaleeshwarar & Parthasarathy Temple visit",
                "⛪  Santhome Basilica guided tour",
                "🌅  Marina Beach sunrise walk",
                "💃  Bharatanatyam classical performance",
                "🚌  AC transport & guide",
                "🎫  All entry tickets"
            ),
            Arrays.asList(
                "Day 1 | Arrival & check-in. Evening Kapaleeshwarar Temple & Bharatanatyam show.",
                "Day 2 | Sunrise Marina Beach. Parthasarathy Temple, Santhome & Fort St. George.",
                "Day 3 | Mahabalipuram day trip — Shore Temple, Arjuna's Penance & Pancha Rathas.",
                "Day 4 | Breakfast, souvenir shopping & departure."
            )
        ));
        chennai.add(new TouristPackage(
            "Chennai Beach & Cuisine", "4.5 ★", "₹9,200", "3N / 4D",
            "Beach • Food • Relaxation",
            "Beach relaxation, filter coffee trail, cooking class & folk museum.",
            "🌊",
            Arrays.asList(
                "✈  Return airfare",
                "🏨  3 nights beachside hotel",
                "🍽  Daily breakfast + guided street food tour",
                "🏖  Marina & Elliot's Beach free time",
                "🏛  Dakshinachitra folk museum visit",
                "👨‍🍳  South Indian cooking masterclass",
                "☕  Filter coffee & tiffin trail",
                "🚌  AC transport & local food guide",
                "🛕  Mahabalipuram shore temple excursion"
            ),
            Arrays.asList(
                "Day 1 | Arrival & check-in. Evening Elliot's Beach sunset & filter coffee trail.",
                "Day 2 | Marina Beach walk. Dakshinachitra museum & South Indian cooking class.",
                "Day 3 | Mahabalipuram Shore Temple, Pancha Rathas & seafood lunch.",
                "Day 4 | Idli-sambar breakfast at iconic local spot & departure."
            )
        ));
        chennai.add(new TouristPackage(
            "Mahabalipuram Heritage Escape", "4.8 ★", "₹11,500", "4N / 5D",
            "UNESCO • Shore Temple • Heritage",
            "UNESCO rock-cut temples, Crocodile Bank, Tiger Cave & coastal seafood.",
            "🪨",
            Arrays.asList(
                "✈  Return airfare",
                "🏨  4 nights (2 Chennai + 2 Mahabalipuram resort)",
                "🍽  All meals included",
                "🏛  Shore Temple, Pancha Rathas & Arjuna's Penance guided tour",
                "🐊  Crocodile Bank wildlife park visit",
                "🌊  Tiger Cave coastal heritage site",
                "🦞  Fresh seafood dinners on the coast",
                "🚌  AC transport & archaeologist guide",
                "🎫  All UNESCO site tickets"
            ),
            Arrays.asList(
                "Day 1 | Arrive Chennai. Check-in & evening Kapaleeshwarar Temple.",
                "Day 2 | Drive to Mahabalipuram. Shore Temple sunrise & Arjuna's Penance.",
                "Day 3 | Pancha Rathas, Tiger Cave & Crocodile Bank. Seafood dinner on beach.",
                "Day 4 | Pondicherry day trip — French Quarter & Auroville visit.",
                "Day 5 | Return to Chennai. Breakfast & departure."
            )
        ));
        data.put("chennai", chennai);

        // ── GOA ───────────────────────────────────────────────────
        List<TouristPackage> goa = new ArrayList<>();
        goa.add(new TouristPackage(
            "Goa Beach Fiesta Package", "4.8 ★", "₹12,800", "3N / 4D",
            "Beach • Nightlife • Water Sports",
            "Best of North & South Goa — beaches, water sports & sunset cruise.",
            "🏖",
            Arrays.asList(
                "✈  Return airfare",
                "🏨  3 nights beachside 4-star resort",
                "🍽  Daily breakfast & beach BBQ dinner",
                "🏄  Water sports: jet ski, parasailing & banana boat",
                "🚢  Sunset cruise on Mandovi River with music",
                "🛍  Anjuna flea market visit",
                "🍹  Welcome cocktail on arrival",
                "🛵  Scooter rental for 1 day",
                "🚌  AC transport for group sightseeing"
            ),
            Arrays.asList(
                "Day 1 | Arrival, resort check-in & welcome cocktail. Anjuna market & beach shack dinner.",
                "Day 2 | North Goa — Baga & Calangute beaches & water sports. Mandovi River sunset cruise.",
                "Day 3 | South Goa — Palolem & Colva beaches. Scooter ride. Beach BBQ dinner.",
                "Day 4 | Morning swim, breakfast & check-out. Airport transfer."
            )
        ));
        goa.add(new TouristPackage(
            "Heritage Old Goa & Spice Tour", "4.6 ★", "₹9,500", "3N / 4D",
            "Heritage • Spices • Portuguese",
            "Portuguese churches, spice plantation & Dudhsagar waterfall adventure.",
            "⛪",
            Arrays.asList(
                "✈  Return airfare",
                "🏨  3 nights heritage Portuguese villa",
                "🍽  Daily breakfast & traditional Goan lunch",
                "⛪  Se Cathedral & Basilica of Bom Jesus (UNESCO)",
                "🌿  Spice plantation guided tour & lunch",
                "💧  Dudhsagar waterfall jeep safari",
                "🚌  AC transport & history guide",
                "🎫  All entry tickets & permits",
                "🍛  Goan fish curry cooking demo"
            ),
            Arrays.asList(
                "Day 1 | Arrival, check-in to Portuguese villa. Old Goa churches evening walk.",
                "Day 2 | Se Cathedral, Bom Jesus Basilica & Archaeological Museum.",
                "Day 3 | Spice plantation morning tour & lunch. Dudhsagar waterfall jeep safari.",
                "Day 4 | Goan cooking demo, breakfast & departure."
            )
        ));
        goa.add(new TouristPackage(
            "Goa Luxury Wellness Retreat", "4.9 ★", "₹22,000", "4N / 5D",
            "Luxury • Wellness • Yoga",
            "5-star wellness escape — yoga, Ayurveda, catamaran cruise & dolphin spotting.",
            "🧘",
            Arrays.asList(
                "✈  Return airfare (Business class)",
                "🏨  4 nights 5-star beachfront resort",
                "🍽  All organic healthy meals (vegan options)",
                "🧘  Daily yoga & pranayama class (sunrise)",
                "💆  2× Ayurvedic full-body spa treatments",
                "🚢  Private catamaran cruise with dolphin spotting",
                "🌊  Snorkelling & kayaking session",
                "🌿  Nutrition & wellness consultation",
                "🚗  Private car for all transfers"
            ),
            Arrays.asList(
                "Day 1 | VIP arrival, resort check-in & Ayurvedic consultation.",
                "Day 2 | Sunrise yoga on the beach. Spa session. Afternoon kayaking.",
                "Day 3 | Morning yoga. Private catamaran & dolphin spotting. Sunset meditation.",
                "Day 4 | Yoga, snorkelling & second spa treatment. Farewell wellness dinner.",
                "Day 5 | Final sunrise yoga, organic breakfast & departure."
            )
        ));
        data.put("goa", goa);

        return data;
    }
}
