-- Insert ETH token
INSERT INTO tokens (token_id, symbol, name) 
VALUES (1, 'ETH', 'Ethereum') 
ON CONFLICT (token_id) DO NOTHING;

-- Get the user_id for Test User
SELECT user_id, name FROM users WHERE name = 'Test User';

-- Insert balance for Test User (assuming user_id = 1)
INSERT INTO balances (user_id, token_id, amount, updated_at)
VALUES (1, 1, 10.0, NOW())
ON CONFLICT (user_id, token_id) DO UPDATE SET amount = EXCLUDED.amount, updated_at = NOW();

-- Verify the data
SELECT u.name, t.symbol, b.amount 
FROM users u 
JOIN balances b ON u.user_id = b.user_id 
JOIN tokens t ON b.token_id = t.token_id 
WHERE u.name = 'Test User'; 