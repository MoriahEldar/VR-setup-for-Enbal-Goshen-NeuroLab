from pathlib import Path

class Experiment:
    def __init__(self, exp_name: str, mouse: str, maze: Path, num_laps: int, rewards_data, directory: Path):
        '''
        TODO add types
        the basic params
        :param exp_name: experiment name
        :param mouse: mouse name
        :param maze: maze file path
        :param num_laps: laps of the experiment
        :param rewards_data: amount in lap, places and so on...
        :param directory: the directory to save the data
        '''
        self.exp_name = exp_name
        self.mouse = mouse
        self.maze = maze
        self.set_blender(maze)
        self.num_laps = num_laps
        self.rewards_data = rewards_data
        self.directory = directory

    def set_blender(self, file):
        '''
        load the file and put on screen
        :param file: maze blender script
        '''
    
    