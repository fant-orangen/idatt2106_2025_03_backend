-- ROLES
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('SUPERADMIN');

-- HOUSEHOLDS
INSERT INTO households (name, address, population_count, latitude, longitude) VALUES ('Smith Family', '123 Main St, Anytown', 5, 63.4305, 10.3951);
INSERT INTO households (name, address, population_count, latitude, longitude) VALUES ('Johnson Household', '456 Oak Ave, Somewhere', 3, 63.4305, 10.3951);
INSERT INTO households (name, address, population_count, latitude, longitude) VALUES ('Brown Residence', '789 Pine St, Elsewhere', 2, 63.4305, 10.3951);

-- USERS (password_hash is 'password' for all users)
INSERT INTO users (email, password_hash, phone_number, role_id, household_id, first_name, last_name, email_verified, location_sharing_enabled)
VALUES ('admin@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '+4712345678', 2, NULL, 'Admin', 'User', TRUE, FALSE);

INSERT INTO users (email, password_hash, phone_number, role_id, household_id, first_name, last_name, home_address, home_latitude, home_longitude, email_verified, location_sharing_enabled)
VALUES ('alice@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '+4723456789', 1, 1, 'Alice', 'L', '123 Main St, Anytown', 63.4305, 10.3951, TRUE, TRUE);

INSERT INTO users (email, password_hash, phone_number, role_id, household_id, first_name, last_name, home_address, home_latitude, home_longitude, email_verified, location_sharing_enabled)
VALUES ('brahimh@stud.ntnu.no', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '+4723456789', 1, 1, 'Brahim', 'Helland', '123 Main St, Anytown', 63.4305, 10.3951, TRUE, TRUE);

INSERT INTO users (email, password_hash, phone_number, role_id, household_id, first_name, last_name, home_address, home_latitude, home_longitude, email_verified, location_sharing_enabled)
VALUES ('sarah@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '+4734567890', 1, NULL, 'Sarah', 'Johnson', '456 Oak Ave, Somewhere', 63.4205, 10.4051, TRUE, TRUE);

INSERT INTO users (email, password_hash, phone_number, role_id, household_id, first_name, last_name, home_address, home_latitude, home_longitude, email_verified, location_sharing_enabled)
VALUES ('mike@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '+4745678901', 3, 3, 'Mike', 'Brown', '789 Pine St, Elsewhere', 63.4105, 10.3851, TRUE, FALSE);

-- For testing emails
INSERT INTO users (email, password_hash, phone_number, role_id, household_id, first_name, last_name, home_address, home_latitude, home_longitude, email_verified, location_sharing_enabled)
VALUES ('havardjvd@gmail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '+4745678901', 1, 1, 'Haavard', 'Daleng', '789 Pine St, Elsewhere', 63.4105, 10.3851, TRUE, FALSE);

-- HOUSEHOLD MEMBERS
INSERT INTO household_member (household_id, name, description, type) VALUES (1, 'Emma Smith', 'Daughter', 'child');
INSERT INTO household_member (household_id, name, description, type) VALUES (1, 'James Smith', 'Son', 'child');
INSERT INTO household_member (household_id, name, description, type) VALUES (1, 'Mary Smith', 'Wife', 'adult');
INSERT INTO household_member (household_id, name, description, type) VALUES (2, 'Tom Johnson', 'Son', 'child');
INSERT INTO household_member (household_id, name, description, type) VALUES (2, 'David Johnson', 'Husband', 'adult');
INSERT INTO household_member (household_id, name, description, type) VALUES (3, 'Lisa Brown', 'Wife', 'adult');
INSERT INTO household_member (household_id, name, description, type) VALUES (1, 'Rex', 'Family dog', 'pet');

-- GROUPS
INSERT INTO groups (name, created_by_user_id) VALUES ('Neighborhood Watch', 2);
INSERT INTO groups (name, created_by_user_id) VALUES ('Emergency Response Team', 1);

-- GROUP MEMBERSHIPS
INSERT INTO group_memberships (group_id, household_id, invited_by_user_id) VALUES (1, 1, 2);
INSERT INTO group_memberships (group_id, household_id, invited_by_user_id) VALUES (1, 2, 2);
INSERT INTO group_memberships (group_id, household_id, invited_by_user_id) VALUES (2, 1, 1);
INSERT INTO group_memberships (group_id, household_id, invited_by_user_id) VALUES (2, 3, 1);

-- PRODUCT TYPES
-- Smith Family (household_id = 1)
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Canned Beans', 'stk', 120.00, 'food'); -- id: 1
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Rice', 'kg', 1300.00, 'food'); -- id: 2
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Bottled Water', 'l', 0.00, 'water'); -- id: 3
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Painkillers', 'mg', 0.00, 'medicine'); -- id: 4
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Antibiotic', 'dose', 0.00, 'medicine'); -- id: 5

-- Additional product types for Smith Family (household_id = 1)
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Oatmeal', 'kg', 370.00, 'food'); -- id: 6
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Sugar', 'kg', 4000.00, 'food'); -- id: 7
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Salt', 'kg', 0.00, 'food'); -- id: 8
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Cooking Oil', 'l', 8840.00, 'food'); -- id: 9
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Powdered Milk', 'kg', 500.00, 'food'); -- id: 10
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Canned Tuna', 'stk', 120.00, 'food'); -- id: 11
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Peanut Butter', 'stk', 588.00, 'food'); -- id: 12
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Crackers', 'stk', 70.00, 'food'); -- id: 13
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Dried Fruit', 'kg', 320.00, 'food'); -- id: 14
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Instant Noodles', 'stk', 350.00, 'food'); -- id: 15
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Honey', 'kg', 3040.00, 'food'); -- id: 16
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Tea Bags', 'stk', 2.00, 'food'); -- id: 17
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Coffee', 'kg', 2.00, 'food'); -- id: 18
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Cereal Bars', 'stk', 90.00, 'food'); -- id: 19
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Canned Corn', 'stk', 80.00, 'food'); -- id: 20
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Canned Tomatoes', 'stk', 30.00, 'food'); -- id: 21
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Apple Juice', 'l', 46.00, 'food'); -- id: 22
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Bottled Sparkling Water', 'l', 0.00, 'water'); -- id: 23
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Electrolyte Solution', 'l', 0.00, 'water'); -- id: 24
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Antihistamine', 'mg', 0.00, 'medicine'); -- id: 25
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Cough Syrup', 'dose', 0.00, 'medicine'); -- id: 26
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Bandages', 'stk', 0.00, 'medicine'); -- id: 27

-- Johnson Household (household_id = 2)
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (2, 'Flour', 'kg', 1640.00, 'food'); -- id: 28
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (2, 'Pasta', 'kg', 1570.00, 'food'); -- id: 29
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (2, 'Purified Water', 'l', 0.00, 'water'); -- id: 30
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (2, 'Flashlight', 'stk', 0.00, 'food'); -- id: 31
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (2, 'Aspirin', 'mg', 0.00, 'medicine'); -- id: 32

-- Brown Residence (household_id = 3)
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (3, 'Bread', 'kg', 2650.00, 'food'); -- id: 33
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (3, 'Canned Soup', 'stk', 200.00, 'food'); -- id: 34
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (3, 'Toilet Paper', 'stk', 0.00, 'food'); -- id: 35
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (3, 'Soap', 'stk', 0.00, 'food'); -- id: 36

-- PRODUCT BATCH
-- Canned Beans batches
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (1, CURRENT_TIMESTAMP, DATEADD('YEAR', 1, CURRENT_TIMESTAMP), 10);
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (1, CURRENT_TIMESTAMP, DATEADD('YEAR', 2, CURRENT_TIMESTAMP), 5);

-- Rice batch
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (2, CURRENT_TIMESTAMP, DATEADD('YEAR', 1, CURRENT_TIMESTAMP), 3);

-- Bottled Water batches
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (3, CURRENT_TIMESTAMP, DATEADD('MONTH', 6, CURRENT_TIMESTAMP), 24);

-- Painkillers batches
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (4, CURRENT_TIMESTAMP, NULL, 1);
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (4, CURRENT_TIMESTAMP, DATEADD('YEAR', 1, CURRENT_TIMESTAMP), 2);

-- Flour batch
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (5, CURRENT_TIMESTAMP, DATEADD('MONTH', 6, CURRENT_TIMESTAMP), 2);

-- Purified Water batches
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (7, CURRENT_TIMESTAMP, DATEADD('YEAR', 2, CURRENT_TIMESTAMP), 5);
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (7, CURRENT_TIMESTAMP, NULL, 3);

-- Bread batch
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (9, CURRENT_TIMESTAMP, DATEADD('DAY', 7, CURRENT_TIMESTAMP), 2);

-- Canned Soup batch
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (10, CURRENT_TIMESTAMP, DATEADD('MONTH', 18, CURRENT_TIMESTAMP), 6);

-- Product batches for new product types (IDs are sequential after previous entries)
-- Oatmeal (id=14)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (14, CURRENT_TIMESTAMP, DATEADD('YEAR', 1, CURRENT_TIMESTAMP), 2);
-- Sugar (id=15)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (15, CURRENT_TIMESTAMP, DATEADD('YEAR', 2, CURRENT_TIMESTAMP), 1);
-- Salt (id=16)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (16, CURRENT_TIMESTAMP, DATEADD('YEAR', 3, CURRENT_TIMESTAMP), 1);
-- Cooking Oil (id=17)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (17, CURRENT_TIMESTAMP, DATEADD('MONTH', 10, CURRENT_TIMESTAMP), 1);
-- Powdered Milk (id=18)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (18, CURRENT_TIMESTAMP, DATEADD('MONTH', 6, CURRENT_TIMESTAMP), 1);
-- Canned Tuna (id=19)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (19, CURRENT_TIMESTAMP, DATEADD('YEAR', 2, CURRENT_TIMESTAMP), 4);
-- Peanut Butter (id=20)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (20, CURRENT_TIMESTAMP, DATEADD('YEAR', 1, CURRENT_TIMESTAMP), 2);
-- Crackers (id=21)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (21, CURRENT_TIMESTAMP, DATEADD('MONTH', 8, CURRENT_TIMESTAMP), 3);
-- Dried Fruit (id=22)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (22, CURRENT_TIMESTAMP, DATEADD('MONTH', 12, CURRENT_TIMESTAMP), 1);
-- Instant Noodles (id=23)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (23, CURRENT_TIMESTAMP, DATEADD('MONTH', 6, CURRENT_TIMESTAMP), 5);
-- Honey (id=24)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (24, CURRENT_TIMESTAMP, DATEADD('YEAR', 5, CURRENT_TIMESTAMP), 1);
-- Tea Bags (id=25)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (25, CURRENT_TIMESTAMP, DATEADD('YEAR', 2, CURRENT_TIMESTAMP), 50);
-- Coffee (id=26)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (26, CURRENT_TIMESTAMP, DATEADD('YEAR', 2, CURRENT_TIMESTAMP), 1);
-- Cereal Bars (id=27)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (27, CURRENT_TIMESTAMP, DATEADD('MONTH', 10, CURRENT_TIMESTAMP), 6);
-- Canned Corn (id=28)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (28, CURRENT_TIMESTAMP, DATEADD('YEAR', 1, CURRENT_TIMESTAMP), 2);
-- Pasta (id=29)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (29, CURRENT_TIMESTAMP, DATEADD('YEAR', 1, CURRENT_TIMESTAMP), 2);
-- Apple Juice (id=30)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (30, CURRENT_TIMESTAMP, DATEADD('MONTH', 6, CURRENT_TIMESTAMP), 3);
-- Bottled Sparkling Water (id=31)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (31, CURRENT_TIMESTAMP, DATEADD('YEAR', 1, CURRENT_TIMESTAMP), 6);
-- Electrolyte Solution (id=32)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (32, CURRENT_TIMESTAMP, DATEADD('MONTH', 8, CURRENT_TIMESTAMP), 2);
-- Antihistamine (id=33)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (33, CURRENT_TIMESTAMP, DATEADD('YEAR', 2, CURRENT_TIMESTAMP), 1);
-- Cough Syrup (id=34)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (34, CURRENT_TIMESTAMP, DATEADD('MONTH', 18, CURRENT_TIMESTAMP), 1);
-- Bandages (id=35)
INSERT INTO product_batch (product_type_id, date_added, expiration_time, number) VALUES (35, CURRENT_TIMESTAMP, DATEADD('YEAR', 5, CURRENT_TIMESTAMP), 10);

-- GROUP INVENTORY CONTRIBUTIONS
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 1, 1); --  Canned Beans
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 1, 7); -- Antibiotics
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (2, 1, 3); -- Rice
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (2, 1, 10); -- Cooking oil
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 1, 2);
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 1, 4);
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 1, 5);
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 2, 29);
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 2, 30);
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 2, 31);


