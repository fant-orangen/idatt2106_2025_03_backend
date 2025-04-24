-- ROLES
INSERT INTO roles (id, name) VALUES (1, 'USER');
INSERT INTO roles (id, name) VALUES (2, 'ADMIN');
INSERT INTO roles (id, name) VALUES (3, 'SUPERADMIN');

-- HOUSEHOLDS
INSERT INTO households (id, name, population_count) VALUES (1, 'Smith Family', 4);
INSERT INTO households (id, name, population_count) VALUES (2, 'Johnson Household', 3);
INSERT INTO households (id, name, population_count) VALUES (3, 'Brown Residence', 2);

-- USERS (password_hash is 'password' for all users)
INSERT INTO users (id, email, password_hash, role_id, household_id, name, email_verified, location_sharing_enabled) 
VALUES (1, 'admin@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 2, NULL, 'Admin User', TRUE, FALSE);

INSERT INTO users (id, email, password_hash, role_id, household_id, name, home_address, home_latitude, home_longitude, email_verified, location_sharing_enabled) 
VALUES (2, 'alice@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 1, 1, 'Alice L', '123 Main St, Anytown', 63.4305, 10.3951, TRUE, TRUE);

INSERT INTO users (id, email, password_hash, role_id, household_id, name, home_address, home_latitude, home_longitude, email_verified, location_sharing_enabled) 
VALUES (3, 'sarah@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 1, 2, 'Sarah Johnson', '456 Oak Ave, Somewhere', 63.4205, 10.4051, TRUE, TRUE);

INSERT INTO users (id, email, password_hash, role_id, household_id, name, home_address, home_latitude, home_longitude, email_verified, location_sharing_enabled) 
VALUES (4, 'mike@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 1, 3, 'Mike Brown', '789 Pine St, Elsewhere', 63.4105, 10.3851, TRUE, FALSE);

-- HOUSEHOLD MEMBERS
INSERT INTO household_member (id, household_id, name, description, type) VALUES (1, 1, 'Emma Smith', 'Daughter', 'child');
INSERT INTO household_member (id, household_id, name, description, type) VALUES (2, 1, 'James Smith', 'Son', 'child');
INSERT INTO household_member (id, household_id, name, description, type) VALUES (3, 1, 'Mary Smith', 'Wife', 'adult');
INSERT INTO household_member (id, household_id, name, description, type) VALUES (4, 2, 'Tom Johnson', 'Son', 'child');
INSERT INTO household_member (id, household_id, name, description, type) VALUES (5, 2, 'David Johnson', 'Husband', 'adult');
INSERT INTO household_member (id, household_id, name, description, type) VALUES (6, 3, 'Lisa Brown', 'Wife', 'adult');
INSERT INTO household_member (id, household_id, name, description, type) VALUES (7, 1, 'Rex', 'Family dog', 'pet');

-- GROUPS
INSERT INTO groups (id, name, created_by_user_id) VALUES (1, 'Neighborhood Watch', 2);
INSERT INTO groups (id, name, created_by_user_id) VALUES (2, 'Emergency Response Team', 1);

-- GROUP MEMBERSHIPS
INSERT INTO group_memberships (group_id, household_id, invited_by_user_id) VALUES (1, 1, 2);
INSERT INTO group_memberships (group_id, household_id, invited_by_user_id) VALUES (1, 2, 2);
INSERT INTO group_memberships (group_id, household_id, invited_by_user_id) VALUES (2, 1, 1);
INSERT INTO group_memberships (group_id, household_id, invited_by_user_id) VALUES (2, 3, 1);

-- PRODUCT TYPES
INSERT INTO product_types (id, name) VALUES (1, 'Food');
INSERT INTO product_types (id, name) VALUES (2, 'Water');
INSERT INTO product_types (id, name) VALUES (3, 'Medicine');
INSERT INTO product_types (id, name) VALUES (4, 'Tools');
INSERT INTO product_types (id, name) VALUES (5, 'Hygiene');

-- PRODUCTS
INSERT INTO products (id, product_type_id, name, description) VALUES (1, 1, 'Canned Beans', 'Long shelf life, high protein');
INSERT INTO products (id, product_type_id, name, description) VALUES (2, 1, 'Rice', '5kg bag of white rice');
INSERT INTO products (id, product_type_id, name, description) VALUES (3, 2, 'Bottled Water', '500ml bottles');
INSERT INTO products (id, product_type_id, name, description) VALUES (4, 3, 'First Aid Kit', 'Basic medical supplies');
INSERT INTO products (id, product_type_id, name, description) VALUES (5, 3, 'Pain Relievers', 'Generic acetaminophen');
INSERT INTO products (id, product_type_id, name, description) VALUES (6, 4, 'Flashlight', 'Battery-powered');
INSERT INTO products (id, product_type_id, name, description) VALUES (7, 5, 'Soap', 'Antibacterial hand soap');
INSERT INTO products (id, product_type_id, name, description) VALUES (8, 5, 'Toilet Paper', '12-pack');

-- HOUSEHOLD INVENTORY
INSERT INTO household_inventory (household_id, product_id, quantity, expiration_date) VALUES (1, 1, 10, '2025-12-31');
INSERT INTO household_inventory (household_id, product_id, quantity, expiration_date) VALUES (1, 3, 24, '2026-06-30');
INSERT INTO household_inventory (household_id, product_id, quantity, expiration_date) VALUES (1, 4, 1, NULL);
INSERT INTO household_inventory (household_id, product_id, quantity, expiration_date) VALUES (2, 2, 2, '2025-10-15');
INSERT INTO household_inventory (household_id, product_id, quantity, expiration_date) VALUES (2, 5, 1, '2024-08-22');
INSERT INTO household_inventory (household_id, product_id, quantity, expiration_date) VALUES (3, 6, 2, NULL);
INSERT INTO household_inventory (household_id, product_id, quantity, expiration_date) VALUES (3, 7, 5, '2026-01-15');
INSERT INTO household_inventory (household_id, custom_name, quantity, expiration_date) VALUES (1, 'Emergency Candles', 8, NULL);

-- GROUP INVENTORY CONTRIBUTIONS
INSERT INTO group_inventory_contributions (group_id, household_id, product_id, quantity) VALUES (1, 1, 1, 5);
INSERT INTO group_inventory_contributions (group_id, household_id, product_id, quantity) VALUES (1, 2, 2, 1);
INSERT INTO group_inventory_contributions (group_id, household_id, product_id, quantity) VALUES (2, 1, 3, 12);
INSERT INTO group_inventory_contributions (group_id, household_id, product_id, quantity) VALUES (2, 3, 6, 1);

-- POI TYPES
INSERT INTO poi_types (id, name) VALUES (1, 'Hospital');
INSERT INTO poi_types (id, name) VALUES (2, 'Police Station');
INSERT INTO poi_types (id, name) VALUES (3, 'Fire Station');
INSERT INTO poi_types (id, name) VALUES (4, 'Shelter');
INSERT INTO poi_types (id, name) VALUES (5, 'Grocery Store');

-- POINTS OF INTEREST
INSERT INTO points_of_interest (poi_type_id, name, description, latitude, longitude, address, created_by_user_id)
VALUES (1, 'City Hospital', 'Main hospital with emergency services', 63.4280, 10.3920, '100 Hospital Road, Trondheim', 1);
INSERT INTO points_of_interest (poi_type_id, name, description, latitude, longitude, address, created_by_user_id)
VALUES (2, 'Central Police Station', '24/7 police services', 63.4310, 10.3980, '200 Police Avenue, Trondheim', 1);
INSERT INTO points_of_interest (poi_type_id, name, description, latitude, longitude, address, created_by_user_id)
VALUES (4, 'Community Shelter', 'Emergency shelter with capacity for 200 people', 63.4250, 10.3950, '300 Shelter Street, Trondheim', 1);
INSERT INTO points_of_interest (poi_type_id, name, description, latitude, longitude, address, created_by_user_id)
VALUES (5, 'SuperMarket', 'Large grocery store with emergency supplies', 63.4270, 10.3930, '400 Market Road, Trondheim', 2);

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
VALUES ('Storm Alert', 'Heavy storm expected tonight', 'green', 63.4250, 10.3900, CURRENT_TIMESTAMP + INTERVAL '1' DAY, 1, TRUE);

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

-- NEWS ARTICLES
INSERT INTO news_articles (title, content, published_at, created_by_user_id)
VALUES ('Preparing for Winter Storms', 'Here are some tips to prepare your household for the upcoming winter storm season...', CURRENT_TIMESTAMP, 1);
INSERT INTO news_articles (title, content, published_at, created_by_user_id)
VALUES ('New Emergency Response Guidelines', 'The city has updated its emergency response guidelines. Key changes include...', CURRENT_TIMESTAMP - INTERVAL '2' DAY, 1);
