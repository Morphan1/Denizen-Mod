package com.morphanone.denizenmod.utilities.java;

import java.io.Serial;

public class ClassReadException extends Exception {
    @Serial
    private static final long serialVersionUID = 640118987812186678L;

    public ClassReadException(String message) {
        super(message);
    }
}
