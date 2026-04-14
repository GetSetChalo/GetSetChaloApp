$basePath = "D:\SoftwareData\AndroidStudioProjects\MatApp22\app\src\main\java\com\myapplication\matapp2\td_pkg"

$fileMap = @{
    "HawaMahalActivity.java"          = @("Hawa Mahal",              "Jaipur")
    "AmberPalaceActivity.java"        = @("Amber Palace",            "Jaipur")
    "CityPalaceActivity.java"         = @("City Palace",             "Jaipur")
    "JantarMantarActivity.java"       = @("Jantar Mantar",           "Jaipur")
    "JalMahalActivity.java"           = @("Jal Mahal",               "Jaipur")
    "JaigardhFortActivity.java"       = @("Jaigarh Fort",            "Jaipur")
    "AlbertHallActivity.java"         = @("Albert Hall Museum",      "Jaipur")
    "DestinationMain.java"            = @("Taj Mahal",               "Agra")
    "AgraFortActivity.java"           = @("Agra Fort",               "Agra")
    "TombOfAkbarActivity.java"        = @("Tomb of Akbar",           "Agra")
    "ItmadUdDaulaActivity.java"       = @("Itmad-ud-Daula",          "Agra")
    "ShahiJamaMasjidActivity.java"    = @("Shahi Jama Masjid",       "Agra")
    "BagaBeachActivity.java"          = @("Baga Beach",              "Goa")
    "PalolemBeachActivity.java"       = @("Palolem Beach",           "Goa")
    "ColvaBeachActivity.java"         = @("Colva Beach",             "Goa")
    "AnjunaMarketActivity.java"       = @("Anjuna Market",           "Goa")
    "BasilicaBomJesusActivity.java"   = @("Basilica of Bom Jesus",   "Goa")
    "DudhsagarFallsActivity.java"     = @("Dudhsagar Falls",         "Goa")
    "FontainhasActivity.java"         = @("Fontainhas",              "Goa")
    "AgondaBeachActivity.java"        = @("Agonda Beach",            "Goa")
    "KashiVishwanathActivity.java"    = @("Kashi Vishwanath Temple", "Varanasi")
    "DashashwamedhaGhatActivity.java" = @("Dashashwamedh Ghat",      "Varanasi")
    "AssiGhatActivity.java"           = @("Assi Ghat",               "Varanasi")
    "ManikarnikaGhatActivity.java"    = @("Manikarnika Ghat",        "Varanasi")
    "SarnathActivity.java"            = @("Sarnath",                 "Varanasi")
    "RamnagarFortActivity.java"       = @("Ramnagar Fort",           "Varanasi")
    "TulsiManasMandirActivity.java"   = @("Tulsi Manas Mandir",      "Varanasi")
    "DhamekStupaActivity.java"        = @("Dhamek Stupa",            "Varanasi")
    "MarinaBeachActivity.java"        = @("Marina Beach",            "Chennai")
    "ElliotsBeachActivity.java"       = @("Elliot's Beach",          "Chennai")
    "KapaleeshwararTempleActivity.java"= @("Kapaleeshwarar Temple",  "Chennai")
    "ParthasarathyTempleActivity.java"= @("Parthasarathy Temple",    "Chennai")
    "SanThomeChurchActivity.java"     = @("San Thome Church",        "Chennai")
    "SnowKingdomActivity.java"        = @("Snow Kingdom",            "Chennai")
}

foreach ($fileName in $fileMap.Keys) {
    $filePath = Join-Path $basePath $fileName
    if (!(Test-Path $filePath)) { Write-Host "MISSING: $fileName"; continue }

    $name = $fileMap[$fileName][0]
    $city = $fileMap[$fileName][1]
    $favCall = "        FavHelper.attachDestination(this, `"$name`", `"$city`");"

    # Already patched?
    $existing = Get-Content $filePath -Raw
    if ($existing -match [regex]::Escape($favCall)) {
        Write-Host "SKIP (already has exact call): $fileName"
        continue
    }

    # Read lines (no Raw - keeps line structure)
    $lines = Get-Content $filePath -Encoding UTF8

    # Remove any FavHelper import that was already (possibly wrongly) inserted
    $lines = $lines | Where-Object { $_ -notmatch 'import com\.myapplication\.matapp2\.FavHelper;' }

    # Find index of first "import " line and insert FavHelper import after it
    $firstImportIdx = -1
    for ($i = 0; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -match '^import ') { $firstImportIdx = $i; break }
    }

    $newLines = [System.Collections.Generic.List[string]]::new($lines)
    if ($firstImportIdx -ge 0) {
        $newLines.Insert($firstImportIdx, "import com.myapplication.matapp2.FavHelper;")
    }

    # Find the setContentView line and insert favCall after it
    $scvIdx = -1
    for ($i = 0; $i -lt $newLines.Count; $i++) {
        if ($newLines[$i] -match 'setContentView\(') { $scvIdx = $i; break }
    }

    if ($scvIdx -ge 0) {
        # Only insert if not already present right after it
        if ($newLines.Count -le $scvIdx + 1 -or $newLines[$scvIdx + 1] -notmatch 'FavHelper') {
            $newLines.Insert($scvIdx + 1, $favCall)
        }
    }

    # Write back WITHOUT BOM using StreamWriter
    $sw = New-Object System.IO.StreamWriter($filePath, $false, (New-Object System.Text.UTF8Encoding($false)))
    foreach ($line in $newLines) { $sw.WriteLine($line) }
    $sw.Close()
    Write-Host "PATCHED: $fileName"
}

Write-Host "Done."
