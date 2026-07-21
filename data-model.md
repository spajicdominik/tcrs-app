# TCRS — data model


```mermaid
erDiagram
    USER ||--o{ RESERVATION : books
    USER |o--o{ RESERVATION : "is partner in"
    USER ||--o{ GROUP_PLAYER
    USER ||--o{ MATCH : "is player1 in"
    USER ||--o{ MATCH : "is player2 in"
    USER |o--o{ MATCH : "won"
    USER |o--o{ MATCH : "entered score for"
    TOURNAMENT ||--|{ GROUP : has
    GROUP ||--o{ GROUP_PLAYER : has
    GROUP ||--o{ MATCH
    MATCH |o--o{ MATCH : "winner advances to"

    USER {
        bigint id PK
        string username
        string password
        string first_name
        string last_name
        string phone_number
        string email
        string status "pending, active, deactivated"
        string role "player, admin"
    }

    RESERVATION {
      bigint id PK
      timestamp time_start
      timestamp time_end
      boolean canceled
      timestamp date_modified
      string match_type "tournament, friendly"
      bigint main_player_id FK
      bigint partner_id FK
    }

    TOURNAMENT {
      bigint id PK
      string name
      string phase "group, elimination, finished, closed"
      int qualifiers_per_group
    }
    
    GROUP {
      bigint id PK
      bigint tournament_id FK
      string name
    }

    GROUP_PLAYER {
      bigint id PK
      bigint group_id FK
      bigint user_id FK
    }

    MATCH {
      bigint id PK
      bigint player1_id FK
      bigint player2_id FK
      int player1_games
      int player2_games
      bigint winner_id FK
      bigint score_entered_by FK null
      bigint group_id FK
      int round_number
      bigint next_match_id FK
    }

```
