# Plugin Architecture of Public Attestation

## Tool Supports

- [PF4J](https://pf4j.org/)

## Projects Organization

Master Project + Plugin Project:

- Master Project: current backend project + plugin related functions, including:
  - plugin repository
  - plugin lifecycle management
  - relater of requests of all kinds of public attestation
- Plugin Project:
  - implementation of the extension point defined by the master project.

## Plugin Development

### How to develop a plugin

1. `git clone` a plugin project starter, which is a plain java gradle project, including a plugin interface java jar.
1. implement the plugin interface.
   - mainly about the verification of the identifier in the request is the one sending the request.
1. test
1. build

### How to publish a plugin

#### attestation.id plugins

If your plugin is opened for [attestation.id](https://stage.attestation.id/), at this point, we only accept a plugin as a GitHub PR.

1. fork [the attestation.id github repo](https://github.com/AlphaWallet/attestation.id).
1. put your plugin project in the right place.
1. send a PR.

NOTE:

- include automation tests.
  - we recommend [Spock](http://spockframework.org/).
- keep the version of a plugin following [Semantic Version](https://semver.org/).
- make sure the name of plugin name unique.
  - we recommend to use `the fully qualified name`.

#### custom plugins

If your plugin is used internally, it is even simpler: put the built jar into the `plugins` directory used by `attestation.id` backend.

Please read the document to get more details.

NOTE: You have to clone and build your own version of `attestation.id`.

## Plugin Types

Basically, there are two plugin types planned:

- Java Plugin
  - a java jar implementing the interfaces defined by `attestation.id`.
- Bridge Plugin
  - a plugin implementing the restful api endpoints defined by `attestation.id`.

The `Bridge Plugin` allows devs to use the tools they are familiar with to develop a plugin.

## Plugins Health Monitoring

All plugins MUST implement a health check interface which is for Plugins health monitoring.

At the same time, there is a monitor page to show the health status of all the plugins running in the `attestation.id` site.

## Plugin Interfaces

Plugins provider third party user identity verify should implement interface `id.attestation.service.auth.AuthenticationService`:

```java
/**
 * Return which pa provider does this plugin uses. E.g: auth0, firebase, ...
 * @return String
 */
String idProvider();

/**
 * Use this extension to verify user identify.
 * @param headers http headers starts with "x-pap" pass to verify, implementation method should extract required params from headers
 * @param paProvider pa = public attestation. paProvider is third party sign in provider does this user sign in from. E.g facebook, twitter, google,...
 * @param userId userId from idProvider
 * @return boolean
 */
boolean verifySocialConnection(Map<String, List<String>> headers, String paProvider, String userId);

/**
 * Use this extension to verify user email.
 *
 * @param headers   http headers starts with "x-pap" pass to verify, implementation method should extract required params from headers
 * @param userEmail user email to be verified
 * @return boolean
 */
boolean verifyEmail(Map<String, List<String>> headers, String userEmail);
```

`idProvider` will be extracted from http request header `x-pap-id-provider`. E.g:

```txt
POST /attestation/public
x-pap-ac: <access_token>
x-pap-id-provider: auth0

{PublicAttestationWebRequest}
```

And backend will use `AuthenticationService` which matches this `idProvider`.

## Plugin Project Example

Full example you can check `plugin-starter/` directory.

A sample plugin project described as blow:

`build.gradle` sample:

```groovy
plugins {
    id 'java'
    id 'com.github.johnrengelman.shadow' version '6.1.0'
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly project(':plugins:api')
    annotationProcessor project(':plugins:api')
    // you can add more dependencies here
}

jar.manifest {
    // MANIFEST.MF MUST contains Plugin-Class, Plugin-Id and Plugin-Version at least, also you may defined more information here.
    // Here we read manifest information from gradle.properties, you can define pluginClass, pluginId, version, and pluginDescription properties in gradle.properties
    attributes(
            // point to your plugin starter class. E.g: id.attestation.plugin.DemoPlugin
            'Plugin-Class': project.pluginClass,
            // your plugin id, must be unique ot other plugins. E.g: DemoPlugin
            'Plugin-Id': project.pluginId,
            // your plugin version, must in semver format, e.g: 1.0.0
            'Plugin-Version': project.version,
            'Plugin-Description': project.hasProperty('pluginDescription') ? project.pluginDescription : ''
    )
    // Another example with more rich manifest:
    //     attributes(
    //             'Built-By': System.properties['user.name'],
    //             'Build-Timestamp': java.time.OffsetDateTime.now(),
    //             'Created-By': "Gradle ${gradle.gradleVersion}",
    //             'Build-Jdk': "${System.properties['java.version']} (${System.properties['java.vm.vendor']} ${System.properties['java.vm.version']})",
    //             'Build-OS': "${System.properties['os.name']} ${System.properties['os.arch']} ${System.properties['os.version']}",
    //             'Implementation-Version': project.version,
    //             'Plugin-Class': project.pluginClass,
    //             'Plugin-Id': project.pluginId,
    //             'Plugin-Version': project.version,
    //             'Plugin-Description': project.hasProperty('pluginDescription') ? project.pluginDescription : ''
    //     )
}

shadowJar {
    archiveClassifier.set('')
}
```

Then you must write a plugin starter class which extends from `org.pf4j.Plugin`, e.g:

```java
package id.attestation.plugins;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class DemoPlugin extends Plugin {

    public DemoPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

}
```

And you should implement the interface from api, **_DO NOT FORGET_** add `@org.pf4j.Extension` annotation to your implement class, e.g:

```java
package id.attestation.plugins;

import id.attestation.service.auth.AuthenticationService;
import org.pf4j.Extension;

@Extension
public class DemoImpl implements AuthenticationService {

    @Override
    public String paProvider() {
        return "auth0";
    }

    @Override
    public boolean verifySocialConnection(Map<String, List<String>> headers, String idProvider, String userId) {
        return true;
    }

    @Override
    public boolean verifyEmail(Map<String, List<String>> headers, String userEmail) {
      return true;
    }
}
```

Then you can build your plugin:

```sh
gradle shadowJar
```

Output should be `build/libs/<plugin_name>-<plugin_version>.jar`.

You can put it into `plugins/` directory and restart backend.

`plugins/` should be in **_CURRENT WORKING DIRECTORY_**. If you want to use another path, you should set `pf4j.pluginsDir` system property. E.g:

```sh
java -Dpf4j.pluginsDir=/path/to/plugins/ -jar backend.jar
```

## Plugins Health Check

Backend provide API `/api/health` to display plugins loaded and domain supports information:

```sh
curl localhost:8080/api/health
```

Output:

```json
{
  "AuthenticationService": [
    {
      "paProvider": "auth0",
      "class": "id.attestation.plugins.auth.Auth0AuthService"
    },
    {
      "paProvider": "firebase",
      "class": "id.attestation.plugins.DemoImpl"
    }
  ],
  "plugins": [
    {
      "id": "Auth0Plugin",
      "version": "0.0.1"
    },
    {
      "id": "DemoPlugin",
      "version": "0.0.1"
    }
  ]
}
```
