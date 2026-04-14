<#
  Tourismapp Phase 2: Code Migration into MatApp22
  - Copies renamed res files (layout, drawable, font, xml) → no name conflicts
  - Smart-merges values XML files (colors, strings, themes, font_certs, preloaded_fonts)
  - Copies Java files into new sub-package ta_pkg and updates package declaration
#>

$ErrorActionPreference = "Stop"

$SRC_ROOT  = "D:\SoftwareData\AndroidStudioProjects\Tourismapp\Tourismapp\app\src\main"
$DST_ROOT  = "D:\SoftwareData\AndroidStudioProjects\MatApp22\app\src\main"
$SRC_PKG   = "com.example.tourismapp"
$DST_PKG   = "com.myapplication.matapp2.ta_pkg"
$DST_JAVA  = "$DST_ROOT\java\com\myapplication\matapp2\ta_pkg"

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Tourismapp Phase 2: Code Migration" -ForegroundColor Cyan
Write-Host "============================================================`n"

# ─────────────────────────────────────────────────────────────────────────────
# HELPER: Smart-merge a XML into MatApp22's equivalent file
# ─────────────────────────────────────────────────────────────────────────────
function Merge-ValuesXml($srcPath, $dstPath) {
    if (-not (Test-Path $srcPath)) {
        Write-Host "  SKIP (src not found): $(Split-Path $srcPath -Leaf)" -ForegroundColor DarkGray
        return
    }

    if (-not (Test-Path $dstPath)) {
        # Destination doesn't exist, just copy it directly
        Copy-Item $srcPath $dstPath -Force
        Write-Host "  Copied new values file: $(Split-Path $srcPath -Leaf)" -ForegroundColor Green
        return
    }

    $srcContent = Get-Content $srcPath -Raw -Encoding UTF8
    $dstContent = Get-Content $dstPath -Raw -Encoding UTF8

    # Extract all inner elements (lines between <resources> and </resources>)
    $srcInnerMatch = [regex]::Match($srcContent, '(?s)<resources[^>]*>(.*?)</resources>')
    if (-not $srcInnerMatch.Success) { return }
    $srcInner = $srcInnerMatch.Groups[1].Value
    
    $dstInnerMatch = [regex]::Match($dstContent, '(?s)<resources[^>]*>(.*?)</resources>')
    if (-not $dstInnerMatch.Success) {
         # If destination is completely unformatted, just append inside
         $dstInner = ""
    } else {
         $dstInner = $dstInnerMatch.Groups[1].Value
    }

    # Get each top-level element block from src
    $srcElements = [regex]::Matches($srcInner, '(?s)<(?!!)(\w+-\w+|\w+)[^>]*?(?:/>|>.*?</\1>)')

    $added = 0
    $skipped = 0
    $newInner = $dstInner

    foreach ($elem in $srcElements) {
        $elemText = $elem.Value.Trim()
        # Extract the name attribute value
        $nameMatch = [regex]::Match($elemText, 'name="([^"]+)"')
        if (-not $nameMatch.Success) { continue }
        $nameVal = $nameMatch.Groups[1].Value

        # Check if this name already exists in dst
        if ($dstContent -match [regex]::Escape("name=""$nameVal""")) {
            $skipped++
            Write-Host "    SKIP (already exists): name=`"$nameVal`"" -ForegroundColor DarkGray
        } else {
            # Append element before </resources>
            $newInner = $newInner.TrimEnd() + "`r`n    $elemText`r`n"
            $added++
            Write-Host "    ADDED: name=`"$nameVal`"" -ForegroundColor Green
        }
    }

    # Rebuild dst with merged inner content
    $newDstContent = [regex]::Replace($dstContent,
        '(?s)(<resources[^>]*>)(.*?)(</resources>)',
        { param($m) $m.Groups[1].Value + $newInner + $m.Groups[3].Value }
    )

    Set-Content -Path $dstPath -Value $newDstContent -Encoding UTF8 -NoNewline
    Write-Host "  Merged $(Split-Path $srcPath -Leaf) into MatApp22 → +$added added, $skipped skipped" -ForegroundColor Cyan
}

