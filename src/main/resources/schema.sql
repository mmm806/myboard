CREATE TABLE  IF NOT EXISTS role (
                      role_id SERIAL PRIMARY KEY,
                      name VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS "user" (
                        user_id SERIAL PRIMARY KEY,
                        email VARCHAR(255) NOT NULL,
                        name VARCHAR(50) NOT NULL,
                        password VARCHAR(500) NOT NULL,
                        regdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_role (
                           user_id INT,
                           role_id INT,
                           PRIMARY KEY (user_id, role_id),
                           FOREIGN KEY (user_id) REFERENCES "user"(user_id),
                           FOREIGN KEY (role_id) REFERENCES role(role_id)
);

CREATE TABLE IF NOT EXISTS board (
                       board_id SERIAL PRIMARY KEY,
                       title VARCHAR(100) NOT NULL,
                       content TEXT,
                       user_id INT NOT NULL,
                       regdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       view_cnt INT DEFAULT 0,
                       FOREIGN KEY (user_id) REFERENCES "user"(user_id)
);
