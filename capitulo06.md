# DEPLOYING USING AN OSGI BUNDLE


## HOT DEPLOYMENT

```bash
cp $ProjectDir/target/foo-1.0-SNAPSHOT.jar $FUSE_HOME/deploy
```

## MANUAL INSTALL DEPLOYMENT

from file jar
```bash
karaf@root()> bundle:install -s file:ProjectDir/target/foo-1.0-SNAPSHOT.jar
```

from maven repository
```bash
karaf@root()> install -s mvn:org.jboss.fuse.quickstarts/beginner-camel-log/7.0.0.fuse-000191-redhat-1
```

## MANUAL UNINSTALL DEPLOYMENT

```bash
karaf@root()> bundle:uninstall 181
```

## REDEPLOYING BUNDLES AUTOMATICALLY USING BUNDLE:WATCH

```bash
karaf@root()> bundle:watch 302
```

---
# DEPLOYING FEATURES

## CREATE A CUSTOM FEATURE REPOSITORY

```xml
<?xml version="1.0" encoding="UTF-8"?>
    <features name="CustomRepository">
</features>
```
## ADD A FEATURE TO THE CUSTOM FEATURE REPOSITORY

```xml
<?xml version="1.0" encoding="UTF-8"?>
<features name="MyFeaturesRepo">
    <feature name="example-camel-bundle">
        <bundle>file:C:/Projects/camel-bundle/target/camel-bundle-1.0-
SNAPSHOT.jar</bundle>
    </feature>
</features>
```


## ADD THE LOCAL REPOSITORY URL TO THE FEATURES SERVICE

```bash
karaf@root()> features:addurl file:C:/Projects/features.xml
karaf@root()> features:refreshurl
karaf@root()> features:list

karaf@root()> features:listUrl file:C:/Projects/features.xml
```
## ADD DEPENDENT FEATURES TO THE FEATURE

```xml
<?xml version="1.0" encoding="UTF-8"?>
<features name="MyFeaturesRepo">
    <feature name="example-camel-bundle">
        <bundle>file:C:/Projects/camel-bundle/target/camel-bundle-1.0-SNAPSHOT.jar</bundle>
        <feature version="7.0.0.fuse-000191-redhat-1">camel-core</feature>
        <feature version="7.0.0.fuse-000191-redhat-1">camel-spring-osgi</feature>
    </feature>
</features>
```

## ADD OSGI CONFIGURATIONS TO THE FEATURE

```xml
<?xml version="1.0" encoding="UTF-8"?>
<features name="MyFeaturesRepo">
    <feature name="example-camel-bundle">
        <config name="org.fusesource.fuseesb.example">
            prefix=MyTransform
        </config>
    </feature>
</features>
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<blueprint xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:cm="http://aries.apache.org/blueprint/xmlns/blueprintcm/v1.1.0">
<!-- osgi blueprint property placeholder -->
    <cm:property-placeholder id="placeholder" persistentid="org.fusesource.fuseesb.example">
        <cm:default-properties>
            <cm:property name="prefix" value="DefaultValue"/>
        </cm:default-properties>
    </cm:property-placeholder>

    <bean id="myTransform" class="org.fusesource.fuseesb.example.MyTransform">
        <property name="prefix" value="${prefix}"/>
    </bean>
    ...
    ...
</blueprint>
```

## AUTOMATICALLY DEPLOY AN OSGI CONFIGURATION

By adding a **configfile** element to a feature, you can ensure that an OSGi configuration file gets
added to the **InstallDir/etc** directory at the same time that the feature is installed. This means that
you can conveniently install a feature and its associated configuration at the same time.
For example, given that the **org.fusesource.fuseesb.example.cfg** configuration file is archived
in a Maven repository at **mvn:org.fusesource.fuseesb.example/configadmin/1.0/cfg**, you
could deploy the configuration file by adding the following element to the feature:

```xml
<configfile finalname="etc/org.fusesource.fuseesb.example.cfg">
    mvn:org.fusesource.fuseesb.example/configadmin/1.0/cfg
</configfile>
```

---
# DEPLOYING A PLAIN JAR

```bash
karaf@root> bundle:install -s wrap:mvn:commons-logging/commonslogging/1.1.1
```
 ---

# OSGI SERVICES

## Location of Blueprint files in a JAR file

> OSGI-INF/blueprint

