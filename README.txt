This project is a command line solitaire game, which can be run using the included jar.
It features three variations, basic, which is a standard game of solitaire with unlimited
opportunities to shuffle the draw pile, limited, which limits the number of times the draw pile can
be shuffled, or whitehead, which has unique rules.

Command line args:
First argument MUST be one of "basic", "limited", or "whitehead"
If the first argument is "limited" then the second argument MUST be an integer
representing the number of cards to draw.
After the required argument(s) there are 2 more optional integer arguments.
The first is the number of cascade piles to play with
The second is the number of draw cards to show at a time.

Playing the game:
The game has 5 commands:
mpp int int which moves the first pile number onto the second pile number if allowed
md int which moves the top card of the draw pile onto the specified pile if allowed
mpf int int which moves the top card of the specified pile to the specified foundation pile if allowed
mdf int which moves the top card of the draw pile to the specified foundation pile if allowed
dd which cycles the draw pile