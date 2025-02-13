import bge 
import serial
import time


SENSITIVITY_PARAMETER = 0.00097

  
player_path = bge.logic.getCurrentScene().objects['player_path']    


# Update the location and IR information in the data table 
player_path['game_counter']+=1 
col = player_path['ROTATE_ENCODER_DATA'] 
row = player_path['game_counter']
player_path['output_data'][col][row] = player_path['last_position']


def apply_movement(): 
  # This function reads the encoder-arduino information and move
  # the player in the game according to it:
  
  encoder_read = player_path['encoder_serial_obj'].readlines() # This is a list of reads from the encoder
  # If the lost is not empty, take the latest value:
  if encoder_read:
      encoder_read = encoder_read[-1]
      new_position = get_read_number(encoder_read)
      # If the encoder rotates, move the player: 
      if abs(new_position - player_path['last_position'])<1000:  # This condition prevents errors when the new position is very far from the last position
          move_player(new_position)
          # Update the 'last position' value:
          player_path['last_position'] = new_position


def get_read_number(read):
  # This function gets the read from the encoder-arduino 
  # as a binary type that looks like: b'-7773\r\n'
  # It turns it into a string and takes the numbers (and the minus if exists), and returns it as a number:
  number = str(read)[2:-5]
  number = int(number)
  if player_path['encoder_calib'] == 0:  # In the begining of the game set the 'encoder_calib' value as the first read of the encoder
      player_path['encoder_calib'] = number
  return number - player_path['encoder_calib']

  
def move_player(new_position):
  # This function gets the new position and moves the player in the game:
  
  delta = player_path['last_position'] - new_position 
  # Find out the current coordinates:
  [current_x,current_y,current_z] = player_path.worldOrientation.to_euler()
  # Change the z coordinate:
  player_path.worldOrientation = [0,0,current_z+SENSITIVITY_PARAMETER *delta]

if player_path['game_on']:
    apply_movement()