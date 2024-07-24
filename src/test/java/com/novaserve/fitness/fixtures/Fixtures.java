/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.fixtures;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaserve.fitness.users.model.User;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;

public class Fixtures {
  public <T> T get(Class<T> clazz) {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException
        | IllegalAccessException
        | NoSuchMethodException
        | InvocationTargetException e) {
      e.printStackTrace();
      return null;
    }
  }

  public <T> T get(Class<T> clazz, Object[][] values) {
    try {
      T obj = clazz.getDeclaredConstructor().newInstance();
      for (int i = 0; i < values.length; i++) {
        Field field = null;
        try {
          field = obj.getClass().getDeclaredField((String) values[i][0]);
          field.setAccessible(true);
          field.getType();
        } catch (NoSuchFieldException e) {
          throw new RuntimeException(e);
        }
        try {
          field.set(obj, values[i][1]);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }

      return obj;
    } catch (InstantiationException
        | IllegalAccessException
        | NoSuchMethodException
        | InvocationTargetException e) {
      e.printStackTrace();
      return null;
    }
  }

  private JsonNode getTree(String jsonPath) throws IOException {
    File file = new File(jsonPath);
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readTree(file);
  }

  private JsonNode getNode(JsonNode tree, String nodeName) {
    return tree.get(nodeName);
  }

  private <T> T getNode(
      JsonNode tree, String topNodeName, String modifierNodeName, Class<T> nodeClass)
      throws JsonProcessingException {
    JsonNode topNode = tree.get(topNodeName);
    JsonNode modifierNode = topNode.get(modifierNodeName);
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.treeToValue(modifierNode, nodeClass);
  }

  private String getFixturesPath() {
    return Paths.get("src/test/java/com/novaserve/fitness", "fixtures", "fixtures.json")
        .toAbsolutePath()
        .toString();
  }

  public static void main(String[] args) throws IOException {
    Fixtures fixtures = new Fixtures();
    User user = fixtures.get(User.class, new Object[][] {{"id", 1L}, {"username", "admin"}});
    JsonNode tree = fixtures.getTree(fixtures.getFixturesPath());
    User res = fixtures.getNode(tree, "User", "Superadmin", User.class);
  }
}
