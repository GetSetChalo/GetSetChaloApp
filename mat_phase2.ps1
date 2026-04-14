<#
  MAT_PROJECT Phase 2: Code Migration into MatApp22
  - Copies renamed res files (layout, drawable, font) → no name conflicts
  - Smart-merges values XML files (colors, strings, themes, styles, ids)
  - Copies Java files into new sub-package mat_project_pkg and updates package declaration
#>

$ErrorActionPreference = "Stop"

$SRC_ROOT  = "D:\SoftwareData\AndroidStudioProjects\MAT_PROJECT\MAT_PROJECT\app\src\main"
$DST_ROOT  = "D:\SoftwareData\AndroidStudioProjects\MatApp22\app\src\main"
$SRC_PKG   = "com.example.mat_project"
$DST_PKG   = "com.myapplication.matapp2.mat_project_pkg"
$DST_JAVA  = "$DST_ROOT\java\com\myapplication\matapp2\mat_project_pkg"
$issues    = 0

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  MAT_PROJECT Phase 2: Code Migration" -ForegroundColor Cyan
Write-Host "============================================================`n"

# ─────────────────────────────────────────────────────────────────────────────
# HELPER: Smart-merge a MAT_PROJECT values XML into MatApp22's equivalent file
# Inserts each child element from src into dst, skipping if name= already exists
# ─────────────────────────────────────────────────────────────────────────────
function Merge-ValuesXml($srcPath, $dstPath) {
    $srcContent = Get-Content $srcPath -Raw -Encoding UTF8
    $dstContent = Get-Content $dstPath -Raw -Encoding UTF8

    # Extract all inner elements (lines between <resources> and </resources>)
    $srcInner = [regex]::Match($srcContent, '(?s)<resources[^>]*>(.*?)</resources>').Groups[1].Value
    $dstInner = [regex]::Match($dstContent, '(?s)<resources[^>]*>(.*?)</resources>').Groups[1].Value

    # Get each top-level element block from src (handles multi-line <style> blocks)
    $srcElements = [regex]::Matches($srcInner, '(?s)<(?!!)(\w+)[^>]*?(?:/>|>.*?</\1>)')

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
# STEP 1: Copy layout files (all mat_*.xml, skip activity_main.xml)
# ─────────────────────────────────────────────────────────────────────────────
Write-Host "--- STEP 1: Copying layout files ---" -ForegroundColor Yellow
$srcLayout = "$SRC_ROOT\res\layout"
$dstLayout = "$DST_ROOT\res\layout"
Get-ChildItem $srcLayout -Filter "*.xml" -File | Where-Object { $_.Name -ne "activity_main.xml" } | ForEach-Object {
    $dst = Join-Path $dstLayout $_.Name
    Copy-Item $_.FullName $dst -Force
    Write-Host "  Copied: $($_.Name)" -ForegroundColor Green
}

# ─────────────────────────────────────────────────────────────────────────────
# STEP 2: Copy drawable files (all mat_*)
# ─────────────────────────────────────────────────────────────────────────────
Write-Host "`n--- STEP 2: Copying drawable files ---" -ForegroundColor Yellow
$srcDrawable = "$SRC_ROOT\res\drawable"
$dstDrawable = "$DST_ROOT\res\drawable"
Get-ChildItem $srcDrawable -File | Where-Object { $_.Name -ne ".DS_Store" } | ForEach-Object {
    $dst = Join-Path $dstDrawable $_.Name
    Copy-Item $_.FullName $dst -Force
    Write-Host "  Copied: $($_.Name)" -ForegroundColor Green
}

# ─────────────────────────────────────────────────────────────────────────────
# STEP 3: Copy font files
# ─────────────────────────────────────────────────────────────────────────────
Write-Host "`n--- STEP 3: Copying font files ---" -ForegroundColor Yellow
$srcFont = "$SRC_ROOT\res\font"
$dstFont = "$DST_ROOT\res\font"
if (-not (Test-Path $dstFont)) { New-Item -ItemType Directory -Path $dstFont | Out-Null }
Get-ChildItem $srcFont -File | ForEach-Object {
    $dst = Join-Path $dstFont $_.Name
    Copy-Item $_.FullName $dst -Force
    Write-Host "  Copied: $($_.Name)" -ForegroundColor Green
}

# ─────────────────────────────────────────────────────────────────────────────
# STEP 4: Smart-merge values XML files
# ─────────────────────────────────────────────────────────────────────────────
Write-Host "`n--- STEP 4: Merging values/ XML files ---" -ForegroundColor Yellow

# colors.xml
Merge-ValuesXml "$SRC_ROOT\res\values\colors.xml" "$DST_ROOT\res\values\colors.xml"

# strings.xml
Merge-ValuesXml "$SRC_ROOT\res\values\strings.xml" "$DST_ROOT\res\values\strings.xml"

# themes.xml
Merge-ValuesXml "$SRC_ROOT\res\values\themes.xml" "$DST_ROOT\res\values\themes.xml"

# styles.xml
Merge-ValuesXml "$SRC_ROOT\res\values\styles.xml" "$DST_ROOT\res\values\styles.xml"

# ids.xml — MatApp22 may not have one; if not, just copy it
$dstIds = "$DST_ROOT\res\values\ids.xml"
if (-not (Test-Path $dstIds)) {
    Copy-Item "$SRC_ROOT\res\values\ids.xml" $dstIds -Force
    Write-Host "  Copied ids.xml (new file)" -ForegroundColor Green
} else {
    Merge-ValuesXml "$SRC_ROOT\res\values\ids.xml" $dstIds
}

# ─────────────────────────────────────────────────────────────────────────────
# STEP 5: Smart-merge values-night/themes.xml
# ─────────────────────────────────────────────────────────────────────────────
Write-Host "`n--- STEP 5: Merging values-night/ XML files ---" -ForegroundColor Yellow
Merge-ValuesXml "$SRC_ROOT\res\values-night\themes.xml" "$DST_ROOT\res\values-night\themes.xml"

# ─────────────────────────────────────────────────────────────────────────────
# STEP 6: Copy Java files → mat_project_pkg, fix package declaration
# ─────────────────────────────────────────────────────────────────────────────
Write-Host "`n--- STEP 6: Migrating Java files to mat_project_pkg ---" -ForegroundColor Yellow
if (-not (Test-Path $DST_JAVA)) {
    New-Item -ItemType Directory -Path $DST_JAVA | Out-Null
    Write-Host "  Created package dir: $DST_JAVA" -ForegroundColor Green
}

$srcJava = "$SRC_ROOT\java\com\example\mat_project"
Get-ChildItem $srcJava -Filter "*.java" -File | Where-Object { $_.Name -ne "MainActivity.java" } | ForEach-Object {
    $content = Get-Content $_.FullName -Raw -Encoding UTF8

    # Update package declaration
    $content = $content -replace "package\s+$([regex]::Escape($SRC_PKG))\s*;", "package $DST_PKG;"

    # Also fix any fully-qualified class references if present
    $content = $content -replace [regex]::Escape($SRC_PKG), $DST_PKG

    $dstFile = Join-Path $DST_JAVA $_.Name
    Set-Content -Path $dstFile -Value $content -Encoding UTF8 -NoNewline
    Write-Host "  Migrated: $($_.Name)  [package → $DST_PKG]" -ForegroundColor Green
}

# ─────────────────────────────────────────────────────────────────────────────
# DONE
# ─────────────────────────────────────────────────────────────────────────────
Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host "  Phase 2 COMPLETE" -ForegroundColor Green
Write-Host "============================================================"
Write-Host "`nSummary:"
Write-Host "  STEP 1 : layout/mat_*.xml     → MatApp22/res/layout/"
Write-Host "  STEP 2 : drawable/mat_*       → MatApp22/res/drawable/"
Write-Host "  STEP 3 : font/mat_*           → MatApp22/res/font/"
Write-Host "  STEP 4 : values/*.xml         → smart-merged into MatApp22/res/values/"
Write-Host "  STEP 5 : values-night/*.xml   → smart-merged into MatApp22/res/values-night/"
Write-Host "  STEP 6 : Java files           → MatApp22/.../mat_project_pkg/ (package updated)"
