package io.github.mm.startup;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@MicronautTest
class ApplicationTest {

    @Inject
    EmbeddedApplication<?> application;

    @Test
    void testApplicationStarts() {
        assertTrue(application.isRunning());
    }
}
