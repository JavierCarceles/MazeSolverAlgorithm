import edu.salle.url.maze.business.MazeSolver;
import edu.salle.url.maze.business.enums.Cell;
import edu.salle.url.maze.business.enums.Direction;
import edu.salle.url.maze.presentation.MazeRenderer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class demoMazeSolver implements MazeSolver {
        int[] config = new int[100000];
        ArrayList<Direction> directions = new ArrayList<>();
        ArrayList<Direction> solucion = new ArrayList<>();
        ArrayList<int[]> jaComprobats = new ArrayList<>();
        int[] coordenadasClone = null;
        int min = Integer.MAX_VALUE;
        public int[] coordSalida;
        public int[] coordenadas;

        public boolean ComprobarMillorSolucio(ArrayList<Direction> directions){
            return directions.size()<min;
        }

        @Override
        public List<Direction> solve(Cell[][] cells, MazeRenderer mazeRenderer) {
            coordenadas = encontrarSTART(cells);
            coordSalida = encontrarSalida(cells);
            coordenadasClone = coordenadas.clone();
            jaComprobats.add(coordenadasClone);
            mazeRenderer.render(cells, directions, 5);
            backtracking(cells,0, coordenadas, mazeRenderer);
            return solucion;
        }

    private int[] encontrarSalida(Cell[][] cells) {
        int[] nums = new int[2];
        for (int i = 0; i < cells[0].length; i++) {
            for (int j = 0; j < cells.length; j++) {
                if (cells[i][j] == Cell.EXIT){
                    nums [0] = i;
                    nums [1] = j;
                    return nums;
                }
            }
        }
        return nums;
    }

    public void backtracking(Cell[][] cells, int nivell, int[] coordenadas, MazeRenderer mazeRenderer) {

                for (int opcio = 1; opcio <= 4; opcio++) {

                    //Elige el bloque en el que se mirarán las direcciones
                    config[nivell] = opcio;

                    //cambia las coordenadas a la opcion que toca, si tocase mirar arriba recoge las coordenadas de arriba y las marca como visitadas.
                    int[] coordenadasBackTracking = actualizarCoordenadas(coordenadas.clone(), opcio);

                    coordenadasClone = coordenadasBackTracking.clone();

                    directions.add(getDirection(opcio));

                    if (comprobaSeguentPosicio(cells, coordenadasClone)) {
                        //mazeRenderer.render(cells, directions, 5);

                        if (!comprobarVisitats(coordenadasClone) && ComprobarMillorSolucio(directions)) {
                            jaComprobats.add(coordenadasClone);
                            //mazeRenderer.render(cells, directions, 5);
                            backtracking(cells, nivell + 1, coordenadasClone.clone(), mazeRenderer);
                        }
                        if ((cells[coordenadasClone[0]][coordenadasClone[1]] == Cell.EXIT)) {
                            if (ComprobarMillorSolucio(directions)) {
                                solucion = (ArrayList<Direction>) directions.clone();
                                mazeRenderer.render(cells, solucion, 1000);
                                min = solucion.size();
                            }
                        }

                        directions.removeLast();
                    } else {
                        if (!comprobarVisitats(coordenadasClone)) {
                            jaComprobats.add(coordenadasBackTracking);
                        }
                        coordenadas = getCoordenadasAnteriores(coordenadasClone, getDirection(opcio));
                        directions.removeLast();
                    }
                }
            eliminarCoordenadas(jaComprobats, coordenadas, cells);
            //mazeRenderer.render(cells, directions, 4);
        }

        private void eliminarCoordenadas(ArrayList<int[]> jaComprobats, int[] coordenadasClone, Cell[][] cells) {
            //Desmarca la coordenada indicada como visitada para poder redirigirla hacia otras direcciones
            for (int i = 0; i < jaComprobats.size(); i++) {
                if (jaComprobats.get(i)[0] == coordenadasClone[0] && jaComprobats.get(i)[1] == coordenadasClone[1]){
                    if(cells[coordenadasClone[0]][coordenadasClone[1]] == Cell.EMPTY) {
                        jaComprobats.remove(i);
                        return;
                    }
                }
            }
        }

        private boolean comprobarVisitats(int[] coordenadas) {
            //Comprueba que la coordenada pasada por parametro no ha estado visitada ya antes
            for (int[] coordenadasGuardadas : jaComprobats) {
                if (Arrays.equals(coordenadasGuardadas, coordenadas)) {
                    return true;
                }
            }
            return false;
        }

        private int[] actualizarCoordenadas(int[] coordenadas, int opcion) {
            //dependiendo de la direccion que toca, avanza la casilla en esa direccion
            switch (opcion){
                case 1:
                    coordenadas[0] = coordenadas[0]-1;
                    return coordenadas;
                case 2:
                    coordenadas[1] = coordenadas[1]+1;
                    return coordenadas;
                case 3:
                    coordenadas[0] = coordenadas[0]+1;
                    return coordenadas;
                case 4:
                    coordenadas[1] = coordenadas[1]-1;
                    return coordenadas;
            }
            return null;
        }

        public boolean comprobaSeguentPosicio(Cell[][] cells, int[] coordenadas) {
            //Comprueba que la coordenada es vacia, salida, muro o punto de partida
            switch (cells[coordenadas[0]][coordenadas[1]]) {
                case EMPTY, EXIT -> {
                    return true;
                }
                case WALL, START -> {
                    return false;
                }
            }
            return false;
        }

        private int[] getCoordenadasAnteriores(int[] coordenadas, Direction direction) {
            //Devuelve las coordenadas antes de aplicar la direccion que se pasa por parametro
            switch (direction) {
                case UP:
                    //Si el movimiento anterior ha sido UP suma 1 a la fila para volver a la coordenada anterior
                    coordenadas[0]++;
                    break;
                case DOWN:
                    coordenadas[0]--;
                    break;
                case LEFT:
                    coordenadas[1]++;
                    break;
                case RIGHT:
                    coordenadas[1]--;
                    break;
            }
            return coordenadas;
        }

    private Direction getDirection(int nivell) {
        int initialRow = coordenadas[0];
        int initialCol = coordenadas[1];
        int exitRow = coordSalida[0];
        int exitCol = coordSalida[1];

        // Verificar la posición de la casilla inicial y de salida
        if (initialRow < exitRow && initialCol < exitCol) {
            // Casilla inicial arriba a la izquierda y casilla de salida abajo a la derecha
            switch (nivell) {
                case 1:
                    return Direction.RIGHT;
                case 2:
                    return Direction.DOWN;
                case 3:
                    return Direction.LEFT;
                case 4:
                    return Direction.UP;
            }
        } else if (initialRow < exitRow && initialCol > exitCol) {
            // Casilla inicial arriba a la derecha y casilla de salida abajo a la izquierda
            switch (nivell) {
                case 1:
                    return Direction.DOWN;
                case 2:
                    return Direction.LEFT;
                case 3:
                    return Direction.UP;
                case 4:
                    return Direction.RIGHT;
            }
        } else if (initialRow > exitRow && initialCol < exitCol) {
            // Casilla inicial abajo a la izquierda y casilla de salida arriba a la derecha
            switch (nivell) {
                case 1:
                    return Direction.UP;
                case 2:
                    return Direction.RIGHT;
                case 3:
                    return Direction.DOWN;
                case 4:
                    return Direction.LEFT;
            }
        } else if (initialRow > exitRow && initialCol > exitCol) {
            // Casilla inicial abajo a la derecha y casilla de salida arriba a la izquierda
            switch (nivell) {
                case 1:
                    return Direction.LEFT;
                case 2:
                    return Direction.UP;
                case 3:
                    return Direction.RIGHT;
                case 4:
                    return Direction.DOWN;
            }
        }
        return null;
    }

        private int[] encontrarSTART(Cell[][] cells) {
            //Encuentra la casilla de salida para empezar a iterar con el algoritmo
            int[] nums = new int[2];
            for (int i = 0; i < cells[0].length; i++) {
                for (int j = 0; j < cells.length; j++) {
                    if (cells[i][j] == Cell.START){
                        nums [0] = i;
                        nums [1] = j;
                        return nums;
                    }
                }
            }
            return nums;
        }

}