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
INSERT INTO product_types (household_id, name, unit, calories_per_unit, is_water) VALUES (1, 'Canned Beans', 'stk', 120.00, FALSE);
INSERT INTO product_types (household_id, name, unit, calories_per_unit, is_water) VALUES (1, 'Rice', 'kg', 1300.00, FALSE);
INSERT INTO product_types (household_id, name, unit, calories_per_unit, is_water) VALUES (1, 'Bottled Water', 'l', 0.00, TRUE);
INSERT INTO product_types (household_id, name, unit, calories_per_unit, is_water) VALUES (1, 'Painkillers', 'stk', 0.00, FALSE);

-- Johnson Household (household_id = 2)
INSERT INTO product_types (household_id, name, unit, calories_per_unit, is_water) VALUES (2, 'Flour', 'kg', 1640.00, FALSE);
INSERT INTO product_types (household_id, name, unit, calories_per_unit, is_water) VALUES (2, 'Pasta', 'kg', 1570.00, FALSE);
INSERT INTO product_types (household_id, name, unit, calories_per_unit, is_water) VALUES (2, 'Purified Water', 'l', 0.00, TRUE);
INSERT INTO product_types (household_id, name, unit, calories_per_unit, is_water) VALUES (2, 'Flashlight', 'stk', 0.00, FALSE);

-- Brown Residence (household_id = 3)
INSERT INTO product_types (household_id, name, unit, calories_per_unit, is_water) VALUES (3, 'Bread', 'kg', 2650.00, FALSE);
INSERT INTO product_types (household_id, name, unit, calories_per_unit, is_water) VALUES (3, 'Canned Soup', 'stk', 200.00, FALSE);
INSERT INTO product_types (household_id, name, unit, calories_per_unit, is_water) VALUES (3, 'Toilet Paper', 'stk', 0.00, FALSE);
INSERT INTO product_types (household_id, name, unit, calories_per_unit, is_water) VALUES (3, 'Soap', 'stk', 0.00, FALSE);

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

-- GROUP INVENTORY CONTRIBUTIONS
INSERT INTO group_inventory_contributions (group_id, household_id, product_id, quantity) VALUES (1, 1, 1, 5); -- Neighborhood Watch, Smith Family, Canned Beans
INSERT INTO group_inventory_contributions (group_id, household_id, product_id, quantity) VALUES (1, 2, 5, 1); -- Neighborhood Watch, Johnson Household, Flour
INSERT INTO group_inventory_contributions (group_id, household_id, product_id, quantity) VALUES (2, 1, 3, 12); -- Emergency Response Team, Smith Family, Bottled Water
INSERT INTO group_inventory_contributions (group_id, household_id, product_id, quantity) VALUES (2, 3, 10, 3); -- Emergency Response Team, Brown Residence, Canned Soup

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

-- CRISIS EVENTS
INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, start_time, created_by_user_id, active)
VALUES ('Flood Warning', 'Potential flooding in downtown area', 'yellow', 63.4300, 10.3950, CURRENT_TIMESTAMP, 1, TRUE);
INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, start_time, created_by_user_id, active)
VALUES ('Storm Alert', 'Heavy storm expected tonight', 'green', 63.4250, 10.3900, DATEADD('DAY', 1, CURRENT_TIMESTAMP), 1, TRUE);

-- SCENARIO THEMES
INSERT INTO scenario_themes (name, description, created_by_user_id)
VALUES ('Power Outage', 'Preparing for extended power outages', 1);
INSERT INTO scenario_themes (name, description, created_by_user_id)
VALUES ('Natural Disaster', 'Earthquake, flood, or storm preparation', 1);
INSERT INTO scenario_themes (name, description, created_by_user_id)
VALUES ('Public Health Emergency', 'Pandemic or disease outbreak', 1);

-- GAMIFICATION ACTIVITIES
INSERT INTO gamification_activities (name, category, description, created_by_user_id)
VALUES ('Complete Inventory', 'Preparation', 'Add at least 10 items to your household inventory', 1);
INSERT INTO gamification_activities (name, category, description, created_by_user_id)
VALUES ('Create Meeting Place', 'Planning', 'Define a meeting place for your household', 1);
INSERT INTO gamification_activities (name, category, description, created_by_user_id)
VALUES ('Join Group', 'Community', 'Join a community preparedness group', 1);

-- USER GAMIFICATION ACTIVITIES
INSERT INTO user_gamification_activities (user_id, activity_id, status, score, completed_at)
VALUES (2, 1, 'completed', 100, CURRENT_TIMESTAMP);
INSERT INTO user_gamification_activities (user_id, activity_id, status)
VALUES (2, 2, 'pending');
INSERT INTO user_gamification_activities (user_id, activity_id, status, score, completed_at)
VALUES (3, 3, 'completed', 50, CURRENT_TIMESTAMP);

-- NOTIFICATION PREFERENCES
INSERT INTO notification_preferences (user_id, preference_type, enabled)
VALUES (2, 'expiration_reminder', TRUE);
INSERT INTO notification_preferences (user_id, preference_type, enabled)
VALUES (2, 'crisis_alert', TRUE);
INSERT INTO notification_preferences (user_id, preference_type, enabled)
VALUES (3, 'expiration_reminder', FALSE);
INSERT INTO notification_preferences (user_id, preference_type, enabled)
VALUES (3, 'crisis_alert', TRUE);
INSERT INTO notification_preferences (user_id, preference_type, enabled)
VALUES (4, 'location_request', FALSE);

-- NOTIFICATION
INSERT INTO notifications (user_id, preference_type, target_type, target_id, description, notify_at, sent_at, read_at, created_at)
VALUES (2, 'crisis_alert', 'event', 101, 'Severe weather alert for your region.', '2025-04-28 14:00:00', NULL, NULL, '2025-04-28 13:45:00');
INSERT INTO notifications (user_id, preference_type, target_type, target_id, description, notify_at, sent_at, read_at, created_at)
VALUES (2, 'crisis_alert', 'event', 102, 'danger: alan walker in the streets', '2025-04-29 09:00:00', NULL, NULL, '2025-04-28 13:55:00');
INSERT INTO notifications (user_id, preference_type, target_type, target_id, description, notify_at, sent_at, read_at, created_at)
VALUES (3, 'crisis_alert', 'event', 103, 'A big bomb on the way', '2025-04-30 16:30:00', NULL, NULL, '2025-04-28 14:00:00');

-- NEWS ARTICLES
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1, 1);
INSERT INTO news_articles (title, content, published_at, created_by_user_id, crisis_event_id)
VALUES ('New Emergency Response Guidelines', 'The city has updated its emergency response guidelines. Key changes include...', DATEADD('DAY', -2, CURRENT_TIMESTAMP), 1, 2);

-- HOUSEHOLD ADMINS
INSERT INTO household_admins (user_id, household_id) VALUES (2, 1); -- Alice is admin of Smith Family
INSERT INTO household_admins (user_id, household_id) VALUES (4, 3); -- Mike is admin of Brown Residence
