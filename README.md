# 🌍 GetSetChalo — Your Ultimate Indian Travel Companion

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Java](https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)

**GetSetChalo** is a premium tourism application designed to simplify travel planning across India's most iconic locations. From spiritual ghats to royal palaces and sun-kissed beaches, we provide a curated end-to-end booking experience with integrated rewards and real-time data.

> [!IMPORTANT]
> **Operational Scope:** Currently, GetSetChalo operates exclusively for **four cities and one state**:
> 🏰 **Jaipur** | 🕉️ **Varanasi** | 🌊 **Chennai** | 🕌 **Agra** | 🏖️ **Goa**

---

## 🚀 App Journey & Experience

Experience a seamless flow designed for the modern traveler:

1.  **Immersive Splash:** A grand entrance featuring localized motifs and smooth animations.
2.  **Secure Authentication:** User-friendly login and registration powered by Firebase.
3.  **Intuitive Home:** A centralized hub to explore featured destinations and start your search.
4.  **Bottom Navigation Bar:** Effortlessly switch between **Home**, **My Bookings**, **Rewards**, and **Profile** using a persistent, modern navigation bar.
5.  **Destination Discovery:** Dive deep into the history and details of landmarks with curated content.
6.  **Open in Maps:** One-tap navigation! Launch system maps or OpenStreetMap to get precise directions to any landmark using LocationIQ coordinates.
7.  **Favorites & Recently Viewed:** "Heart" your top picks to save them for later and track your journey through a history of recently visited places.
8.  **Curated Packages:** Select from pre-designed itineraries (Heritage, Adventure, Relax).
9.  **Hotel Browsing:** Real-time hotel search with live pricing, reviews, and ratings.
10. **Final Checkout & Rewards:** Secure your trip with a comprehensive summary. Upon booking, earn **Reward Points** for your next adventure!

---

## ✨ Core Features

### 💎 Reward Points System
Our loyalty program rewards you for every trip you take.
- **The Logic:** Points are calculated based on your transaction value. For every **₹100 spent, you earn 1 Reward Point**. These points are stored in a secure Ledger on Firebase.
- **Usage:** Points can be tracked in the Rewards section and redeemed to get discounts on future bookings.

### ❤️ Favourites & Recently Viewed
Stay organized with a personalized travel list. The app uses a local database/Firestore to keep track of your "Liked" destinations and automatically updates a "Recently Viewed" carousel so you can pick up right where you left off.

### 🗺️ Intelligent Mapping
Integrating **OSMDroid** and **LocationIQ**, we provide precise coordinates for every destination. The "Open in Maps" feature bridges our app with your preferred navigation tool via Android Intents.

---

## 🛠️ Tech Stack

- **Language:** Java (Android)
- **Backend:** Firebase (Authentication, Firestore, Analytics)
- **Architecture:** Component-based modular architecture
- **Libraries:** OkHttp3 (Networking), Glide (Image Loading), OSMDroid (Maps)

---

## 📡 External APIs

*   **[Booking.com via RapidAPI](https://rapidapi.com/bookingcom/api/booking-com/):** Powers real-time hotel listings, pricing, and availability.
*   **[LocationIQ](https://locationiq.com/):** Provides geocoding and mapping services for accurate landmark positioning.

---

## ⚙️ Setup & Installation Guide

### 1. API Key Configuration
To run this project, you must insert your own API keys:
- **Booking.com:** Open `TourismWaysActivity.java`, locate the `API_KEY` constant, and replace it with your RapidAPI key.
- **LocationIQ:** Insert your access token in the mapping utility classes to enable "Open in Maps".

### 2. Firebase Setup
1.  Download your `google-services.json` from the Firebase Console.
2.  Place it in the `app/` directory of the project.
3.  **Console Settings:**
    - Go to **Authentication** > **Sign-in Method**.
    - **Enable Email/Password** to ensure the login/register pages work perfectly.
    - Enable **Cloud Firestore** to store user details.

---

## 👥 Meet the Team

**Samriddh Anand**, #TheExpeditionsAlchemist                    
**Role** - Tourist Packages Feature

**Sara Deshpande**, #WeavingThePointOfNoReturn                          
**Role** - Checkout Experience

**Shivli Soni**, #TheArchmageOfAesthetics                                
**Role** - Splash Screen and Home Page

**Shourya Jain**, #KeeperOfTheKeys                                
**Role** - Login Page and Tourism Destinations

**Singhania Divyam Pareshkumar**, #JackOfAllTradesMasterOfNone                               
**Role** - Hotels Page and Backend Architecture

---
*Created with ❤️ for travelers by Team GetSetChalo.*
