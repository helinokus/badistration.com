INSERT INTO users (id, email, password, first_name, last_name, birth_date, phone_number, team_name, is_active, registration_date) VALUES
                              -- Organizers & Moderators
                              (2, 'moderator@gmail.com', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Anna', 'Nowak', '1985-03-20', '+48502345678', 'SKB Kraków', true, '2024-01-05 11:00:00'),
                              (3, 'turnieje@gmail.com', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Piotr', 'Wiśniewski', '1982-07-10', '+48503456789', 'UKS Hubertus Poznań', true, '2024-01-10 09:00:00'),

                              -- Senior Players (OPEN/A/B/C categories)
                              (4, 'mateusz.grabowski@gmail.com', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Mateusz', 'Grabowski', '1995-08-12', '+48504567890', 'SKB Kraków', true, '2024-02-01 14:30:00'),
                              (5, 'karolina.mazur@wp.pl', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Karolina', 'Mazur', '1998-11-25', '+48505678901', 'KS Warta Poznań', true, '2024-02-03 16:00:00'),
                              (6, 'tomasz.lewandowski@o2.pl', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Tomasz', 'Lewandowski', '1992-04-18', '+48506789012', 'UKS Hubertus Poznań', true, '2024-02-05 10:15:00'),
                              (7, 'agnieszka.zielinska@interia.pl', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Agnieszka', 'Zielińska', '1997-02-28', '+48507890123', 'LKS Strzelce Opolskie', true, '2024-02-07 12:45:00'),
                              (8, 'pawel.kaminski@gmail.com', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Paweł', 'Kamiński', '1990-09-03', '+48508901234', 'SKB Suwałki', true, '2024-02-10 08:30:00'),
                              (9, 'magdalena.wojcik@onet.pl', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Magdalena', 'Wójcik', '1999-06-14', '+48509012345', 'KKB Warszawa', true, '2024-02-12 15:20:00'),
                              (10, 'adam.szymanski@gmail.com', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Adam', 'Szymański', '1994-12-07', '+48510123456', 'BKS Bielsko-Biała', true, '2024-02-15 11:00:00'),
                              (11, 'natalia.dabrowska@wp.pl', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Natalia', 'Dąbrowska', '1996-01-22', '+48511234567', 'SKB Kraków', true, '2024-02-18 09:45:00'),

                              -- Junior Players (U15, U17, U19)
                              (12, 'jakub.kowalczyk@gmail.com', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Jakub', 'Kowalczyk', '2008-03-15', '+48512345678', 'UKS Hubertus Poznań', true, '2024-03-01 10:00:00'),
                              (13, 'julia.jankowska@wp.pl', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Julia', 'Jankowska', '2009-07-22', '+48513456789', 'SKB Litpol-Malow', true, '2024-03-03 14:30:00'),
                              (14, 'michal.olszewski@o2.pl', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Michał', 'Olszewski', '2007-11-08', '+48514567890', 'KS Warta Poznań', true, '2024-03-05 16:15:00'),
                              (15, 'zuzanna.kwiatkowska@interia.pl', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Zuzanna', 'Kwiatkowska', '2008-09-30', '+48515678901', 'BKS Bielsko-Biała', true, '2024-03-07 11:45:00'),
                              (16, 'kacper.wozniak@gmail.com', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Kacper', 'Woźniak', '2006-05-12', '+48516789012', 'KKB Warszawa', true, '2024-03-10 08:00:00'),
                              (17, 'maja.stepien@onet.pl', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Maja', 'Stępień', '2007-02-18', '+48517890123', 'LKS Strzelce Opolskie', true, '2024-03-12 13:20:00'),
                              (18, 'filip.kozlowski@wp.pl', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Filip', 'Kozłowski', '2009-12-05', '+48518901234', 'SKB Suwałki', true, '2024-03-15 10:30:00'),
                              (19, 'oliwia.pawlak@gmail.com', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Oliwia', 'Pawlak', '2008-08-25', '+48519012345', 'SKB Kraków', true, '2024-03-18 15:00:00'),

                              -- Masters Players (30+, 40+, 50+)
                              (20, 'robert.kaczmarek@gmail.com', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Robert', 'Kaczmarek', '1978-06-20', '+48520123456', 'BKS Bielsko-Biała', true, '2024-03-20 09:00:00'),
                              (21, 'beata.piotrowska@wp.pl', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Beata', 'Piotrowska', '1975-04-10', '+48521234567', 'KS Warta Poznań', true, '2024-03-22 11:30:00'),
                              (22, 'andrzej.walczak@o2.pl', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Andrzej', 'Walczak', '1970-10-15', '+48522345678', 'UKS Hubertus Poznań', true, '2024-03-25 14:00:00'),
                              (23, 'ewa.gorska@interia.pl', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Ewa', 'Górska', '1972-01-28', '+48523456789', 'SKB Litpol-Malow', true, '2024-03-28 16:45:00'),
                              (24, 'marek.rutkowski@onet.pl', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Marek', 'Rutkowski', '1965-08-05', '+48524567890', 'KKB Warszawa', true, '2024-03-30 08:15:00'),
                              (25, 'dorota.sikora@gmail.com', '$2a$10$0dtU0bI1DHhmLqNg3v1HQOo2nmmgSNTdoqFk.Pfe4zkreEVaFUPae', 'Dorota', 'Sikora', '1968-12-12', '+48525678901', 'LKS Strzelce Opolskie', true, '2024-03-31 12:00:00')
ON CONFLICT (id) DO NOTHING;


INSERT INTO users_roles (user_id, role_id) VALUES

                                (3, 1),
                               -- Regular users
                               (4, 1), (5, 1), (6, 1), (7, 1), (8, 1), (9, 1), (10, 1), (11, 1),
                               (12, 1), (13, 1), (14, 1), (15, 1), (16, 1), (17, 1), (18, 1), (19, 1),
                               (20, 1), (21, 1), (22, 1), (23, 1), (24, 1), (25, 1);


INSERT INTO tournament (id, name_tournament, date_of_tournament, registration_expired, location, description, entry_fee, max_players, max_categories_to_play, registration_closed, creator_id, url_tournament) VALUES

   -- Tournament 2: Mistrzostwa Juniorów Poznań
   (2,
    'Mistrzostwa Wielkopolski Juniorów 2026',
    '2026-04-05 08:00:00',
    '2026-03-31 23:59:59',
    'Hala Sportowa AWF, ul. Królowej Jadwigi 27/39, 61-871 Poznań',
    'Mistrzostwa Wielkopolski Juniorów w badmintonie. Kategorie: U11, U13, U15, U17, U19. Wymagana aktualna licencja PZBad. Turniej rankingowy.',
    '40 PLN za kategorię',
    150,
    2,
    false,
    3,
    'https://pzbad.tournamentsoftware.com/tournament/068333E7-0A43-4704-823A-BA69EF1679C1'),

   (3,
    'Grand Prix Warszawy w Badmintonie 2026',
    '2026-04-20 09:00:00',
    '2026-04-15 23:59:59',
    'COS Torwar, ul. Łazienkowska 6A, 00-449 Warszawa',
    'Prestiżowy turniej Grand Prix Warszawy. Kategorie OPEN, A, B, C dla seniorów oraz wybrane kategorie młodzieżowe. Transmisja live na YouTube.',
    '60-100 PLN za kategorię',
    300,
    4,
    false,
    1,
    'https://pzbad.tournamentsoftware.com/tournament/5295F652-C345-4670-A3F8-C624FA5EA56B'),

   (4,
    'XII Turniej Masters Bielsko-Biała 2026',
    '2026-05-10 10:00:00',
    '2026-05-05 23:59:59',
    'Hala Sportowa MOSiR, ul. Startowa 7, 43-300 Bielsko-Biała',
    'Turniej dla zawodników kategorii wiekowych 30+, 40+, 50+, 60+. Gra pojedyncza, debel i mikst. Atmosfera przyjazna, nagrody rzeczowe.',
    '45 PLN za kategorię',
    120,
    3,
    false,
    2,
    'https://pzbad.tournamentsoftware.com/tournament/0F6D09FF-B69A-4ED7-BB93-F3A8F4A0B438'),

   -- Tournament 5: Puchar Polski Juniorów Suwałki
   (5,
    'Puchar Polski Juniorów U15-U19 Suwałki 2026',
    '2026-06-01 08:00:00',
    '2026-05-25 23:59:59',
    'Hala Widowiskowo-Sportowa, ul. Zarzecze 26, 16-400 Suwałki',
    'Puchar Polski Juniorów w kategoriach U15, U17 i U19. Turniej eliminacyjny do Mistrzostw Polski. Obowiązkowa licencja zawodnicza PZBad.',
    '55 PLN za kategorię',
    180,
    2,
    false,
    1,
    'https://pzbad.tournamentsoftware.com/tournament/EAE48A9F-B8F3-45BE-8E55-A91777D8D3B9')
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 5. TOURNAMENT MODERATORS
-- =====================================================

INSERT INTO tournament_moderators (tournament_id, user_id) VALUES
   (2, 3), -- Poznań - Piotr Wiśniewski
   (3, 2), (3, 3), -- Warszawa - both moderators
   (4, 2), -- Bielsko-Biała - Anna Nowak
   (5, 3); -- Suwałki - Piotr Wiśniewski

-- =====================================================
-- 6. TOURNAMENT CATEGORIES
-- =====================================================

-- Tournament 2: Poznań (Juniors only)
INSERT INTO tournament_categories (tournament_id, category) VALUES
    (2, 'MS_U11'), (2, 'MS_U13'), (2, 'MS_U15'), (2, 'MS_U17'), (2, 'MS_U19'),
    (2, 'WS_U11'), (2, 'WS_U13'), (2, 'WS_U15'), (2, 'WS_U17'), (2, 'WS_U19'),
    (2, 'MD_U15'), (2, 'MD_U17'), (2, 'MD_U19'),
    (2, 'WD_U15'), (2, 'WD_U17'), (2, 'WD_U19'),
    (2, 'XD_U15'), (2, 'XD_U17'), (2, 'XD_U19');

-- Tournament 3: Warszawa (Full program)
INSERT INTO tournament_categories (tournament_id, category) VALUES
    (3, 'MS_OPEN'), (3, 'MS_A'), (3, 'MS_B'), (3, 'MS_C'),
    (3, 'WS_OPEN'), (3, 'WS_A'), (3, 'WS_B'), (3, 'WS_C'),
    (3, 'MD_OPEN'), (3, 'MD_A'), (3, 'MD_B'), (3, 'MD_C'),
    (3, 'WD_OPEN'), (3, 'WD_A'), (3, 'WD_B'),
    (3, 'XD_OPEN'), (3, 'XD_A'), (3, 'XD_B'), (3, 'XD_C'),
    (3, 'MS_U19'), (3, 'WS_U19'), (3, 'MD_U19'), (3, 'WD_U19'), (3, 'XD_U19');

-- Tournament 4: Bielsko-Biała (Masters)
INSERT INTO tournament_categories (tournament_id, category) VALUES
    (4, 'MS_30PLUS'), (4, 'MS_40PLUS'), (4, 'MS_50PLUS'), (4, 'MS_60PLUS'),
    (4, 'WS_30PLUS'), (4, 'WS_40PLUS'), (4, 'WS_50PLUS'),
    (4, 'MD_30PLUS'), (4, 'MD_40PLUS'), (4, 'MD_50PLUS'), (4, 'MD_60PLUS'),
    (4, 'WD_30PLUS'), (4, 'WD_40PLUS'), (4, 'WD_50PLUS'),
    (4, 'XD_30PLUS'), (4, 'XD_40PLUS'), (4, 'XD_50PLUS');

-- Tournament 5: Suwałki (Juniors U15-U19)
INSERT INTO tournament_categories (tournament_id, category) VALUES
    (5, 'MS_U15'), (5, 'MS_U17'), (5, 'MS_U19'),
    (5, 'WS_U15'), (5, 'WS_U17'), (5, 'WS_U19'),
    (5, 'MD_U15'), (5, 'MD_U17'), (5, 'MD_U19'),
    (5, 'WD_U15'), (5, 'WD_U17'), (5, 'WD_U19'),
    (5, 'XD_U15'), (5, 'XD_U17'), (5, 'XD_U19');

-- =====================================================
-- 7. TOURNAMENT CATEGORY PRICES
-- =====================================================

-- Tournament 2: Poznań
INSERT INTO tournament_category_prices (tournament_id, category, price) VALUES
                (2, 'MS_U11', 40.00), (2, 'MS_U13', 40.00), (2, 'MS_U15', 40.00), (2, 'MS_U17', 40.00), (2, 'MS_U19', 40.00),
                (2, 'WS_U11', 40.00), (2, 'WS_U13', 40.00), (2, 'WS_U15', 40.00), (2, 'WS_U17', 40.00), (2, 'WS_U19', 40.00),
                (2, 'MD_U15', 35.00), (2, 'MD_U17', 35.00), (2, 'MD_U19', 35.00),
                (2, 'WD_U15', 35.00), (2, 'WD_U17', 35.00), (2, 'WD_U19', 35.00),
                (2, 'XD_U15', 35.00), (2, 'XD_U17', 35.00), (2, 'XD_U19', 35.00);

-- Tournament 3: Warszawa
INSERT INTO tournament_category_prices (tournament_id, category, price) VALUES
                (3, 'MS_OPEN', 100.00), (3, 'MS_A', 80.00), (3, 'MS_B', 70.00), (3, 'MS_C', 60.00),
                (3, 'WS_OPEN', 100.00), (3, 'WS_A', 80.00), (3, 'WS_B', 70.00), (3, 'WS_C', 60.00),
                (3, 'MD_OPEN', 90.00), (3, 'MD_A', 75.00), (3, 'MD_B', 65.00), (3, 'MD_C', 55.00),
                (3, 'WD_OPEN', 90.00), (3, 'WD_A', 75.00), (3, 'WD_B', 65.00),
                (3, 'XD_OPEN', 90.00), (3, 'XD_A', 75.00), (3, 'XD_B', 65.00), (3, 'XD_C', 55.00),
                (3, 'MS_U19', 60.00), (3, 'WS_U19', 60.00), (3, 'MD_U19', 55.00), (3, 'WD_U19', 55.00), (3, 'XD_U19', 55.00);

-- Tournament 4: Bielsko-Biała
INSERT INTO tournament_category_prices (tournament_id, category, price) VALUES
                (4, 'MS_30PLUS', 45.00), (4, 'MS_40PLUS', 45.00), (4, 'MS_50PLUS', 45.00), (4, 'MS_60PLUS', 45.00),
                (4, 'WS_30PLUS', 45.00), (4, 'WS_40PLUS', 45.00), (4, 'WS_50PLUS', 45.00),
                (4, 'MD_30PLUS', 40.00), (4, 'MD_40PLUS', 40.00), (4, 'MD_50PLUS', 40.00), (4, 'MD_60PLUS', 40.00),
                (4, 'WD_30PLUS', 40.00), (4, 'WD_40PLUS', 40.00), (4, 'WD_50PLUS', 40.00),
                (4, 'XD_30PLUS', 40.00), (4, 'XD_40PLUS', 40.00), (4, 'XD_50PLUS', 40.00);

-- Tournament 5: Suwałki
INSERT INTO tournament_category_prices (tournament_id, category, price) VALUES
                (5, 'MS_U15', 55.00), (5, 'MS_U17', 55.00), (5, 'MS_U19', 55.00),
                (5, 'WS_U15', 55.00), (5, 'WS_U17', 55.00), (5, 'WS_U19', 55.00),
                (5, 'MD_U15', 50.00), (5, 'MD_U17', 50.00), (5, 'MD_U19', 50.00),
                (5, 'WD_U15', 50.00), (5, 'WD_U17', 50.00), (5, 'WD_U19', 50.00),
                (5, 'XD_U15', 50.00), (5, 'XD_U17', 50.00), (5, 'XD_U19', 50.00);


INSERT INTO user_tournament (id, user_id, tournament_id, registration_time, status, is_paid, total_fee, admin_notes, is_downloaded) VALUES
    -- Poznań Junior Tournament registrations
    (7, 12, 2, '2026-03-15 08:30:00', 'APPROVED', true, 75.00, NULL, false),
    (8, 13, 2, '2026-03-16 10:00:00', 'APPROVED', true, 75.00, NULL, false),
    (9, 14, 2, '2026-03-17 14:30:00', 'APPROVED', false, 75.00, NULL, false),
    (10, 15, 2, '2026-03-18 09:15:00', 'APPROVED', true, 75.00, NULL, false),
    (11, 16, 2, '2026-03-19 11:45:00', 'PENDING', false, 40.00, NULL, false),
    (12, 17, 2, '2026-03-20 15:30:00', 'APPROVED', true, 75.00, NULL, false),
    (13, 18, 2, '2026-03-21 08:00:00', 'APPROVED', true, 40.00, NULL, false),
    (14, 19, 2, '2026-03-22 12:20:00', 'APPROVED', true, 75.00, NULL, false),

    -- Warszawa GP registrations
    (15, 4, 3, '2026-03-25 09:00:00', 'APPROVED', true, 190.00, NULL, false),
    (16, 5, 3, '2026-03-26 10:30:00', 'APPROVED', true, 180.00, NULL, false),
    (17, 6, 3, '2026-03-27 14:00:00', 'APPROVED', true, 170.00, NULL, false),
    (18, 8, 3, '2026-03-28 11:15:00', 'APPROVED', false, 80.00, 'Potwierdzone telefonicznie', false),
    (19, 9, 3, '2026-03-29 16:30:00', 'APPROVED', true, 180.00, NULL, false),
    (20, 10, 3, '2026-03-30 08:45:00', 'PENDING', false, 155.00, NULL, false),
    (21, 11, 3, '2026-03-31 13:00:00', 'APPROVED', true, 180.00, NULL, false),

    -- Bielsko-Biała Masters registrations
    (22, 20, 4, '2026-04-10 10:00:00', 'APPROVED', true, 85.00, NULL, false),
    (23, 21, 4, '2026-04-11 11:30:00', 'APPROVED', true, 85.00, NULL, false),
    (24, 22, 4, '2026-04-12 09:45:00', 'APPROVED', true, 85.00, NULL, false),
    (25, 23, 4, '2026-04-13 14:15:00', 'APPROVED', false, 85.00, NULL, false),
    (26, 24, 4, '2026-04-14 16:00:00', 'APPROVED', true, 85.00, NULL, false),
    (27, 25, 4, '2026-04-15 08:30:00', 'PENDING', false, 85.00, NULL, false),

    -- Suwałki PP Juniors registrations
    (28, 12, 5, '2026-05-01 10:00:00', 'APPROVED', true, 105.00, NULL, false),
    (29, 14, 5, '2026-05-02 11:30:00', 'APPROVED', true, 105.00, NULL, false),
    (30, 16, 5, '2026-05-03 09:00:00', 'APPROVED', true, 55.00, NULL, false),
    (31, 17, 5, '2026-05-04 14:00:00', 'APPROVED', false, 105.00, NULL, false),
    (32, 18, 5, '2026-05-05 12:15:00', 'PENDING', false, 55.00, NULL, false)
ON CONFLICT (id) DO NOTHING;


-- =====================================================
-- 9. REGISTERED CATEGORIES
-- =====================================================
INSERT INTO registered_category (id, user_tournament_id, game_category, is_downloaded) VALUES
    -- Poznań Junior registrations
    (14, 7, 'MS_U17', false), (15, 7, 'MD_U17', false),
    (16, 8, 'WS_U15', false), (17, 8, 'WD_U15', false),
    (18, 9, 'MS_U17', false), (19, 9, 'MD_U17', false),
    (20, 10, 'WS_U17', false), (21, 10, 'WD_U17', false),
    (22, 11, 'MS_U19', false),
    (23, 12, 'WS_U17', false), (24, 12, 'WD_U17', false),
    (25, 13, 'MS_U15', false),
    (26, 14, 'WS_U17', false), (27, 14, 'WD_U17', false),

    -- Warszawa GP registrations
    (28, 15, 'MS_OPEN', false), (29, 15, 'MD_OPEN', false), (30, 15, 'XD_OPEN', false),
    (31, 16, 'WS_OPEN', false), (32, 16, 'WD_OPEN', false), (33, 16, 'XD_OPEN', false),
    (34, 17, 'MS_A', false), (35, 17, 'MD_A', false),
    (36, 18, 'MS_B', false),
    (37, 19, 'WS_A', false), (38, 19, 'WD_A', false), (39, 19, 'XD_A', false),
    (40, 20, 'MS_A', false), (41, 20, 'MD_A', false), (42, 20, 'XD_A', false),
    (43, 21, 'WS_A', false), (44, 21, 'WD_A', false), (45, 21, 'XD_A', false),

    -- Bielsko-Biała Masters registrations
    (46, 22, 'MS_40PLUS', false), (47, 22, 'MD_40PLUS', false),
    (48, 23, 'WS_40PLUS', false), (49, 23, 'WD_40PLUS', false),
    (50, 24, 'MS_50PLUS', false), (51, 24, 'MD_50PLUS', false),
    (52, 25, 'WS_50PLUS', false), (53, 25, 'XD_50PLUS', false),
    (54, 26, 'MS_60PLUS', false), (55, 26, 'MD_60PLUS', false),
    (56, 27, 'WS_50PLUS', false), (57, 27, 'XD_50PLUS', false),

    -- Suwałki PP Juniors registrations
    (58, 28, 'MS_U17', false), (59, 28, 'MD_U17', false),
    (60, 29, 'MS_U17', false), (61, 29, 'MD_U17', false),
    (62, 30, 'MS_U19', false),
    (63, 31, 'WS_U17', false), (64, 31, 'WD_U17', false),
(65, 32, 'MS_U15', false)
ON CONFLICT (id) DO NOTHING;


-- =====================================================
-- 10. PARTNERSHIPS (Doubles registrations)
-- =====================================================
INSERT INTO partnership (id, player1_id, player2_id, tournament_id, category, status, created_at, responded_at) VALUES
        -- Poznań Junior partnerships
        (7, 12, 14, 2, 'MD_U17', 'CONFIRMED', '2026-03-15 08:35:00', '2026-03-15 10:00:00'),
        (8, 13, 15, 2, 'WD_U15', 'CONFIRMED', '2026-03-16 10:05:00', '2026-03-16 12:30:00'),
        (9, 15, 17, 2, 'WD_U17', 'CONFIRMED', '2026-03-18 09:20:00', '2026-03-18 11:00:00'),
        (10, 17, 19, 2, 'WD_U17', 'PENDING', '2026-03-20 15:35:00', NULL),

        -- Warszawa GP partnerships
        (11, 4, 6, 3, 'MD_OPEN', 'CONFIRMED', '2026-03-25 09:10:00', '2026-03-25 11:30:00'),
        (12, 4, 5, 3, 'XD_OPEN', 'CONFIRMED', '2026-03-25 09:15:00', '2026-03-26 08:00:00'),
        (13, 5, 9, 3, 'WD_OPEN', 'CONFIRMED', '2026-03-26 10:35:00', '2026-03-26 14:00:00'),
        (14, 5, 6, 3, 'XD_OPEN', 'DECLINED', '2026-03-26 10:40:00', '2026-03-26 16:30:00'),
        (15, 6, 8, 3, 'MD_A', 'PENDING', '2026-03-27 14:05:00', NULL),
        (16, 9, 11, 3, 'WD_A', 'CONFIRMED', '2026-03-29 16:35:00', '2026-03-30 09:00:00'),
        (17, 9, 10, 3, 'XD_A', 'CONFIRMED', '2026-03-29 16:40:00', '2026-03-30 10:15:00'),
        (18, 10, 11, 3, 'XD_A', 'PENDING', '2026-03-30 08:50:00', NULL),
        (19, 11, 4, 3, 'XD_A', 'CONFIRMED', '2026-03-31 13:05:00', '2026-03-31 15:00:00'),

        -- Bielsko-Biała Masters partnerships
        (20, 20, 22, 4, 'MD_40PLUS', 'CONFIRMED', '2026-04-10 10:05:00', '2026-04-10 14:30:00'),
        (21, 21, 23, 4, 'WD_40PLUS', 'CONFIRMED', '2026-04-11 11:35:00', '2026-04-11 16:00:00'),
        (22, 22, 24, 4, 'MD_50PLUS', 'CONFIRMED', '2026-04-12 09:50:00', '2026-04-12 13:45:00'),
        (23, 23, 20, 4, 'XD_50PLUS', 'PENDING', '2026-04-13 14:20:00', NULL),
        (24, 24, 22, 4, 'MD_60PLUS', 'CONFIRMED', '2026-04-14 16:05:00', '2026-04-14 18:30:00'),
        (25, 25, 23, 4, 'XD_50PLUS', 'PENDING', '2026-04-15 08:35:00', NULL),

        -- Suwałki PP Juniors partnerships
        (26, 12, 14, 5, 'MD_U17', 'CONFIRMED', '2026-05-01 10:05:00', '2026-05-01 12:30:00'),
        (27, 17, 19, 5, 'WD_U17', 'PENDING', '2026-05-04 14:05:00', NULL)
ON CONFLICT (id) DO NOTHING;


-- =====================================================
-- 11. EXPORT HISTORY
-- =====================================================
INSERT INTO export_history (id, tournament_id, user_id, export_type, stamp_of_export, export_count, notes) VALUES
                                                                                                               (1, 1, 1, 'FULL', '2026-02-28 10:00:00', 1, 'Pierwszy eksport przed zamknięciem rejestracji'),
                                                                                                               (2, 2, 3, 'FULL', '2026-03-25 14:30:00', 1, 'Eksport startowy'),
                                                                                                               (3, 2, 3, 'NEW_REGISTERED', '2026-03-28 09:00:00', 2, 'Nowi zarejestrowani po pierwszym eksporcie'),
                                                                                                               (4, 3, 1, 'FULL', '2026-04-01 11:00:00', 1, 'Lista startowa GP'),
                                                                                                               (5, 3, 2, 'RANGED_REGISTERED', '2026-04-05 16:00:00', 2, 'Rejestracje z ostatniego tygodnia')
ON CONFLICT (id) DO NOTHING;