-- POI TYPES
INSERT INTO poi_types (name) VALUES ('Hospital');
INSERT INTO poi_types (name) VALUES ('Police Station');
INSERT INTO poi_types (name) VALUES ('Fire Station');
INSERT INTO poi_types (name) VALUES ('Shelter');
INSERT INTO poi_types (name) VALUES ('Grocery Store');
INSERT INTO poi_types (name) VALUES ('Water Distribution Point');
INSERT INTO poi_types (name) VALUES ('Gas Station');
INSERT INTO poi_types (name) VALUES ('Pharmacy');

-- POINTS OF INTEREST
INSERT INTO points_of_interest (poi_type_id, name, description, latitude, longitude, address, created_by_user_id)
VALUES (1, 'City Hospital', 'Main hospital with emergency services', 63.4280, 10.3920, '100 Hospital Road, Trondheim', 1);
INSERT INTO points_of_interest (poi_type_id, name, description, latitude, longitude, address, created_by_user_id)
VALUES (2, 'Central Police Station', '24/7 police services', 63.4310, 10.3980, '200 Police Avenue, Trondheim', 1);
INSERT INTO points_of_interest (poi_type_id, name, description, latitude, longitude, address, created_by_user_id)
VALUES (4, 'Community Shelter', 'Emergency shelter with capacity for 200 people', 63.4250, 10.3950, '300 Shelter Street, Trondheim', 1);
INSERT INTO points_of_interest (poi_type_id, name, description, latitude, longitude, address, created_by_user_id)
VALUES (5, 'SuperMarket', 'Large grocery store with emergency supplies', 63.4270, 10.3930, '400 Market Road, Trondheim', 2);
INSERT INTO points_of_interest (poi_type_id, name, description, latitude, longitude, address, created_by_user_id)
VALUES (1, 'St. Olavs Hospital', 'Main university hospital with 24/7 emergency services', 63.4195, 10.4022, 'Mauritz Hansens gate 4, 7030 Trondheim', 1);

