# ============================================================
# TD Phase 0 + Phase 1: Resource Isolation Script
# Target: Touristdestinations project
# Prefix: td_
# ============================================================

$tdProject = "D:\SoftwareData\AndroidStudioProjects\Touristdestinations\Touristdestinations"
$tdRoot    = "$tdProject\app\src\main"
$resBase   = "$tdRoot\res"
$javaBase  = "$tdRoot\java"
$manifest  = "$tdRoot\AndroidManifest.xml"
$PREFIX    = "td_"

# -----------------------------------------------------------------------
# PHASE 0: Exclude _MACOSX
# -----------------------------------------------------------------------
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " TD Phase 0: Exclude _MACOSX folders" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$macosxDirs = Get-ChildItem -Path "D:\SoftwareData\AndroidStudioProjects\Touristdestinations" `
    -Recurse -Directory -Filter "_MACOSX" -ErrorAction SilentlyContinue
if ($macosxDirs -and $macosxDirs.Count -gt 0) {
    foreach ($dir in $macosxDirs) {
        Write-Host "Removing _MACOSX dir: $($dir.FullName)" -ForegroundColor Yellow
        Remove-Item $dir.FullName -Recurse -Force
    }
    Write-Host "_MACOSX folders removed." -ForegroundColor Green
} else {
    Write-Host "No _MACOSX directories found. Skipping." -ForegroundColor Green
}

# -----------------------------------------------------------------------
# PHASE 1, STEP 1: Prefix name= attributes in res/values/ & res/values-night/
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 1: Prefix name= in values XML files" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$valuesDirs = @("$resBase\values", "$resBase\values-night")
foreach ($dir in $valuesDirs) {
    if (-not (Test-Path $dir)) { continue }
    foreach ($file in (Get-ChildItem $dir -Filter "*.xml")) {
        Write-Host "  Processing: $($file.Name)" -ForegroundColor Yellow
        $content = Get-Content $file.FullName -Raw -Encoding UTF8

        # Prefix all name="xxx" attributes that don't already start with td_
        # Also: prefix parent="xxx" in <style parent="Base.Theme.TouristDestinations"> etc.
        # We do NOT prefix external parents (Theme.Material3.*, android:*, etc.)
        $content = $content -replace '(?<=\bname=")(?!td_)', $PREFIX

        # Internal style parent references (e.g., parent="Base.Theme.TouristDestinations")
        # These are local style names that will be renamed. Update them too.
        $content = $content -replace 'parent="Base\.Theme\.TouristDestinations"', "parent=`"${PREFIX}Base.Theme.TouristDestinations`""
        $content = $content -replace 'parent="Theme\.TouristDestinations"',      "parent=`"${PREFIX}Theme.TouristDestinations`""

        Set-Content $file.FullName $content -Encoding UTF8 -NoNewline
        Write-Host "  Done." -ForegroundColor Green
    }
}

# -----------------------------------------------------------------------
# PHASE 1, STEP 2: Rename layout XML files
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 2a: Rename layout files with $PREFIX prefix" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$layoutDir = "$resBase\layout"
if (Test-Path $layoutDir) {
    $filesToRename = Get-ChildItem $layoutDir -Filter "*.xml" | Where-Object { $_.Name -notlike "${PREFIX}*" }
    foreach ($file in $filesToRename) {
        $newName = "$PREFIX$($file.Name)"
        Write-Host "  $($file.Name) -> $newName" -ForegroundColor Yellow
        Rename-Item $file.FullName (Join-Path $file.DirectoryName $newName)
    }
}

# -----------------------------------------------------------------------
# PHASE 1, STEP 3: Rename drawable files
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 2b: Rename drawable files with $PREFIX prefix" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$drawableDirs = Get-ChildItem $resBase -Directory | Where-Object { $_.Name -like "drawable*" }
foreach ($drawableDir in $drawableDirs) {
    $filesToRename = Get-ChildItem $drawableDir.FullName -File |
        Where-Object { $_.Name -notlike "${PREFIX}*" -and $_.Name -notlike "ic_launcher*" }
    foreach ($file in $filesToRename) {
        $newName = "$PREFIX$($file.Name)"
        Write-Host "  [$($drawableDir.Name)] $($file.Name) -> $newName" -ForegroundColor Yellow
        Rename-Item $file.FullName (Join-Path $file.DirectoryName $newName)
    }
}

# -----------------------------------------------------------------------
# PHASE 1, STEP 4: Rename res/xml/ files
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 2c: Rename res/xml/ files with $PREFIX prefix" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$xmlResDir = "$resBase\xml"
if (Test-Path $xmlResDir) {
    $filesToRename = Get-ChildItem $xmlResDir -Filter "*.xml" | Where-Object { $_.Name -notlike "${PREFIX}*" }
    foreach ($file in $filesToRename) {
        $newName = "$PREFIX$($file.Name)"
        Write-Host "  $($file.Name) -> $newName" -ForegroundColor Yellow
        Rename-Item $file.FullName (Join-Path $file.DirectoryName $newName)
    }
}

# -----------------------------------------------------------------------
# PHASE 1, STEP 5: Update all references in XML and Java files
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Step 3: Update all resource references" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

function Update-XmlReferences {
    param([string]$FilePath)
    $content = Get-Content $FilePath -Raw -Encoding UTF8
    $original = $content

    # @color/xxx  -> @color/td_xxx  (skip already-prefixed and android: system colors)
    $content = $content -replace '@color/(?!td_)(?!android:)([A-Za-z0-9_]+)', "@color/td_`$1"

    # @string/xxx -> @string/td_xxx
    $content = $content -replace '@string/(?!td_)([A-Za-z0-9_]+)', "@string/td_`$1"

    # @drawable/xxx -> @drawable/td_xxx (skip launcher icons and already prefixed)
    $content = $content -replace '@drawable/(?!td_)(?!ic_launcher)([A-Za-z0-9_]+)', "@drawable/td_`$1"

    # @layout/xxx -> @layout/td_xxx
    $content = $content -replace '@layout/(?!td_)([A-Za-z0-9_]+)', "@layout/td_`$1"

    # @xml/xxx -> @xml/td_xxx
    $content = $content -replace '@xml/(?!td_)([A-Za-z0-9_]+)', "@xml/td_`$1"

    # @style/Theme.TouristDestinations and @style/Base.Theme.TouristDestinations
    $content = $content -replace '@style/(?!td_)(Theme\.TouristDestinations|Base\.Theme\.TouristDestinations)', "@style/td_`$1"

    if ($content -ne $original) {
        Set-Content $FilePath $content -Encoding UTF8 -NoNewline
        Write-Host "  [XML Updated] $FilePath" -ForegroundColor Green
        return $true
    }
    return $false
}

