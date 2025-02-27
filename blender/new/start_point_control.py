import bge
import GameLogic
import numpy as np

# this script is linked to start
# This script is in charge of setting all the necessary rewards in place for each new round, based on the
# reward sequences set beforehand by the user
# I put all the air station code in comments, because it is not used in the current version of the experiment
# If you want to use it, probbably a lot of changes well be needed.

def collision(cont):
    # Loops through all connected sensors and returns if one is False
    # Basically makes it work like an And conroller.
    for sens in cont.sensors:
        if not sens.positive:
            return False
    return True


def apply_start_logic():
    outer_wall = bge.logic.getCurrentScene().objects['outer_wall']
    obj = bge.logic.getCurrentScene().objects['player_path']
    if not obj['running']:
        return
    cont = bge.logic.getCurrentController()
    own = cont.owner

    # initializing the stations at the beginning of the experiment
    if not own['initiated']:
        player_path['reward_collision'] = False
        
        # set rewards defenitions
        own['num_pass'] = 0
        #! what happens if there is no rewards?
        own['num_rewards'] = len(own['rewards'][0])
        
        # set first reward position
        position = own['rewards'][0][0]
        position.append(0)
        station = bge.logic.getCurrentScene().objects["rewardST"]
        station.worldPosition = position

        # if not outer_wall['rand_air']:
        #     set_new_air_stations(own)
        own['initiated'] = True
        
    # when the player has collided with the start object
    if collision(cont):
        player_path = bge.logic.getCurrentScene().objects['player_path'] 
        # if it passed all the rewards and straight
        if own['num_pass'] >= own['num_rewards'] and player_path['backwards_movment'] > 0:
            # passed a lap
            player_path['laps_counter'] += 1
            
            # lap reward initilaization
            own['num_pass'] = 0
            own['num_rewards'] = len(own['rewards'][0])

        # if not outer_wall['rand_air']:
        #  if own['air_pass'] >= own['num_air']:
        #     own['air_pass'] = 1
            
            
        #     if own['air_rounds'] == 1:
        #         # If we finished the number of laps in this sequence, we set new stations
        #         set_new_air_stations(own)
        #     else:
        #         if own['air_rounds'] != 'inf':
        #             own['air_rounds'] -= 1

  
# def set_new_air_stations(own):
#     """
#     re-builds according with personal program  
#     """
    
#     # clean round from last tones
#     for i in range(15, 0, -1):
#         objName = "sound" + str(i)
#         obj = bge.logic.getCurrentScene().objects[objName]
#         obj.worldPosition = [0, 0, 0]
    
#     # get the number of rewards for current sequence
#     own['num_air'] = 0
#     own['current_air_sequence'] = []
#     for rew in own['air_stations']:
#         if 'sequence:' + str(own['air_sequence']) in rew:
#             if rew[0] != '0':
#                 own['num_air'] += 1
#             own['current_air_sequence'].append(rew)
#     if len(own['current_air_sequence']) == 0:
#         own['air_rounds'] = 'inf'
#         return
    
#     # get the number of laps for current phase
#     line = own['current_air_sequence'][0].split(DELIMITER)
#     rounds = line[-1].replace('laps:', '').strip()
#     if rounds == 'inf':
#         own['air_rounds'] = 'inf'
#     else:
#         own['air_rounds'] = int(rounds)
    
#     print("setting num air stations to ", own['num_air'])    
        
#     for i in range (own['num_air']-1,-1,-1):
#         airName = "sound"  + str(own['num_air'] - i)
#         obj = bge.logic.getCurrentScene().objects[airName]
#         line = own['current_air_sequence'][own['num_air'] - i - 1].split(DELIMITER)
#         x = float(line[1])
#         y = float(line[2])
#         obj.worldPosition = [x, y, 0]
#         print (airName," ", obj.worldPosition)
    
#     own['air_sequence'] += 1
    
apply_start_logic()