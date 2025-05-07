-- ROLES
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('SUPERADMIN');

-- HOUSEHOLDS
INSERT INTO households (name, address, population_count, latitude, longitude) VALUES ('Smith Family', '123 Main St, Anytown', 4, 63.4305, 10.3951);
INSERT INTO households (name, address, population_count, latitude, longitude) VALUES ('Johnson Household', '456 Oak Ave, Somewhere', 3, 63.4305, 10.3951);
INSERT INTO households (name, address, population_count, latitude, longitude) VALUES ('Brown Residence', '789 Pine St, Elsewhere', 2, 63.4305, 10.3951);

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
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 1, 1); --  Canned Beans
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 1, 7); -- Antibiotics
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (2, 1, 3); -- Rice
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (2, 1, 10); -- Cooking oil
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 1, 2);
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 1, 4);
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 1, 5);
INSERT INTO group_inventory_contributions (group_id, household_id, product_id) VALUES (1, 2, 29);


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
    'Power Outage',
    'Comprehensive guide for preparing and managing extended power outages in your area. Power outages can occur due to severe weather, equipment failure, or planned maintenance. Being prepared is crucial for maintaining safety and comfort during these situations.',
    '## Before a Power Outage

### Emergency Kit Preparation
- **Flashlights** and extra batteries
- **Battery-powered radio**
- **Portable phone chargers**
- **Non-perishable food** and water
- **First aid supplies**

### Additional Preparations
1. Install surge protectors for sensitive electronics
2. Keep your vehicle''s gas tank at least half full
3. Learn how to manually open your garage door
4. Consider installing a backup generator
5. Keep important phone numbers in a physical list',
    '## During a Power Outage

### Immediate Actions
1. **Unplug sensitive electronics** to prevent damage from power surges
2. Keep refrigerator and freezer doors closed
3. Use flashlights instead of candles for safety

### Ongoing Safety Measures
- Monitor local news via battery-powered radio
- Check on neighbors, especially elderly or vulnerable individuals
- Use generators outdoors only, away from windows
- Keep mobile devices charged and use them sparingly',
    '## After Power is Restored

### Safety Checks
1. **Check food** in refrigerator and freezer for spoilage
2. Reset clocks, alarms, and other electronic devices
3. Restock emergency supplies

### Follow-up Actions
- Report any damage to utility companies
- Review your emergency plan and update as needed
- Check on neighbors and community members
- Document any losses for insurance purposes',
    'active',
    1
);

INSERT INTO scenario_themes (name, description, before, under, after, status, created_by_user_id)
VALUES (
    'Natural Disaster',
    'Detailed preparation and response guide for various natural disasters including earthquakes, floods, storms, and extreme weather events. Natural disasters can strike with little warning, making preparedness essential for community safety.',
    '## Before a Natural Disaster

### Emergency Plan Creation
1. **Identify safe meeting places**
2. **Establish communication protocols**
3. **Plan evacuation routes**

### Emergency Supply Preparation
- **Three days** of food and water per person
- **First aid kit**
- **Emergency blankets**
- **Weather-appropriate clothing**
- **Important documents** in waterproof container

### Home Security Measures
- Anchor heavy furniture
- Install storm shutters
- Clear gutters and drains
- Trim trees and remove dead branches

> **Important:** Stay informed about local risks and warning systems',
    '## During a Natural Disaster

### Critical Safety Rules
1. **Follow official evacuation orders immediately**
2. Stay indoors if advised
3. Avoid floodwaters and downed power lines

### Communication & Monitoring
- Listen to emergency broadcasts
- Use social media to check in with family
- Keep emergency supplies accessible
- Stay away from windows and exterior walls
- Monitor local emergency services for updates',
    '## After a Natural Disaster

### Immediate Safety Steps
1. **Wait for official clearance** before returning home
2. Check for structural damage before entering buildings
3. Document all damage with photos

### Recovery Actions
- Contact insurance companies
- Check utilities for damage
- Help neighbors if safe to do so
- Report hazards to authorities

### Long-term Steps
1. Begin cleanup only when safe
2. Seek professional help for repairs
3. Update emergency plans based on experience',
    'active',
    1
);

