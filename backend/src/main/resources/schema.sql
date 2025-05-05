-- ROLES
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- HOUSEHOLDS
CREATE TABLE households (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    address TEXT NOT NULL,
    population_count INT DEFAULT 1,
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- USERS
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    role_id INT NOT NULL,
    household_id INT,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    home_address TEXT,
    home_latitude DECIMAL(10,7),
    home_longitude DECIMAL(10,7),
    privacy_accepted_at DATETIME,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    location_sharing_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_using_2fa BOOLEAN NOT NULL DEFAULT FALSE,
    kcal_requirement INT NOT NULL DEFAULT 2000,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (household_id) REFERENCES households(id)
);

-- HOUSEHOLD MEMBERS
CREATE TABLE household_member (
    id INT AUTO_INCREMENT PRIMARY KEY,
    household_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(10) NOT NULL CHECK (type IN ('child', 'adult', 'pet')),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    kcal_requirement INT NOT NULL DEFAULT 2000,
    FOREIGN KEY (household_id) REFERENCES households(id)
);

-- GROUPS
CREATE TABLE groups (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_by_user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

-- INVITATIONS (for both households & groups)
CREATE TABLE invitations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    inviter_user_id INT NOT NULL,
    invitee_email VARCHAR(255) NOT NULL,
    household_id INT,
    group_id INT,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at DATETIME NOT NULL,
    accepted_at DATETIME,
    declined_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (inviter_user_id) REFERENCES users(id),
    FOREIGN KEY (household_id) REFERENCES households(id),
    FOREIGN KEY (group_id) REFERENCES groups(id)
);

-- GROUP MEMBERSHIPS (households join crisis‐supply groups)
CREATE TABLE group_memberships (
    group_id INT NOT NULL,
    household_id INT NOT NULL,
    invited_by_user_id INT,
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at DATETIME DEFAULT NULL,
    PRIMARY KEY (group_id, household_id),
    FOREIGN KEY (group_id) REFERENCES groups(id),
    FOREIGN KEY (household_id) REFERENCES households(id),
    FOREIGN KEY (invited_by_user_id) REFERENCES users(id)
);

-- PRODUCT CATALOG
CREATE TABLE product_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    household_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    unit VARCHAR(10) NOT NULL CHECK (unit IN ('l', 'stk', 'kg', 'gram', 'dl', 'mg', 'dose', 'mcg')),
    calories_per_unit DECIMAL(10,2),
    category VARCHAR(10) NOT NULL CHECK (category IN ('food', 'water', 'medicine')),
    FOREIGN KEY (household_id) REFERENCES households(id),
    UNIQUE (household_id, name)
);

CREATE TABLE product_batch (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_type_id INT NOT NULL,
    date_added DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expiration_time DATETIME,
    number INT NOT NULL,
    FOREIGN KEY (product_type_id) REFERENCES product_types(id) ON DELETE CASCADE
);

-- GROUP SUPPLY CONTRIBUTIONS
CREATE TABLE group_inventory_contributions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT NOT NULL,
    household_id INT NOT NULL,
    product_id INT,
    custom_name VARCHAR(255),
    expiration_date DATE,
    contributed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    FOREIGN KEY (household_id) REFERENCES households(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product_batch(id) ON DELETE CASCADE
);

-- MEETING PLACES (user/household–defined POIs)
CREATE TABLE meeting_places (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    address TEXT,
    status VARCHAR(10) NOT NULL DEFAULT 'active' CHECK (status IN ('active','archived')),
    created_by_user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

-- SYSTEM‐MANAGED POINTS OF INTEREST
CREATE TABLE poi_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE points_of_interest (
    id INT AUTO_INCREMENT PRIMARY KEY,
    poi_type_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    address TEXT,
    open_from TEXT,
    open_to TEXT,
    contact_info TEXT,
    created_by_user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (poi_type_id) REFERENCES poi_types(id),
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

-- SCENARIO THEMES (admin‐managed crisis scenarios)
CREATE TABLE scenario_themes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    instructions TEXT,
    status VARCHAR(10) NOT NULL DEFAULT 'active' CHECK (status IN ('active','archived')),
    created_by_user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

-- CRISIS EVENTS & EPICENTERS
CREATE TABLE crisis_events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    severity VARCHAR(10) NOT NULL DEFAULT 'GREEN' CHECK (severity IN ('green','yellow','red')),
    epicenter_latitude DECIMAL(10,7) NOT NULL,
    epicenter_longitude DECIMAL(10,7) NOT NULL,
    radius DECIMAL(10,2),
    start_time DATETIME NOT NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_user_id INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    scenario_theme_id INT DEFAULT NULL,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id),
    FOREIGN KEY (scenario_theme_id) REFERENCES scenario_themes(id)
);