INSERT INTO points_of_interest (poi_type_id, name, description, latitude, longitude, address, created_by_user_id)
VALUES (2, 'Trøndelag Police District', 'Central police station serving Trondheim and surrounding areas', 63.4321, 10.3978, 'Gryta 4, 7005 Trondheim', 1);

INSERT INTO points_of_interest (poi_type_id, name, description, latitude, longitude, address, created_by_user_id)
VALUES (3, 'Trondheim Fire Station', 'Main fire station providing emergency fire and rescue services', 63.3985, 10.3589, 'Kvenildstrøa 4, 7093 Heimdal, Trondheim', 1);

INSERT INTO points_of_interest (poi_type_id, name, description, latitude, longitude, address, created_by_user_id)
VALUES (6, 'Vitusapotek Trondheim Torg', 'Central pharmacy offering prescription and over-the-counter medications', 63.4308, 10.3943, 'Kongens gate 11, 7013 Trondheim', 1);

INSERT INTO points_of_interest (poi_type_id, name, description, latitude, longitude, address, created_by_user_id)
VALUES (7, 'Circle K Tempe', 'Fuel station with convenience store, open 24/7', 63.4080, 10.4140, 'Breidablikkveien 136, 7020 Trondheim', 1);

INSERT INTO points_of_interest (poi_type_id, name, description, latitude, longitude, address, created_by_user_id)
VALUES (8, 'Jonsvatnet Water Treatment Plant', 'Primary water source and treatment facility for Trondheim', 63.3870, 10.5750, 'Jonsvannsveien 690, 7050 Trondheim', 1);


-- MEETING PLACES
INSERT INTO meeting_places (name, latitude, longitude, address, created_by_user_id)
VALUES ('Park Entrance', 63.4290, 10.3940, 'City Park Main Entrance', 2);
INSERT INTO meeting_places (name, latitude, longitude, address, created_by_user_id)
VALUES ('School Playground', 63.4300, 10.3960, 'Elementary School', 3);
INSERT INTO meeting_places (name, latitude, longitude, address, created_by_user_id)
VALUES ('Shopping Mall', 63.4320, 10.3970, 'Downtown Mall', 4);

-- SCENARIO THEMES
INSERT INTO scenario_themes (name, description, before, under, after, status, created_by_user_id)
VALUES (
    'Strømbrudd',
    'En veiledning for å forberede og håndtere lengre strømbrudd. Strømbrudd kan skyldes ekstremvær, teknisk feil eller planlagt vedlikehold. God forberedelse er viktig for sikkerhet og komfort.',
    '## Før et Strømbrudd

### Nødutstyrspakke
- **Lommelykter** og ekstra batterier
- **Batteridrevet DAB-radio**
- **Bærbar telefonlader/powerbank** (fulladet)
- **Nok drikkevann** (anbefalt 20 liter per person for en uke)
- **Matvarer som ikke krever kjøling eller tilberedning** (nok for en uke)
- **Førstehjelpsutstyr** og nødvendige medisiner
- **Alternative varmekilder** (vedovn, parafinovn – husk god ventilasjon)
- **Varme klær, tepper, soveposer**
- **Fyrstikker/lighter**
- **Litt kontanter**

### Ytterligere Forberedelser
1. Sikre elektronisk utstyr med overspenningsvern.
2. Hold bilens drivstofftank minst halvfull.
3. Lær hvordan du manuelt åpner garasjeporten.
4. Vurder en nødgenerator (bruk kun utendørs).
5. Ha en liste med viktige telefonnumre på papir.
6. Sørg for mat og vann til kjæledyr.',
    '## Under et Strømbrudd

### Umiddelbare Tiltak
1. **Koble fra følsomt elektronisk utstyr** for å unngå skade ved strømgjennomslag.
2. Hold kjøleskaps- og fryserdører lukket så mye som mulig.
3. Bruk lommelykter i stedet for levende lys for å redusere brannfare.

### Sikkerhetstiltak
- Lytt til informasjon fra myndighetene på batteridrevet radio.
- Sjekk hvordan det går med naboer, spesielt eldre eller sårbare.
- Bruk eventuell generator kun utendørs og borte fra vinduer.
- Spar på batteriet på mobiltelefoner; bruk dem kun når nødvendig.
- Unngå unødvendig bilkjøring.',
    '## Etter at Strømmen er Tilbake

### Sikkerhetssjekk
1. **Sjekk mat** i kjøleskap og fryser for tegn til bedervelse. Mat som har vært tint bør ikke fryses på nytt.
2. Tilbakestill klokker, alarmer og annet elektronisk utstyr.
3. Fyll på nødlagre som er brukt.

### Oppfølging
- Meld fra om eventuelle skader på strømnettet til netteier.
- Gjennomgå og oppdater egen beredskapsplan.
- Del erfaringer med naboer og lokalsamfunn.',
    'active',
    1
);