function Update-JavaReferences {
    param([string]$FilePath)
    $content = Get-Content $FilePath -Raw -Encoding UTF8
    $original = $content

    # R.layout.xxx -> R.layout.td_xxx
    $content = $content -replace '(?<=R\.layout\.)(?!td_)([A-Za-z0-9_]+)', "td_`$1"

    # R.drawable.xxx -> R.drawable.td_xxx (skip ic_launcher*)
    $content = $content -replace '(?<=R\.drawable\.)(?!td_)(?!ic_launcher)([A-Za-z0-9_]+)', "td_`$1"

    # R.string.xxx -> R.string.td_xxx
    $content = $content -replace '(?<=R\.string\.)(?!td_)([A-Za-z0-9_]+)', "td_`$1"

    # R.color.xxx -> R.color.td_xxx
    $content = $content -replace '(?<=R\.color\.)(?!td_)([A-Za-z0-9_]+)', "td_`$1"

    # R.xml.xxx -> R.xml.td_xxx
    $content = $content -replace '(?<=R\.xml\.)(?!td_)([A-Za-z0-9_]+)', "td_`$1"

    # NOTE: R.id.xxx is NOT prefixed - view IDs in layouts are not resource file names

    if ($content -ne $original) {
        Set-Content $FilePath $content -Encoding UTF8 -NoNewline
        Write-Host "  [Java Updated] $FilePath" -ForegroundColor Green
        return $true
    }
    return $false
}

# 5a: Layout XML files (already renamed)
Write-Host "  Processing layout XML files..." -ForegroundColor Cyan
foreach ($file in (Get-ChildItem $layoutDir -Filter "*.xml")) {
    Update-XmlReferences $file.FullName | Out-Null
}

# 5b: res/xml/ files (already renamed)
Write-Host "  Processing res/xml/ files..." -ForegroundColor Cyan
if (Test-Path $xmlResDir) {
    foreach ($file in (Get-ChildItem $xmlResDir -Filter "*.xml")) {
        Update-XmlReferences $file.FullName | Out-Null
    }
}

# 5c: Drawable XML files (already renamed)
Write-Host "  Processing drawable XML files..." -ForegroundColor Cyan
foreach ($drawableDir in $drawableDirs) {
    foreach ($file in (Get-ChildItem $drawableDir.FullName -Filter "*.xml")) {
        Update-XmlReferences $file.FullName | Out-Null
    }
}

# 5d: AndroidManifest.xml
Write-Host "  Processing AndroidManifest.xml..." -ForegroundColor Cyan
Update-XmlReferences $manifest | Out-Null

# 5e: Java files
Write-Host "  Processing Java files..." -ForegroundColor Cyan
foreach ($file in (Get-ChildItem $javaBase -Recurse -Filter "*.java")) {
    Update-JavaReferences $file.FullName | Out-Null
}

# -----------------------------------------------------------------------
# VERIFICATION REPORT
# -----------------------------------------------------------------------
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Verification: Checking for remaining un-prefixed refs" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

Write-Host "Layout files now:" -ForegroundColor White
Get-ChildItem $layoutDir -Filter "*.xml" | ForEach-Object { Write-Host "  $($_.Name)" } | Sort-Object

Write-Host "`nDrawable files now:" -ForegroundColor White
foreach ($drawableDir in $drawableDirs) {
    Get-ChildItem $drawableDir.FullName | Where-Object { $_.Name -notlike "ic_launcher*" } |
        ForEach-Object { Write-Host "  [$($drawableDir.Name)] $($_.Name)" }
}

Write-Host "`nres/xml files now:" -ForegroundColor White
if (Test-Path $xmlResDir) {
    Get-ChildItem $xmlResDir | ForEach-Object { Write-Host "  $($_.Name)" }
}

Write-Host ""
Write-Host "=======================================" -ForegroundColor Green
Write-Host " Phase 0 + Phase 1 COMPLETE!" -ForegroundColor Green
Write-Host "=======================================" -ForegroundColor Green
