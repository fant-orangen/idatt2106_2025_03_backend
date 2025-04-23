-- ROLES
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- HOUSEHOLDS
CREATE TABLE households (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    population_count INT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- USERS
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    household_id INT,
    name VARCHAR(255),
    home_address TEXT,
    home_latitude DECIMAL(10,7),
    home_longitude DECIMAL(10,7),
    privacy_accepted_at DATETIME,
    email_verified TINYINT(1) NOT NULL DEFAULT 0,
    location_sharing_enabled TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (household_id) REFERENCES households(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- GROUPS
CREATE TABLE groups (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_by_user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (inviter_user_id) REFERENCES users(id),
    FOREIGN KEY (household_id) REFERENCES households(id),
    FOREIGN KEY (group_id) REFERENCES groups(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- GROUP MEMBERSHIPS (households join crisis‐supply groups)
CREATE TABLE group_memberships (
    group_id INT NOT NULL,
    household_id INT NOT NULL,
    invited_by_user_id INT,
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (group_id, household_id),
    FOREIGN KEY (group_id) REFERENCES groups(id),
    FOREIGN KEY (household_id) REFERENCES households(id),
    FOREIGN KEY (invited_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- PRODUCT CATALOG
CREATE TABLE product_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_type_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    FOREIGN KEY (product_type_id) REFERENCES product_types(id),
    UNIQUE KEY (product_type_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- HOUSEHOLD INVENTORY
CREATE TABLE household_inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    household_id INT NOT NULL,
    product_id INT,
    custom_name VARCHAR(255),
    quantity DECIMAL(10,2) NOT NULL,
    expiration_date DATE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (household_id) REFERENCES households(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- GROUP SUPPLY CONTRIBUTIONS
CREATE TABLE group_inventory_contributions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT NOT NULL,
    household_id INT NOT NULL,
    product_id INT,
    custom_name VARCHAR(255),
    quantity DECIMAL(10,2) NOT NULL,
    expiration_date DATE,
    contributed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES groups(id),
    FOREIGN KEY (household_id) REFERENCES households(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- MEETING PLACES (user/household–defined POIs)
CREATE TABLE meeting_places (
    id INT AUTO_INCREMENT PRIMARY KEY,
    household_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    address TEXT,
    created_by_user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (household_id) REFERENCES households(id),
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- SYSTEM‐MANAGED POINTS OF INTEREST
CREATE TABLE poi_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE points_of_interest (
    id INT AUTO_INCREMENT PRIMARY KEY,
    poi_type_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    address TEXT,
    opening_hours TEXT,
    contact_info TEXT,
    created_by_user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (poi_type_id) REFERENCES poi_types(id),
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- CRISIS EVENTS & EPICENTERS
CREATE TABLE crisis_events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    severity ENUM('green','yellow','red') NOT NULL DEFAULT 'green',
    epicenter_latitude DECIMAL(10,7) NOT NULL,
    epicenter_longitude DECIMAL(10,7) NOT NULL,
    start_time DATETIME NOT NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by_user_id INT NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- SCENARIO THEMES (admin‐managed crisis scenarios)
CREATE TABLE scenario_themes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_by_user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- GAMIFICATION ACTIVITIES & USER LOG
CREATE TABLE gamification_activities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    category VARCHAR(255),
    description TEXT,
    created_by_user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_gamification_activities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    activity_id INT NOT NULL,
    status ENUM('pending','completed','failed') NOT NULL DEFAULT 'pending',
    score INT,
    completed_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (activity_id) REFERENCES gamification_activities(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- POST‐CRISIS REFLECTIONS
CREATE TABLE reflections (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    shared TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- NEWS ARTICLES
CREATE TABLE news_articles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    published_at DATETIME NOT NULL,
    created_by_user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- EMAIL TOKEN MANAGEMENT (verification & password reset)
CREATE TABLE email_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    type ENUM('verification','reset') NOT NULL,
    expires_at DATETIME NOT NULL,
    used_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- NOTIFICATION SETTINGS & LOG
CREATE TABLE notification_preferences (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    preference_type ENUM('expiration_reminder','crisis_alert','location_request') NOT NULL,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY (user_id, preference_type),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    preference_type ENUM('expiration_reminder','crisis_alert','location_request') NOT NULL,
    target_type ENUM('inventory','event','location_request') NOT NULL,
    target_id INT,
    notify_at DATETIME NOT NULL,
    sent_at DATETIME,
    read_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
