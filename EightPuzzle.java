package eightpuzzle;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

public class EightPuzzle {

    // Goal state for puzzle
    static final byte [] goalTiles = {2,3,4,1,8,5,7,6,0};

    //Priority queue for A*
    final PriorityQueue <State> queue = new PriorityQueue<State>(100, new Comparator<State>() {
        public int compare(State a, State b) { 
            return a.priority() - b.priority();
        }
    });

    //Closed state set
    final HashSet <State> closed = new HashSet <State>();

    //Keeps track of the state of the puzzle
    class State {
        final byte [] tiles;    // ordered left-> right, top->bottom
        final int blankIndex;   // Index of the blank (0) in the puzzle
        final int g;            // g = number of moves from start
        final int h;            // heuristic value = distance from goal
        final State prevState;       // last visited state in chain

        // f(n) = g+h function
        int priority() {
            return g + h;
        }

        // build an initial state with the inital array values passed in
        State(byte [] initial) {
            tiles = initial;
            blankIndex = index(tiles, 0);
            g = 0;
            h = heuristic(tiles);
            prevState = null;
        }

        // build the next state by sliding the blank in a direction (S,N,W,E)
        State(State prevState, int slideFromIndex) {
            tiles = Arrays.copyOf(prevState.tiles, prevState.tiles.length);
            tiles[prevState.blankIndex] = tiles[slideFromIndex];
            tiles[slideFromIndex] = 0;
            blankIndex = slideFromIndex;
            g = prevState.g + 1;
            h = heuristic(tiles);
            this.prevState = prevState;
        }

        // check if we are at goal state
        boolean checkGoal() {
            return Arrays.equals(tiles, goalTiles);
        }

        // Result of creating states due to south, north, west, and east moves.
        State moveSouth() { return blankIndex > 2 ? new State(this, blankIndex - 3) : null; }       
        State moveNorth() { return blankIndex < 6 ? new State(this, blankIndex + 3) : null; }       
        State moveEast() { return blankIndex % 3 > 0 ? new State(this, blankIndex - 1) : null; }       
        State moveWest() { return blankIndex % 3 < 2 ? new State(this, blankIndex + 1) : null; }

        // Print this state
        void print() {
            System.out.println("p = " + priority() + " = g+h = " + g + "+" + h);
            for (int i = 0; i < 9; i += 3)
                System.out.println(tiles[i] + " " + tiles[i+1] + " " + tiles[i+2]);
        }

        // Print the chain of solutions generated
        void printAll() {
            if (prevState != null) prevState.printAll();
            System.out.println();
            print();
        }

		 
        public boolean equals(Object obj) {
            if (obj instanceof State) {
                State other = (State)obj;
                return Arrays.equals(tiles, other.tiles);
            }
            return false;
        }

        public int hashCode() {
            return Arrays.hashCode(tiles);
        }
    }

    // adds a successor node to the A* queue
    void addNext(State successor) {
        if (successor != null && !closed.contains(successor)) 
            queue.add(successor);
    }

    // solves the puzzle
    void solve(byte [] initial) {

        queue.clear();
        closed.clear();

        // Add initial state to queue
        queue.add(new State(initial));

        while (!queue.isEmpty()) {

            // Get the lowest priority state
            State state = queue.poll();

            // if goal node = done, if not, add a successor and generate children
            if (state.checkGoal()) {
                state.printAll();
                return;
            }

            // close off current state
            closed.add(state);

            // Add successors to the queue.
            addNext(state.moveSouth());
            addNext(state.moveNorth());
            addNext(state.moveWest());
            addNext(state.moveEast());
        }
    }

    // Return index of value in given byte array, -1 if none found.
    static int index(byte [] a, int value) {
        for (int i = 0; i < a.length; i++)
            if (a[i] == value) 
				return i;
        return -1;
    }

    // return manhattan distances between indicies a/b
    static int manhattanD(int a, int b) {
        return Math.abs(a / 3 - b / 3) + Math.abs(a % 3 - b % 3);
    }

    //h(2)
    static int heuristic(byte [] tiles) {
        int h = 0;
        for (int i = 0; i < tiles.length; i++)
            if (tiles[i] != 0)
                h += manhattanD(i, tiles[i]);
        return h;
    }

	 //main program entry point
    public static void main(String[] args) {

        //initial state of tiles
        byte [] initial = { 2, 8, 3, 1, 6, 4, 7, 0, 5 };
		  //new instance of EightPuzzle, passing in the initial state of the tiles
        new EightPuzzle().solve(initial);
    }
}
