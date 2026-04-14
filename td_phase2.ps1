# ============================================================
# TD Phase 2: Code Migration & File Renaming
# Source:      Touristdestinations project (already td_-prefixed)
# Destination: MatApp22 master project
# New package:  com.myapplication.matapp2.td_pkg
# ============================================================

$tdSrc     = "D:\SoftwareData\AndroidStudioProjects\Touristdestinations\Touristdestinations\app\src\main"
$matDst    = "D:\SoftwareData\AndroidStudioProjects\MatApp22\app\src\main"

$tdRes     = "$tdSrc\res"
$matRes    = "$matDst\res"

$tdJava    = "$tdSrc\java\com\example\touristdestinations"
$matPkg    = "$matDst\java\com\myapplication\matapp2"
$tdPkgDst  = "$matPkg\td_pkg"

$OLD_PKG   = "package com.example.touristdestinations;"
$NEW_PKG   = "package com.myapplication.matapp2.td_pkg;"

# -----------------------------------------------------------------------
# STEP 1: Copy res/layout/ files -> MatApp22 res/layout/
# -----------------------------------------------------------------------
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 1: Copying res/layout/ XML files" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$srcLayout = "$tdRes\layout"
$dstLayout = "$matRes\layout"
if (Test-Path $srcLayout) {
    $files = Get-ChildItem $srcLayout -Filter "*.xml"
    foreach ($f in $files) {
        $dst = Join-Path $dstLayout $f.Name
        if (Test-Path $dst) {
            Write-Host "  [SKIP - already exists] $($f.Name)" -ForegroundColor DarkGray
        } else {
            Copy-Item $f.FullName $dst
            Write-Host "  [COPIED] $($f.Name)" -ForegroundColor Green
        }
    }
    Write-Host "  Layout: $($files.Count) files processed." -ForegroundColor Cyan
}

# -----------------------------------------------------------------------
# STEP 2: Copy res/drawable/ files -> MatApp22 res/drawable/
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 2: Copying res/drawable/ files" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$dstDrawable = "$matRes\drawable"
if (-not (Test-Path $dstDrawable)) { New-Item -ItemType Directory $dstDrawable | Out-Null }

$drawableDirs = Get-ChildItem $tdRes -Directory | Where-Object { $_.Name -like "drawable*" }
foreach ($drawableDir in $drawableDirs) {
    $dstDir = "$matRes\$($drawableDir.Name)"
    if (-not (Test-Path $dstDir)) {
        New-Item -ItemType Directory $dstDir | Out-Null
        Write-Host "  Created dir: $($drawableDir.Name)" -ForegroundColor Yellow
    }
    $files = Get-ChildItem $drawableDir.FullName -File
    foreach ($f in $files) {
        # Skip mipmap launcher icons
        if ($f.Name -like "ic_launcher*") {
            Write-Host "  [SKIP launcher] $($f.Name)" -ForegroundColor DarkGray
            continue
        }
        $dst = Join-Path $dstDir $f.Name
        if (Test-Path $dst) {
            Write-Host "  [SKIP - exists] $($f.Name)" -ForegroundColor DarkGray
        } else {
            Copy-Item $f.FullName $dst
            Write-Host "  [COPIED] [$($drawableDir.Name)] $($f.Name)" -ForegroundColor Green
        }
    }
}

# -----------------------------------------------------------------------
# STEP 3: Copy res/xml/ files -> MatApp22 res/xml/
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 3: Copying res/xml/ files" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$srcXml = "$tdRes\xml"
$dstXml = "$matRes\xml"
if (Test-Path $srcXml) {
    $files = Get-ChildItem $srcXml -Filter "*.xml"
    foreach ($f in $files) {
        $dst = Join-Path $dstXml $f.Name
        if (Test-Path $dst) {
            Write-Host "  [SKIP - exists] $($f.Name)" -ForegroundColor DarkGray
        } else {
            Copy-Item $f.FullName $dst
            Write-Host "  [COPIED] $($f.Name)" -ForegroundColor Green
        }
    }
}

# -----------------------------------------------------------------------
# STEP 4: Merge res/values/ XML files into MatApp22 res/values/
# Strategy: Append the <resources> body of each TD values file
#           into MatApp22's matching file (without duplicating the wrapper).
#           colors.xml, strings.xml -> append entries inside <resources>
#           themes.xml -> append <style> entries inside <resources>
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 4: Merging res/values/ XML files" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

function Merge-ValuesXml {
    param(
        [string]$SrcFile,
        [string]$DstFile,
        [string]$Label
    )
    if (-not (Test-Path $SrcFile)) {
        Write-Host "  [SKIP] Source not found: $SrcFile" -ForegroundColor DarkGray
        return
    }

    $srcContent = Get-Content $SrcFile -Raw -Encoding UTF8
    $dstContent = Get-Content $DstFile -Raw -Encoding UTF8

    # Extract everything BETWEEN <resources...> and </resources>
    # (handles optional attributes on <resources xmlns:...>)
    $innerMatch = [regex]::Match($srcContent, '(?s)<resources[^>]*>(.*?)</resources>')
    if (-not $innerMatch.Success) {
        Write-Host "  [ERROR] Could not parse inner content of $SrcFile" -ForegroundColor Red
        return
    }
    $innerContent = $innerMatch.Groups[1].Value.Trim()
    if ([string]::IsNullOrWhiteSpace($innerContent)) {
        Write-Host "  [SKIP - empty content] $Label" -ForegroundColor DarkGray
        return
    }

    # Check if already merged (idempotency guard - check first td_ entry)
    $firstEntry = ($innerContent -split "`n")[0].Trim()
    if ($dstContent -match [regex]::Escape($firstEntry.Substring(0, [Math]::Min(40, $firstEntry.Length)))) {
        Write-Host "  [SKIP - already merged] $Label" -ForegroundColor DarkGray
        return
    }

    # Insert before </resources> in destination
    $separator = "`n`n    <!-- ==================== td_ (TouristDestinations) ==================== -->`n"
    $newContent = $dstContent -replace '</resources>', "$separator    $($innerContent -replace "`n", "`n    ")`n</resources>"

    Set-Content $DstFile $newContent -Encoding UTF8 -NoNewline
    Write-Host "  [MERGED] $Label -> $DstFile" -ForegroundColor Green
}

