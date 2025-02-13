import serial
import bge 
import socket
import datetime
import random
# this script is linked to the outer_wall object
# This script is activated as soon as the game starts (even before the start button is pressed). It's
# in charge of reading the configuration file and extracting all relevant information from it
# ?shold it be change to be manual called?

CONFIG_PATH = "C:\\Users\\owner\\Desktop\\Ido\\VR_PROJECT\\VR_DRIVE\\CONF\\system_configuration"
EMPTY_PROP ="#"
DELIMITER = ","
cont = bge.logic.getCurrentController()
own = cont.owner


def init():
    # makes sure this happens once
    if own['configed']:
        return
    own['configed'] = True
    
    # opens configuration file
    config_file = open(CONFIG_PATH, "r") # Change to get data from socket
    # gets reward path
    reward_list_path = config_file.readline().replace("\n","")
    # get tone with personal indicator
    tone_with_personal = config_file.readline().replace("\n","")

    # call for build functions
    init_rewards(reward_list_path, tone_with_personal)
    
    config_file.close()




def init_rewards(path, mit_tone):
    """
    builds the personal program
    """
    start_obj = bge.logic.getCurrentScene().objects['start']
        
    if path == EMPTY_PROP:
        start_obj['personal'] = False 
        return
    
    if mit_tone == EMPTY_PROP:
        start_obj['mit_tone'] = False 
    
    # build queues for personal option
    personal_conf = open("C:\\Users\\owner\\Desktop\\Ido\\VR_PROJECT\\VR_DRIVE\\CONF\\rand_rewards_new.txt", "r")
    start_obj['rewards'] = [line for line in personal_conf]
    personal_conf.close()
    
    
def init_encoder_serial(): 
    """
    opens encoder-arduino port
    """
    # open serial Arduino communication
    player_path = bge.logic.getCurrentScene().objects['player_path']
    player_path['game_on'] = False
    player_path['game_counter']=0 # counts game loops (updated in movement logic) 
    player_path['last_position']=0 # used in movement logic script to moove the player
    player_path['encoder_calib']=0 # used to set the first value readen from the encoder (movement logic)
    player_path['IR']=False # used to detect laps starts (in calib script)

   
    
init()    
init_encoder_serial()  