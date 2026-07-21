-- V1: initial schema for the tennis court reservation system
-- Tables are created in foreign-key dependency order:
-- app_user -> tournament -> tournament_group -> group_player -> match -> reservation

CREATE TABLE app_user (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    phone_number  VARCHAR(30)  NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    status        VARCHAR(20)  NOT NULL DEFAULT 'pending'
                      CHECK (status IN ('pending', 'active', 'deactivated')),
    role          VARCHAR(20)  NOT NULL DEFAULT 'player'
                      CHECK (role IN ('player', 'admin'))
);

CREATE TABLE tournament (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name                 VARCHAR(100) NOT NULL UNIQUE,
    phase                VARCHAR(20)  NOT NULL DEFAULT 'group'
                             CHECK (phase IN ('group', 'elimination', 'finished', 'closed')),
    qualifiers_per_group INT NOT NULL DEFAULT 1
);

CREATE TABLE tournament_group (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tournament_id BIGINT NOT NULL REFERENCES tournament(id),
    name          VARCHAR(50) NOT NULL
);

CREATE TABLE group_player (
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    group_id BIGINT NOT NULL REFERENCES tournament_group(id),
    user_id  BIGINT NOT NULL REFERENCES app_user(id),
    UNIQUE (group_id, user_id)
);

CREATE TABLE match (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    group_id         BIGINT REFERENCES tournament_group(id),
    round_number     INT NOT NULL,
    player1_id       BIGINT NOT NULL REFERENCES app_user(id),
    player2_id       BIGINT NOT NULL REFERENCES app_user(id),
    player1_games    INT,
    player2_games    INT,
    winner_id        BIGINT REFERENCES app_user(id),
    score_entered_by BIGINT REFERENCES app_user(id),
    next_match_id    BIGINT REFERENCES match(id)
);

CREATE TABLE reservation (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    time_start     TIMESTAMPTZ NOT NULL,
    time_end       TIMESTAMPTZ NOT NULL,
    canceled       BOOLEAN NOT NULL DEFAULT FALSE,
    date_modified  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    match_type     VARCHAR(20) NOT NULL DEFAULT 'friendly'
                       CHECK (match_type IN ('friendly', 'tournament')),
    main_player_id BIGINT NOT NULL REFERENCES app_user(id),
    partner_id     BIGINT REFERENCES app_user(id),
    match_id       BIGINT REFERENCES match(id)
);
