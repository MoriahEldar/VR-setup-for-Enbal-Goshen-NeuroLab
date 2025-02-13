import bge 
import datetime
# this script is linked to the outer_wall object
# This script is called when the 'Enter' key is pressed, and it happens automatically when the game is
# launched through the java engine.

player_path = bge.logic.getCurrentScene().objects['player_path']
outer_wall = bge.logic.getCurrentScene().objects['outer_wall']
# init player TODO move to start logi

if not player_path['running']:
    player_path['running'] = True
    outer_wall['start_time'] = datetime.datetime.now()
    
    # send init ttl through tone_reward arduino
    player_path['serial_obj'].write(b'4')
    
    print("#################################")
    print("Strted Test Date")
    print (datetime.datetime.now())
    print("#################################")