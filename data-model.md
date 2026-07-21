# TCRS — data model


```mermaid
erDiagram
    USER ||--o{ RESERVATION : books
    USER |o--o{ RESERVATION : "is partner in"
    USER ||--o{ GROUP_PLAYER : is
    USER ||--o{ MATCH : "is player1 in"
    USER ||--o{ MATCH : "is player2 in"
    USER |o--o{ MATCH : "won"
    USER |o--o{ MATCH : "entered score for"
    TOURNAMENT ||--|{ GROUP : has
    GROUP ||--o{ GROUP_PLAYER : has
    GROUP ||--o{ MATCH : has
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

---

## Design decisions

Only the decisions that were genuine forks are recorded here; the rest are
visible directly in the diagram.

### Q2 — one MATCH table, not two
Group and elimination matches share almost all behaviour: two players, a score,
a winner, a "who entered it", and the same rule for who may enter or edit a
score. They differ only in context — a group match has a `group_id` and
`round_number`; an elimination match has a `next_match_id`. Splitting them into
two tables would duplicate the score-entry endpoint, the permission check, and
the "my unresolved matches" query, and the two copies would drift apart over
time. One table with a few nullable context columns keeps that behaviour in a
single place. The cost — some columns are null depending on the match kind — is
worth paying for that single source of logic.

### Q8 — round as a column, not its own table
A `ROUND` table would only earn its place if a round had **stored** attributes of
its own. It mostly doesn't: whether a round is "complete" is *computed* (are all
of its matches resolved?), not stored, and "start the next round" is an admin
action, not a persisted piece of state. So a round is really just a label that
groups matches, which an integer `round_number` on `MATCH` captures fully —
"round 2's matches" is simply `WHERE round_number = 2`, no join. If a round ever
needs genuinely stored state (a scheduled date, or an explicit admin-set
"closed" flag that must persist even while some matches are unresolved), that is
the signal to promote it to a table.

### Keeping `winner_id` instead of deriving it
The winner is derivable — it's whoever has more games. I store it anyway as a
deliberate, small denormalisation, because two things read the winner directly
and often: the standings computation and elimination advancement
(`next_match_id`). Deriving it at every read (comparing games row by row) is more
error-prone than maintaining one column, which is set once, when the score is
entered. The price is a consistency rule — `winner_id` must always match the
player with the higher game count — enforced at score-entry time.
