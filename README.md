Maze Generator and Solver
========================

A simple Java applet that generates and solves grid mazes.

---

How to use?


Click the GENERATE button to generate a new maze with preset start tile (yellow) and end tile (green).
Next, click SOLVE and the program will display the shortest path (blue tiles) between the start and end tiles.
You can change the start tile with right click and the end tile with middle click on the desired tile.
You can also create your own mazes. Simply left click on any tile and it will change from wall (red) to room (gray).
You can change multiple tiles by dragging the mouse with the left button pressed.
 
---

Behind the scenes


The maze generation algorithm
 1. All tiles are walls, except for start and end
 2. A wall tile chosen at random is made a room
 3. Check if start and end are connected (if there is a way to get from start to end)
    * if not go to 2 and repeat
    * else generation finished
    
The solver uses simple [breadth first search (BFS)](http://en.wikipedia.org/wiki/Breadth-first_search) to find the shortest path.


The maze generation can sometimes lead to very sparse mazes. A much [better algorithm](https://github.com/gajduk/text-maze-generation) for maze generation is based on [spanning trees](http://en.wikipedia.org/wiki/Spanning_tree) and a variant of the [Kruskal's algorithm](http://en.wikipedia.org/wiki/Kruskal%27s_algorithm).


---


Some sample images from the demo:


![alt tag](https://raw.githubusercontent.com/gajduk/maze-generator-and-solver-applet/master/maze1.PNG)


![alt tag](https://raw.githubusercontent.com/gajduk/maze-generator-and-solver-applet/master/maze2.PNG)


![alt tag](https://raw.githubusercontent.com/gajduk/maze-generator-and-solver-applet/master/maze3.PNG)


![alt tag](https://raw.githubusercontent.com/gajduk/maze-generator-and-solver-applet/master/maze4.PNG)
