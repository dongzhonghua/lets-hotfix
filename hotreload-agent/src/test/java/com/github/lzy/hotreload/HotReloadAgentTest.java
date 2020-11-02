package com.github.lzy.hotreload;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author dongzhonghua
 * Created on 2020-11-02
 */
class HotReloadAgentTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void agentmain() {
    }

    @Test
    void findTargetClass() {
    }

    @Test
    void testAgentmain() {
    }

    @Test
    void testFindTargetClass() {
    }

    @Test
    void testComputeIA() {
        HashMap<String, String> map = new HashMap<>();
        map.put("a", "aa");
        map.put("b", "bb");
        map.putIfAbsent("c", "cc");
        map.compute("com1", (k, v) -> {
            System.out.println(v);
            return "cc1";
        });
        map.computeIfAbsent("com1", c -> "com2");
        // map.computeIfPresent("com1", (k, v) -> "com1111");
        System.out.println(map);
    }
    @ParameterizedTest
    @ValueSource(strings = { "racecar", "radar", "able was I ere I saw elba" })
    void palindromes(String candidate) {
        System.out.println(candidate);
    }
}