import bge
import GameLogic
import numpy as np
# this script is linked to rewardSTi object for 1 < i < 26
# This script is in charge of deciding if a reward appears (in case its probability is neither 0 nor 1)
# and then in charge of giving a reward signal to the multi_ttl arduino, so that the mouse may recieve
# its reward.

def collision(cont):
    # Loops through all connected sensors and returns if one is False
    # Basically makes it work like an And conroller.
    for sens in cont.sensors:
        if not sens.positive:
            return False
    return True


def apply_reward_logic():
    cont = bge.logic.getCurrentController()
    own = cont.owner
  
 
 ## my code:

              
    
    # check for proper activation
    if collision(cont):
        player_path = bge.logic.getCurrentScene().objects['player_path']
        start = bge.logic.getCurrentScene().objects['start']
        print("At reward station number \t %s \n already passed \t %s rewards" %(str(own['id']), str(start['num_pass'])))
        # check if this station has been reached in the correct order
        if own['id'] == start['num_pass']:
            own['prob'] = np.random.binomial(1, own['initProb'])
            start['num_pass'] += 1
            # check if this station should give a reward
            if own['prob'] == 1:
                if own['reward'] != "0.0":
                    player_path['serial_obj'].write(b'1') 
                    print("reward at station %s was given" %(own['id']))
                    
                    # Update reward information in the data table:
                    col = player_path['REWARD_DATA'] 
                    row = player_path['game_counter'] 
                    player_path['output_data'][col][row] = 1  
                
                
        
        # (Ido): I commented all of this out, but its supposed to prevent a long sequence of rewards
        # appearing/not appearing, if their probability isn't 1 or 0. I think it's pretty irrelevant
        # as it completely undermines the concept of probability, but if someone wants to add this 
        # feature they're welcome to use it... It will probably not work as it is because I made
        # some changes, so you will need to debug it
        
         
        # fixing random reward
        #start_obj = bge.logic.getCurrentScene().objects['start']
        #if start_obj['num_rewards'] <= 2 and own['id'] == 1 and own['prob'] != 1.0 and own['prob'] != 0:
            #if own['prevProb'] == own['prob']:
             #   own['inRow'] += 1
              #  if own['inRow'] >= 3:
               #     own['inRow'] = 0
                #    own['prob'] = 1 - own['prob']
            #own['prevProb'] = own['prob']
                

apply_reward_logic()             