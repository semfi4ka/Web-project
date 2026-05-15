CREATE DATABASE IF NOT EXISTS webtask_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE webtask_db;

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(100) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cocktails (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           description TEXT,
                           status VARCHAR(20) NOT NULL,
                           author_id BIGINT,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE TABLE ingredients (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             unit VARCHAR(50)
);

CREATE TABLE cocktail_ingredients (
                                      cocktail_id BIGINT,
                                      ingredient_id BIGINT,
                                      amount DOUBLE,
                                      PRIMARY KEY (cocktail_id, ingredient_id),
                                      FOREIGN KEY (cocktail_id) REFERENCES cocktails(id),
                                      FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);

CREATE TABLE IF NOT EXISTS cocktail_ratings (
                                                cocktail_id BIGINT NOT NULL,
                                                user_id BIGINT NOT NULL,
                                                rating INT NOT NULL,
                                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                PRIMARY KEY (cocktail_id, user_id),
                                                FOREIGN KEY (cocktail_id) REFERENCES cocktails(id) ON DELETE CASCADE,
                                                FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS cocktail_comments (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                cocktail_id BIGINT NOT NULL,
                                                user_id BIGINT NOT NULL,
                                                text TEXT NOT NULL,
                                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                FOREIGN KEY (cocktail_id) REFERENCES cocktails(id) ON DELETE CASCADE,
                                                FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS blog_posts (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          title VARCHAR(255) NOT NULL,
                                          content TEXT NOT NULL,
                                          author_id BIGINT NOT NULL,
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS blog_comments (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             post_id BIGINT NOT NULL,
                                             user_id BIGINT NOT NULL,
                                             text TEXT NOT NULL,
                                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                             FOREIGN KEY (post_id) REFERENCES blog_posts(id) ON DELETE CASCADE,
                                             FOREIGN KEY (user_id) REFERENCES users(id)
);
