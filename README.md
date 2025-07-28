# Campus Chaos – A Strategic Board Game (Malefiz-Inspired)

**Campus Chaos** is a digital strategy board game inspired by the classic *Malefiz*. Developed as part of a university project, the game challenges players to move their tokens from a starting point to a goal while strategically blocking opponents and navigating obstacles.

---

## Game Overview

Campus Chaos is a turn-based multiplayer game in which each player tries to move their figure from their unique starting position to a common goal field. Along the way, players can:

- **Knock out opponent figures** (send them back to start)
- **Place and move barriers** to block paths
- **Navigate a dynamically generated game board**

The first player to reach the goal field with one of their figures wins the game.

---

## Game Board Structure

The game board is represented as a grid (`X × Y`) made up of connected fields:

- Fields are connected **horizontally and vertically only** (no diagonal connections).
- Each field can have **up to 4 neighbors** (north, south, east, west).
- There is always exactly **one goal field**.
- Each `2x2` block must contain at least one **non-playable field** to prevent too much clustering.
- Fields must form one connected graph (no isolated cells).
- All **non-special fields** (except Start, Goal, or Forest) must have **at least two connections**.
- The board must include at least as many **free fields** as there are total **player figures**.

---

## Features

- Support for different map sizes and player counts
- Custom game logic for:
  - Movement and valid paths
  - Collision handling and knockout logic
  - Dynamic obstacle placement
- Rule validation to ensure fairness and connectedness of the board

---

## Technologies

- Java
- Object-oriented design
- Modular board and player logic

---

## Author

Loredana Calin  
Bachelor’s student in Computer Science  
Karlsruhe Institute of Technology (KIT)
