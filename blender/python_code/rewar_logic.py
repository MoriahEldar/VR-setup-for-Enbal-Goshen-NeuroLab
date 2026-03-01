import bge
import GameLogic
import numpy as np
# this script is linked to rewardST
# This script is in charge of configuring the reward to the right place each time and to announce a collision
# This code is triggered ONLY if the system identifies a collision (but not always it is really a collision)!!
player_path = bge.logic.getCurrentScene().objects['player_path']
player = bge.logic.getCurrentScene().objects['player']

def apply_reward_logic():
    cont = bge.logic.getCurrentController()
    own = cont.owner

    # collision only if the player is moving forward
    if player_path['backwards_movment'] > 0:
        start = bge.logic.getCurrentScene().objects['start']
        if not player_path['reward_collision'] and own['id'] == start['num_pass'] + 1 and own.getDistanceTo(player) < 10.0:
            player_path['reward_collision'] = True
            start['num_pass'] += 1
            position = []

            if start['num_pass'] < len(start['rewards'][0]):
                position = start['rewards'][0][start['num_pass']]
                own['id'] = start['num_pass'] + 1
            else:
                # If the round finished, calculate the next round reward
                # Do the last lap forever
                if len(start['rewards']) > 1:
                    start['rewards'].pop(0)
                if len(start['rewards'][0]) > 0:
                    position = start['rewards'][0][0]
                    own['id'] = 1
                else:
                    own['id'] = 0

            positionDeepCopy = position.copy()
            positionDeepCopy.append(0)
            own.worldPosition = positionDeepCopy

apply_reward_logic()             