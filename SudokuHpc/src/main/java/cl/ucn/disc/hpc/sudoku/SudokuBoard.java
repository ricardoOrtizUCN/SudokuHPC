package cl.ucn.disc.hpc.sudoku;

import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.lang.Byte;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;

class SudokuBoard {

    private byte[] grid;

    private short[] colCandidates;
    private short[] rowCandidates;
    private short[] secCandidates;

    // 0...9 one-hot representation
    private final static short[] bitMasks = { 0, 1, 1 << 1, 1 << 2, 1 << 3, 1 << 4, 1 << 5, 1 << 6, 1 << 7, 1 << 8 };

    SudokuBoard(byte[] grid) { this.grid = grid; }

    public void setSecCandidates(short[] candidates) { secCandidates = candidates; }
    public void setRowCandidates(short[] candidates) { rowCandidates = candidates; }
    public void setColCandidates(short[] candidates) { colCandidates = candidates; }

    public boolean insert(byte val, byte pos) {

        grid[pos] = val;

        // update column and row candidates
        final byte x = (byte) (pos % 9);
        final byte y = (byte) (pos / 9);

        rowCandidates[y] &= ~bitMasks[val];
        colCandidates[x] &= ~bitMasks[val];

        // update section candidates
        final byte sectionX = (byte) (x / 3);
        final byte sectionY = (byte) (y / 3);
        final byte sectionIdx = (byte) (sectionY * 3 + sectionX);

        secCandidates[sectionIdx] &= ~bitMasks[val];

        // if left without candidates then configuration is not valid
        return (rowCandidates[y] != 0 && colCandidates[x] != 0 && secCandidates[sectionIdx] != 0);
    }

    public List<Byte> getCandidates(byte pos) {

        List<Byte> out = new ArrayList<>();

        final byte x = (byte) (pos % 9);
        final byte y = (byte) (pos / 9);

        final byte sectionX = (byte) (x / 3);
        final byte sectionY = (byte) (y / 3);
        final byte sectionIdx = (byte) (sectionY * 3 + sectionX);

        final short candidates = (short) (colCandidates[x] & rowCandidates[y] & secCandidates[sectionIdx]);

        for (byte val=1; val<10; val++) if ((bitMasks[val] & candidates) != 0) out.add(val);

        return out;
    }

    public double completedPercentage() {
        int filled = 0;
        for (byte i=0; i<81; i++) if (grid[i] != 0) filled++;
        return filled / 81.0 * 100;
    }

    public BigInteger solutionsSpace() {
        BigInteger out = new BigInteger("1");
        for (byte pos: getEmptyCells()) out = out.multiply(new BigInteger(String.valueOf(getCandidates(pos).size())));
        return out;
    }

    public List<Byte> getEmptyCells() {
        List<Byte> out = new ArrayList<>();
        for (byte i=0; i<81; i++) if (grid[i] == 0) out.add(i);
        return out;
    }

    public byte getNextCell() {
        return getEmptyCells().stream()
                .sorted(Comparator.comparing(pos -> getCandidates(pos).size()))
                .findFirst()
                .get();
    }

    public static SudokuBoard parseFile(String filePath) throws IOException {

        List<String> input = Files.readAllLines(Paths.get(filePath));

        byte[] outputGrid = new byte[81];

        for (byte y=0; y<9; y++) {
            char[] row = input.get(y).toCharArray();
            for (byte x=0; x<9; x++)
                outputGrid[y * 9 + x] = (row[x] != '.') ? Byte.parseByte(String.valueOf(row[x])) : 0;
        }

        SudokuBoard out = new SudokuBoard(outputGrid);

        // initialize candidates
        short[] colCandidates = new short[9];
        short[] rowCandidates = new short[9];
        short[] secCandidates = new short[9];

        // set masks to not candidates
        for (byte row=0; row<9; row++) {
            for (byte col=0; col<9; col++) {

                byte idx = (byte) (row * 9 + col);

                rowCandidates[row] |= bitMasks[outputGrid[idx]];
                colCandidates[col] |= bitMasks[outputGrid[idx]];
                secCandidates[(row/3) * 3 + (col/3)] |= bitMasks[outputGrid[idx]];
            }
        }

        // complement bit masks
        for (byte i=0; i<9; i++) {
            colCandidates[i] = (short) ~colCandidates[i];
            rowCandidates[i] = (short) ~rowCandidates[i];
            secCandidates[i] = (short) ~secCandidates[i];
        }

        out.setColCandidates(colCandidates);
        out.setRowCandidates(rowCandidates);
        out.setSecCandidates(secCandidates);

        return out;
    }

    public boolean isComplete() { return getEmptyCells().size() == 0; }

    @Override
    public SudokuBoard clone() {

        SudokuBoard out = new SudokuBoard(grid.clone());
        out.setColCandidates(colCandidates.clone());
        out.setRowCandidates(rowCandidates.clone());
        out.setSecCandidates(secCandidates.clone());

        return out;
    }

    @Override
    public String toString() {

        StringBuilder output = new StringBuilder();

        for (byte y=0; y<9; y++) {
            for (byte x=0; x<9; x++) {
                byte n = grid[y * 9 + x];
                output.append((n != 0) ? n : ".");
            }
            if (y != 8) output.append("\n");
        }

        return output.toString();
    }
}

