# Test script for crypto exchange endpoints

Write-Host "Testing Crypto Exchange Endpoints..." -ForegroundColor Green

# Set up headers
$headers = @{
    "Content-Type" = "application/json"
    "Authorization" = "Basic bmlrZ2VvOnBhc3N3b3Jk"
}

# Test 1: Authentication
Write-Host "`n1. Testing Authentication..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/v1/auth/login" -Headers $headers -Body "{}"
    Write-Host "Authentication SUCCESS: $($response | ConvertTo-Json)" -ForegroundColor Green
} catch {
    Write-Host "Authentication FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Ethereum Block Number
Write-Host "`n2. Testing Ethereum Block Number..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Method Get -Uri "http://localhost:8081/api/v1/ethereum/block-number" -Headers $headers
    Write-Host "Ethereum Block Number SUCCESS: $($response | ConvertTo-Json)" -ForegroundColor Green
} catch {
    Write-Host "Ethereum Block Number FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Deposit
Write-Host "`n3. Testing Deposit..." -ForegroundColor Yellow
$depositBody = @{
    "userId" = 1
    "tokenSymbol" = "ETH"
    "amount" = 2.0
    "walletAddress" = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/v1/deposit" -Headers $headers -Body $depositBody
    Write-Host "Deposit SUCCESS: $($response | ConvertTo-Json)" -ForegroundColor Green
} catch {
    Write-Host "Deposit FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Withdraw Test Endpoint
Write-Host "`n4. Testing Withdraw Test Endpoint..." -ForegroundColor Yellow
$withdrawBody = @{
    "userId" = 1
    "tokenSymbol" = "ETH"
    "amount" = 0.5
    "toAddress" = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/v1/withdraw/test" -Headers $headers -Body $withdrawBody
    Write-Host "Withdraw Test SUCCESS: $($response | ConvertTo-Json)" -ForegroundColor Green
} catch {
    Write-Host "Withdraw Test FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Withdraw Sync Endpoint
Write-Host "`n5. Testing Withdraw Sync Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/v1/withdraw/sync" -Headers $headers -Body $withdrawBody
    Write-Host "Withdraw Sync SUCCESS: $($response | ConvertTo-Json)" -ForegroundColor Green
} catch {
    Write-Host "Withdraw Sync FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Original Withdraw Endpoint
Write-Host "`n6. Testing Original Withdraw Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/v1/withdraw" -Headers $headers -Body $withdrawBody -TimeoutSec 30
    Write-Host "Original Withdraw SUCCESS: $($response | ConvertTo-Json)" -ForegroundColor Green
} catch {
    Write-Host "Original Withdraw FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 7: Transfer
Write-Host "`n7. Testing Transfer..." -ForegroundColor Yellow
$transferBody = @{
    "fromUserId" = 1
    "toUserId" = 2
    "tokenSymbol" = "ETH"
    "amount" = 0.1
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/v1/transfer" -Headers $headers -Body $transferBody
    Write-Host "Transfer SUCCESS: $($response | ConvertTo-Json)" -ForegroundColor Green
} catch {
    Write-Host "Transfer FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nTesting completed!" -ForegroundColor Green 