import bge
import GameLogic
import numpy as np
# this script is linked to the starting point, gives TTL when the start point is reached

def collision(cont):
    # Loops through all connected sensors and returns if one is False
    # Basically makes it work like an And conroller.
    for sens in cont.sensors:
        if not sens.positive:
            return False
    return True


def apply_IR_logic():
    cont = bge.logic.getCurrentController()
    own = cont.owner
    
    # check for proper activation
    if collision(cont):
        player_path = bge.logic.getCurrentScene().objects['player_path']
        print("At start station")
        player_path['serial_IR_obj'].write(b'9') 
        print("IR sig given to NI")
        # Update reward information in the data table:
        col = player_path['IR_DATA'] 
        row = player_path['game_counter'] 
        player_path['output_data'][col][row] = 1  
                

apply_IR_logic()    
