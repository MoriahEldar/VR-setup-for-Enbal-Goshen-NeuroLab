# this script is linked to the player_path object
# The script is in charge of adding info the the data table whenever the 'd' key is pressed. It is triggered
# automatically when the lic sensor recognizes lics

import bge 
ply = bge.logic.getCurrentScene().objects['player_path']

col = ply['LIC_DATA']
row = ply['game_counter']
ply['output_data'][col][row] = 1