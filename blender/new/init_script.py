import json
import serial
import bge 
import socket
import sys
import datetime
import random
# this script is linked to the outer_wall object
# This script is activated as soon as the game starts (even before the start button is pressed). It's
# in charge of reading the configuration file and extracting all relevant information from it
# ?shold it be change to be manual called?

PORT = 0
cont = bge.logic.getCurrentController()
own = cont.owner


def init():
    # makes sure this happens once
    if own['configed']:
        return
    own['configed'] = True

    # call for build functions
    init_rewards()
    init_java_socket()


def init_rewards():
    """
    builds the personal program
    """
    start_obj = bge.logic.getCurrentScene().objects['start']

    # load data from the command-line parameters
    args = sys.argv
    start_obj['rewards'] = []
    if '--' in args:
        params_index = args.index('--') + 1
        start_params = args[params_index:]
        if len(start_params) >= 1:
            start_obj['rewards'] = json.loads(start_params)
    
    
def init_java_socket(): 
    """
    opens a socket to the java program
    """
    # open serial Arduino communication
    player_path = bge.logic.getCurrentScene().objects['player_path']
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(('localhost', PORT)) # TODO change to the right port
    server_socket.listen(1) # TODO check if to add more
    client_socket, addr = server_socket.accept() # works? knows how to wait?
    player_path['java_socket_obj'] = client_socket
    
    player_path['game_on'] = False
    player_path['game_counter']=0 # counts game loops (updated in movement logic) 
    player_path['last_position']=0 # used in movement logic script to moove the player
    player_path['encoder_calib']=0 # used to set the first value readen from the encoder (movement logic)
    player_path['IR']=False # used to detect laps starts (in calib script)
    


init()