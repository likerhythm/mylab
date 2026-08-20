# k6-script.js 를 10번 실행하고, 각 실행 결과를 results\aggregated.json 으로 합친다. (Windows PowerShell)

$ErrorActionPreference = "Stop"

New-Item -ItemType Directory -Force -Path "results" | Out-Null
Remove-Item -Path "results\run_*.json" -ErrorAction SilentlyContinue

for ($i = 1; $i -le 5; $i++) {
    $no = "{0:D2}" -f $i   # 01, 02, ... 10
    Write-Host "========== Run $no / 10 =========="
    k6 run -e RUN_NO=$no traffic-test.js
    Write-Host ">> permit 회복 대기 (10초)..."
    Start-Sleep -Seconds 10
}

# --- 10개 결과 병합 (PowerShell 내장, jq/node 불필요) ---
$runs = Get-ChildItem "results\run_*.json" | Sort-Object Name | ForEach-Object {
    Get-Content $_.FullName -Raw | ConvertFrom-Json
}
# @($runs) 로 감싸 항상 배열이 되게 함 (run이 1개여도 배열 유지)
$out = [ordered]@{ runs = @($runs) }
$out | ConvertTo-Json -Depth 6 | Set-Content -Path "results\aggregated.json" -Encoding UTF8

Write-Host ""
Write-Host "완료 -> results\aggregated.json"
Write-Host "이 파일을 ratelimiter-report.html 에 업로드해서 그래프를 확인하세요."