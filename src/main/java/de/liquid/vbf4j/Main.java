package de.liquid.vbf4j;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    private static final Options options = new Options();
    private static final HelpFormatter helpFormatter = new HelpFormatter();

    public static void main(String[] args) {

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;


        helpFormatter.setOptPrefix("-");
        helpFormatter.setLongOptPrefix(" -");

        options.addOption(
                Option.builder()
                        .option("h")
                        .longOpt("help")
                        .desc("Shows this help message")
                        .build()
        );

        options.addOption(
                Option.builder()
                        .option("i")
                        .longOpt("input")
                        .numberOfArgs(1)
                        .argName("file")
                        .desc("Path to your code")
                        .required()
                        .build()
        );

        options.addOption(
                Option.builder()
                        .option("nv")
                        .longOpt("notvisual")
                        .desc("Run the interpreter without live output/memory/code display. Output will be printed after finishing")
                        .build()
        );

        options.addOption(
                Option.builder()
                        .option("cs")
                        .longOpt("clockspeed")
                        .numberOfArgs(1)
                        .argName("milliseconds")
                        .desc("How long the interpreter should wait between actions in milliseconds (100ms default)")
                        .build()
        );

        options.addOption(
                Option.builder()
                        .option("sj")
                        .longOpt("softjumping")
                        .desc("Makes the interpreter walk back to the start of a loop instead of jumping")
                        .build()
        );

        options.addOption(
                Option.builder()
                        .option("cm")
                        .longOpt("charmode")
                        .desc("Switches to char mode. Input and output will be the corresponding numbers to the charachters and in reverse." +
                                " Default is number mode which means input and output are only numbers")
                        .build()
        );

        options.addOption(
                Option.builder()
                        .option("b")
                        .longOpt("bits")
                        .numberOfArgs(1)
                        .argName("bits")
                        .desc("How many bits each memory cell should have (default 8)")
                        .build()
        );


        try {


            try {

                args = new String(Files.readAllBytes(Paths.get("vbf4j.args")))
                        .replace("\n", " ")
                        .replace("\r", " ")
                        .replaceAll("\\s{2,}", " ")
                        .trim()
                        .split(" ");

            } catch (IOException ignored) {
            }


            cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                printHelp("Help: ", " ");
                return;
            }

            boolean visual = !cmd.hasOption("nv");
            boolean softJumping = cmd.hasOption("sj");
            boolean charMode = cmd.hasOption("cm");

            long clockSpeedMs = 100;
            if (cmd.hasOption("cs")) {
                try {
                    clockSpeedMs = Long.parseLong(cmd.getOptionValue("cs"));
                } catch (NumberFormatException e) {
                    throw new ParseException("Invalid clock speed");
                }
            }

            int bits = 8;
            if (cmd.hasOption("b")) {
                try {
                    bits = Integer.parseInt(cmd.getOptionValue("b"));
                } catch (NumberFormatException e) {
                    throw new ParseException("Invalid bits amount");
                }
            }

            if (cmd.hasOption("i")) {
                try {

                    String code = new String(Files.readAllBytes(Paths.get(cmd.getOptionValue("i"))));

                    new Program(code, visual, clockSpeedMs, softJumping, charMode, bits);

                } catch (IOException e) {
                    throw new ParseException("Could not load input file (wrong path?)");
                }
            }


        } catch (ParseException e) {

            printHelp("Error: ", e.getMessage());

        }

    }

    private static void printHelp(String syntaxPrefix, String message) {

        helpFormatter.setSyntaxPrefix(syntaxPrefix);
        helpFormatter.printHelp(message, options);

        System.out.println("\nIf you dont want to type your args every time\n" +
                "create a file named vbf4j.args in the directory you\n" +
                "execute this command from and paste your args in there.\n" +
                "Command line arguments will be ignored.");

    }

}
