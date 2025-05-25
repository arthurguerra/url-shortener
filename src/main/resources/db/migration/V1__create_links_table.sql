CREATE TABLE links (
                       id SERIAL PRIMARY KEY,
                       short_code VARCHAR(20) UNIQUE NOT NULL,
                       original_url TEXT NOT NULL,
                       created_at TIMESTAMP DEFAULT now()
);