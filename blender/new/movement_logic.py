import json
import bge 
import serial
import time

#! check this file!!!
# This script is linked to the player_path object
# This script is in charge of moving the player in the game according to the encoder information send from the java program

SENSITIVITY_PARAMETER = 0.00097

  
player_path = bge.logic.getCurrentScene().objects['player_path']    


# Update the location and IR information in the data table 
# player_path['game_counter']+=1 
# col = player_path['ROTATE_ENCODER_DATA'] 
# row = player_path['game_counter']
# player_path['output_data'][col][row] = player_path['last_position']
# player_path['output_data'][col][row] = player_path['last_position']


def apply_movement(): 
  # This function reads the encoder-arduino information and move
  # the player in the game according to it:
  
  java_code_read = player_path['java_socket_obj'].recv(1024).decode("utf-8") # This is a reads from the java program
  # should be -1/1
  if java_code_read and java_code_read in ['1', '-1']:
    # new_position = get_read_number(encoder_read)
    new_position = int(java_code_read)
    # If the encoder rotates, move the player: 
    # if abs(new_position - player_path['last_position'])<1000:  # This condition prevents errors when the new position is very far from the last position
    move_player(new_position)

    # Update the 'backwards movment' value if necessary 
    # (counts the backwards movments of the mouse in order to keep him from getting things if he went backwards for them)
    if player_path['backwards_movment'] > 0:
      if new_position < 0:
        player_path['backwards_movment'] = new_position
    elif player_path['backwards_movment'] <= 0:
      player_path['backwards_movment'] += new_position
      # on a positive count (the count doesn't matter, just that it's positive)
      if player_path['backwards_movment'] > 0:
        player_path['backwards_movment'] = 1

    sendDataToJava(new_position) # will do it on time? check after


# def get_read_number(read):
#   # This function gets the read from the encoder-arduino 
#   # as a binary type that looks like: b'-7773\r\n'
#   # It turns it into a string and takes the numbers (and the minus if exists), and returns it as a number:
#   number = str(read)[2:-5]
#   number = int(number)
#   if player_path['encoder_calib'] == 0:  # In the begining of the game set the 'encoder_calib' value as the first read of the encoder
#       player_path['encoder_calib'] = number
#   return number - player_path['encoder_calib']

  
def move_player(new_position):
  # This function gets the new position and moves the player in the game:
  
  #delta = player_path['last_position'] - new_position
  delta = new_position #? check if makes sense 
  # Find out the current coordinates:
  [_,_,current_z] = player_path.worldOrientation.to_euler()
  # Change the z coordinate:
  player_path.worldOrientation = [0,0,current_z+SENSITIVITY_PARAMETER *delta]


def sendDataToJava(new_position):
  # This function sends parameters to the java program:
  # like: lap number, if got reward, etc.
  #? will it send the right parameters? before or after the updating laps and so on? maybe I need a sleep or something
  #! check that the data is sent after the recalculation of the treat and lap number!!
  # Send the parameters to the java program:

  #? add the time here? or in the java program, before sending?
  #? add maze location?
  data_to_send = {
        "laps": player_path['laps_counter'],
        "reward": player_path['reward_collision'],
        "location": player_path.worldOrientation
    }
  json_data = json.dumps(data_to_send)
  player_path['reward_collision'] = False
  player_path['java_socket_obj'].send(json_data.encode("utf-8"))
  return

if player_path['game_on']:
    apply_movement()