package cl.ucn.disc.hpc.sudoku;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

class ParallelSudoku implements SudokuAnalyser {

    private SudokuBoard board;

    ParallelSudoku(SudokuBoard board) { this.board = board; }

    @Override
    public int computeSolutionsNumber() { return new ForkJoinPool().invoke(new RecursiveSolver(board)); }

    class RecursiveSolver extends RecursiveTask<Integer> {

        SudokuBoard board;

        RecursiveSolver(SudokuBoard board) { this.board = board; }

        @Override
        protected Integer compute() {

            // sequential cutoff
            if (board.completedPercentage() > 80.0)
                return new SequentialSudoku(board).computeSolutionsNumber();

            List<RecursiveSolver> tasks = new ArrayList<>();

            // get empty cell with less candidates
            byte cell = board.getNextCell();

            // recursively try every candidate for that cell
            for (byte val: board.getCandidates(cell)) {

                SudokuBoard newBoard = board.clone();

                // if new configuration is valid
                if (newBoard.insert(val, cell)) tasks.add(new RecursiveSolver(newBoard));
            }

            if (tasks.size() == 0) return 0;

            // keep one task to be run by this
            RecursiveSolver onThis = tasks.remove(0);
            // fork other tasks
            tasks.forEach(t -> t.fork());

            // compute this
            int out = onThis.compute();

            // sum solutions from other tasks
            return tasks.stream()
                    .map(t -> t.join())
                    .reduce(out, Integer::sum);
        }
    }

}
