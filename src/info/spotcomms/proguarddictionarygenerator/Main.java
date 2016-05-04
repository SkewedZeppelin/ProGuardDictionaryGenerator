package info.spotcomms.proguarddictionarygenerator;

import java.io.File;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.Random;

public class Main {

  public static void main(String[] args) {
    System.out.println("Control Code Test: " + check('\u0000'));
    System.out.println("Example String: " + csRandomAlphaNumericString(256));
    createDatabase("ObfuscationDictionary.txt", 2500, 20000);
    createDatabase("ClassObfuscationDictionary.txt", 5000, 250);
    createDatabase("PackageObfuscationDictionary.txt", 5000, 250);
  }

  private static void createDatabase(String name, int amount, int lineLength) {
    try {
      System.out.println("Starting generation of " + name);
      PrintWriter writer = new PrintWriter(new File(name));
      for (int x = 0; x < amount; x++) {
        writer.println(csRandomAlphaNumericString(lineLength));
        if (x % 50 == 0) {
          VALID_CHARACTERS = generateRandomArray();
        }
        if(x % 250 == 0) {
          System.out.println("\tCompletion: " + x + "/" + amount);
        }
      }
      writer.close();
      System.out.println("Generation of " + name + " completed");
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  private static boolean check(char c) {
    if (Character.isLetter(c) || Character.isJavaIdentifierStart(c)) {
      return true;
    }
    return false;
  }

  private static char[] generateRandomArray() {
    char[] out = new char[65536];
    Random rand = new Random();
    for (int x = 0; x < out.length; x++) {
      rand.setSeed(new SecureRandom().nextLong() * System.nanoTime());
      char tmp = (char) Integer.parseInt(("\\u" + Integer.toHexString(rand.nextInt(0x10FFFF))).substring(2), 16);
      if (check(tmp)) {
        out[x] = tmp;
      } else {
        x--;
      }
    }
    return out;
  }

  private static char[] VALID_CHARACTERS = generateRandomArray();

  public static String csRandomAlphaNumericString(int numChars) {
    SecureRandom srand = new SecureRandom();
    Random rand = new Random();
    char[] buff = new char[numChars];
    for (int i = 0; i < numChars; ++i) {
      if ((i % 10) == 0) {
        rand.setSeed(srand.nextLong());
      }
      buff[i] = VALID_CHARACTERS[rand.nextInt(VALID_CHARACTERS.length)];
    }
    return new String(buff);
  }

}
