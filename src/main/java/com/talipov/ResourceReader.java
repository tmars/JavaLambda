package com.talipov;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Марсель on 03.03.2017.
 */
public class ResourceReader {

    /**
     * Логгер
     */
    protected static final Logger logger = Logger.getLogger(ResourceReader.class);

    /**
     * Возвращает поток данных соответствующего ресурса
     * @param path ресурс: URL или путь до файла
     * @return потом данных для чтения
     */
    public static InputStream getStream(String path) throws ResourceNotFoundException {
        InputStream stream = null;

        if (path.startsWith("http://") || path.startsWith("https://")) {
            try {
                URL url = new URL(path);
                stream = url.openStream();
            } catch (MalformedURLException e) {
                logger.error("Ошибка чтения ресурса по URL: " + path);
            } catch (IOException e) {
                logger.error("Ошибка при работе с ресурсом:" + path);
            }
        } else {
            try {
                stream = new FileInputStream(path);
            } catch (FileNotFoundException e) {
                logger.error("Ошибка чтения файла ресурса: " + path);
            }
        }

        if (stream == null) {
            throw new ResourceNotFoundException();
        }
        return stream;
    }

    public static List<Long> read(String filename) throws ParserErrorException, ResourceNotFoundException {
        Scanner input = new Scanner(getStream(filename));
        ArrayList<Long> result = new ArrayList<Long>();
        while (input.hasNext()) {
            if (input.hasNextLong()) {
                result.add(input.nextLong());
            } else {
                String s = input.next();
                input.close();
                logger.error("Ошибка чтения файла ресурса: " + filename);
                throw new ParserErrorException();
            }
        }
        input.close();

        return result;
    }
}
