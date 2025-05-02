-- ROLES
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('SUPERADMIN');

-- HOUSEHOLDS
INSERT INTO households (name, address, population_count) VALUES ('Smith Family', '123 Main St, Anytown', 4);
INSERT INTO households (name, address, population_count) VALUES ('Johnson Household', '456 Oak Ave, Somewhere', 3);
INSERT INTO households (name, address, population_count) VALUES ('Brown Residence', '789 Pine St, Elsewhere', 2);

-- USERS (password_hash is 'password' for all users)
INSERT INTO users (email, password_hash, phone_number, role_id, household_id, first_name, last_name, email_verified, location_sharing_enabled)
VALUES ('admin@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '+4712345678', 2, NULL, 'Admin', 'User', TRUE, FALSE);

INSERT INTO users (email, password_hash, phone_number, role_id, household_id, first_name, last_name, home_address, home_latitude, home_longitude, email_verified, location_sharing_enabled)
VALUES ('alice@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '+4723456789', 1, 1, 'Alice', 'L', '123 Main St, Anytown', 63.4305, 10.3951, TRUE, TRUE);

INSERT INTO users (email, password_hash, phone_number, role_id, household_id, first_name, last_name, home_address, home_latitude, home_longitude, email_verified, location_sharing_enabled)
VALUES ('sarah@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '+4734567890', 1, NULL, 'Sarah', 'Johnson', '456 Oak Ave, Somewhere', 63.4205, 10.4051, TRUE, TRUE);

INSERT INTO users (email, password_hash, phone_number, role_id, household_id, first_name, last_name, home_address, home_latitude, home_longitude, email_verified, location_sharing_enabled)
VALUES ('mike@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '+4745678901', 3, 3, 'Mike', 'Brown', '789 Pine St, Elsewhere', 63.4105, 10.3851, TRUE, FALSE);

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
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Canned Beans', 'stk', 120.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Rice', 'kg', 1300.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Bottled Water', 'l', 0.00, 'water');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Painkillers', 'mg', 0.00, 'medicine');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Antibiotic', 'dose', 0.00, 'medicine');

-- Additional product types for Smith Family (household_id = 1)
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Oatmeal', 'kg', 370.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Sugar', 'kg', 4000.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Salt', 'kg', 0.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Cooking Oil', 'l', 8840.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Powdered Milk', 'kg', 500.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Canned Tuna', 'stk', 120.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Peanut Butter', 'stk', 588.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Crackers', 'stk', 70.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Dried Fruit', 'kg', 320.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Instant Noodles', 'stk', 350.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Honey', 'kg', 3040.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Tea Bags', 'stk', 2.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Coffee', 'kg', 2.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Cereal Bars', 'stk', 90.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Canned Corn', 'stk', 80.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Canned Tomatoes', 'stk', 30.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Apple Juice', 'l', 46.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Bottled Sparkling Water', 'l', 0.00, 'water');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Electrolyte Solution', 'l', 0.00, 'water');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Antihistamine', 'mg', 0.00, 'medicine');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Cough Syrup', 'dose', 0.00, 'medicine');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (1, 'Bandages', 'stk', 0.00, 'medicine');

-- Johnson Household (household_id = 2)
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (2, 'Flour', 'kg', 1640.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (2, 'Pasta', 'kg', 1570.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (2, 'Purified Water', 'l', 0.00, 'water');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (2, 'Flashlight', 'stk', 0.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (2, 'Aspirin', 'mg', 0.00, 'medicine');

-- Brown Residence (household_id = 3)
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (3, 'Bread', 'kg', 2650.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (3, 'Canned Soup', 'stk', 200.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (3, 'Toilet Paper', 'stk', 0.00, 'food');
INSERT INTO product_types (household_id, name, unit, calories_per_unit, category) VALUES (3, 'Soap', 'stk', 0.00, 'food');

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
-- Canned Tomatoes (id=29)
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
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 1, 1); -- Neighborhood Watch, Smith Family, Canned Beans
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 2, 5); -- Neighborhood Watch, Johnson Household, Flour
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (2, 1, 3); -- Emergency Response Team, Smith Family, Bottled Water
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (2, 3, 10); -- Emergency Response Team, Brown Residence, Canned Soup

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
INSERT INTO meeting_places (household_id, name, latitude, longitude, address, created_by_user_id)
VALUES (1, 'Park Entrance', 63.4290, 10.3940, 'City Park Main Entrance', 2);
INSERT INTO meeting_places (household_id, name, latitude, longitude, address, created_by_user_id)
VALUES (2, 'School Playground', 63.4300, 10.3960, 'Elementary School', 3);
INSERT INTO meeting_places (household_id, name, latitude, longitude, address, created_by_user_id)
VALUES (3, 'Shopping Mall', 63.4320, 10.3970, 'Downtown Mall', 4);

-- SCENARIO THEMES
INSERT INTO scenario_themes (name, description, instructions, status, created_by_user_id)
VALUES ('Power Outage', 'Preparing for extended power outages', 'Have flashlights, batteries, and non-perishable food ready. Unplug sensitive electronics.', 'active', 1);
INSERT INTO scenario_themes (name, description, instructions, status, created_by_user_id)
VALUES ('Natural Disaster', 'Earthquake, flood, or storm preparation', 'Follow evacuation orders. Prepare an emergency kit and know your local shelter locations.', 'active', 1);
INSERT INTO scenario_themes (name, description, instructions, status, created_by_user_id)
VALUES ('Public Health Emergency', 'Pandemic or disease outbreak', 'Practice good hygiene, follow public health advice, and stock up on essential medicines.', 'active', 1);

-- CRISIS EVENTS
INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active)
VALUES ('Flood Warning', 'Potential flooding in downtown area', 'yellow', 63.4300, 10.3950, 1000.0, CURRENT_TIMESTAMP, 1, TRUE);
INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active)
VALUES ('Storm Alert', 'Heavy storm expected tonight', 'green', 60.4250, 11.3900, 5000.0, DATEADD('DAY', 1, CURRENT_TIMESTAMP), 1, TRUE);
INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active)
VALUES ('lightning storm', 'Potential flooding in downtown area', 'red', 63.4300, 10.3950, 1000.0, TIMESTAMP '2025-05-01 12:00:00', 1, TRUE);
-- Inactive crisis event
INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active, scenario_theme_id)
VALUES ('Past Earthquake', 'Earthquake event that has ended', 'red', 63.4200, 10.4000, 20, DATEADD('DAY', -10, CURRENT_TIMESTAMP), 1, FALSE, 2);

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
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('New Emergency Response Guidelines', 'The city has updated its emergency response guidelines. Key changes include...', DATEADD('DAY', -2, CURRENT_TIMESTAMP), 1, 2, 'published');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Draft Article', 'This is a draft article that is not yet published...', CURRENT_TIMESTAMP, 1, 1, 'draft');
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id, status)
VALUES ('Archived Article', 'This is an old article that has been archived...', DATEADD('DAY', -30, CURRENT_TIMESTAMP), 1, 2, 'archived');

-- HOUSEHOLD ADMINS
INSERT INTO household_admins (user_id, household_id) VALUES (2, 1); -- Alice is admin of Smith Family
INSERT INTO household_admins (user_id, household_id) VALUES (4, 3); -- Mike is admin of Brown Residence

-- QUIZZES
INSERT INTO quizzes (name, description, created_by_user_id) VALUES ('Emergency Preparedness Basics', 'A quiz to test your knowledge on basic emergency preparedness.', 1);

-- QUIZ QUESTIONS
INSERT INTO quiz_questions (quiz_id, question_body) VALUES (1, 'What is the recommended amount of water to store per person per day?');
INSERT INTO quiz_questions (quiz_id, question_body) VALUES (1, 'Which of the following items is NOT recommended for an emergency kit?');

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
