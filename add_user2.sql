INSERT INTO users (name, email, created_at) 
VALUES ('Test User 2', 'user2@example.com', CURRENT_TIMESTAMP) 
ON CONFLICT DO NOTHING; 