# ─────────────────────────────────────────────────────────────────────────────
# STEP 1: Copy static res files (layout, drawable, font, xml)
# ─────────────────────────────────────────────────────────────────────────────
Write-Host "--- STEP 1: Copying res files (layout, drawable, font, xml) ---" -ForegroundColor Yellow
$resFolders = @("layout", "drawable", "font", "xml")

foreach ($folder in $resFolders) {
    $srcFolder = Join-Path $SRC_ROOT "res\$folder"
    $dstFolder = Join-Path $DST_ROOT "res\$folder"
    
    if (-not (Test-Path $srcFolder)) { continue }
    
    if (-not (Test-Path $dstFolder)) {
        New-Item -ItemType Directory -Path $dstFolder | Out-Null
    }

    Get-ChildItem $srcFolder -File | Where-Object { $_.Name -ne ".DS_Store" } | ForEach-Object {
        $dst = Join-Path $dstFolder $_.Name
        Copy-Item $_.FullName $dst -Force
        Write-Host "  Copied $folder/: $($_.Name)" -ForegroundColor Green
    }
}

# ─────────────────────────────────────────────────────────────────────────────
# STEP 2: Smart-merge values and values-night XML files
# ─────────────────────────────────────────────────────────────────────────────
Write-Host "`n--- STEP 2: Merging values/ and values-night/ XML files ---" -ForegroundColor Yellow

# values/ files
$valuesSrcPath = "$SRC_ROOT\res\values"
if (Test-Path $valuesSrcPath) {
    Get-ChildItem $valuesSrcPath -Filter "*.xml" -File | ForEach-Object {
        Merge-ValuesXml $_.FullName "$DST_ROOT\res\values\$($_.Name)"
    }
}

# values-night/ files
$valuesNightSrcPath = "$SRC_ROOT\res\values-night"
if (Test-Path $valuesNightSrcPath) {
    if (-not (Test-Path "$DST_ROOT\res\values-night")) {
        New-Item -ItemType Directory -Path "$DST_ROOT\res\values-night" | Out-Null
    }
    Get-ChildItem $valuesNightSrcPath -Filter "*.xml" -File | ForEach-Object {
        Merge-ValuesXml $_.FullName "$DST_ROOT\res\values-night\$($_.Name)"
    }
}


# ─────────────────────────────────────────────────────────────────────────────
# STEP 3: Copy Java files → ta_pkg, fix package declaration
# ─────────────────────────────────────────────────────────────────────────────
Write-Host "`n--- STEP 3: Migrating Java files to ta_pkg ---" -ForegroundColor Yellow
if (-not (Test-Path $DST_JAVA)) {
    New-Item -ItemType Directory -Path $DST_JAVA | Out-Null
    Write-Host "  Created package dir: $DST_JAVA" -ForegroundColor Green
}

$srcJava = "$SRC_ROOT\java\com\example\tourismapp"
if (Test-Path $srcJava) {
    Get-ChildItem $srcJava -Filter "*.java" -File | ForEach-Object {
        $content = Get-Content $_.FullName -Raw -Encoding UTF8

        # Update package declaration
        $content = $content -replace "package\s+$([regex]::Escape($SRC_PKG))\s*;", "package $DST_PKG;"

        # Also fix any fully-qualified class references if present
        $content = $content -replace [regex]::Escape($SRC_PKG), $DST_PKG

        $dstFile = Join-Path $DST_JAVA $_.Name
        Set-Content -Path $dstFile -Value $content -Encoding UTF8 -NoNewline
        Write-Host "  Migrated: $($_.Name)  [package → $DST_PKG]" -ForegroundColor Green
    }
}

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host "  Phase 2 COMPLETE" -ForegroundColor Green
Write-Host "============================================================"
