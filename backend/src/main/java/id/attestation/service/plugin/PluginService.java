package id.attestation.service.plugin;

import jakarta.inject.Singleton;
import org.pf4j.*;

import java.util.List;

@Singleton
public class PluginService {

    private final PluginManager pluginManager = new DefaultPluginManager() {
        @Override
        protected ExtensionFactory createExtensionFactory() {
            return new SingletonExtensionFactory();
        }
    };

    public void loadPlugins() {
        pluginManager.loadPlugins();
    }

    public List<PluginWrapper> getPlugins() {
        return pluginManager.getPlugins();
    }

    public void startPlugins() {
        pluginManager.startPlugins();
    }

    public void stopPlugins() {
        pluginManager.stopPlugins();
    }

    public <T> List<T> getExtensions(Class<T> type) {
        return pluginManager.getExtensions(type);
    }

    public <T> T getExtension(Class<T> type) {
        List<T> extensions = getExtensions(type);
        return extensions.isEmpty() ? null : extensions.get(0);
    }

}
