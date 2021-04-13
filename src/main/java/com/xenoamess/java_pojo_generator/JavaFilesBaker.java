package com.xenoamess.java_pojo_generator;

import org.apache.commons.lang3.NotImplementedException;

public class JavaFilesBaker {
    public void bake(
            JavaCodeBakeProperties javaCodeBakeProperties
    ) {
        assertLegal(javaCodeBakeProperties);
        // TODO

    }

    private void assertLegal(JavaCodeBakeProperties javaCodeBakeProperties) {
        if (!javaCodeBakeProperties.isIfLombok()) {
            throw new NotImplementedException(
                    "sorry but lombok is really needed in this version... " +
                            "It will be optional in future, I promise.");
        }
    }
}
