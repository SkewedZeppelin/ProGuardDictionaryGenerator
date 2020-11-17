/*
Copyright (c) 2015-2017 Divested Computing Group

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package info.spotcomms.proguarddictionarygenerator;

import org.uncommons.maths.random.AESCounterRNG;
import org.uncommons.maths.random.DevRandomSeedGenerator;
import org.uncommons.maths.random.SecureRandomSeedGenerator;
import org.uncommons.maths.random.SeedGenerator;

import java.io.File;
import java.io.PrintWriter;
import java.util.Locale;

public class Main {

    private static SeedGenerator seedGenerator = null;
    private static AESCounterRNG secureRandom = null;
    private static int unicodePlaneEnd = 0xFFFF;
    private static String[] availableCharacters = null;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("full")) {
            unicodePlaneEnd = 0x10FFFF;
        }
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
            if (os.equals("Linux") || os.equals("Mac")) {
                seedGenerator = new DevRandomSeedGenerator();
            } else {
                seedGenerator = new SecureRandomSeedGenerator();
            }
            updateSeed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateSeed() {
        try {
            try {
                secureRandom = new AESCounterRNG(seedGenerator.generateSeed(32));
            } catch (Exception e) {
                secureRandom = new AESCounterRNG(seedGenerator.generateSeed(16));
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createDatabase(String name, int amount, int lineLength) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new File(name));
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeRandoms();
        System.out.println("Starting generation of " + name);
        for (int x = 0; x < amount; x++) {
            writer.println(generateRandomUnicodeString(lineLength));
            if (x % 256 == 0) {
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
            int uchar = secureRandom.nextInt(unicodePlaneEnd);
            char[] charPair = Character.toChars(uchar);
            if (Character.isJavaIdentifierStart(uchar)) {
                out[x] = new String(charPair);
            } else {
                x--;
            }
        }
        return out;
    }

    private static String generateRandomUnicodeString(int numChars) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < numChars; ++i) {
            out.append(availableCharacters[secureRandom.nextInt(availableCharacters.length)]);
        }
        return out.toString();
    }

}