INSERT INTO scenario_themes (name, description, before, under, after, status, created_by_user_id)
VALUES (
    'Storm',
    'Veiledning for forberedelse og håndtering av kraftig storm med sterk vind og nedbør. Storm kan forårsake store materielle skader, spesielt i kystnære strøk.',
    '## Før en Storm

### Sikring av Eiendom
1. **Fest løse gjenstander** utendørs (hagemøbler, trampoline, grill, søppelkasser).
2. **Parker bilen** i garasje hvis mulig, ellers unna trær og strømlinjer.
3. **Sjekk tak og vegger** for løse deler (takstein, beslag). Sikre det som er løst hvis det er trygt.
4. **Rens takrenner, avløp og kummer** for løv og rusk slik at vann kan renne fritt.
5. **Sjekk at båten er forsvarlig fortøyd** eller tatt på land.
6. **Lukk vinduer og dører** godt.

### Personlig Forberedelse
- **Følg med på værmeldinger** og råd fra myndighetene (f.eks. på `varsom.no` og `yr.no`).
- **Ha nødutstyr klart** (se Strømbrudd-listen: vann, mat, radio, lommelykt, førstehjelpsutstyr).
- **Lad opp mobiltelefoner** og powerbank.
- **Planlegg for mulig strømbrudd** og tap av kommunikasjon.
- Informer naboer hvis du reiser bort.',
    '## Under en Storm

### Sikkerhet Først
1. **Hold deg innendørs**. Unngå å gå ut for å redde eiendeler når stormen er på sitt verste.
2. **Vær forsiktig nær vinduer** på grunn av fare for flyvende gjenstander.
3. **Unngå å kjøre bil** hvis ikke absolutt nødvendig. Vær obs på veltede trær og oversvømte veier.

### Overvåking
- Følg med på nyheter og informasjon fra myndighetene.
- Se etter tegn på vanninntrenging eller skader på boligen.
- Hvis vann trenger inn og når elektriske installasjoner, slå av hovedsikringen hvis det er trygt.',
    '## Etter en Storm

### Skadevurdering og Opprydding
1. **Sjekk eiendommen for skader** når det er trygt å gå ut. Vær obs på løse takstein, ødelagte strømlinjer (hold avstand!) og skadet vegetasjon.
2. **Fotografer skader** for dokumentasjon til forsikringsselskapet.
3. **Meld fra om skader** til forsikringsselskapet så snart som mulig.
4. **Rydd opp forsiktig**. Vær obs på farer.
5. **Hjelp naboer** hvis du har mulighet.

### Gjenoppretting
- Fyll på nødlagre.
- Evaluer og forbedre beredskapsplanen.',
    'active',
    1
);

INSERT INTO scenario_themes (name, description, before, under, after, status, created_by_user_id)
VALUES (
    'Jordskjelv',
    'Informasjon og råd for forberedelse og håndtering av jordskjelv. Selv om store jordskjelv er sjeldne i Norge, kan de forekomme og forårsake betydelige skader, spesielt på Vestlandet og i Oslofjordområdet.',
    '## Før et Jordskjelv

### Hjemme-Forberedelser
1. **Identifiser trygge steder** i hvert rom (under solide bord, mot en innervegg).
2. **Lær familiemedlemmer** "Slipp ned, Søk dekning, Hold fast".
3. **Fest tunge gjenstander** som bokhyller, speil og lamper til veggen for å hindre at de faller.
4. **Oppbevar tunge og skjøre gjenstander** på lave hyller.
5. **Lær hvordan du skrur av gass, vann og strøm** (hvis nødvendig).
6. **Ha et nødsett klart** (vann, mat, førstehjelpsutstyr, lommelykt, radio, etc.).

### Planlegging
- Lag en kommunikasjonsplan for familien (f.eks. en kontaktperson utenfor området).
- Gjør deg kjent med evakueringsruter fra bygningen din.
- Vurder jordskjelvforsikring hvis du bor i et utsatt område.',
    '## Under et Jordskjelv

### Innendørs
1. **SLIPP NED** på hender og knær.
2. **SØK DEKNING** under et solid bord eller skrivebord. Hvis ikke mulig, søk dekning mot en innervegg og beskytt hodet og nakken med armene. Hold deg unna vinduer, speil og høye møbler.
3. **HOLD FAST** i det du søker dekning under, og vær forberedt på å bevege deg med det.
4. **IKKE løp ut** under rystelsene. Det er større fare for å bli skadet av fallende gjenstander utenfor.
5. **IKKE bruk heis.**

### Utendørs
1. **Flytt deg til et åpent område**, vekk fra bygninger, trær, gatelys og strømledninger.
2. **Slipp deg ned** og bli der til rystelsene stopper.

### I Bil
1. **Stopp bilen** på et trygt sted så raskt som mulig. Unngå å stoppe nær eller under bygninger, trær, broer eller strømledninger.
2. **Bli i bilen** til rystelsene stopper.',
    '## Etter et Jordskjelv

### Umiddelbar Sikkerhet
1. **Vær forberedt på etterskjelv.** Disse kan være sterke nok til å forårsake ytterligere skade.
2. **Sjekk deg selv og andre for skader.** Gi førstehjelp om nødvendig.
3. **Sjekk for farer i hjemmet:**
    - Gasslekkasjer (lukt, suselyd). Hvis du mistenker lekkasje, åpne vinduer, forlat bygningen og varsle brannvesenet fra utsiden. Ikke bruk lysbrytere eller elektriske apparater.
    - Skader på elektriske ledninger (gnister, ødelagte ledninger). Slå av hovedstrømmen hvis det er trygt.
    - Strukturelle skader på bygningen. Hvis bygningen ser usikker ut, evakuer.
4. **Lytt til en batteridrevet radio** for informasjon og instrukser fra myndighetene.

### Oppfølging
- Bruk telefon kun til nødsamtaler for å holde linjene åpne.
- Inspiser skorsteiner for skader.
- Hold deg unna skadede områder med mindre du har fått beskjed om at det er trygt.
- Hjelp naboer, spesielt de som kan trenge ekstra assistanse.',
    'active',
    1
);

