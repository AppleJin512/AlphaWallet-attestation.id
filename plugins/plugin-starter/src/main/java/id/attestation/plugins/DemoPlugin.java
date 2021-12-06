package id.attestation.plugins;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class DemoPlugin extends Plugin {

    public DemoPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        log.info("My holy plugin started.");
    }

}
