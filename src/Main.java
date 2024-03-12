import edu.salle.url.maze.Maze;
import edu.salle.url.maze.MazeBuilder;
import edu.salle.url.maze.business.enums.Cell;
import edu.salle.url.maze.presentation.MazeRenderer;

public class Main {
    public static void main(String[] args) {
        demoMazeSolver demoMazeSolver = new demoMazeSolver();
        int tamaño = 45;
        Maze maze = new MazeBuilder()
                .setMazeColumns(tamaño)
                .setMazeRows(tamaño)
                .setMazeSolver(demoMazeSolver)
                .buildDungeonMaze();
        maze.run();
        Cell[][] cells = new Cell[tamaño][tamaño];
        demoMazeSolver.solve(cells, (MazeRenderer) demoMazeSolver);
    }
}