INSERT INTO scenario_themes (name, description, before, under, after, status, created_by_user_id)
VALUES (
    'Flom',
    'Veiledning for å forberede seg på og håndtere flom forårsaket av kraftig regn, snøsmelting eller stormflo. Flom kan føre til store skader på eiendom og infrastruktur.',
    '## Før en Flom

### Følg med og Planlegg
1. **Hold deg oppdatert** på flomvarsler fra `varsom.no` og værmeldinger fra `yr.no`. Abonner på varsler.
2. **Følg med på lokale nyheter** og informasjon fra din kommune.
3. **Kjenn flomrisikoen** der du bor.

### Sikre Eiendom
1. **Rens avløp, sluk, kummer, grøfter og bekkeløp** for løv, is, kvister og annet rusk slik at vannet kan renne fritt.
2. **Flytt verdisaker** og gjenstander fra kjeller, garasje og lavtliggende områder til et høyere sted.
3. **Sikre eller flytt løse gjenstander utendørs** (hagemøbler, grill, søppelkasser) som kan flyte av gårde eller forårsake skade.
4. **Lukk vinduer og dører**, spesielt til kjeller. Tett eventuelle åpninger med sandsekker om nødvendig.
5. **Flytt biler, campingvogner, båter** og annet verdifullt utstyr til et tryggere sted hvis det er fare for flom.
6. **Vurder å anskaffe en vannpumpe.**

### Personlig Beredskap
- **Ha nødutstyr klart** (se Strømbrudd-listen: vann, mat for minst 3 dager, radio, lommelykt, førstehjelpsutstyr, medisiner).
- **Lad mobiltelefoner** og powerbank.
- Planlegg for at veier kan bli stengt og at strøm, vann og internett kan bli borte.
- Avtal med naboer om å se etter hverandres eiendommer hvis noen er bortreist.',
    '## Under en Flom

### Sikkerhetstiltak
1. **Følg råd og evakueringsordre** fra myndighetene umiddelbart.
2. **Unngå å gå eller kjøre gjennom flomvann.** Vannet kan være dypere og strømmen sterkere enn du tror. Det kan også inneholde kloakk og farlige gjenstander. Kun 15 cm med strømmende vann kan velte en person.
3. **Hold deg unna elver og bekker** med stor vannføring. Erosjon langs kantene kan utløse skred.
4. **Slå av strømmen** i områder av huset som er truet av vanninntrenging, hvis det er trygt å gjøre det. Ikke rør elektrisk utstyr hvis du står i vann.
5. **Lytt til lokalradio** for oppdatert informasjon.

### Hjelp
- Kontakt nødetater (110, 112, 113) eller kommunen ved akutt fare for liv og helse, eller fare for skade på ditt hjem.',
    '## Etter en Flom

### Returner Forsiktig
1. **Vent på klarsignal** fra myndighetene før du returnerer til et evakuert område.
2. **Vær forsiktig** når du går inn i bygninger som har vært oversvømt. Strukturelle skader kan ha oppstått.
3. **Sjekk for synlige skader**, som sprekker i grunnmur.

### Skadehåndtering og Helse
1. **Fotografer all skade** før du begynner opprydding, for dokumentasjon til forsikring.
2. **Meld skaden** til forsikringsselskapet ditt.
3. **Unngå direkte kontakt med flomvann** da det kan være forurenset. Bruk verneutstyr (hansker, støvler).
4. **Kast matvarer** som har vært i kontakt med flomvann. Drikk ikke vann fra springen før det er erklært trygt.
5. **Luft ut og tørk opp** berørte områder så raskt som mulig for å hindre muggvekst.
6. **Få elektriske systemer sjekket** av en fagperson før strømmen slås på igjen hvis det har vært vann i nærheten av elektriske installasjoner.
7. **Rengjør og desinfiser** alt som har vært i kontakt med flomvann.

### Gjenoppbygging
- Fyll på nødlagrene.
- Vurder flomsikringstiltak for fremtiden.',
    'active',
    1
);