Any files with the suffix, .xml, under this directory are interpreted as Blueprint configuration files; in
other words, any files that match the pattern, OSGI-INF/blueprint/*.xml.


## Location of Blueprint files in a Maven project

> ProjectDir/src/main/resources/OSGI-INF/blueprint


## Blueprint namespace and root element

> http://www.osgi.org/xmlns/blueprint/v1.0.0

```xml
<?xml version="1.0" encoding="UTF-8"?>
<blueprint xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0">
...
</blueprint>
```

## Blueprint Manifest configuration

Some aspects of Blueprint configuration are controlled by headers in the JAR’s manifest file, METAINF/MANIFEST.MF, as follows:

### Custom Blueprint file locations

If you need to place your Blueprint configuration files in a non-standard location (that is, somewhere other than OSGI-INF/blueprint/*.xml), you can specify a  comma-separated list of alternative
locations in the Bundle-Blueprint header in the manifest file—for example:

> Bundle-Blueprint: lib/account.xml, security.bp, cnf/*.xml

### Mandatory dependencies

Normally, while a Blueprint container is initializing, it passes through a grace period, during which time it
attempts to resolve all mandatory dependencies. If the mandatory dependencies cannot be resolved in
this time (the default timeout is 5 minutes), container initialization is aborted and the bundle is not started.
The following settings can be appended to the **Bundle-SymbolicName** manifest header to configure
the grace period:

**blueprint.graceperiod**
If **true (the default)**, the grace period is enabled and the Blueprint container waits for mandatory dependencies to be resolved during initialization.
if **false**, the grace period is skipped and the container does not check whether the mandatory dependencies are resolved.

**blueprint.timeout**
Specifies the grace period timeout in milliseconds. The default is 300000 (5 minutes).

```properties
Bundle-SymbolicName: org.fusesource.example.osgi-client;
blueprint.graceperiod:=true;
blueprint.timeout:= 10000
```

## Defining a Service Bean

```xml
<blueprint xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0">
    <bean id="label" class="java.lang.String">
        <argument value="LABEL_VALUE"/>
    </bean>
    <bean id="myList" class="java.util.ArrayList">
        <argument type="int" value="10"/>
    </bean>
    <bean id="account" class="org.fusesource.example.Account">
        <property name="accountName" value="john.doe"/>
        <property name="balance" value="10000"/>
    </bean>
</blueprint>
```

```java
package org.fusesource.example;

public class Account {
    private String accountName;
    private int balance;
    public Account () { }
    public void setAccountName(String name) {
        this.accountName = name;
    }
    public void setBalance(int bal) {
        this.balance = bal;
    }
...
}
```

## Using properties to configure Blueprint

```xml
<blueprint xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0" xmlns:ext="http://aries.apache.org/blueprint/xmlns/blueprintext/
v1.2.0">
    <ext:property-placeholder>
        <ext:location>file:etc/ldap.properties</ext:location>
    </ext:property-placeholder>
    ...
    <bean ...>
        <property name="myProperty" value="${myProperty}" />
    </bean>
</blueprint>
```

The specification of property-placeholder configuration options can be found at [Aries blueprint-ext](http://aries.apache.org/schemas/blueprint-ext/blueprint-ext.xsd)


## EXPORTING A SERVICE

### Exporting with a single interface

```xml
<blueprint xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0">
    <bean id="savings" class="org.fusesource.example.SavingsAccountImpl"/>
    <service ref="savings" interface="org.fusesource.example.Account"/>
</blueprint>
```
```java
package org.fusesource.example
public interface Account { ... }
public interface SavingsAccount { ... }
public interface CheckingAccount { ... }
public class SavingsAccountImpl implements SavingsAccount
{
...
}
public class CheckingAccountImpl implements CheckingAccount
{
...
}
```

### Exporting with multiple interfaces

```xml
<blueprint xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0">
    <bean id="savings" class="org.fusesource.example.SavingsAccountImpl"/>
    <service ref="savings">
        <interfaces>
            <value>org.fusesource.example.Account</value>
            <value>org.fusesource.example.SavingsAccount</value>
        </interfaces>
</service>
...
</blueprint>
```

### Exporting with auto-export

```xml
<blueprint xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0">
    <bean id="savings" class="org.fusesource.example.SavingsAccountImpl"/>
    <service ref="savings" auto-export="interfaces"/>
...
</blueprint>
```

## IMPORTING A SERVICE

