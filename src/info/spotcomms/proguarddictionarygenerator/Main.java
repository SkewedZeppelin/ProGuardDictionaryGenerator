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
  private static String[] availableCharacters = null;

  public static void main(String[] args) {
    initializeRandoms();
    availableCharacters = generateRandomUnicodeCharacterArray();
    System.out.println("Example String: " + generateRandomUnicodeString(512));
    createDatabase("ObfuscationDictionary.txt", 2048, 6144);
    createDatabase("ClassObfuscationDictionary.txt", 4096, 240);
    createDatabase("PackageObfuscationDictionary.txt", 4096, 240);
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
    PrintWriter writer = null;
    try {
      writer = new PrintWriter(new File(name));
    } catch(Exception e) {
      e.printStackTrace();
    }
    initializeRandoms();
    System.out.println("Starting generation of " + name);
    for (int x = 0; x < amount; x++) {
      writer.println(generateRandomUnicodeString(lineLength));
      if(x % 250 == 0) {
        updateSeed();
        availableCharacters = generateRandomUnicodeCharacterArray();
        System.out.println("\tCompletion: " + x + "/" + amount);
      }
    }
    writer.close();
    System.out.println("Generation of " + name + " completed");
  }

  private static String[] generateRandomUnicodeCharacterArray() {
    String[] out = new String[65536];
    for (int x = 0; x < out.length; x++) {
      int uchar = secureRandom.nextInt(0x10FFFF);
      char[] charPair = Character.toChars(uchar);
      if(Character.isJavaIdentifierStart(uchar)) {
        out[x] = new String(charPair);
      } else {
        x--;
      }
    }
    return out;
  }

  public static String generateRandomUnicodeString(int numChars) {
    String out = "";
    for (int i = 0; i < numChars; ++i) {
      out += availableCharacters[secureRandom.nextInt(availableCharacters.length)];
    }
    return out;
  }

}
