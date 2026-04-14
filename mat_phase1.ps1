<#
  MAT_PROJECT Resource Isolation Script
  Phase 0: Exclusions
  Phase 1: Deep Resource Isolation (prefix all resources with mat_)
  Operates ONLY inside MAT_PROJECT directory.
#>

$ErrorActionPreference = "Stop"
$PREFIX = "mat_"
$MAT_ROOT = "D:\SoftwareData\AndroidStudioProjects\MAT_PROJECT\MAT_PROJECT\app\src\main"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " MAT_PROJECT Phase 0 + Phase 1 Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# ─────────────────────────────────────────────
# PHASE 0: Define exclusions
# ─────────────────────────────────────────────
Write-Host "`n[Phase 0] Defining exclusions..." -ForegroundColor Yellow
$EXCLUDE_JAVA   = "MainActivity.java"
$EXCLUDE_LAYOUT = "activity_main.xml"
# __MACOSX is outside MAT_PROJECT/MAT_PROJECT so it is automatically excluded.
Write-Host "  Excluded Java:   $EXCLUDE_JAVA"
Write-Host "  Excluded Layout: $EXCLUDE_LAYOUT"
Write-Host "  Excluded Dir:    __MACOSX (outside scope)"

# ─────────────────────────────────────────────
# PHASE 1A: Rename name= attributes in values XML
# ─────────────────────────────────────────────
Write-Host "`n[Phase 1A] Prefixing name attributes in values XML files..." -ForegroundColor Yellow

$valuesFiles = @(
    "$MAT_ROOT\res\values\colors.xml",
    "$MAT_ROOT\res\values\strings.xml",
    "$MAT_ROOT\res\values\themes.xml",
    "$MAT_ROOT\res\values\styles.xml",
    "$MAT_ROOT\res\values\ids.xml",
    "$MAT_ROOT\res\values-night\themes.xml"
)

foreach ($file in $valuesFiles) {
    if (-not (Test-Path $file)) {
        Write-Host "  SKIP (not found): $file" -ForegroundColor DarkGray
        continue
    }
    $content = Get-Content $file -Raw -Encoding UTF8

    # Prefix name="xxx" where xxx does NOT already start with mat_
    # Also prefix parent="xxx" in <style> tags only when NOT an Android/Material parent
    $newContent = [regex]::Replace($content,
        '(?<=\bname=")(?!mat_)([^"]+)(?=")',
        { param($m) "${PREFIX}$($m.Value)" }
    )

    if ($newContent -ne $content) {
        Set-Content -Path $file -Value $newContent -Encoding UTF8 -NoNewline
        Write-Host "  Updated: $file" -ForegroundColor Green
    } else {
        Write-Host "  No changes: $file" -ForegroundColor DarkGray
    }
}

# ─────────────────────────────────────────────
# PHASE 1B: Rename files in layout/, drawable/, font/
# ─────────────────────────────────────────────
Write-Host "`n[Phase 1B] Renaming resource files with mat_ prefix..." -ForegroundColor Yellow

$resDirs = @(
    "$MAT_ROOT\res\layout",
    "$MAT_ROOT\res\drawable",
    "$MAT_ROOT\res\font"
)

# Build a rename map: oldName -> newName (for reference updating later)
$renameMap = @{}

foreach ($dir in $resDirs) {
    if (-not (Test-Path $dir)) {
        Write-Host "  SKIP (not found): $dir" -ForegroundColor DarkGray
        continue
    }
    $files = Get-ChildItem -Path $dir -File
    foreach ($f in $files) {
        # Phase 0: skip activity_main.xml in layout
        if ($f.Directory.Name -eq "layout" -and $f.Name -eq "activity_main.xml") {
            Write-Host "  SKIP (Phase 0 exclusion): $($f.FullName)" -ForegroundColor DarkGray
            continue
        }
        # Skip .DS_Store and other non-resource files
        if ($f.Name -eq ".DS_Store") {
            Write-Host "  SKIP (.DS_Store): $($f.FullName)" -ForegroundColor DarkGray
            continue
        }
        # Skip if already prefixed
        if ($f.Name.StartsWith($PREFIX)) {
            Write-Host "  SKIP (already prefixed): $($f.Name)" -ForegroundColor DarkGray
            continue
        }

        $newName = "${PREFIX}$($f.Name)"
        $newPath = Join-Path $f.DirectoryName $newName

        # Store old base name (no extension) -> new base name (no extension) for ref updates
        $oldBase = [System.IO.Path]::GetFileNameWithoutExtension($f.Name)
        $newBase = [System.IO.Path]::GetFileNameWithoutExtension($newName)
        $renameMap[$oldBase] = $newBase

        Rename-Item -Path $f.FullName -NewName $newName
        Write-Host "  Renamed: $($f.Name) -> $newName" -ForegroundColor Green
    }
}

# ─────────────────────────────────────────────
# PHASE 1C: Update all internal references
# ─────────────────────────────────────────────
Write-Host "`n[Phase 1C] Updating internal references in XML and Java files..." -ForegroundColor Yellow

