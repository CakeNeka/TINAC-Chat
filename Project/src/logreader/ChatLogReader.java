package logreader;

import helper.EncryptionHelper;
import helper.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Para leer los mensajes encriptados
 */
public class ChatLogReader {
    public static void main(String[] args) {
        try {
            readLogs();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void readLogs() throws IOException {
        File logFolder = new File(Logger.CHAT_LOG_PATH);
        if (!logFolder.exists() || !logFolder.isDirectory()) {
            throw new IOException("Log folder does not exist");
        }

        File[] logs = logFolder.listFiles();
        for (File log : logs) {
            List<String> lines = Files.readAllLines(log.toPath());
            try {
                printDecryptedLines(log.getName(), lines);
            } catch (GeneralSecurityException e) {
                System.err.println("Unable to decrypt " + log.getName());
            }
        }
    }

    private static void printDecryptedLines(String fileName, List<String> lines) throws GeneralSecurityException {
        System.out.println("Decrypting " + fileName + "...");
        for (String line : lines) {
            String decryptedLine = EncryptionHelper.decrypt(line, Logger.secretKey);
            System.out.println(decryptedLine);
        }
    }
}
