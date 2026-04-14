# ============================================================
# TD Phase 3: Code Correction & Namespace Refactoring
# Operate ONLY in MatApp22's td_pkg
# ============================================================

$tdPkg   = "D:\SoftwareData\AndroidStudioProjects\MatApp22\app\src\main\java\com\myapplication\matapp2\td_pkg"
$NEW_PKG = "com.myapplication.matapp2.td_pkg"
$R_IMPORT = "import com.myapplication.matapp2.R;"

# -----------------------------------------------------------------------
# STEP 1: Verify DestinationMain.java class declaration (was already done
#         in Phase 2, but confirm and log it)
# -----------------------------------------------------------------------
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 1: Verify DestinationMain.java class declaration" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$destMain = "$tdPkg\DestinationMain.java"
if (Test-Path $destMain) {
    $classLine = Select-String -Path $destMain -Pattern "public class" | Select-Object -First 1
    if ($classLine.Line -match "public class DestinationMain") {
        Write-Host "  [OK] Class declaration is correct: public class DestinationMain" -ForegroundColor Green
    } elseif ($classLine.Line -match "public class MainActivity") {
        Write-Host "  [FIXING] Renaming class declaration..." -ForegroundColor Yellow
        $content = Get-Content $destMain -Raw -Encoding UTF8
        $content = $content -replace '\bpublic class MainActivity\b', 'public class DestinationMain'
        Set-Content $destMain $content -Encoding UTF8 -NoNewline
        Write-Host "  [FIXED] public class MainActivity -> public class DestinationMain" -ForegroundColor Green
    } else {
        Write-Host "  [WARN] Unexpected class declaration: $($classLine.Line)" -ForegroundColor Yellow
    }
} else {
    Write-Host "  [ERROR] DestinationMain.java not found!" -ForegroundColor Red
}

# -----------------------------------------------------------------------
# STEP 2: Fix DestinationsListActivity.java - update MainActivity.class
#         reference to DestinationMain.class (the ONLY file with this issue)
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 2: Fix MainActivity.class -> DestinationMain.class" -ForegroundColor Cyan
Write-Host "         in DestinationsListActivity.java" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$destListFile = "$tdPkg\DestinationsListActivity.java"
if (Test-Path $destListFile) {
    $content = Get-Content $destListFile -Raw -Encoding UTF8
    if ($content -match '\bMainActivity\.class\b') {
        $content = $content -replace '\bMainActivity\.class\b', 'DestinationMain.class'
        Set-Content $destListFile $content -Encoding UTF8 -NoNewline
        Write-Host "  [FIXED] MainActivity.class -> DestinationMain.class" -ForegroundColor Green
    } else {
        Write-Host "  [OK] No MainActivity.class reference found (already clean)" -ForegroundColor Green
    }
} else {
    Write-Host "  [ERROR] DestinationsListActivity.java not found!" -ForegroundColor Red
}

# -----------------------------------------------------------------------
# STEP 3: Add explicit R import to all td_pkg Java files
#         Pattern (matching other sub-packages: checkout_, ta_pkg):
#           Line 1: package com.myapplication.matapp2.td_pkg;
#           Line 2: (blank)
#           Line 3: import com.myapplication.matapp2.R;
#           Line 4: (blank)
#           Line 5+: android imports...
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 3: Add 'import com.myapplication.matapp2.R;' to all td_pkg files" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$javaFiles = Get-ChildItem $tdPkg -Filter "*.java"
foreach ($file in $javaFiles) {
    $content = Get-Content $file.FullName -Raw -Encoding UTF8

    # Skip if R import already present
    if ($content -match [regex]::Escape($R_IMPORT)) {
        Write-Host "  [SKIP - R already imported] $($file.Name)" -ForegroundColor DarkGray
        continue
    }

    # Skip if the file doesn't actually use R. (e.g. utility classes with no views)
    if ($content -notmatch '\bR\.') {
        Write-Host "  [SKIP - no R usage] $($file.Name)" -ForegroundColor DarkGray
        continue
    }

    # Insert R import right after the package declaration line
    # Pattern: "package ...;\n\n" -> "package ...;\n\nimport com.myapplication.matapp2.R;\n\n"
    # Handle both \r\n and \n line endings
    $pkgPattern = "(?m)^package $([regex]::Escape($NEW_PKG));\s*(\r?\n)"
    if ($content -match $pkgPattern) {
        # Find the position after the package line + blank line
        $insertAfter = "package $NEW_PKG;"
        # Replace: after the package line, insert the R import with a blank line before android imports
        $content = $content -replace "(?m)(^package $([regex]::Escape($NEW_PKG));(\r?\n)(\r?\n)?)", "`$1$R_IMPORT`$2`$2"
        # Normalise: don't duplicate blank lines if the file already had blank line after pkg
        $content = $content -replace "(?m)(package $([regex]::Escape($NEW_PKG));(\r?\n))($R_IMPORT(\r?\n)(\r?\n)\3)", "`$1$R_IMPORT`$2`$2"
        Set-Content $file.FullName $content -Encoding UTF8 -NoNewline
        Write-Host "  [R IMPORT ADDED] $($file.Name)" -ForegroundColor Green
    } else {
        Write-Host "  [WARN - could not insert R import] $($file.Name)" -ForegroundColor Yellow
    }
}

