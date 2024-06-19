package skySky;

import java.awt.Color;
import java.util.*;

public class Tmp {
    static Color c;
    static Color newColor;
    static boolean zeroPadded = true;
    static Color[] meow = new Color[6];
    static Color[] meow2 = new Color[6];

    public static void main(String[] args) {
        float[] hsb = Color.RGBtoHSB(0x0, 0xff, 0xff, null);
        System.out.printf("HSB: %3f°, %3f%%, %3f%%%n", hsb[0] * 360, hsb[1] * 100, hsb[2] * 100);
        float[] newHsb = { (hsb[0] * 360f - 19f) / 360, hsb[1] - 0.49f, hsb[2] };
        System.out.printf("HSB: %3f°, % 3f%%, %3f%%%n", newHsb[0] * 360, newHsb[1] * 100, newHsb[2] * 100);
        Color newColor = Color.getHSBColor(newHsb[0], newHsb[1], newHsb[2]);
        System.out.println(Utils.formatHex(true, 6, newColor.getRGB() & 0x00ffffff));
    }

    static String formatthing(int n) {
        String s = String.format("%8s", Integer.toBinaryString(n)).replaceAll(" ", "0");
        s = s.substring(s.length() - 8);
        return s.substring(0, 4) + " " + s.substring(4, 8);
    }

    static void colorthing() {

        String[] colStrings = {

                "00FF00",
                "FF0000",
                "FFFF00",
                "0089FF",
                "FFB300",
                "BA00FF",
        }, colAnimStrings = {

                "00FF00",
                "FF8B8B",
                "FFFF57",
                "77D1FF",
                "FFBE28",
                "FFFFFF",
        };
        for (int i = 0; i < 6; i++) {
            String a = colStrings[i], b = colAnimStrings[i];
            int aInt = Integer.parseInt(a, 16), bInt = Integer.parseInt(b, 16);
            meow[i] = new Color(aInt);
            int addThis = 0x08b;
            meow2[i] = new Color(Math.min(255, meow[i].getRed() + addThis), Math.min(255, meow[i].getGreen() + addThis),
                    Math.min(255, meow[i].getBlue() + addThis));
            System.out.printf("%6s - %6s = %06X\n", a, b, Math.abs(aInt - bInt));
        }
        uithing();
    }

    static void uithing() {
        javax.swing.JFrame frame = new javax.swing.JFrame();
        frame.setBackground(Color.BLACK);
        javax.swing.JPanel mainPanel = new javax.swing.JPanel(new java.awt.GridLayout(meow.length, 2));
        for (int i = 0; i < meow.length; i++) {
            javax.swing.JPanel p1 = new javax.swing.JPanel();
            p1.setBackground(meow[i]);
            mainPanel.add(p1);
            javax.swing.JPanel p2 = new javax.swing.JPanel();
            p2.setBackground(meow2[i]);
            mainPanel.add(p2);
        }
        frame.add(mainPanel);
        frame.setBounds(1920 / 4, 1080 / 4, 1920 / 2, 1080 / 2);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException iex) {
            iex.printStackTrace();
        }
        // System.exit(0);
    }
}
