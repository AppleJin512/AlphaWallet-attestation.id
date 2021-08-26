package id.attestation;

import id.attestation.utils.CryptoUtils;
import io.micronaut.runtime.Micronaut;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException {
        if (args.length == 0 || "server".equalsIgnoreCase(args[0])) {
            Micronaut.run(Application.class);
        } else if ("keys".equalsIgnoreCase(args[0])) {
            CryptoUtils.generateKeys();
        } else if ("help".equalsIgnoreCase(args[0])) {
            System.out.println("Usage: java -jar backend-<version>-all.jar subcommand.\n");
            System.out.println("Available subcommands:\n");
            System.out.println("    keys, \tgenerate a key pair used by server.");
            System.out.println("    server, \tkick off the server, which is default.");
            System.out.println("    help, \thelp of this program.\n");
            System.out.println("Start server: java -jar backend-<version>-all.jar");
        }

    }
}