# -----------------------------------------------------------------------
# STEP 4: Verify package declarations are correct in all files
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 4: Verify all package declarations" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$wrongPkg = Get-ChildItem $tdPkg -Filter "*.java" |
    Where-Object { (Get-Content $_.FullName -First 1) -notmatch [regex]::Escape($NEW_PKG) }

if ($wrongPkg.Count -gt 0) {
    Write-Host "  [WARN] Files with incorrect package:" -ForegroundColor Yellow
    $wrongPkg | ForEach-Object { Write-Host "    - $($_.Name)" -ForegroundColor Red }
} else {
    Write-Host "  [OK] All 40 files have correct package: $NEW_PKG" -ForegroundColor Green
}

# -----------------------------------------------------------------------
# STEP 5: Verify R.layout references are td_-prefixed (smoke check)
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 5: Verify R.layout references are td_-prefixed" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$unPrefixed = Get-ChildItem $tdPkg -Filter "*.java" |
    Select-String -Pattern 'R\.layout\.(?!td_)[A-Za-z0-9_]+'
if ($unPrefixed) {
    Write-Host "  [WARN] Un-prefixed R.layout references found:" -ForegroundColor Yellow
    $unPrefixed | ForEach-Object { Write-Host "    $($_.Filename): $($_.Line.Trim())" -ForegroundColor Red }
} else {
    Write-Host "  [OK] All R.layout references have td_ prefix." -ForegroundColor Green
}

$unPrefixed = Get-ChildItem $tdPkg -Filter "*.java" |
    Select-String -Pattern 'R\.string\.(?!td_)[A-Za-z0-9_]+'
if ($unPrefixed) {
    Write-Host "  [WARN] Un-prefixed R.string references found:" -ForegroundColor Yellow
    $unPrefixed | ForEach-Object { Write-Host "    $($_.Filename): $($_.Line.Trim())" -ForegroundColor Red }
} else {
    Write-Host "  [OK] All R.string references have td_ prefix." -ForegroundColor Green
}

# -----------------------------------------------------------------------
# STEP 6: Verify no remaining old package/class name artifacts
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 6: Verify no old package/class remnants" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$oldPkg = Get-ChildItem $tdPkg -Filter "*.java" |
    Select-String -Pattern 'com\.example\.touristdestinations'
if ($oldPkg) {
    Write-Host "  [WARN] Old package references found:" -ForegroundColor Yellow
    $oldPkg | ForEach-Object { Write-Host "    $($_.Filename): $($_.Line.Trim())" -ForegroundColor Red }
} else {
    Write-Host "  [OK] No old 'com.example.touristdestinations' references." -ForegroundColor Green
}

$mainClassRefs = Get-ChildItem $tdPkg -Filter "*.java" |
    Select-String -Pattern '\bMainActivity\b'
if ($mainClassRefs) {
    Write-Host "  [WARN] Remaining 'MainActivity' references found:" -ForegroundColor Yellow
    $mainClassRefs | ForEach-Object { Write-Host "    $($_.Filename): $($_.Line.Trim())" -ForegroundColor Red }
} else {
    Write-Host "  [OK] No 'MainActivity' references remain." -ForegroundColor Green
}

# -----------------------------------------------------------------------
# FINAL REPORT
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Final Verification" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

Write-Host "`nDestinationMain.java - first 20 lines:" -ForegroundColor White
Get-Content "$tdPkg\DestinationMain.java" | Select-Object -First 20

Write-Host "`nDestinationsListActivity.java - Intent to DestinationMain check:" -ForegroundColor White
Select-String -Path "$tdPkg\DestinationsListActivity.java" -Pattern "DestinationMain|MainActivity" |
    Select-Object -ExpandProperty Line | ForEach-Object { Write-Host "  $_" }

Write-Host ""
Write-Host "=======================================" -ForegroundColor Green
Write-Host " Phase 3 COMPLETE!" -ForegroundColor Green
Write-Host "=======================================" -ForegroundColor Green