INSERT INTO scenario_themes (name, description, before, under, after, status, created_by_user_id)
VALUES (
    'Skogbrann',
    'Veiledning for å forberede og håndtere skogbrann. Skogbranner kan spre seg raskt, spesielt i tørre perioder med vind, og utgjør en trussel mot liv, eiendom og natur.',
    '## Før en Skogbrann

### Generell Forebygging
1.  **Respekter bålforbudet**: Det er generelt bålforbud i og nær skog og annen utmark mellom 15. april og 15. september. Sjekk alltid lokale forskrifter.
2.  **Vær forsiktig med all ild**: Unngå bruk av åpen ild når skogbrannfaren er høy.
3.  **Slokk sigaretter forsvarlig**: Sørg for at sigarettstumper er helt slokket før de kastes (kun der det er tillatt).
4.  **Parker aldri på tørt gress**: Varm katalysator kan antenne gresset.
5.  **Følg med på skogbrannfarevarselet**: Sjekk `yr.no` eller `varsom.no`.

### For Huseiere i Utsatte Områder
1.  **Lag en sikkerhetssone**: Fjern brennbart materiale (løv, kvister, høyt gress) minst 10-15 meter rundt hus og bygninger.
2.  **Beskjær trær**: Fjern lave greiner på trær nær bygninger, og greiner som henger over tak eller pipe.
3.  **Rens takrenner** for løv og barnåler.
4.  **Oppbevar ved** og annet brennbart materiale trygt unna bygninger.
5.  **Vurder brannsikkert takmateriale.**
6.  **Sørg for god adkomst** for brannbiler (tydelig merket adresse, fri vei).
7.  **Ha slokkeutstyr tilgjengelig**: Hageslange, brannslokningsapparat, brannteppe, spade.
8.  **Ha en evakueringsplan** for familien og øv på den.
9.  **Ha en nødpakke klar** (se råd for Strømbrudd).',
    '## Under en Skogbrann

### Hvis du Oppdager Brann
1.  **Varsle brannvesenet umiddelbart på 110.** Oppgi nøyaktig sted. Ikke anta at andre har varslet.
2.  **Prøv å slokke brannen KUN hvis det er trygt** og du har egnet utstyr, uten å utsette deg selv eller andre for fare. Ikke ta sjanser.
3.  **Evakuer** hvis brannen er stor, sprer seg raskt, eller hvis du blir bedt om det av myndighetene.

### Ved Evakuering
1.  **Følg myndighetenes instrukser** og evakueringsruter.
2.  **Lukk alle vinduer og dører** på huset ditt. Slå av ventilasjon.
3.  **Ta med nødpakken** og viktige dokumenter.
4.  **Informer naboer** hvis mulig.

### Beskyttelse mot Røyk
1.  **Hold deg innendørs** hvis mulig, med lukkede vinduer og dører.
2.  **Bruk N95-maske** hvis du må være ute i røykfylte områder.
3.  **Lytt til råd** fra helsemyndighetene angående røyknivåer.',
    '## Etter en Skogbrann

### Retur til Området
1.  **Returner ikke hjem** før myndighetene har gitt klarsignal.
2.  **Vær obs på farer**: Glør som kan blusse opp, ustabile trær, skadede strømlinjer.
3.  **Hold avstand** til nedbrente områder til de er erklært trygge.

### Skadevurdering og Opprydning
1.  **Sjekk boligen for skader** og gjenværende glør (loft, krypkjeller, tak).
2.  **Dokumenter skader** med bilder for forsikringsselskapet.
3.  **Vær forsiktig ved opprydding**: Bruk verneutstyr (hansker, maske).
4.  **Sjekk vannkvaliteten** hvis du har privat brønn; den kan være forurenset av aske eller slokkemidler.

### Gjenoppbygging og Lærdom
1.  **Kontakt forsikringsselskapet** for veiledning.
2.  **Gjennomgå og forbedre** egen beredskap og brannsikringstiltak.',
    'active',
    1
);

INSERT INTO scenario_themes (name, description, before, under, after, status, created_by_user_id)
VALUES (
    'Krig og Væpnet Konflikt',
    'Råd for sivilbefolkningens beredskap og håndtering ved krig eller væpnet konflikt. Slike situasjoner kan medføre omfattende samfunnsforstyrrelser, direkte trusler og behov for sivil støtte til totalforsvaret.',
    '## Før Krig eller Væpnet Konflikt

### Egenberedskap (minimum 7 dager)
1.  **Drikkevann**: Lagre minst 20 liter rent vann per person.
2.  **Mat**: Ha et lager av tørrvarer og hermetikk som ikke krever mye tilberedning.
3.  **Varme**: Alternative varmekilder (ved, parafinovn), varme klær, tepper.
4.  **Lys**: Lommelykter, stearinlys, fyrstikker.
5.  **Informasjon**: Batteridrevet DAB-radio, fulladet powerbank til mobil.
6.  **Medisiner og førstehjelp**: Nødvendige medisiner, omfattende førstehjelpsskrin.
7.  **Hygieneartikler**: Såpe, desinfeksjonsmiddel, toalettpapir.
8.  **Kontanter**: Ha noe kontanter tilgjengelig.
9.  **Jodtabletter**: Anbefales for personer under 40 år, gravide og ammende (skal kun tas etter råd fra myndighetene).
10. **Viktige dokumenter**: Samle og sikre kopier av ID-papirer, forsikringer, etc.

### Planlegging og Kunnskap
1.  **Informasjonskanaler**: Vit hvor du finner offisiell informasjon (Nødvarsel, `sikkerhverdag.no`, `dsb.no`, NRK).
2.  **Vær kildekritisk**: Vær forberedt på desinformasjon. Stol på offisielle kilder.
3.  **Tilfluktsrom**: Finn ut hvor nærmeste offentlige tilfluktsrom er, hvis tilgjengelig. Forstå at råd om å søke ly hjemme også kan bli gitt.
4.  **Evakueringsplan**: Tenk gjennom evakueringsmuligheter og hva du må ha med deg.
5.  **Samarbeid**: Snakk med naboer om samarbeid og felles ressurser.',
    '## Under Krig eller Væpnet Konflikt

### Følg Myndighetenes Råd
1.  **Lytt til offisielle kanaler** (radio, Nødvarsel) og følg instrukser nøye.
2.  **Evakuering**: Evakuer hvis du får beskjed om det. Følg angitte ruter.
3.  **Tilflukt**: Søk dekning i tilfluktsrom eller det sikreste stedet i boligen din (kjeller, rom uten vinduer) ved flyalarm eller angrep. Hold deg unna vinduer.

### Personlig Sikkerhet
1.  **Begrens ferdsel utendørs**, spesielt under aktive trusler.
2.  **Vær forberedt på brudd** i strøm, vann, kommunikasjon og vareleveranser.
3.  **Spar på ressurser** (vann, mat, drivstoff).
4.  **Unngå å spre rykter.** Del kun verifisert informasjon.
5.  **Hjelp andre** hvis det er trygt. Prioriter sårbare grupper.
6.  **Slå av lys og dekk til vinduer** om natten hvis det er gitt beskjed om blending.',
    '## Etter Krig eller Væpnet Konflikt

### Sikkerhet Først
1.  **Vent på offisiell tillatelse** før du forlater tilfluktsrom eller returnerer til evakuerte områder.
2.  **Vær ekstremt forsiktig med ueksplodert ammunisjon (UXO)**. Ikke rør mistenkelige gjenstander. Rapporter dem til politi eller Sivilforsvaret.
3.  **Sjekk boligen for skader** og farer før du tar den i bruk.
4.  **Følg med på informasjon** fra myndighetene om sikkerhet, helse og gjenoppretting av tjenester.

### Gjenoppbygging og Normalisering
1.  **Rapporter skader** og behov til relevante myndigheter.
2.  **Bidra til samfunnsinnsatsen** for opprydding og gjenoppbygging, etter anvisning.
3.  **Vær tålmodig**: Det kan ta lang tid å gjenopprette normale forhold.
4.  **Ta vare på din psykiske helse**: Søk støtte hvis du eller dine nærmeste sliter med ettervirkninger.
5.  **Fortsett å være kildekritisk** da informasjonssituasjonen kan være ustabil.',
    'active',
    1
);

