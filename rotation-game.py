# Simple pygame program

# Import and initialize the pygame library
import pygame
from collections import namedtuple
import numpy as np
import math

pygame.init()

# Set up the drawing window
screen = pygame.display.set_mode([500, 500])

origin = np.ndarray([0, 0, 0], np.int32)

def degrees_to_radians(degrees):
    return degrees * math.pi / 180

def rotate(point, degrees, axis):
    radians = degrees_to_radians(degrees)
    axis_dict = {
        'x': np.ndarray([[1, 0,                 0                 ],
                         [0, math.cos(radians), -math.sin(radians)],
                         [0, math.sin(radians), math.cos(radians)]],
                         np.int32),
        'y': np.ndarray([[math.cos(radians), 0, -math.sin(radians)],
                         [0,                 1, 0                 ],
                         [math.sin(radians), 0, math.cos(radians)]],
                         np.int32),
        'z': np.ndarray([[math.cos(radians), -math.sin(radians), 0],
                         [math.sin(radians), math.cos(radians),  0],
                         [0,                 0,                  1]],
                         np.int32)
    }
    rotation_matrix = axis_dict[axis]
    return np.dot(point, rotation_matrix)
    
def get_distance(point1, point2):
    return float(abs(np.linalg.norm(point1-point2)))

def get_skewed_point(axis, camera_coords, angle):
    if not -90 < angle < 90:
        raise AssertionError("Angle must be between -90 and 90 degrees.")
    theta = degrees_to_radians(angle)
    axis_dict = {'x': 0, 'y': 1, 'z': 2}
    axis_index = axis_dict[axis]
    D = np.zeros((3))
    j = camera_coords[axis_index]
    D[axis_index] = j
    i = get_distance(camera_coords, origin)
    phi = math.arcsin(j/i)
    mu = 180 - phi
    psi = 180 - mu - theta
    m = i * math.sin(theta) / math.sin(psi)
    

def make_limit_funcs(camera_orientation, camera_coords, horz_angle, vert_angle):
    x, y, z = camera_coords
    run = get_distance(camera_coords, origin)
    camera_slope = camera_coords / run

# Run until the user asks to quit
running = True
while running:

    # Did the user click the window close button?
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False

    # Fill the background with white
    screen.fill((255, 255, 255))

    # Draw a solid blue circle in the center
    pygame.draw.circle(screen, (0, 0, 0), (250, 250), 3)

    # Flip the display
    pygame.display.flip()

# Done! Time to quit.
pygame.quit()