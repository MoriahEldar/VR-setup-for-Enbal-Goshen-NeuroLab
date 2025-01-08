'''
struct or class that saves the data in a list (use py)
should have: 
{
    time,
    lick: YES/NO
    reward: YES/NO
    lap_number,
    space in maze?
}
'''
import pandas as pd

def move_blender(signal):
    # TODO: move the blender accourding to the signal
    '''
    When program gets a signal from the arduino, it preomts the blender 
    :param signal: TODO figure out what it should do
    program flow: start premoting the blender. If lap ends - add data of lap and load the next lap (or premote to it?)
    if done - tell that done
    if got to treat - do treat stuff
    learn how to configure next treats (only when lap ends?) so it will be fluent 
    '''

def lap_ends():
    '''
    configure new treats and so on
    '''

def configure_treat(params):
    '''
    :param params: The treat size. Are there any more parameters?
    '''

def if_licked():
    '''
    get signal and write in frame.
    '''

def frames():