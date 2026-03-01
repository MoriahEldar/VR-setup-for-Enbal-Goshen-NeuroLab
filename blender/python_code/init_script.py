import json
import bge 
import socket
import sys
import datetime
import random
# this script is linked to the outer_wall object
# This script is activated as soon as the game starts (even before the start button is pressed). It's
# in charge of reading the configuration file and extracting all relevant information from it

PORT = 65432
cont = bge.logic.getCurrentController()
own = cont.owner
player_path = bge.logic.getCurrentScene().objects['player_path']

SENSITIVITY_PARAMETER = 0.00097

def init():
    # makes sure this happens once
    if player_path['running']:
        return
    player_path['running'] = True
    player_path['laps_counter'] = 1
    # for checking the senstivity parameter:
    # player_path['first'] = True

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
        start_params = ''.join(args[params_index:])
        if len(start_params) >= 1:
            start_obj['rewards'] = json.loads(start_params)
    
    
def init_java_socket(): 
    """
    opens a socket to the java program
    """
    # open serial Arduino communication
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(('localhost', PORT)) # TODO change to the right port
    server_socket.listen(1) # TODO check if to add more
    print("trying to connect to java program")
    client_socket, addr = server_socket.accept() # works? knows how to wait?
    print("connected to java program")
    player_path['java_socket_obj'] = client_socket
    player_path['server_socket_obj'] = server_socket
    
    player_path['sockets_configed'] = True



init()