# Collect all XML files (layout, drawable, values, values-night) + all Java files
# EXCLUDE: activity_main.xml and MainActivity.java
$xmlFiles  = @()
$javaFiles = @()

$xmlDirs = @(
    "$MAT_ROOT\res\layout",
    "$MAT_ROOT\res\drawable",
    "$MAT_ROOT\res\values",
    "$MAT_ROOT\res\values-night"
)

foreach ($dir in $xmlDirs) {
    if (Test-Path $dir) {
        Get-ChildItem -Path $dir -Filter "*.xml" -File | Where-Object {
            -not ($_.Directory.Name -eq "layout" -and $_.Name -eq "activity_main.xml")
        } | ForEach-Object { $xmlFiles += $_.FullName }
    }
}

# Java files (excluding MainActivity.java)
Get-ChildItem -Path "$MAT_ROOT\java" -Filter "*.java" -Recurse -File | Where-Object {
    $_.Name -ne "MainActivity.java"
} | ForEach-Object { $javaFiles += $_.FullName }

$allFiles = $xmlFiles + $javaFiles

# ── XML replacements ──────────────────────────
# Patterns to replace in XML (use negative lookahead so we never double-prefix)
# Order matters: do specific patterns first.
$xmlPatterns = @(
    # @+id/xxx  -> @+id/mat_xxx
    @{ Pattern = '(?<=@\+id/)(?!mat_)(\w+)'; Replacement = "${PREFIX}`$1" },
    # @id/xxx   -> @id/mat_xxx
    @{ Pattern = '(?<=@id/)(?!mat_)(\w+)';   Replacement = "${PREFIX}`$1" },
    # @color/xxx
    @{ Pattern = '(?<=@color/)(?!mat_)(\w+)'; Replacement = "${PREFIX}`$1" },
    # @string/xxx
    @{ Pattern = '(?<=@string/)(?!mat_)(\w+)'; Replacement = "${PREFIX}`$1" },
    # @drawable/xxx  (skip ic_launcher_ which is from Android default, still prefix it)
    @{ Pattern = '(?<=@drawable/)(?!mat_)(\w+)'; Replacement = "${PREFIX}`$1" },
    # @layout/xxx
    @{ Pattern = '(?<=@layout/)(?!mat_)(\w+)'; Replacement = "${PREFIX}`$1" },
    # @font/xxx
    @{ Pattern = '(?<=@font/)(?!mat_)(\w+)'; Replacement = "${PREFIX}`$1" },
    # style="@style/xxx"  (only custom styles, not android: or Widget. or Base.)
    @{ Pattern = '(?<=@style/)(?!mat_)(?!Theme\.)(?!Widget\.)(?!Base\.)(\w+)'; Replacement = "${PREFIX}`$1" }
)

foreach ($file in $xmlFiles) {
    $content = Get-Content $file -Raw -Encoding UTF8
    $original = $content
    foreach ($p in $xmlPatterns) {
        $content = [regex]::Replace($content, $p.Pattern, $p.Replacement)
    }
    if ($content -ne $original) {
        Set-Content -Path $file -Value $content -Encoding UTF8 -NoNewline
        Write-Host "  XML updated: $(Split-Path $file -Leaf)" -ForegroundColor Green
    }
}

# ── Java replacements ─────────────────────────
# R.layout.xxx  R.drawable.xxx  R.color.xxx  R.string.xxx  R.font.xxx  R.id.xxx
$javaPatterns = @(
    @{ Pattern = '(?<=R\.layout\.)(?!mat_)(\w+)';   Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=R\.drawable\.)(?!mat_)(\w+)'; Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=R\.color\.)(?!mat_)(\w+)';    Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=R\.string\.)(?!mat_)(\w+)';   Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=R\.font\.)(?!mat_)(\w+)';     Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=R\.id\.)(?!mat_)(\w+)';       Replacement = "${PREFIX}`$1" }
)

foreach ($file in $javaFiles) {
    $content = Get-Content $file -Raw -Encoding UTF8
    $original = $content
    foreach ($p in $javaPatterns) {
        $content = [regex]::Replace($content, $p.Pattern, $p.Replacement)
    }
    if ($content -ne $original) {
        Set-Content -Path $file -Value $content -Encoding UTF8 -NoNewline
        Write-Host "  Java updated: $(Split-Path $file -Leaf)" -ForegroundColor Green
    }
}

# ─────────────────────────────────────────────
# DONE
# ─────────────────────────────────────────────
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host " Phase 0 + Phase 1 COMPLETE" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "`nSummary:"
Write-Host "  - Excluded: MainActivity.java, activity_main.xml, __MACOSX"
Write-Host "  - Values XML name= attributes prefixed with mat_"
Write-Host "  - Layout/Drawable/Font files renamed with mat_"
Write-Host "  - All @color, @string, @drawable, @layout, @font, @id, @style, R.xxx references updated"
