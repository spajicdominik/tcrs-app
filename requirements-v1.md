# Tennis court reservation system (TCRS) requirements

## Reservations

The object contains only one tennis court with the opening hours from 07:00 - 20:30. The players are able to insert a reservation of a  hour and a half with the start of the reservation being any hour or half hour on the timeline, as long as it is available. The gaps of half hour or hour cannot be booked. It is not possible to book more than an hour and a half. Leftover gaps shorter than 90 minutes are accepted dead space.

A player can book a reservation maximum one week in advance. There is no limit on how many active reservations a player can have. The reservation cannot be cancelled within 24 hours of start.

A player booking a reservation is a registered user of the app with its credentials. The player is able to add another player to the reservation where the other player does not recieve an invitation, but rather is added imidiatelly to the booking. The partner cannot delete the reservation, but can delete himself from the reservation. Then the player making the reservation is able to add another player to the booking. Adding of the partner is optional and a player is able to book a reservation without adding a partner.

The admin user is able to make a reservation, as well as modifying and deleting any existing reservation by any user. Admin has the rights to cancel a reservation even if its within 24 hours of the booking.

If a player is participating in the tournament, the player should be able to select if the booking is for a friendly match or for a tournament match. If it is for a tournament match, then the reservation shows the player a list of their open fixtures, whether its a regular tournament match or a unresolved tournament match and the player has to choose which match he is playing.

## Tournament

This system also contains a tournament section which has two phases - the group phase and the elimination phase.

The matches are played in a best of 9 games, one set format, where the games are the regular 0-15-30-40-deuce-adv format. Example, player 1 won 9:6 against player 2. If the match is at 8:8, then a 10 point tie-break is played to determine the winner. The tie break score is not stored, just 9:8 is the input.

The group phase of the tournament consists of n number of groups with m number of players in each group. Each player has to play exactly one match with each of the players in his group. When a player wins a match, he gets a point. For a defeat, he gets no points. The table contains the names of the players along with the number of matches played, number of points and games difference. Games difference are calculated by subtracting the numbers of games lost versus the number of games won by the player in all of the matches played. Example, if player 1 won one match 9:3 and lost one match 6:9, then his game difference is +3.

The ranking of players in a group is determined by the following chain, applied at every position (not only the qualifying cut-off):

- Points (matches won) — higher ranks higher.
- Game difference over all group matches — higher ranks higher.
- Mini-table among the tied players, computed from only the matches played between those tied players: first by points won in those    matches, then by game difference in those matches. (For a 2-way tie this is just the head-to-head result.)
- Total games won across all group matches.
- If players are still exactly tied, the admin decides (or draws lots).
This ordering only has to be strict at the moment the elimination bracket is generated; before then, the live group table may show tied players as level.


At the end of the group phase, first x players from each group go to the elimination phase of the tournament. The number of players going to the next phase is determined by the admin when making the tournament.

In the elimination phase, there is one match played per phase (example. quarter final- semifinal - final).

For each round of the group and elimination phase players should be able to input the result of the match played and then the tables (group phase) or diagram (elimination phase) are updated accordingly.

There can only be one tournament active at one time. The admin user creates the tournament where he inputs the number of groups with the number of players in each group. The system then gives out the possible options for the elimination phase. For an example, if an admin user creates a tournament with 2 groups of 10 people, then the admin can choose to select 2, 4, 8 or 16 best ranked players from total of 20 people to go to the elimination phase. If selected 2, then the system just creates the elimination phase consisting of a final, or if he selects 8 players, then the system creates the elimination phase consisting of a quarter final, semi final and a final. When the tournament is finished (when the final match is played), then admin has the option to close the tournament. Admin cannot create a new tournament if the current tournament isn't finished and closed. Admin also can't close the tournament which isn't finished. 

The bracket generator for the elimination phase takes the best ranked players from all of the groups and matches them with the lest ranked players from all of the groups. For instance, if there are 4 groups, then the generator will pair 1st place player from the first group with the last (qualifying) place player from the forth group etc.

For each round of the group phase there is a input bracket for each match of that round. Only the 2 players of that match can input the result of the match played. After the round is finished, then the new matches for the new round are generated until all matches are played. After one of the players have inputed the result, only the player who entered the result can modify the score. The admin can also modify the result for any match. If it happens that two players haven't played and the tournament has to go to the next round, then the match remains unresolved and the players are able to input their score later. This match is then visible under "unresolved matches" on their profile, where they can later input the score and the group bracket will be adjusted accordingly. Before the elimination phase starts, every group match must be resolved - which means the admin needs a walkover/forfeit action (e.g., admin awards 9:0) for matches that were never played. Group-phase scores lock the moment the elimination phase is generated — after that, admin only changes.

The round advances after all of the matches have been played or when the admin starts the next round. If an admin starts the next round with some of the matches not played, then they automatically get put in unresolved matches of corresponding players.

## User

User represents a player. User has an username, password, first name, last name, phone number and mail. A player has to register first by inputing the following data and a request for registration is then sent. Upon registring, the admin user has to approve of the request for the player to be able to login. 

The user is able to update his credentials, but if a user wants to update his password or his mail, then a verification proccess is needed through the mail that is currently in the system.

User can see his upcoming reservations and the history of previously played matches on his profile. The user can also see if he is participating in a current active tournament. If he is participating, then there should be displayed his next match in the tournament. If player is not in the tournament or didn't pass through to the elimination phase, this should be empty.

Admin user is able to deactivate other users and modify his first name, last name and phone number. The admin user is not able to change the password or the mail of the other users. A regular user is not able to deactivate his user or any other user for that matter.

Admin user has access to the archive of all of the past tournaments. The player users only have access to archive of all of the past tournaments they played in.

## Out of scope

- Email verification for password/email changes
- Notifications/reminders
- Multiple courts
- Multiple tournaments
