package io.github.mdraihan27.routinemanager;

import org.junit.Test;

import io.github.mdraihan27.routinemanager.core.util.CustomColorParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CustomColorParserTest {

    @Test
    public void testValidHex() {
        assertTrue(CustomColorParser.isValidHex("#7EAAEE"));
        assertTrue(CustomColorParser.isValidHex("7EAAEE"));
        assertTrue(CustomColorParser.isValidHex("#FF7EAAEE"));
        assertTrue(CustomColorParser.isValidHex("FF7EAAEE"));
    }

    @Test
    public void testInvalidHex() {
        assertFalse(CustomColorParser.isValidHex(null));
        assertFalse(CustomColorParser.isValidHex(""));
        assertFalse(CustomColorParser.isValidHex("ZZZZZZ"));
        assertFalse(CustomColorParser.isValidHex("#1234"));
    }

    @Test
    public void testToHexString() {
        assertEquals("#7EAAEE", CustomColorParser.toHexString(0xFF7EAAEE));
    }
}
