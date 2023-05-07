package org.ssd.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private static final Scanner sc = new Scanner(System.in);
    private final String title;
    private final List<String> options;
    private final List<MenuHandler> handlers;

    public Menu(String title, String[] options) {
        this.title = title;
        this.options = Arrays.asList(options);
        this.handlers = new ArrayList<>();
    }

    public static void clear() {
        final String ANSI_CLS = "\u001b[2J";
        final String ANSI_HOME = "\u001b[H";
        System.out.print(ANSI_CLS + ANSI_HOME);
    }


    public void setHandler(int option, MenuHandler handler) {
        this.handlers.add(option - 1, handler);
    }

    private void show() {
        System.out.println(this.title + '\n');
        for (int i = 0; i < this.options.size(); i++) {
            System.out.print(i + 1);
            System.out.print(" - ");
            System.out.println(this.options.get(i));
        }
        System.out.println("0 - Exit\n");
    }

    private int readOption() {
        int opt;
        System.out.print("Option: ");
        try {
            String line = sc.nextLine();
            opt = Integer.parseInt(line);
        } catch (Exception e) {
            opt = -1;
        }

        return opt;
    }

    public boolean run() {
        show();
        int opt = readOption();
        if (opt == 0) {
            System.exit(0);
        } else if (opt < 0 || opt > this.options.size()) {
            System.out.println("Invalid Option");
            return true;
        } else {
            try {
                this.handlers.get(opt - 1).execute();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
     * Functional interface for handlers
     */
    public interface MenuHandler {
        void execute() throws Exception;
    }
}
