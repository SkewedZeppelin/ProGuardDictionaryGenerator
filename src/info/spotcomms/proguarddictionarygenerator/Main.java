package info.spotcomms.proguarddictionarygenerator;

import java.io.File;
import java.io.PrintWriter;
import java.util.Locale;

import org.uncommons.maths.random.AESCounterRNG;
import org.uncommons.maths.random.DevRandomSeedGenerator;
import org.uncommons.maths.random.SecureRandomSeedGenerator;
import org.uncommons.maths.random.SeedGenerator;

public class Main {

  private static SeedGenerator seedGenerator = null;
  private static AESCounterRNG secureRandom = null;
  private static char[] VALID_CHARACTERS = null;

  public static void main(String[] args) {
    initializeRandoms();
    VALID_CHARACTERS = generateRandomUnicodeArray();
    System.out.println("Example String: " + generateRandomUnicodeString(256));
    createDatabase("ObfuscationDictionary.txt", 2500, 20000);
    createDatabase("ClassObfuscationDictionary.txt", 5000, 250);
    createDatabase("PackageObfuscationDictionary.txt", 5000, 250);
  }

  private static void initializeRandoms() {
    try {
      String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
      if(os.equals("Linux") || os.equals("Mac")) {
        seedGenerator = new DevRandomSeedGenerator();
      } else {
        seedGenerator = new SecureRandomSeedGenerator();
      }
      updateSeed();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  private static void updateSeed() {
    try {
      try {
        secureRandom = new AESCounterRNG(seedGenerator.generateSeed(32));
      } catch(Exception e) {
        secureRandom = new AESCounterRNG(seedGenerator.generateSeed(16));
        e.printStackTrace();
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  private static void createDatabase(String name, int amount, int lineLength) {
    try {
      initializeRandoms();
      System.out.println("Starting generation of " + name);
      PrintWriter writer = new PrintWriter(new File(name));
      for (int x = 0; x < amount; x++) {
        writer.println(generateRandomUnicodeString(lineLength));
        if (x % 25 == 0) {
          updateSeed();
          VALID_CHARACTERS = generateRandomUnicodeArray();
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

  private static char[] generateRandomUnicodeArray() {
    char[] out = new char[65536];
    for (int x = 0; x < out.length; x++) {
      int uchar = Integer.parseInt(("\\u" + Integer.toHexString(secureRandom.nextInt(0x10FFFF))).substring(2), 16);
      if(Character.isJavaIdentifierStart(uchar)) {
        out[x] = (char) uchar;
      } else {
        x--;
      }
    }
    return out;
  }

  public static String generateRandomUnicodeString(int numChars) {
    char[] buff = new char[numChars];
    for (int i = 0; i < numChars; ++i) {
      buff[i] = VALID_CHARACTERS[secureRandom.nextInt(VALID_CHARACTERS.length)];
    }
    return new String(buff);
  }

}
