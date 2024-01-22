-- Users
INSERT INTO users (email, password, nickname, articles_count)
VALUES
    ('user1@example.com', 'password1', 'user1', 1),
    ('user2@example.com', 'password2', 'user2', 1),
    ('user3@example.com', 'password3', 'user3', 1);

-- Articles
INSERT INTO articles (title, content, user_id, created_at, updated_at, likes_count)
VALUES
    ('Article Title 1', 'Content of article 1.', 1, NOW(), NOW(), 10),
    ('Article Title 2', 'Content of article 2.', 2, NOW(), NOW(), 5),
    ('Article Title 3', 'Content of article 3.', 3, NOW(), NOW(), 15);

-- Comments
INSERT INTO comments (article_id, user_id, content, likes_count, updated_at)
VALUES
    (1, 2, 'Comment on article 1 by user 2.', 3, NOW()),
    (1, 3, 'Comment on article 1 by user 3.', 1, NOW()),
    (2, 1, 'Comment on article 2 by user 1.', 8, NOW());
