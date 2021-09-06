# Plugin Architecture of Public Attestation

## Tool Supports

- [PF4J](https://pf4j.org/)

## Projects Organization

Master Poject + Plugin Project:

- Master Project: current backend project + plugin related functions, including:
  - plugin respositry
  - plugin lifecycle management
  - relayer of requests of all kinds of public attestation
- Plugin Project:
  - implmentation of the extension point defined by the master project.

## Plugin Development

### How to develop a plugin

1. `git clone` a plugin project starter, which is a plain java gradle project, including a plugin interface java jar.
1. implment the plugin interface.
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

If your plugin is used internally, it is even simpler: put the built jar into the plugins directory used by `attestation.id` backend.

Please read the document to get more details.

NOTE: You have to clone and build your own version of `attestation.id`.

## Plugin Types

Basically, there are two plugin types planned:

- Java Plugin
  - a java jar implmenting the interfaces defined by `attestation.id`.
- Bridge Plugin
  - a plugin implmenting the restful api endpoints defined by `attestation.id`.

The `Bridge Plugin` allows devs to use the tools they are familar with to develop a plugin.

## Plugins Health Monitoring

All plugins MUST implment a health check interface which is for Plugins health monitoring.

At the same time, there is a monitor page to show the health status of all the plugins running in the `attestation.id` site.

## Plugin Interfaces

TBD
