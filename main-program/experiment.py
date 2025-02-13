from pathlib import Path
from time import sleep
from configurations import *
import pandas as pd
import subprocess

class Experiment:
    # ============================================
    # setup
    # ============================================
    def __init__(self, exp_name: str, mouse: str, maze: Path, num_laps: int, rewards_data, directory: Path):
        """
        TODO add types
        the basic params
        Args:
            exp_name (str): experiment name
            mouse (str): mouse name
            maze (Path): maze file path
            num_laps (int): laps of the experiment
            rewards_data: amount in lap, places and so on...
            directory (Path): the directory to save the data at the end
        """

        self.exp_name = exp_name
        self.mouse = mouse
        self.maze = maze
        self.num_laps = num_laps
        self.rewards_data = rewards_data
        self.directory = directory

        self.end = False
        self.frames_data = pd.DataFrame(columns = DATA_FRAME_COLUMS) # add dtypes?
        self.frame_num = 0
        self.is_licking = False
        self.lap_number = 1
        self.treadmill_ticks_lap = 0

    def start(self):
        """
        start exp
        """
        self.set_blender()
        self.send_start_signal()

    def set_blender(self):
        """
        load the file and put on screen
        """
        subprocess.run([BLENDER_PATH, "--background", "--python", BLENDER_SCRIPT_PATH]) # TODO check

    def send_start_signal(self):
        """
        send start signal to arduino and maze
        """

    def get_start_signal(self):
        """
        get start signal from arduino and maze and start the program
        """
        self.frame_loop()
    

    # ============================================
    # experiment
    # ============================================
    def handle_signal_from_arduino(self, signal: int):
        if signal in [Signal.MOVE_FORWARD.value, Signal.MOVE_BACKWARDS.value]:
            self.move(signal)
        if signal == Signal.START_LICK.value:
            self.is_licking = True
        if signal == Signal.END_LICK.value:
            self.is_licking = False

    def move(self, move_num: int):
        """
        Args:
            move (int): should be 1 or -1
        """
        """
        When program gets a signal from the arduino, it preomts the blender 
        :param signal: TODO figure out what it should do
        program flow: start premoting the blender. If lap ends - add data of lap and load the next lap (or premote to it?)
        if done - tell that done
        if got to treat - do treat stuff
        learn how to configure next treats (only when lap ends?) so it will be fluent 
        """
        self.sync_ttl += move_num
        self.move_blender(move_num)
        # TODO check if out of maze and lap ends, got to treat and so on...
        # TODO add if lap ends:
        # self.lap_ends()

    def move_blender(self, move_num: int):
        """
        move the blender accourding to the mouse movment

        Args:
            move (int): should be 1 or -1
        """

    def lap_ends(self):
        """
        configure new treats and so on
        """
        if not self.more_laps():
            self.end_exp() # TODO ask
            return
        
        self.treadmill_ticks_lap = 0
        #? wht happens if program finished before mice finished lap?
        self.lap_number += 1
        self.configure_treat()
        # TODO add treats configurations

    def more_laps(self):
        """
        is there more laps?
        """

    def configure_treat(self, params):
        """
        :param params: The treat size. Are there any more parameters?
        does the maze do it automatically?
        """

    def check_if_on_reward():
        """
        returns true if on reward
        TODO what counts on reward?
        """

    def get_maze_location():
        """
        location on maze
        """
        #? if takes to long maybe consider dropping it so it would be on time for 15.49 per sec?

    def add_frame(self):
        self.frame_num += 1
        new_frame = {
            [Frame_Colums.NUM.value]: self.frame_num,
            [Frame_Colums.LICK.value]: self.is_licking,
            [Frame_Colums.REWARD.value]: self.check_if_on_reward(),
            [Frame_Colums.LAP_NUMBER.value]: self.lap_number,
            [Frame_Colums.MAZE_LOCATION.value]: self.get_maze_location(),
            [Frame_Colums.TICKS_PER_LAP.value]: self.treadmill_ticks_lap
        }
        self.frames_data = pd.concat([self.frames_data, new_frame], ignore_index=True)

    def frame_loop(self):
        """
        samples the data repeatedly
        """
        #? is it a good system? how to check time good?
        while self.end == False:
            self.add_frame()
            sleep(1/SAMPLING_RATE)

    # ============================================
    # end
    # ============================================
    def end_exp(self):
        """
        end exp
        """
        self.end = True

    def sync_ttl(ttl_data): 
        """
        talk with yael
        """

    def make_files():
        """
        """

    def close_experiment():
        """
        """

    