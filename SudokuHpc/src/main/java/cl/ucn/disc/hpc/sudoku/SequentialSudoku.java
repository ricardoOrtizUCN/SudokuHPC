package cl.ucn.disc.hpc.sudoku;

class SequentialSudoku implements SudokuAnalyser {

    private SudokuBoard board;

    SequentialSudoku(SudokuBoard board) { this.board = board; }

    @Override
    public int computeSolutionsNumber() {

        if (board.isComplete()) {
            System.out.println(board);
            return 1;
        }

        int solutions = 0;

        // get empty cell with less candidates
        byte cell = board.getNextCell();

        // recursively try every candidate for that cell
        for (byte val: board.getCandidates(cell)) {

            SudokuBoard newBoard = board.clone();

            if (newBoard.insert(val, cell))
                solutions += new SequentialSudoku(newBoard).computeSolutionsNumber();
        }

        return solutions;
    }

}