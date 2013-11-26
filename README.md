Grails Optimus Plugin
=======

Grails Optimus Plugin lets you create some of the most popular artifacts that are not part of the Grails automation and scaffolding but are important to consider when building a Java Web/Grails application, such as log configuration, unit tests, services, i18n messages, logging and responsive and asynchronous views.

To get started, you need to install the plugin by adding the following line in the <b>grails-app/conf/BuildConfig.groovy</b> file:

```groovy
plugins {
    ...
    compile ':optimus:0.3.1'
}
```

You must have at least one domain class with some constraints, for example:

```groovy
package mypackage

class Person {

    String name
    String lastName
    Date birthdate
    Boolean enabled

    static constraints = {
        name blank:false, size:1..100
        lastName blank:false, size:1..100
    }

}
```

Then, you can generate the corresponding artifacts by executing the <b>optimus-all</b> command:

```
grails optimus-all Person
```

The command will tell you about what artifacts have been created, such as config files, unit tests, services, controllers, log files and views:

```
$ grails optimus-all Person
| Finished generation of 'config.properties' file
| Finished generation of 'Config.groovy' file
| Finished generation of 'DataSource.groovy' file
| Finished generation of config artifacts
| Finished generation of constraints unit tests
| Finished generation of ListUtils class file
| Finished generation of service class files
| Finished generation of mock classes
| Finished generation of 'list-max' service unit tests
| Finished generation of 'list-offset' service unit tests
| Finished generation of 'list-sort-order' service unit tests
| Finished generation of 'list' service unit tests
| Finished generation of 'create' service unit tests
| Finished generation of 'update' service unit tests
| Finished generation of 'get' service unit tests
| Finished generation of 'delete' service unit tests
| Finished generation of service unit tests
| Finished generation of 'list' service logs
| Finished generation of 'create' service logs
| Finished generation of 'update' service logs
| Finished generation of 'get' service logs
| Finished generation of 'delete' service logs
| Finished generation of service logs
| Finished generation of cracking service
| Finished generation of controller class files
| Finished generation of 'index' controller unit tests
| Finished generation of 'content' controller unit tests
| Finished generation of 'list' controller unit tests
| Finished generation of 'create' controller unit tests
| Finished generation of 'save' controller unit tests
| Finished generation of 'edit' controller unit tests
| Finished generation of 'update' controller unit tests
| Finished generation of 'delete' controller unit tests
| Finished generation of controller unit tests
| Finished generation of 'index' files
| Finished installation of Optimus templates
| Finished generation of 'content' templates
| Finished generation of 'list' templates
| Finished generation of 'form' templates
| Finished generation of views artifacts
| Finished generation of i18n messages
| Finished generation of artifacts
$
```

You can try the generated artifacts by executing the <b>test-app</b> and <b>run-app</b> commands. For the tests, you can realize that all the class domain constraints, services and controllers have their respective unit tests, and when you run the application, you will see that the views have been built with the 3.0.0 version of the Twitter Bootstrap framework (http://getbootstrap.com/) and they are AJAX-based. Also, you will see some logs in the console output.

You can see the full documentation at http://satvaimal.github.io/optimus/
