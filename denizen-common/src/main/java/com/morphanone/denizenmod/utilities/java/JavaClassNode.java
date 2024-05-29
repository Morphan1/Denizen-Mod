package com.morphanone.denizenmod.utilities.java;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Parent interface of all {@link PartialJavaClass} substructures
 */
public interface JavaClassNode {
    void writeTo(DataOutputStream data) throws IOException;
}
