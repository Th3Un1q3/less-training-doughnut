CREATE TABLE `comment` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `note_id` int unsigned NOT NULL,
  `user_id` int unsigned NOT NULL,
  `content` text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_note_id` (`note_id`),
  KEY `fk_user_id` (`user_id`),
  CONSTRAINT `fk_comment_id` FOREIGN KEY (`note_id`) REFERENCES `note` (`id`),
  CONSTRAINT `fk_comment_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);