INSERT INTO scenario_themes (name, description, before, under, after, status, created_by_user_id)
VALUES (
    'Public Health Emergency',
    'Comprehensive guide for preparing and responding to public health emergencies such as pandemics, disease outbreaks, and other health crises. Public health emergencies require both individual and community-level preparedness.',
    '## Before a Public Health Emergency

### Health Emergency Kit
- **Prescription medications** (30-day supply)
- **Over-the-counter medicines**
- **First aid supplies**
- **Thermometer**
- **Face masks and gloves**
- **Hand sanitizer**
- **Disinfectant wipes**

### Preparedness Steps
1. Stay informed about local health risks
2. Maintain up-to-date vaccinations
3. Establish a support network
4. Keep important medical records accessible
5. Learn basic first aid and CPR
6. Identify local healthcare resources',
    '## During a Public Health Emergency

### Safety Protocols
1. **Follow official health guidelines strictly**
2. Practice enhanced hygiene:
   - Frequent hand washing
   - Proper cough/sneeze etiquette
   - Regular surface disinfection
3. Maintain social distancing when required
4. Wear appropriate protective equipment

### Health Monitoring
- Monitor symptoms daily
- Stay informed through reliable sources
- Support vulnerable community members

### Mental Health Care
- Regular exercise
- Healthy eating
- Adequate sleep
- Stress management techniques',
    '## After a Public Health Emergency

### Health Monitoring
1. Continue monitoring health for any delayed symptoms
2. Complete any required quarantine periods
3. Seek medical attention if needed

### Recovery Steps
- Update vaccination records
- Restock emergency supplies
- Review and update emergency plans
- Participate in community recovery efforts

### Mental Health Recovery
1. Share lessons learned with community
2. Support mental health recovery
3. Document experience for future reference

> **Remember:** Mental health is as important as physical health during recovery.',
    'active',
    1
);

-- CRISIS EVENTS
INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active, scenario_theme_id)
VALUES ('Flood Warning', 'Potential flooding in downtown area', 'yellow', 60.4300, 10.3950, 1.0, CURRENT_TIMESTAMP, 1, TRUE, 1);
INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active, scenario_theme_id)
VALUES ('Storm Alert', 'Heavy storm expected tonight', 'green', 60.4250, 11.3900, 5.0, DATEADD('DAY', 1, CURRENT_TIMESTAMP), 1, TRUE, 2);
INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active, scenario_theme_id)
VALUES ('lightning storm', 'Potential flooding in downtown area', 'red', 59.4300, 10.3950, 1.0, TIMESTAMP '2025-05-01 12:00:00', 1, TRUE, 3);
-- Inactive crisis event
INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active, scenario_theme_id)
VALUES ('Past Earthquake', 'Earthquake event that has ended', 'red', 63.4200, 12.4000, 20, DATEADD('DAY', -10, CURRENT_TIMESTAMP), 1, FALSE, 2);

INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active, scenario_theme_id)
VALUES ('Severe Storm Warning', 'Heavy storm with potential flooding in central Trondheim', 'red', 63.4300, 10.3950, 2.0, CURRENT_TIMESTAMP, 1, TRUE, 2);

INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active, scenario_theme_id)
VALUES ('Power Outage Alert', 'Planned maintenance causing temporary power disruption', 'yellow', 63.4280, 10.3940, 1.5, CURRENT_TIMESTAMP, 1, TRUE, 1);

INSERT INTO crisis_events (name, description, severity, epicenter_latitude, epicenter_longitude, radius, start_time, created_by_user_id, active, scenario_theme_id)
VALUES ('Minor Traffic Incident', 'Road closure due to minor accident', 'green', 63.4290, 10.3960, 0.5, CURRENT_TIMESTAMP, 1, TRUE, 3);


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
VALUES ('Archived Article', 'This is an old article that has been archived...', DATEADD('DAY', -30, CURRENT_TIMESTAMP), 1, 2, 'archived');

-- HOUSEHOLD ADMINS
INSERT INTO household_admins (user_id, household_id) VALUES (2, 1); -- Alice is admin of Smith Family
INSERT INTO household_admins (user_id, household_id) VALUES (4, 3); -- Mike is admin of Brown Residence

-- QUIZZES
INSERT INTO quizzes (name, description, created_by_user_id) VALUES ('Emergency Preparedness Basics', 'A quiz to test your knowledge on basic emergency preparedness.', 1);

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
