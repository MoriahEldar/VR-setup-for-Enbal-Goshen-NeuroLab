import bge 
# this script is linked to sound objects for 1 < i < 15
# I (Ido) changed it to work with the air puffs instead, but
# haven't renamed it. You're welcome to do so!
#! Moriah: not in use with no air puffs

def collision(cont):
    # Loops through all connected sensors and returns if one is False
    # Basically makes it work like an And conroller.
    for sens in cont.sensors:
        if not sens.positive:
            return False
    return True


def apply_air():
    cont = bge.logic.getCurrentController()
    own = cont.owner
    
    # check if station was properly activated
    if collision(cont):
        start_obj = bge.logic.getCurrentScene().objects['start']
        print("At air station number \t %s \n already passed \t %s puffs" %(str(own['id']), str(start_obj['air_pass'])))
        
        # check if this tone is supposed to be activated now
        if own['id'] == start_obj['air_pass']:
            start_obj['air_pass'] += 1
            player_path = bge.logic.getCurrentScene().objects['player_path']
            # call on serial with option 2
            player_path['serial_obj'].write(b'2')
            

apply_air()