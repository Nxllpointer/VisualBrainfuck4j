package de.liquid.vbf4j;

import org.fusesource.jansi.Ansi;

import java.util.Arrays;

public class Printer {

    private final Program program;

    public Printer(Program program) {

        this.program = program;

    }

    public void print() {

        if (!program.visual) return;

        String outputString = "\n".repeat(100);
        outputString += Ansi.ansi().fgBrightBlue().a("VisualBrainfuck4j").reset();
        outputString += "\n\n";
        outputString += renderProgramOutput();
        outputString += "\n";
        outputString += renderMemory();
        outputString += "\n\n";
        outputString += renderCode();

        outputString = outputString.replace("\n", "\n     ");

        System.out.println(outputString);

    }

    private String renderProgramOutput() {


        char[][] programOutputArray = new char[3][50];

        for (char[] chararray : programOutputArray) {
            Arrays.fill(chararray, ' ');
        }

        int currentOutputPos = 0;
        for (char c : program.output) {

            if (currentOutputPos == programOutputArray[0].length && c != '\n') {

                scrollProgramOutputArray(programOutputArray);
                currentOutputPos = 0;

            }

            if (c == '\n') {

                scrollProgramOutputArray(programOutputArray);
                currentOutputPos = 0;

            } else {

                programOutputArray[2][currentOutputPos] = c;

                currentOutputPos++;

            }

        }

        StringBuilder renderedString = new StringBuilder();

        renderedString.append("-OUTPUT").append("-".repeat(programOutputArray[0].length + 2 - 7)).append("\n");

        for (char[] chararray : programOutputArray) {
            renderedString
                    .append("|")
                    .append(Ansi.ansi().fgBrightGreen().a(new String(chararray)).reset())
                    .append("|\n");
        }

        renderedString.append("-".repeat(programOutputArray[0].length + 2)).append("\n");

        return renderedString.toString();

    }

    private void scrollProgramOutputArray(char[][] programOutputArray) {

        programOutputArray[0] = Arrays.copyOf(programOutputArray[1], programOutputArray[1].length);
        programOutputArray[1] = Arrays.copyOf(programOutputArray[2], programOutputArray[2].length);
        Arrays.fill(programOutputArray[2], ' ');

    }

    private String renderMemory() {
        StringBuilder memoryStringSide = new StringBuilder();
        StringBuilder memoryStringMid = new StringBuilder();
        StringBuilder memoryStringSub = new StringBuilder();

        for (int i = program.currentMemoryAddress - 2; i < program.currentMemoryAddress + 3; i++) {

            String cellSide;
            String cellMid;
            String cellSub;

            if (i < 0) {
                cellSide = "-----";
                cellMid = "|   |";
                cellSub = "  -  ";
            } else {

                long memoryData = program.memory.get(i);

                int minSize = 4 + Math.max((String.valueOf(i)).length(), String.valueOf(memoryData).length());

                cellSide = "-".repeat(minSize);
                cellMid = "|" + centerString(String.valueOf(memoryData), minSize - 2) + "|";
                cellSub = centerString(String.valueOf(i), minSize);
            }

            if (i == program.currentMemoryAddress) {
                cellSide = Ansi.ansi().fgBrightYellow().a(cellSide).reset().toString();
                cellMid = Ansi.ansi().fgBrightYellow().a(cellMid).reset().toString();
                cellSub = Ansi.ansi().fgBrightYellow().a(cellSub).reset().toString();
            }

            memoryStringSide.append(cellSide);
            memoryStringMid.append(cellMid);
            memoryStringSub.append(cellSub);

        }

        String memoryStringTop = memoryStringSide.toString();

        memoryStringTop = "-MEMORY" + memoryStringTop.substring(7);

        return memoryStringTop + "\n" + memoryStringMid + "\n" + memoryStringSide + "\n" + memoryStringSub;

    }

    private String centerString(String s, int availableSpace) {

        int spacesNeeded = availableSpace - s.length();
        int spacesBefore = (int) (spacesNeeded / 2D);
        int spacesAfter = spacesNeeded - spacesBefore;

        return " ".repeat(spacesBefore) + s + " ".repeat(spacesAfter);

    }

    private String renderCode() {

        String[] codeOutput = new String[7];
        Arrays.fill(codeOutput, "");

        String[] codeLines = program.code.split("\n");

        int currentLineNumber = getReaderLineNumber();
        String currentLine = codeLines[currentLineNumber];
        int linePos = getReaderLinePos();

        currentLine = currentLine.substring(0, linePos)
                + Ansi.ansi().fgBrightRed().a(currentLine.charAt(linePos)).reset()
                + currentLine.substring(linePos + 1);


        codeLines[currentLineNumber] = currentLine;

        for (int i = 0; i < codeOutput.length; i++) {

            int thisLineNumber = currentLineNumber + i - 3;

            if (thisLineNumber >= 0 && thisLineNumber < codeLines.length) {

                String thisLineNumberDisplay = " ".repeat(String.valueOf(codeLines.length).length() - String.valueOf(thisLineNumber).length()) + thisLineNumber;

                codeOutput[i] = (i == 3 ?
                        Ansi.ansi().fgBrightMagenta().a(thisLineNumberDisplay + " -> ").reset().toString()
                        : thisLineNumberDisplay + "    ");

                codeOutput[i] += codeLines[thisLineNumber] + "\n";


            }

        }

        return "-CODE-------- -- -\n" + String.join("", codeOutput) + "------------- -- -";


    }

    private int getReaderLineNumber() {

        int lineNumber = 0;

        for (int i = 0; i <= program.readerPosition; i++) {

            if (program.code.charAt(i) == '\n') lineNumber++;

        }

        return lineNumber;

    }

    private int getReaderLinePos() {

        int posInLine = 0;
        for (int i = 0; i < program.readerPosition; i++) {

            posInLine++;
            if (program.code.charAt(i) == '\n') {
                posInLine = 0;
            }

        }

        return posInLine;

    }

}
