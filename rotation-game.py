# Simple pygame program

# Import and initialize the pygame library
import pygame
from collections import namedtuple
import numpy as np
import math

pygame.init()

# Set up the drawing window
screen = pygame.display.set_mode([500, 500])

origin = np.zeros((3,), np.float32)
origin = np.ndarray([0, 0, 0], np.float32)

def degrees_to_radians(degrees):
    return degrees * math.pi / 180

def rotate(point, degrees, axis):
    radians = degrees_to_radians(degrees)
    axis_dict = {
        'x': np.array([[1, 0,                 0                 ],
                         [0, math.cos(radians), -math.sin(radians)],
                         [0, math.sin(radians), math.cos(radians)]],
                         np.float32),
        'y': np.array([[math.cos(radians), 0, -math.sin(radians)],
                         [0,                 1, 0                 ],
                         [math.sin(radians), 0, math.cos(radians)]],
                         np.float32),
        'z': np.array([[math.cos(radians), -math.sin(radians), 0],
                         [math.sin(radians), math.cos(radians),  0],
                         [0,                 0,                  1]],
                         np.float32)
    }
    rotation_matrix = axis_dict[axis]
    return np.dot(point, rotation_matrix)
    
def get_distance(point1, point2):
    return np.abs(np.linalg.norm(point1-point2))

def get_skewed_axis(axis, camera_coords, angle):
    # 0 = x, 1 = y, 2 = z
    if not -90 < angle < 90:
        raise AssertionError("Angle must be between -90 and 90 degrees.")
    theta = degrees_to_radians(angle)
    j = camera_coords[axis]
    i = get_distance(camera_coords, origin)
    phi = math.arcsin(j/i)
    mu = math.pi - phi
    psi = math.pi - mu - theta
    m = i * math.sin(theta) / math.sin(psi)
    return m

def get_slope(camera_coords, orientation_vec):
    skewed_point = np.array([get_skewed_axis(i, camera_coords, orientation_vec[i]) for i in range(3)], np.float32)
    run = get_distance(skewed_point)
    camera_slope = camera_coords / run
    return camera_slope

def get_line_equation(camera_coords, orientation_vec):
    camera_slope = get_slope(camera_coords, orientation_vec)
    def eqn(x):
        return x * camera_slope + camera_coords
    return eqn

def get_f

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