INSERT INTO scenario_themes (name, description, before, under, after, status, created_by_user_id)
VALUES (
    'Atomulykke',
    'Råd for beredskap ved atomulykker, som kan føre til utslipp av radioaktive stoffer. Dette inkluderer råd om jodtabletter og hvordan man beskytter seg mot radioaktivt nedfall.',
    '## Før en Atomulykke

### Egenberedskap
1.  **Jodtabletter (Kaliumjodid)**:
    -   Anbefales lagret hjemme for alle under 40 år, gravide, ammende og de med hjemmeboende barn.
    -   Kjøpes reseptfritt på apotek.
    -   **Skal KUN tas etter direkte råd fra myndighetene.** Gir beskyttelse mot radioaktivt jod, ikke andre radioaktive stoffer.
2.  **Nødproviant og Vann**: Ha lager for minst 3 dager (helst 7 dager) med mat og drikke (se Strømbrudd). Dette er viktig hvis man må oppholde seg lenge innendørs.
3.  **Batteridrevet DAB-radio**: For å motta viktig informasjon hvis strøm og internett er nede.
4.  **Lommelykt og batterier.**
5.  **Førstehjelpsutstyr.**
6.  **Plan for innendørs opphold**:
    -   Identifiser det best egnede rommet for langvarig opphold (helst uten vinduer, i kjeller eller sentralt i huset).
    -   Ha plastfolie og sterk tape klart for å kunne tette ventilasjonsåpninger, sprekker rundt vinduer og dører om nødvendig.

### Informasjon
1.  **Kjenn til varslingskanaler**: Nødvarsel på mobil, radio (NRK P1), TV, og nettsider som `dsa.no` (Direktoratet for strålevern og atomsikkerhet) og `sikkerhverdag.no`.
2.  **Forstå rådene**: Sett deg inn i hva rådene om å "holde seg inne" og "ta jodtabletter" innebærer.',
    '## Under en Atomulykke (ved radioaktivt nedfall)

### Følg Myndighetenes Råd Nøye
1.  **Hold deg innendørs**: Hvis myndighetene anbefaler det, bli inne. Lukk alle dører, vinduer, ventiler, og slå av ventilasjonsanlegg. Tett eventuelle sprekker.
2.  **Ta jodtabletter KUN hvis og når myndighetene gir beskjed om det.** Rådene vil spesifisere hvem som skal ta tabletter.
3.  **Lytt til informasjon**: Følg kontinuerlig med på offisielle kanaler for oppdateringer og spesifikke instrukser for ditt område.
4.  **Begrens eksponering**:
    -   Hvis du må kortvarig ut, dekk til hud og hår, og bruk om mulig en maske.
    -   Ved retur inn, ta av ytterklær og sko i yttergangen. Vask hender og ansikt grundig.
5.  **Mat og drikke**: Ikke spis eller drikk mat eller vann som kan være forurenset (f.eks. fra hagen, ubeskyttet regnvann). Bruk lagret mat og flaskevann.
6.  **Beskytt kjæledyr**: Hold også kjæledyr innendørs.

### Ved Evakuering
- Følg kun offisielle evakueringsordre og ruter. Ta med nødpakke.',
    '## Etter en Atomulykke

### Fortsatt Informasjonsinnhenting
1.  **Følg myndighetenes råd** om når det er trygt å gå ut, og hvilke forholdsregler som gjelder for mat, vann og ferdsel.
2.  **Vær tålmodig**: Restriksjoner kan vare over tid.

### Håndtering av Forurensning
1.  **Rengjøring**: Følg råd om eventuell rengjøring av bolig og eiendom.
2.  **Mat og Vann**: Myndighetene vil overvåke og gi råd om sikkerheten til lokalprodusert mat og drikkevann. Ikke konsumer mat fra egen hage eller vilt/fisk fra området før det er klarert.
3.  **Helse**: Vær oppmerksom på helseråd. Kontakt lege ved bekymring eller symptomer. Personer som har tatt jodtabletter pga. spesifikke tilstander bør følges opp av lege.

### Psykisk Helse
- Det er normalt å føle uro. Snakk med noen og søk hjelp om nødvendig.',
    'active',
    1
);

-- CRISIS EVENTS
INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active, scenario_theme_id)
VALUES ('Flood Warning', 'Potential flooding in downtown area', 'yellow', 60.4300, 10.3950, 1.0, CURRENT_TIMESTAMP, 1, TRUE, 4);
INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active, scenario_theme_id)
VALUES ('Storm Alert', 'Heavy storm expected tonight', 'green', 60.4250, 11.3900, 5.0, DATEADD('DAY', 1, CURRENT_TIMESTAMP), 1, TRUE, 2);
INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active, scenario_theme_id)
VALUES ('lightning storm', 'Potential flooding in downtown area', 'red', 59.4300, 10.3950, 1.0, TIMESTAMP '2025-05-01 12:00:00', 1, TRUE, 2);
-- Inactive crisis event
INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active, scenario_theme_id)
VALUES ('Past Earthquake', 'Earthquake event that has ended', 'red', 63.4200, 12.4000, 20, DATEADD('DAY', -10, CURRENT_TIMESTAMP), 1, FALSE, 3);

INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active, scenario_theme_id)
VALUES ('Severe Storm Warning', 'Heavy storm with potential flooding in central Trondheim', 'red', 63.4300, 10.3950, 2.0, CURRENT_TIMESTAMP, 1, TRUE, 2);

INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active, scenario_theme_id)
VALUES ('Power Outage Alert', 'Planned maintenance causing temporary power disruption', 'yellow', 63.4280, 10.3940, 1.5, CURRENT_TIMESTAMP, 1, TRUE, 1);

INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active, scenario_theme_id)
VALUES ('Minor Traffic Incident', 'Road closure due to minor accident', 'green', 63.4290, 10.3960, 0.5, CURRENT_TIMESTAMP, 1, TRUE, 2);


-- CRISIS EVENTS CHANGES

INSERT INTO crisis_event_changes (crisis_event_id, change_type, old_value, new_value, created_by_user_id, created_at, updated_at)
VALUES (1, 'creation', NULL, 'Initial creation of crisis event.', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO crisis_event_changes (crisis_event_id, change_type, old_value, new_value, created_by_user_id, created_at, updated_at)
VALUES (1, 'level_change', 'Level 2', 'Level 3', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO crisis_event_changes (crisis_event_id, change_type, old_value, new_value, created_by_user_id, created_at, updated_at)
VALUES (1, 'description_update', 'Flooding in lower region', 'Flooding spreading to midtown area', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO crisis_event_changes (crisis_event_id, change_type, old_value, new_value, created_by_user_id, created_at, updated_at)
VALUES (1, 'creation', NULL, 'Initial creation of crisis event.', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO crisis_event_changes (crisis_event_id, change_type, old_value, new_value, created_by_user_id, created_at, updated_at)
VALUES (1, 'level_change', 'Level 2', 'Level 3', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO crisis_event_changes (crisis_event_id, change_type, old_value, new_value, created_by_user_id, created_at, updated_at)
VALUES (1, 'description_update', 'Flooding in lower region', 'Flooding spreading to midtown area', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO crisis_event_changes (crisis_event_id, change_type, old_value, new_value, created_by_user_id, created_at, updated_at)
VALUES (1, 'creation', NULL, 'Initial creation of crisis event.', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO crisis_event_changes (crisis_event_id, change_type, old_value, new_value, created_by_user_id, created_at, updated_at)
VALUES (1, 'level_change', 'Level 2', 'Level 3', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO crisis_event_changes (crisis_event_id, change_type, old_value, new_value, created_by_user_id, created_at, updated_at)
VALUES (1, 'description_update', 'Flooding in lower region', 'Flooding spreading to midtown area', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO crisis_event_changes (crisis_event_id, change_type, old_value, new_value, created_by_user_id, created_at, updated_at)
VALUES (2, 'epicenter_moved', '40.7128,-74.0060', '40.7306,-73.9352', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO crisis_event_changes (crisis_event_id, change_type, old_value, new_value, created_by_user_id, created_at, updated_at)
VALUES (2, 'level_change', 'Level 1', 'Level 2', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- NEWS ARTICLES
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('New Emergency Response Guidelines', 'The city has updated its emergency response guidelines. Key changes include...', DATEADD('DAY', -2, CURRENT_TIMESTAMP), 1, 2, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Draft Article', 'This is a draft article that is not yet published...', CURRENT_TIMESTAMP, 1, 1, 'draft');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Archived Article', 'This is an old article that has been archived...', DATEADD('DAY', -30, CURRENT_TIMESTAMP), 1, 2, 'archived');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('New Emergency Response Guidelines', 'The city has updated its emergency response guidelines. Key changes include...', DATEADD('DAY', -2, CURRENT_TIMESTAMP), 1, 2, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Draft Article', 'This is a draft article that is not yet published...', CURRENT_TIMESTAMP, 1, 1, 'draft');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Archived Article', 'This is an old article that has been archived...', DATEADD('DAY', -30, CURRENT_TIMESTAMP), 1, 2, 'archived');

-- Winter storms
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', DATEADD('DAY', -2, CURRENT_TIMESTAMP), 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');


-- HOUSEHOLD ADMINS
INSERT INTO household_admins (user_id, household_id) VALUES (2, 1); -- Alice is admin of Smith Family
INSERT INTO household_admins (user_id, household_id) VALUES (4, 3); -- Mike is admin of Brown Residence

-- QUIZZES
INSERT INTO quizzes (name, description, created_by_user_id) VALUES ('Emergency Preparedness Basics', 'A quiz to test your knowledge on basic emergency preparedness.', 1);
INSERT INTO quizzes (id, name, description, created_by_user_id) VALUES (123, 'test', 'This is a test quiz.', 1);

-- QUIZ QUESTIONS
INSERT INTO quiz_questions (quiz_id, question_body, position) VALUES (1, 'What is the recommended amount of water to store per person per day?', 1);
INSERT INTO quiz_questions (quiz_id, question_body, position) VALUES (1, 'Which of the following items is NOT recommended for an emergency kit?', 2);

-- QUIZ ANSWERS
-- For Question 1
INSERT INTO quiz_answers (quiz_id, question_id, answer_body, is_correct) VALUES (1, 1, '1 liter', FALSE);
INSERT INTO quiz_answers (quiz_id, question_id, answer_body, is_correct) VALUES (1, 1, '2 liters', FALSE);
INSERT INTO quiz_answers (quiz_id, question_id, answer_body, is_correct) VALUES (1, 1, '4 liters', TRUE);
-- For Question 2
INSERT INTO quiz_answers (quiz_id, question_id, answer_body, is_correct) VALUES (1, 2, 'Flashlight', FALSE);
INSERT INTO quiz_answers (quiz_id, question_id, answer_body, is_correct) VALUES (1, 2, 'Canned food', FALSE);
INSERT INTO quiz_answers (quiz_id, question_id, answer_body, is_correct) VALUES (1, 2, 'Perishable meat', TRUE);

-- USER QUIZ ATTEMPTS
INSERT INTO user_quiz_attempts (user_id, quiz_id, completed_at) VALUES (2, 1, CURRENT_TIMESTAMP);

-- USER QUIZ ANSWERS (Alice's attempt)
-- She selects '4 liters' for Q1 and 'Perishable meat' for Q2
INSERT INTO user_quiz_answers (user_quiz_attempt_id, quiz_id, question_id, answer_id) VALUES (1, 1, 1, 3);
INSERT INTO user_quiz_answers (user_quiz_attempt_id, quiz_id, question_id, answer_id) VALUES (1, 1, 2, 6);
