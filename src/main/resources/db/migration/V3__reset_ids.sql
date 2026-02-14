SELECT setval('users_id_seq', COALESCE((SELECT MAX(id) FROM users), 1), true);
SELECT setval('tournament_id_seq', COALESCE((SELECT MAX(id) FROM tournament), 1), true);
SELECT setval('user_tournament_id_seq', COALESCE((SELECT MAX(id) FROM user_tournament), 1), true);
SELECT setval('registered_category_id_seq', COALESCE((SELECT MAX(id) FROM registered_category), 1), true);
SELECT setval('partnership_id_seq', COALESCE((SELECT MAX(id) FROM partnership), 1), true);
SELECT setval('export_history_id_seq', COALESCE((SELECT MAX(id) FROM export_history), 1), true);


