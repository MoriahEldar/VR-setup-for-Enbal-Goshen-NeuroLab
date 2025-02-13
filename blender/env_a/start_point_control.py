import bge
import GameLogic
import numpy as np
RADIUS = 35
DELIMITER = ','

# this script is linked to start
# This script is in charge of setting all the necessary rewards in place for each new round, based on the
# reward sequences set beforehand by the user

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
    print("at start station #######################")
    cont = bge.logic.getCurrentController()
    own = cont.owner
    if not own['initiated']:
        print("setting new stations")
        set_new_stations(own)
        if not outer_wall['rand_air']:
            set_new_air_stations(own)
        own['initiated'] = True
        
    # when the player has collided with the start object
    if collision(cont):
        print("numpass is:" + str(own['num_pass']))
        if own['num_pass'] >= own['num_rewards']:
            own['num_pass'] = 1
            
            player_path = bge.logic.getCurrentScene().objects['player_path'] 
            player_path['prev'] = player_path ['data']
            
            if own['personal']:
                if own['round_count'] == 1:
                    # If we finished the number of laps in this sequence, we set new stations
                    set_new_stations(own)
                else:
                    if own['round_count'] != 'inf':
                        own['round_count'] -= 1
        if not outer_wall['rand_air']:
         if own['air_pass'] >= own['num_air']:
            own['air_pass'] = 1
            
            
            if own['air_rounds'] == 1:
                # If we finished the number of laps in this sequence, we set new stations
                set_new_air_stations(own)
            else:
                if own['air_rounds'] != 'inf':
                    own['air_rounds'] -= 1

def set_new_stations(own):
    """
    re-builds according with personal program  
    """
    
    # clean round from last stations
    for i in range (26, 0, -1):
        objName = "rewardST" + str(i)
        obj = bge.logic.getCurrentScene().objects[objName]
        obj['prob'] = 0
        obj.worldPosition = [0, 0, 0]
    
    # clean round from last tones
    #for i in range(15, 0, -1):
     #   objName = "sound" + str(i)
      #  obj = bge.logic.getCurrentScene().objects[objName]
       # obj.worldPosition = [0, 0, 0]

# get the number of rewards for current sequence
    own['num_rewards'] = 0
    own['current_sequence'] = []
    #print("own['rewards'] = ",  own['rewards'])
    #print("own['sequence'] = ", own['sequence'])
    
    
    for rew in own['rewards']:
        if 'sequence:' + str(own['sequence']) in rew:
            
            # remove annoying matlab apostrophe
            rew = rew.replace("'", "")
            print("rew = ", rew)
            # this is for when there are 2 digit sequence numbers (1~=11...)
            temp = rew.split(DELIMITER)
            # print("temp[6] = ", temp[6])
            temp2 = temp[6].replace('sequence:', '').strip()
            
            # print("temp2 = ", temp2)
            
            if rew[0] != '0' and temp2 == str(own['sequence']):
                own['num_rewards'] += 1
                # print("got to cond. sequence number =", own['current_sequence'])
            own['current_sequence'].append(rew)
    if len(own['current_sequence']) == 0:
        own['round_count'] = 'inf'
        return


    
    # get the number of rewards for current sequence - original version
#    own['num_rewards'] = 0
 #   own['current_sequence'] = []
#    for rew in own['rewards']:
#        if 'sequence:' + str(own['sequence']) in rew:
 #           if rew[0] != '0':
  #              own['num_rewards'] += 1
   #         own['current_sequence'].append(rew)
#    if len(own['current_sequence']) == 0:
 #       own['round_count'] = 'inf'
  #      return
    
    # get the number of laps for current phase
    line = own['current_sequence'][0].split(DELIMITER)
    rounds = line[-1].replace('laps:', '').strip()
    if rounds == 'inf':
        own['round_count'] = 'inf'
    else:
        own['round_count'] = int(rounds)
    
    # set rewards uniformly in the maze
    #section = 2 * np.pi / (own['num_rewards'])
    print("setting num rewards to ", own['num_rewards'])
    # print("current sequence: " + own['current_sequence'])
    #shift = 0.3
    #prev_angle = 0
    #first_angle = (own['num_rewards']- 1) * section + shift
    
    # fix space ratio 
    if own['num_rewards'] == 1:
        TONE_PLACE = 0.5
    else:
        TONE_PLACE = 0.8
        
    for i in range (own['num_rewards']-1,-1,-1):
        objName = "rewardST" + str(own['num_rewards'] - i)
        #toneName = "sound"  + str(own['num_rewards'] - i)
        obj = bge.logic.getCurrentScene().objects[objName]
        line = own['current_sequence'][own['num_rewards'] - i - 1].split(DELIMITER)
        x = float(line[1])
        y = float(line[2])
        obj['initProb'] = float(line[5])
        
        #angle = i * section +  shift
        #x = RADIUS * np.cos(angle)
        #y = RADIUS * np.sin(angle)
        obj.worldPosition = [x, y, 0]
        obj['reward'] = line[4]
        
        # set corresponding tone station
        #if own['mit_tone']:
            #tone = bge.logic.getCurrentScene().objects[toneName]
            #tone_angle = angle + shift
            #x = RADIUS * np.cos(tone_angle)
            #y = RADIUS * np.sin(tone_angle)    
            #tone.worldPosition = [x, y, 0]
            
        #prev_angle = angle
        # print 
        print (objName," ", obj.worldPosition)
    
    own['sequence'] += 1
    
def set_new_air_stations(own):
    """
    re-builds according with personal program  
    """
    
    # clean round from last tones
    for i in range(15, 0, -1):
        objName = "sound" + str(i)
        obj = bge.logic.getCurrentScene().objects[objName]
        obj.worldPosition = [0, 0, 0]
    
    # get the number of rewards for current sequence
    own['num_air'] = 0
    own['current_air_sequence'] = []
    for rew in own['air_stations']:
        if 'sequence:' + str(own['air_sequence']) in rew:
            if rew[0] != '0':
                own['num_air'] += 1
            own['current_air_sequence'].append(rew)
    if len(own['current_air_sequence']) == 0:
        own['air_rounds'] = 'inf'
        return
    
    # get the number of laps for current phase
    line = own['current_air_sequence'][0].split(DELIMITER)
    rounds = line[-1].replace('laps:', '').strip()
    if rounds == 'inf':
        own['air_rounds'] = 'inf'
    else:
        own['air_rounds'] = int(rounds)
    
    print("setting num air stations to ", own['num_air'])    
        
    for i in range (own['num_air']-1,-1,-1):
        airName = "sound"  + str(own['num_air'] - i)
        obj = bge.logic.getCurrentScene().objects[airName]
        line = own['current_air_sequence'][own['num_air'] - i - 1].split(DELIMITER)
        x = float(line[1])
        y = float(line[2])
        obj.worldPosition = [x, y, 0]
        print (airName," ", obj.worldPosition)
    
    own['air_sequence'] += 1
    
apply_start_logic()