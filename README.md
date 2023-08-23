# Log-Plugin
application-log library enhanced with distributed tracing capabilities, and consistent log formatting.
## Features
* Can integrate as plugin.
* Constant log pattern.
* File spenders and Console appends.
* Distributed tracing using Sluth-plugin.
* Time based and size based rolling policies.

## Supported versions

System will support the following versions.  
Other versions might also work, but we have not tested it.

* Java 8, 11
* Spring Boot 3.1.2

## Usage

add below dependency to POM file

		<dependency>
			<groupId>com.plugin</groupId>
			<artifactId>logging</artifactId>
			<version>1.0.0</version>
		</dependency>

Then add below annotation on Application Main class.

    @EnableLogPlugin

Then add below properties to application.yml file

```yml
plugin:
  log:
    app:
      name: base-app
      msName: base-app
    level: DEBUG
    rolling:
      policy: size
      size: 100000kb
```


## Contributing

Bug reports and pull requests are welcome :)