import bge
import serial
import csv

# this script is linked to outer_wall
# This script is called when the 'Esc' key is pressed (either automatically or manually). It closes
# the communication with the multi_ttl arduino
 

player_path = bge.logic.getCurrentScene().objects['player_path']
#player_path['serial_obj'].write(b'4')
#player_path['serial_obj'].close()
LOC_CONFIG_PATH='C:/Users/owner/Desktop/Ido/VR_PROJECT/VR_DRIVE/CONF/loc_config.conf'
loc_config_file=open(LOC_CONFIG_PATH,'r')
# gets output path
output_path = loc_config_file.readline()

file_name = 'output_training_data.csv'
table = player_path['output_data']


# Export the game data to a new csv file:

with open(output_path+'/'+file_name, 'w', newline='') as file:
    writer = csv.writer(file)
    writer.writerows(table)
