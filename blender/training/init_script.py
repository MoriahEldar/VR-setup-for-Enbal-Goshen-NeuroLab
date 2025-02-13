import serial
import bge 
import socket
import datetime
import random
# this script is linked to the outer_wall object
# This script is activated as soon as the game starts (even before the start button is pressed). It's
# in charge of reading the configuration file and extracting all relevant information from it

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
    config_file = open(CONFIG_PATH, "r")
    # gets reward path
    reward_list_path = config_file.readline().replace("\n","")
    # gets air station path
    air_list_path = config_file.readline().replace("\n","")
    # get tone with personal indicator
    tone_with_personal = config_file.readline().replace("\n","")
    # gets port information of tone reward arduino
    tone_reward_com_port = config_file.readline().replace("\n","").split(DELIMITER)
    # get clibaration value
    calib_val = float(config_file.readline().replace("\n",""))
    
    # call for build functions
    init_rewards(reward_list_path, tone_with_personal)
    init_air(air_list_path)
    init_tone_reward_serial(tone_reward_com_port[0], int(tone_reward_com_port[1]), calib_val)


    
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
    
def init_air(path):
    """
    builds the personal air program
    """
    start_obj = bge.logic.getCurrentScene().objects['start']
    
    # build queues for personal option
    air_conf = open(path, "r")
    start_obj['air_stations'] = [line for line in air_conf]
    if len(start_obj['air_stations']) > 0 and 'rand' in start_obj['air_stations'][0]:
        own['rand_air'] = True
        data = start_obj['air_stations'][0].split(';')
        own['rand_time'] = int(data[1])
        own['rand_var'] = int(data[2])
    air_conf.close()

def init_tone_reward_serial(port, speed, calib_val):
    """
    opens arduino port
    """
    # open serial Arduino communication
    player_path = bge.logic.getCurrentScene().objects['player_path']
    
    player_path['serial_obj'] = serial.Serial(port, speed)
    player_path['calib_val'] = calib_val

    

def rand_air():
    obj = bge.logic.getCurrentScene().objects['player_path']
    if not obj['running']:
        return
    if own['rand_air']:
        delta = (datetime.datetime.now() - own['start_time']).total_seconds() 
        if own['rand_timer'] - delta <= 0:
            own['rand_timer'] = random.randint(own['rand_time'] - own['rand_var'], own['rand_time'] + own['rand_var'])
            own['start_time'] = datetime.datetime.now()
            obj['serial_obj'].write(b'2')
            print('rand air puff')

def init_encoder_serial(): 
    """
    opens encoder-arduino port
    """
    # open serial Arduino communication
    player_path = bge.logic.getCurrentScene().objects['player_path']
    player_path['encoder_serial_obj'] = serial.Serial('COM3', 9600, timeout=0)
    player_path['game_on'] = False
    player_path['game_counter']=0 # counts game loops (updated in movement logic) 
    player_path['last_position']=0 # used in movement logic script to moove the player
    player_path['encoder_calib']=0 # used to set the first value readen from the encoder (movement logic)
    player_path['IR']=False # used to detect laps starts (in calib script)
    # create the output data table:
    player_path['output_data']=[[0]*400000 for x in range(4)] #(4 columns, 400000 rows)
    # columns in the 'output data' table:
    player_path['IR_DATA']=0
    player_path['ROTATE_ENCODER_DATA']=1
    player_path['REWARD_DATA']=2
    player_path['LIC_DATA']=3


def init_IR_serial():
    """
    opens arduino port
    """
    # open serial Arduino communication
    player_path = bge.logic.getCurrentScene().objects['player_path']
    player_path['serial_IR_obj'] = serial.Serial('COM6', 9600, timeout=0)
    
  
 
 
 
init()  
rand_air()
init_encoder_serial()
init_IR_serial()