-- GAMIFICATION ACTIVITIES & USER LOG
CREATE TABLE gamification_activities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    --category VARCHAR(255), Category is unnecessary
    description TEXT,
    created_by_user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

CREATE TABLE user_gamification_activities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    activity_id INT NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending','completed','failed')),
    score INT,
    completed_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (activity_id) REFERENCES gamification_activities(id)
);

-- POST‐CRISIS REFLECTIONS
CREATE TABLE reflections (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    shared BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- NEWS ARTICLES
CREATE TABLE news_articles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    published_at DATETIME NOT NULL,
    created_by_user_id INT NOT NULL,
    crisis_event_id INT NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('draft', 'published', 'archived')),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id),
    FOREIGN KEY (crisis_event_id) REFERENCES crisis_events(id)
);

-- EMAIL TOKEN MANAGEMENT (verification & password reset)
CREATE TABLE email_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(15) NOT NULL CHECK (type IN ('VERIFICATION','RESET')),
    expires_at DATETIME NOT NULL,
    used_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- HOUSEHOLD ADMINS
CREATE TABLE household_admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    household_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (household_id) REFERENCES households(id),
    UNIQUE (user_id)
);

-- NOTIFICATION SETTINGS & LOG
CREATE TABLE notification_preferences (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    preference_type VARCHAR(20) NOT NULL CHECK (preference_type IN ('expiration_reminder','crisis_alert','location_request', 'remaining_supply_alert', 'system')),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, preference_type),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    preference_type VARCHAR(25) NOT NULL CHECK (preference_type IN ('expiration_reminder', 'remaining_supply_alert', 'crisis_alert','location_request', 'system')),
    target_type VARCHAR(20) CHECK (target_type IN ('inventory','event','location_request','invitation')), -- If the notification is associated with another table, add a target type and target id pointing to the table
    target_id INT,
    description TEXT DEFAULT NULL,
    notify_at DATETIME NOT NULL,
    sent_at DATETIME,
    read_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE crisis_event_changes ( -- TODO: when a crisis event is updated, a new entry is added to this column
    id INT AUTO_INCREMENT PRIMARY KEY,
    crisis_event_id INT NOT NULL,
    change_type VARCHAR(30) NOT NULL CHECK (change_type IN ('creation', 'level_change', 'description_update', 'epicenter_moved')),
    old_value TEXT,
    new_value TEXT,
    created_by_user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (crisis_event_id) REFERENCES crisis_events(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

CREATE TABLE two_factor_codes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(6) NOT NULL,
    email VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

-- QUIZZES
CREATE TABLE quizzes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_by_user_id INT NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'active' CHECK (status IN ('active','archived')),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

-- QUIZ QUESTIONS
CREATE TABLE quiz_questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT NOT NULL,
    question_body TEXT NOT NULL,
    position INT DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);

-- QUIZ ANSWERS
CREATE TABLE quiz_answers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT NOT NULL,
    question_id INT NOT NULL,
    answer_body TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES quiz_questions(id) ON DELETE CASCADE
);

-- USER QUIZ ATTEMPTS
CREATE TABLE user_quiz_attempts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    quiz_id INT NOT NULL,
    completed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);

-- USER QUIZ ANSWERS
CREATE TABLE user_quiz_answers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_quiz_attempt_id INT NOT NULL,
    quiz_id INT NOT NULL,
    question_id INT NOT NULL,
    answer_id INT NOT NULL,
    FOREIGN KEY (user_quiz_attempt_id) REFERENCES user_quiz_attempts(id),
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES quiz_questions(id) ON DELETE CASCADE,
    FOREIGN KEY (answer_id) REFERENCES quiz_answers(id) ON DELETE CASCADE
);


