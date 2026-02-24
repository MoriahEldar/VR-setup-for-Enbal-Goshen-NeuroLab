# close the connection with the JAVA program (close socket)

# this script is linked to outer_wall
# This script is called when the 'Esc' key is pressed (either automatically or manually). It closes
# the communication with the java program
import bge 
player_path = bge.logic.getCurrentScene().objects['player_path']
player_path['java_socket_obj'].close()
player_path['server_socket_obj'].close()
