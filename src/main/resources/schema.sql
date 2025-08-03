-- Create a table from the csv only if it doesn't exist
CREATE TABLE IF NOT EXISTS PLAYERS AS SELECT * FROM CSVREAD('classpath:Player.csv');

-- Add indexes to improve query performance for common search and sort operations.
-- We use "IF NOT EXISTS" to prevent errors on subsequent application startups.
CREATE INDEX IF NOT EXISTS idx_lastname ON PLAYERS(nameLast);
CREATE INDEX IF NOT EXISTS idx_firstname ON PLAYERS(nameFirst);
CREATE INDEX IF NOT EXISTS idx_birthyear ON PLAYERS(birthYear);
