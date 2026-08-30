-- 1. Widen the old country layout column to allow full text country names instead of 2 characters
ALTER TABLE profiles
    ALTER COLUMN country TYPE VARCHAR(50);

-- 2. Add New Shared Fields
ALTER TABLE profiles
    ADD COLUMN tax_id VARCHAR(50);

-- 3. Add New Personal Profile Fields
ALTER TABLE profiles
    ADD COLUMN nationality VARCHAR(100),
    ADD COLUMN occupation VARCHAR(100);

-- 4. Add New Business Profile Fields
ALTER TABLE profiles
    ADD COLUMN industry VARCHAR(150),
    ADD COLUMN website VARCHAR(255);

-- 5. Add New Embedded Address Structure Fields
ALTER TABLE profiles
    ADD COLUMN address_street VARCHAR(150),
    ADD COLUMN address_city VARCHAR(100),
    ADD COLUMN address_state VARCHAR(100),
    ADD COLUMN address_postal_code VARCHAR(20),
    ADD COLUMN address_country VARCHAR(50);

-- 6. Add Comments for clarity (Highly recommended for team documentation)
COMMENT ON COLUMN profiles.tax_id IS 'Unified identification number for tax filing (Personal or Business)';
COMMENT ON COLUMN profiles.address_street IS 'Embedded address street line';
COMMENT ON COLUMN profiles.country IS 'Country string matching incoming text payload';
