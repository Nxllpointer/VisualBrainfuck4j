package de.liquid.vbf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Program {

    // Options
    public final String code;
    public final boolean visual;
    public final long clockSpeedMs;
    public final boolean softJumping;
    public final boolean charMode;
    public final int bits;

    public final String validCommands = "+-><[].,";

    public final Printer printer = new Printer(this);

    // Prepare and run execution loop
    public Program(String code, boolean visual, long clockSpeedMs, boolean softJumping, boolean charMode, int bits) {

        this.visual = visual;
        this.clockSpeedMs = clockSpeedMs;
        this.softJumping = softJumping;
        this.charMode = charMode;
        this.bits = bits;

        code = code.replaceAll("\n\n\n+", "\n\n");
        this.code = code;

        for (int i = 0; i < 5; i++) memory.add(0L);

        try {

            executionLoop();

            if (!visual) {

                StringBuilder outputString = new StringBuilder();

                for (char c : output) {
                    outputString.append(c);
                }

                System.out.println("OUTPUT: " + outputString);

            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }


    }

    // More variables
    public boolean running = true;
    boolean jumpingBackwards = false;
    boolean jumpingForwards = false;
    public int readerPosition = -1;
    public List<Long> memory = new ArrayList<>();
    public int currentMemoryAddress = 0;
    public List<Character> output = new ArrayList<>();
    public int loopCounter = 0;


    // Executes code
    private void executionLoop() throws InterruptedException, IOException {

        while (running) {

            if (jumpingForwards) {

                switch (code.charAt(readerPosition)) {
                    case '[':
                        loopCounter++;
                        break;
                    case ']':
                        loopCounter--;
                        break;
                }

                if (loopCounter == 0) {
                    jumpingForwards = false;
                } else {
                    moveReaderForwards();
                }

            } else if (jumpingBackwards) {

                switch (code.charAt(readerPosition)) {
                    case ']':
                        loopCounter++;
                        break;
                    case '[':
                        loopCounter--;
                        break;
                }

                if (loopCounter == 0) {
                    jumpingBackwards = false;
                } else {
                    moveReaderBackwards();
                }

            } else {

                moveReaderForwards();
                if (!running) return;

                printer.print();
                TimeUnit.MILLISECONDS.sleep(clockSpeedMs);

                switch (code.charAt(readerPosition)) {

                    case '+':
                        memory.set(currentMemoryAddress, (long) ((memory.get(currentMemoryAddress) + Math.pow(2, bits) + 1) % Math.pow(2, bits)));
                        break;

                    case '-':
                        memory.set(currentMemoryAddress, (long) ((memory.get(currentMemoryAddress) + Math.pow(2, bits) - 1) % Math.pow(2, bits)));
                        break;

                    case '>':
                        memory.add(0L);
                        currentMemoryAddress++;
                        break;

                    case '<':
                        if (currentMemoryAddress > 0) {
                            currentMemoryAddress--;
                        }
                        break;

                    case '[':
                        if (memory.get(currentMemoryAddress) == 0) {
                            jumpingForwards = true;
                            loopCounter = 0;
                        }
                        break;

                    case ']':
                        if (memory.get(currentMemoryAddress) > 0) {
                            jumpingBackwards = true;
                            loopCounter = 0;
                        }
                        break;

                    case '.':
                        if (charMode) {
                            output.add((char) memory.get(currentMemoryAddress).intValue());
                        } else {
                            char[] chars = String.valueOf(memory.get(currentMemoryAddress)).toCharArray();
                            for (char c : chars) {
                                output.add(c);
                            }
                            output.add(' ');
                        }
                        break;

                    case ',':
                        // Just here to see if all cases fail
                        break;

                }

                if (code.charAt(readerPosition) == ',') {
                    Scanner scanner = new Scanner(System.in);

                    if (charMode) {

                        while (true) {
                            if (!visual) System.out.println(" ");
                            System.out.print("Input (1 Char): ");
                            char[] chars = scanner.nextLine().toCharArray();
                            if (chars.length == 1) {
                                memory.set(currentMemoryAddress, (long) chars[0]);
                                break;
                            }
                            printer.print();
                        }

                    } else {

                        while (true) {
                            if (!visual) System.out.println(" ");
                            System.out.print("Input (Any Number): ");
                            char[] chars = scanner.nextLine().toCharArray();
                            try {
                                memory.set(currentMemoryAddress, Long.parseLong(new String(chars)));
                                break;
                            } catch (NumberFormatException ignored) {
                                printer.print();
                                System.out.print("Invalid Number! ");
                            }
                        }

                    }
                } else {
                    if (visual) System.out.println(" ");
                }

            }

            if (!jumpingForwards && !jumpingBackwards || softJumping) {
                printer.print();
                TimeUnit.MILLISECONDS.sleep(clockSpeedMs);
            }

        }

    }

    private void moveReaderForwards() {

        int posBefore = readerPosition;

        do {
            if (readerPosition == code.length() - 1) break;
            readerPosition++;
        }
        while (!validCommands.contains(
                String.valueOf(code.charAt(readerPosition))
        ));

        if (readerPosition == posBefore || !validCommands.contains(String.valueOf(code.charAt(readerPosition)))) {
            running = false;
        }

    }

    private void moveReaderBackwards() {

        do {
            if (readerPosition == 0) break;
            readerPosition--;
        }
        while (!validCommands.contains(
                String.valueOf(code.charAt(readerPosition))
        ));

    }

}
