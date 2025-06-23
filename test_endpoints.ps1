# Automated Crypto Exchange Testing Script
# This script tests all functionality and shows database updates

Write-Host "=== CRYPTO EXCHANGE AUTOMATED TESTING ===" -ForegroundColor Cyan
Write-Host "Starting comprehensive testing..." -ForegroundColor Green

# Set up headers
$headers = @{
    "Authorization" = "Basic bmlrZ2VvOnBhc3N3b3Jk"
    "Content-Type" = "application/json"
}

# Test 1: Login
Write-Host "`n=== 1. TESTING LOGIN ===" -ForegroundColor Yellow
try {
    $loginResponse = (Invoke-WebRequest -Uri "http://localhost:8081/api/v1/auth/login" -Method POST -Headers @{"Authorization"="Basic bmlrZ2VvOnBhc3N3b3Jk"}).Content | ConvertFrom-Json
    Write-Host "Login Response:" -ForegroundColor Green
    $loginResponse | Format-Table -AutoSize
} catch {
    Write-Host "Login FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Ethereum Block Number
Write-Host "`n=== 2. TESTING ETHEREUM BLOCK NUMBER ===" -ForegroundColor Yellow
try {
    $blockResponse = (Invoke-WebRequest -Uri "http://localhost:8081/api/v1/ethereum/block-number" -Method GET -Headers @{"Authorization"="Basic bmlrZ2VvOnBhc3N3b3Jk"}).Content | ConvertFrom-Json
    Write-Host "Ethereum Block Number Response:" -ForegroundColor Green
    $blockResponse | Format-Table -AutoSize
} catch {
    Write-Host "Ethereum Block Number FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Deposit for User 1
Write-Host "`n=== 3. TESTING DEPOSIT FOR USER 1 ===" -ForegroundColor Yellow
$depositBody = @{
    userId = 1
    tokenSymbol = "ETH"
    amount = 2.5
    walletAddress = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e"
} | ConvertTo-Json

try {
    $depositResponse = (Invoke-WebRequest -Uri "http://localhost:8081/api/v1/deposit" -Method POST -Headers @{"Authorization"="Basic bmlrZ2VvOnBhc3N3b3Jk"; "Content-Type"="application/json"} -Body $depositBody).Content | ConvertFrom-Json
    Write-Host "Deposit Response:" -ForegroundColor Green
    $depositResponse | Format-Table -AutoSize
} catch {
    Write-Host "Deposit FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Check balance after deposit
Write-Host "`n--- Balance After Deposit ---" -ForegroundColor Cyan
try {
    $balanceAfterDeposit = docker exec crypto_exchange_postgres psql -U nikgeo -d CryptoEXchangeDB -c "
SELECT 
    u.email,
    t.symbol,
    ub.amount as balance_after_deposit,
    ub.updated_at
FROM balances ub
JOIN users u ON ub.user_id = u.user_id
JOIN tokens t ON ub.token_id = t.token_id
WHERE u.user_id = 1 AND t.symbol = 'ETH';"
    Write-Host $balanceAfterDeposit -ForegroundColor White
} catch {
    Write-Host "Database query FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Withdraw for User 1
Write-Host "`n=== 4. TESTING WITHDRAW FOR USER 1 ===" -ForegroundColor Yellow
$withdrawBody = @{
    userId = 1
    tokenSymbol = "ETH"
    amount = 0.5
    toAddress = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e"
} | ConvertTo-Json

# Test withdraw sync
try {
    $withdrawSyncResponse = (Invoke-WebRequest -Uri "http://localhost:8081/api/v1/withdraw/sync" -Method POST -Headers @{"Authorization"="Basic bmlrZ2VvOnBhc3N3b3Jk"; "Content-Type"="application/json"} -Body $withdrawBody).Content | ConvertFrom-Json
    Write-Host "Withdraw Sync Response:" -ForegroundColor Green
    $withdrawSyncResponse | Format-Table -AutoSize
} catch {
    Write-Host "Withdraw Sync FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Check balance after withdrawal
Write-Host "`n--- Balance After Withdrawal ---" -ForegroundColor Cyan
try {
    $balanceAfterWithdrawal = docker exec crypto_exchange_postgres psql -U nikgeo -d CryptoEXchangeDB -c "
SELECT 
    u.email,
    t.symbol,
    ub.amount as balance_after_withdrawal,
    ub.updated_at
FROM balances ub
JOIN users u ON ub.user_id = u.user_id
JOIN tokens t ON ub.token_id = t.token_id
WHERE u.user_id = 1 AND t.symbol = 'ETH';"
    Write-Host $balanceAfterWithdrawal -ForegroundColor White
} catch {
    Write-Host "Database query FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Transfer from User 1 to User 2
Write-Host "`n=== 5. TESTING TRANSFER FROM USER 1 TO USER 2 ===" -ForegroundColor Yellow
$transferBody = @{
    fromUserId = 1
    toUserId = 2
    tokenSymbol = "ETH"
    amount = 1.0
} | ConvertTo-Json

try {
    $transferResponse = (Invoke-WebRequest -Uri "http://localhost:8081/api/v1/transfer" -Method POST -Headers @{"Authorization"="Basic bmlrZ2VvOnBhc3N3b3Jk"; "Content-Type"="application/json"} -Body $transferBody).Content | ConvertFrom-Json
    Write-Host "Transfer Response:" -ForegroundColor Green
    $transferResponse | Format-Table -AutoSize
} catch {
    Write-Host "Transfer FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Check balances after transfer
Write-Host "`n--- Balances After Transfer ---" -ForegroundColor Cyan
try {
    $balancesAfterTransfer = docker exec crypto_exchange_postgres psql -U nikgeo -d CryptoEXchangeDB -c "
SELECT 
    u.email,
    t.symbol,
    ub.amount as balance_after_transfer,
    ub.updated_at
FROM balances ub
JOIN users u ON ub.user_id = u.user_id
JOIN tokens t ON ub.token_id = t.token_id
WHERE u.user_id IN (1, 2) AND t.symbol = 'ETH'
ORDER BY u.user_id;"
    Write-Host $balancesAfterTransfer -ForegroundColor White
} catch {
    Write-Host "Database query FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: View All Transactions Completed
Write-Host "`n=== 6. VIEWING ALL TRANSACTIONS COMPLETED ===" -ForegroundColor Yellow
try {
    $allTransactions = docker exec crypto_exchange_postgres psql -U nikgeo -d CryptoEXchangeDB -c "
SELECT 
    tl.log_id,        
    tl.transaction_type,
    u.name as user_name,
    t.symbol as token_symbol,
    tl.amount,          
    tl.status,
    tl.tx_hash,
    tl.created_at
FROM transaction_logs tl 
JOIN users u ON tl.user_id = u.user_id 
JOIN tokens t ON tl.token_id = t.token_id 
ORDER BY tl.created_at DESC;"
    Write-Host "All Transactions:" -ForegroundColor Green
    Write-Host $allTransactions -ForegroundColor White
} catch {
    Write-Host "Transaction query FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Final Summary
Write-Host "`n=== TESTING COMPLETED ===" -ForegroundColor Cyan
Write-Host "All crypto exchange functionality has been tested!" -ForegroundColor Green
Write-Host "Check the database queries above to see how transactions updated the database." -ForegroundColor Yellow 