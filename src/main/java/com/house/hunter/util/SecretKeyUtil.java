package com.house.hunter.util;

import io.github.cdimascio.dotenv.Dotenv;

public class SecretKeyUtil {
    private static final String SECRET_KEY_ENV_NAME = "JWT_SECRET_KEY";

    public static String readEncryptedSecretFromEnv() {
        Dotenv dotenv;

        // Check if running inside a Docker container
        if (System.getenv("DOCKER_ENV") != null) {
            // Load .env file from the container-specific directory
            dotenv = Dotenv.configure()
                    .directory("/usr/local/lib")
                    .load();
        } else {
            // Load .env file from the classpath (for running from IDE)
            dotenv = Dotenv.configure().load();
        }

        return dotenv.get(SECRET_KEY_ENV_NAME);
    }



}
