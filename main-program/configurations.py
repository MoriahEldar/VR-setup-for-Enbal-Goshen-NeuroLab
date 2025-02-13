from enum import Enum, IntEnum

SAMPLING_RATE = 15.49 # per_sec

# Path to Blender executable
BLENDER_PATH = "C:/Program Files/Blender Foundation/Blender 3.6/blender.exe"

# Path to your script
BLENDER_SCRIPT_PATH = "C:/path/to/my_script.py"

"""
DATA_FRAME_COLUMS:         
    num_frame # not important
    lick: YES/NO
    reward: YES/NO
    lap_number,
    space in maze 
    ticks (move back and forward)
"""
class Frame_Colums(Enum):
    NUM = 'frame'
    LICK = 'lick'
    REWARD = 'reward'
    LAP_NUMBER = 'lap_number'
    MAZE_LOCATION = 'location_maze'
    TICKS_PER_LAP = 'ticks_from_lap'
    
DATA_FRAME_COLUMS = [col.value for col in Frame_Colums]

class Signal(IntEnum):
    MOVE_FORWARD = 1
    MOVE_BACKWARDS = -1 #possible?
    START_LICK = 3
    END_LICK = 4
