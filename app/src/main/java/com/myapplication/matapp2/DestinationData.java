package com.myapplication.matapp2;

import com.myapplication.matapp2.td_pkg.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DestinationData {

    public static List<TouristDestination> getDestinationsForCity(String cityName) {
        Map<String, List<TouristDestination>> db = new HashMap<>();

        // AGRA
        List<TouristDestination> agra = new ArrayList<>();
        agra.add(new TouristDestination("Taj Mahal", "Dharmapuri, Agra — 282001", "★ 4.9", "ENTRY FEE", "₹50 / ₹1100", "Indian / Foreign", R.drawable.td_taj_mahal_hero, DestinationMain.class, true));
        agra.add(new TouristDestination("Agra Fort", "Rakabganj, Agra — 282003", "★ 4.7", "ENTRY FEE", "₹40 / ₹600", "Indian / Foreign", R.drawable.td_agra_fort_hero, AgraFortActivity.class, true));
        agra.add(new TouristDestination("Tomb of Akbar", "Sikandra, Agra — 282007", "★ 4.5", "ENTRY FEE", "₹30 / ₹310", "Indian / Foreign", R.drawable.td_tomb_akbar_hero, TombOfAkbarActivity.class, false));
        agra.add(new TouristDestination("Itmad-ud-Daula", "Moti Bagh, Agra — 282001", "★ 4.6", "ENTRY FEE", "₹20 / ₹210", "Indian / Foreign", R.drawable.td_itmad_ud_daula_hero, ItmadUdDaulaActivity.class, false));
        agra.add(new TouristDestination("Shahi Jama Masjid", "Kinari Bazar, Agra — 282003", "★ 4.4", "ENTRY FEE", "Free Entry", "Active place of worship", R.drawable.td_jama_masjid_hero, ShahiJamaMasjidActivity.class, false));
        db.put("agra", agra);

        // JAIPUR
        List<TouristDestination> jaipur = new ArrayList<>();
        jaipur.add(new TouristDestination("Hawa Mahal", "Badi Choupad, Jaipur", "★ 4.6", "ENTRY FEE", "₹50 / ₹200", "Indian / Foreign", R.drawable.td_hawa_mahal_hero, HawaMahalActivity.class, false));
        jaipur.add(new TouristDestination("Amber Palace", "Devasthan, Jaipur", "★ 4.8", "ENTRY FEE", "₹100 / ₹500", "Indian / Foreign", R.drawable.td_amber_palace_hero, AmberPalaceActivity.class, true));
        jaipur.add(new TouristDestination("City Palace", "Jaleb Chowk, Jaipur", "★ 4.7", "ENTRY FEE", "₹200 / ₹700", "Indian / Foreign", R.drawable.td_city_palace_hero, CityPalaceActivity.class, false));
        jaipur.add(new TouristDestination("Jantar Mantar", "Gangori Bazaar, Jaipur", "★ 4.5", "ENTRY FEE", "₹50 / ₹200", "Indian / Foreign", R.drawable.td_jantar_mantar_hero, JantarMantarActivity.class, true));
        jaipur.add(new TouristDestination("Jal Mahal", "Amer Road, Jaipur", "★ 4.4", "ENTRY FEE", "Free View", "No Entry Allowed", R.drawable.td_jal_mahal_hero, JalMahalActivity.class, false));
        jaipur.add(new TouristDestination("Jaigarh Fort", "Amer Road, Jaipur", "★ 4.6", "ENTRY FEE", "₹35 / ₹85", "Indian / Foreign", R.drawable.td_jaigarh_fort_hero, JaigardhFortActivity.class, false));
        jaipur.add(new TouristDestination("Albert Hall Museum", "Ram Niwas Garden, Jaipur", "★ 4.5", "ENTRY FEE", "₹40 / ₹300", "Indian / Foreign", R.drawable.td_albert_hall_hero, AlbertHallActivity.class, false));
        db.put("jaipur", jaipur);

        // GOA
        List<TouristDestination> goa = new ArrayList<>();
        goa.add(new TouristDestination("Baga Beach", "North Goa", "★ 4.4", "ENTRY FEE", "Free Entry", "Public Beach", R.drawable.td_baga_beach_hero, BagaBeachActivity.class, false));
        goa.add(new TouristDestination("Palolem Beach", "South Goa", "★ 4.7", "ENTRY FEE", "Free Entry", "Public Beach", R.drawable.td_palolem_beach_hero, PalolemBeachActivity.class, false));
        goa.add(new TouristDestination("Colva Beach", "South Goa", "★ 4.5", "ENTRY FEE", "Free Entry", "Public Beach", R.drawable.td_colva_beach_hero, ColvaBeachActivity.class, false));
        goa.add(new TouristDestination("Anjuna Market", "North Goa", "★ 4.3", "ENTRY FEE", "Free Entry", "Flea Market", R.drawable.td_anjuna_market_hero, AnjunaMarketActivity.class, false));
        goa.add(new TouristDestination("Basilica of Bom Jesus", "Old Goa", "★ 4.8", "ENTRY FEE", "Free Entry", "Historical Church", R.drawable.td_basilica_bom_jesus_hero, BasilicaBomJesusActivity.class, true));
        goa.add(new TouristDestination("Dudhsagar Falls", "Sonaulim, Goa", "★ 4.6", "ENTRY FEE", "₹400", "Per Person (Jeep Safari)", R.drawable.td_dudhsagar_falls_hero, DudhsagarFallsActivity.class, false));
        goa.add(new TouristDestination("Fontainhas", "Panaji, Goa", "★ 4.7", "ENTRY FEE", "Free Visit", "Heritage Walk", R.drawable.td_fontainhas_hero, FontainhasActivity.class, false));
        goa.add(new TouristDestination("Agonda Beach", "South Goa", "★ 4.8", "ENTRY FEE", "Free Entry", "Quiet Beach", R.drawable.td_agonda_beach_hero, AgondaBeachActivity.class, false));
        db.put("goa", goa);

        // VARANASI
        List<TouristDestination> varanasi = new ArrayList<>();
        varanasi.add(new TouristDestination("Kashi Vishwanath Temple", "Varanasi", "★ 4.8", "ENTRY FEE", "Free Entry", "VIP Darshan Paid", R.drawable.td_kashi_vishwanath_hero, KashiVishwanathActivity.class, false));
        varanasi.add(new TouristDestination("Dashashwamedh Ghat", "Varanasi", "★ 4.7", "ENTRY FEE", "Free Entry", "Ganga Aarti", R.drawable.td_dashashwamedha_ghat_hero, DashashwamedhaGhatActivity.class, false));
        varanasi.add(new TouristDestination("Assi Ghat", "Varanasi", "★ 4.6", "ENTRY FEE", "Free Entry", "Public Ghat", R.drawable.td_assi_ghat_hero, AssiGhatActivity.class, false));
        varanasi.add(new TouristDestination("Manikarnika Ghat", "Varanasi", "★ 4.5", "ENTRY FEE", "Free Entry", "Cremation Ghat", R.drawable.td_manikarnika_ghat_hero, ManikarnikaGhatActivity.class, false));
        varanasi.add(new TouristDestination("Sarnath", "Varanasi", "★ 4.7", "ENTRY FEE", "₹30 / ₹300", "Indian / Foreign", R.drawable.td_sarnath_hero, SarnathActivity.class, false));
        varanasi.add(new TouristDestination("Ramnagar Fort", "Ramnagar", "★ 4.2", "ENTRY FEE", "₹20 / ₹150", "Indian / Foreign", R.drawable.td_ramnagar_fort_hero, RamnagarFortActivity.class, false));
        varanasi.add(new TouristDestination("Tulsi Manas Mandir", "Sankat Mochan Rd", "★ 4.6", "ENTRY FEE", "Free Entry", "Temple Visit", R.drawable.td_tulsi_manas_mandir_hero, TulsiManasMandirActivity.class, false));
        varanasi.add(new TouristDestination("Dhamek Stupa", "Sarnath", "★ 4.6", "ENTRY FEE", "Included with Sarnath", "Historical Stupa", R.drawable.td_dhamek_stupa_hero, DhamekStupaActivity.class, false));
        db.put("varanasi", varanasi);

        // CHENNAI
        List<TouristDestination> chennai = new ArrayList<>();
        chennai.add(new TouristDestination("Marina Beach", "Chennai", "★ 4.4", "ENTRY FEE", "Free Entry", "Public Beach", R.drawable.td_marina_beach_hero, MarinaBeachActivity.class, false));
        chennai.add(new TouristDestination("Elliot's Beach", "Besant Nagar", "★ 4.5", "ENTRY FEE", "Free Entry", "Public Beach", R.drawable.td_elliots_beach_hero, ElliotsBeachActivity.class, false));
        chennai.add(new TouristDestination("Kapaleeshwarar Temple", "Mylapore", "★ 4.8", "ENTRY FEE", "Free Entry", "Temple Visit", R.drawable.td_kapaleeshwarar_temple_hero, KapaleeshwararTempleActivity.class, false));
        chennai.add(new TouristDestination("Parthasarathy Temple", "Triplicane", "★ 4.7", "ENTRY FEE", "Free Entry", "Temple Visit", R.drawable.td_parthasarathy_temple_hero, ParthasarathyTempleActivity.class, false));
        chennai.add(new TouristDestination("San Thome Church", "Mylapore", "★ 4.6", "ENTRY FEE", "Free Entry", "Church Visit", R.drawable.td_san_thome_church_hero, SanThomeChurchActivity.class, false));
        chennai.add(new TouristDestination("Snow Kingdom", "VGP Golden Beach", "★ 4.3", "ENTRY FEE", "₹750", "Per Person", R.drawable.td_snow_kingdom_hero, SnowKingdomActivity.class, false));
        db.put("chennai", chennai);

        if (cityName == null) return null;
        return db.get(cityName.toLowerCase());
    }
}
