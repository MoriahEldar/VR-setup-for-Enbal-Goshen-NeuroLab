# this script is linked to the player_path object
# The script is in charge of starting the game
# automatically when the shift keyboard is pressed (by the matlab code)
# ?why is it not with the init?!

import bge 
player_path = bge.logic.getCurrentScene().objects['player_path']
player_path['game_on'] = True
player_path['game_counter'] = 0
player_path['laps_counter'] = 1