package kz.uco.tsadv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CopyFilesAndRenameExtension {
    public static void main(String[] args) {
        try {
            method1();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void method1() throws IOException {
        Files.walk(Paths.get("D:\\Projects\\tsadvanced\\modules\\core")).filter(Files::isRegularFile)
                .forEach(e -> {
                    try {
                        String ext = e.getFileName().toString().substring(e.getFileName().toString().lastIndexOf('.') + 1);
                        if (ext.equals("java") || ext.equals("xml")) {
                                                    method2(e);

                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });
    }

    private static void method2(Path e) throws IOException {
        String fileName = e.getFileName().toString();
        String finalName = fileName.substring(0, fileName.lastIndexOf('.'));

        Path copied = Paths.get("D:\\kek\\" + finalName + ".txt");

        Files.copy(e, copied, StandardCopyOption.REPLACE_EXISTING);
    }
}