$matValues = "$matRes\values"
$tdValues  = "$tdRes\values"

Merge-ValuesXml "$tdValues\colors.xml"  "$matValues\colors.xml"  "colors.xml"
Merge-ValuesXml "$tdValues\strings.xml" "$matValues\strings.xml" "strings.xml"
Merge-ValuesXml "$tdValues\themes.xml"  "$matValues\themes.xml"  "themes.xml"

# -----------------------------------------------------------------------
# STEP 5: Merge res/values-night/ XML files
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 5: Merging res/values-night/ XML files" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$matValuesNight = "$matRes\values-night"
$tdValuesNight  = "$tdRes\values-night"

if (-not (Test-Path $matValuesNight)) {
    New-Item -ItemType Directory $matValuesNight | Out-Null
}

Merge-ValuesXml "$tdValuesNight\themes.xml" "$matValuesNight\themes.xml" "values-night/themes.xml"

# -----------------------------------------------------------------------
# STEP 6: Create td_pkg directory and copy Java files
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 6: Copying Java files to td_pkg" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

if (-not (Test-Path $tdPkgDst)) {
    New-Item -ItemType Directory -Path $tdPkgDst | Out-Null
    Write-Host "  Created package dir: $tdPkgDst" -ForegroundColor Yellow
}

$javaFiles = Get-ChildItem $tdJava -Filter "*.java"
foreach ($f in $javaFiles) {
    $dst = Join-Path $tdPkgDst $f.Name
    Copy-Item $f.FullName $dst -Force
    Write-Host "  [COPIED] $($f.Name)" -ForegroundColor Green
}
Write-Host "  Total Java files copied: $($javaFiles.Count)" -ForegroundColor Cyan

# -----------------------------------------------------------------------
# STEP 7: Update package declarations in all copied Java files
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 7: Updating package declarations in td_pkg Java files" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$copiedJava = Get-ChildItem $tdPkgDst -Filter "*.java"
foreach ($f in $copiedJava) {
    $content = Get-Content $f.FullName -Raw -Encoding UTF8
    if ($content -match [regex]::Escape($OLD_PKG)) {
        $content = $content -replace [regex]::Escape($OLD_PKG), $NEW_PKG
        Set-Content $f.FullName $content -Encoding UTF8 -NoNewline
        Write-Host "  [PKG UPDATED] $($f.Name)" -ForegroundColor Green
    } else {
        Write-Host "  [SKIP - pkg not found] $($f.Name)" -ForegroundColor DarkGray
    }
}

# -----------------------------------------------------------------------
# STEP 8: Rename MainActivity.java -> DestinationMain.java
#         AND update the class declaration inside it
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 8: Renaming MainActivity.java -> DestinationMain.java" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$mainSrc = Join-Path $tdPkgDst "MainActivity.java"
$mainDst = Join-Path $tdPkgDst "DestinationMain.java"

if (Test-Path $mainSrc) {
    $content = Get-Content $mainSrc -Raw -Encoding UTF8
    # Update class declaration: public class MainActivity -> public class DestinationMain
    $content = $content -replace '\bpublic class MainActivity\b', 'public class DestinationMain'
    Set-Content $mainDst $content -Encoding UTF8 -NoNewline
    Remove-Item $mainSrc
    Write-Host "  [RENAMED] MainActivity.java -> DestinationMain.java" -ForegroundColor Green
    Write-Host "  [CLASS]   public class MainActivity -> public class DestinationMain" -ForegroundColor Green
} elseif (Test-Path $mainDst) {
    Write-Host "  [SKIP] DestinationMain.java already exists." -ForegroundColor DarkGray
} else {
    Write-Host "  [ERROR] MainActivity.java not found in $tdPkgDst" -ForegroundColor Red
}

# -----------------------------------------------------------------------
# VERIFICATION REPORT
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Verification Summary" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

Write-Host "`nLayout files copied to MatApp22 (td_ prefixed):" -ForegroundColor White
Get-ChildItem "$matRes\layout" -Filter "td_*.xml" | ForEach-Object { Write-Host "  $($_.Name)" }

Write-Host "`nJava files in td_pkg:" -ForegroundColor White
Get-ChildItem $tdPkgDst -Filter "*.java" | ForEach-Object { Write-Host "  $($_.Name)" }

Write-Host "`nPackage declaration in DestinationMain.java:" -ForegroundColor White
Get-Content "$tdPkgDst\DestinationMain.java" | Select-Object -First 2 | ForEach-Object { Write-Host "  $_" }

Write-Host "`nClass declaration in DestinationMain.java:" -ForegroundColor White
Select-String -Path "$tdPkgDst\DestinationMain.java" -Pattern "public class" | ForEach-Object { Write-Host "  $($_.Line.Trim())" }

Write-Host ""
Write-Host "=======================================" -ForegroundColor Green
Write-Host " Phase 2 COMPLETE!" -ForegroundColor Green
Write-Host "=======================================" -ForegroundColor Green
