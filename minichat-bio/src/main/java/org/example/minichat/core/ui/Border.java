package org.example.minichat.core.ui;

public interface Border {

    char[] getBorderCharacters();

    default String getTop() {
        return String.valueOf(getBorderCharacters()[0]);
    }

    default String getDown() {
        return String.valueOf(getBorderCharacters()[2]);
    }

    default String getLeft() {
        return String.valueOf(getBorderCharacters()[3]);
    }

    default String getRight() {
        return String.valueOf(getBorderCharacters()[1]);
    }

    default String getTopLeft() {
        return String.valueOf(getBorderCharacters()[4]);
    }

    default String getTopRight() {
        return String.valueOf(getBorderCharacters()[5]);
    }

    default String getDownLeft() {
        return String.valueOf(getBorderCharacters()[7]);
    }

    default String getDownRight() {
        return String.valueOf(getBorderCharacters()[6]);
    }

}
