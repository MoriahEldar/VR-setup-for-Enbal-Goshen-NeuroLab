# this script is linked to the player_path object
# The script is in charge of resetting the mouse's location whenever the 'c' key is pressed. It is triggered
# automatically when the IR sensor recognizes the starting line on the tape

import bge 
ply = bge.logic.getCurrentScene().objects['player_path']
print("################### calibrating ")
ply.worldOrientation = [0, 0, 0]

# If the C keyboard is pressed change the value of ply['IR'].
# In the beginning of press it turns from False to True, and in the end from True to False.
# Only in the beginning (from F to T) add 1 to the lap counter:

last_IR_value = ply['IR'] # todo understand what IR is
ply['IR']=not(ply['IR']) 
if last_IR_value == False and ply['IR'] == True: 
    # TODO send another (lap) to java system
    #? where is the automatic code that changes the IR?