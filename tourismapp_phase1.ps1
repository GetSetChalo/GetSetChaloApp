<#
  Tourismapp Resource Isolation Script
  Phase 1: Deep Resource Isolation (prefix all resources with ta_)
  Operates ONLY inside Tourismapp directory.
#>

$ErrorActionPreference = "Stop"
$PREFIX = "ta_"
$PROJECT_ROOT = "D:\SoftwareData\AndroidStudioProjects\Tourismapp\Tourismapp\app\src\main"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Tourismapp Phase 1 Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# ─────────────────────────────────────────────
# PHASE 1A: Rename name= attributes in values XML
# ─────────────────────────────────────────────
Write-Host "`n[Phase 1A] Prefixing name attributes in values XML files..." -ForegroundColor Yellow

$valuesFiles = @()
if (Test-Path "$PROJECT_ROOT\res\values") {
    $valuesFiles += Get-ChildItem "$PROJECT_ROOT\res\values" -Filter "*.xml" -File
}
if (Test-Path "$PROJECT_ROOT\res\values-night") {
    $valuesFiles += Get-ChildItem "$PROJECT_ROOT\res\values-night" -Filter "*.xml" -File
}

foreach ($fileInfo in $valuesFiles) {
    if (-not (Test-Path $fileInfo.FullName)) {
        continue
    }
    $file = $fileInfo.FullName
    $content = Get-Content $file -Raw -Encoding UTF8

    # Prefix name="xxx" where xxx does NOT already start with ta_
    # Avoid android: namespace
    $newContent = [regex]::Replace($content,
        '(?<=\bname=")(?!ta_)([^"]+)(?=")',
        { param($m) 
            $val = $m.Value
            if ($val -match '^android:') { return $val }
            return "${PREFIX}$val"
        }
    )

    if ($newContent -ne $content) {
        Set-Content -Path $file -Value $newContent -Encoding UTF8 -NoNewline
        Write-Host "  Updated: $file" -ForegroundColor Green
    } else {
        Write-Host "  No changes: $file" -ForegroundColor DarkGray
    }
}

# ─────────────────────────────────────────────
# PHASE 1B: Rename files in layout/, drawable/, font/, xml/
# ─────────────────────────────────────────────
Write-Host "`n[Phase 1B] Renaming resource files with ta_ prefix..." -ForegroundColor Yellow

$resDirs = @(
    "$PROJECT_ROOT\res\layout",
    "$PROJECT_ROOT\res\drawable",
    "$PROJECT_ROOT\res\font",
    "$PROJECT_ROOT\res\xml"
)

foreach ($dir in $resDirs) {
    if (-not (Test-Path $dir)) {
        Write-Host "  SKIP (not found): $dir" -ForegroundColor DarkGray
        continue
    }
    $files = Get-ChildItem -Path $dir -File
    foreach ($f in $files) {
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

        Rename-Item -Path $f.FullName -NewName $newName
        Write-Host "  Renamed: $($f.Name) -> $newName" -ForegroundColor Green
    }
}

# ─────────────────────────────────────────────
# PHASE 1C: Update all internal references
# ─────────────────────────────────────────────
Write-Host "`n[Phase 1C] Updating internal references in XML and Java files..." -ForegroundColor Yellow

$xmlFiles  = @()
$javaFiles = @()

$xmlDirs = @(
    "$PROJECT_ROOT\res\layout",
    "$PROJECT_ROOT\res\drawable",
    "$PROJECT_ROOT\res\font",
    "$PROJECT_ROOT\res\xml",
    "$PROJECT_ROOT\res\values",
    "$PROJECT_ROOT\res\values-night"
)

foreach ($dir in $xmlDirs) {
    if (Test-Path $dir) {
        Get-ChildItem -Path $dir -Filter "*.xml" -File | ForEach-Object { $xmlFiles += $_.FullName }
    }
}

if (Test-Path "$PROJECT_ROOT\java") {
    Get-ChildItem -Path "$PROJECT_ROOT\java" -Filter "*.java" -Recurse -File | ForEach-Object { $javaFiles += $_.FullName }
}

$xmlPatterns = @(
    @{ Pattern = '(?<=@\+id/)(?!ta_)(\w+)'; Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=@id/)(?!ta_)(\w+)';   Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=@color/)(?!ta_)(\w+)'; Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=@string/)(?!ta_)(\w+)'; Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=@drawable/)(?!ta_)(\w+)'; Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=@layout/)(?!ta_)(\w+)'; Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=@font/)(?!ta_)(\w+)'; Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=@xml/)(?!ta_)(\w+)'; Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=@array/)(?!ta_)(\w+)'; Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=@style/)(?!ta_)(?!Theme\.)(?!Widget\.)(?!Base\.)(?!TextAppearance\.)(?!Widget\.)(\w+)'; Replacement = "${PREFIX}`$1" }
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

$javaPatterns = @(
    @{ Pattern = '(?<=R\.layout\.)(?!ta_)(\w+)';   Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=R\.drawable\.)(?!ta_)(\w+)'; Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=R\.color\.)(?!ta_)(\w+)';    Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=R\.string\.)(?!ta_)(\w+)';   Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=R\.font\.)(?!ta_)(\w+)';     Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=R\.id\.)(?!ta_)(\w+)';       Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=R\.xml\.)(?!ta_)(\w+)';       Replacement = "${PREFIX}`$1" },
    @{ Pattern = '(?<=R\.style\.)(?!ta_)(\w+)';       Replacement = "${PREFIX}`$1" }
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

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host " Phase 1 COMPLETE" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
