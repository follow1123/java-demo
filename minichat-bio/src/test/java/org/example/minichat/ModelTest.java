package org.example.minichat;

import org.example.minichat.model.PropsModel;
import org.example.minichat.utils.ResourcesUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelTest {

    static PropsModel propsModel;

    @BeforeAll
    static void setUpModel() {
        propsModel = new PropsModel(ResourcesUtil.getRootPath(), "test_model.properties");
        propsModel.setProp("key1", "value1");
        propsModel.setProp("key2", "value2");
        propsModel.store();
    }

    @AfterAll
    static void tearDownModel() {
        if (propsModel != null) {
            propsModel.delete();
        }
    }

    @ParameterizedTest
    @CsvSource({
            "key1, value1",
            "key2, value2"
    })
    public void testGetFormProperties(String key, String value) {
        assertEquals(value, propsModel.getProp(key));
    }

    @Test
    public void testStoreProperties() {
        String key = "key3";
        String value = "value3";
        propsModel.setProp(key, value);
        propsModel.store();
        assertEquals(value, propsModel.getProp(key));

        String filePath = propsModel.getFilePath();
        File file = new File(filePath);
        PropsModel pm = new PropsModel(file.getParent(), file.getName());
        assertEquals(value, pm.getProp(key));
    }
}
