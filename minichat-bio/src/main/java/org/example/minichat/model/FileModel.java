package org.example.minichat.model;

import java.io.*;
import java.util.Base64;

public class FileModel extends PropsModel {

    private final File fileDir;

    public FileModel() {
        super("files.properties");
        fileDir = new File(dataRootDir, "files");
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
    }

    public boolean storeFile(String uuid, String filename, String base64File) {
        Base64.Decoder decoder = Base64.getDecoder();
        ByteArrayInputStream bais = new ByteArrayInputStream(decoder.decode(base64File));
        File file = new File(fileDir, uuid);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] data = new byte[1024];
            int len;
            while ((len = bais.read(data)) != -1) {
                fos.write(data, 0, len);
            }
            fos.flush();
            setProp(uuid, filename);
            store();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFile(String uuid) {
        File file = new File(fileDir, uuid);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[1024];
            int len;
            while ((len = fis.read(data)) != -1) {
                baos.write(data, 0, len);
            }
            baos.flush();
            return new String(Base64.getEncoder().encode(baos.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
