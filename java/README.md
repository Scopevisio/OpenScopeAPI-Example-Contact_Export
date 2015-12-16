## Java-Beispiel

Voraussetzung für die Ausführung ist ein JDK ab Version 6. Die Implementierung des Java-Beispiels ist in der `Example.java` selbst dokumentiert. 

### Aufruf des Code-Beispiels

- Kompilieren `javac com/scopevisio/openscope/*.java`
- Ausführen `java com.scopevisio.openscope.Example 2000000 user@example.com password`

#### Zusätzliche VM-Argumente:

- com.scopevisio.openscope.verbose (valider Wert ist ausschließlich "verbose")
- com.scopevisio.openscope.webservice.url (ausschließlich für interne Zwecke)

Beispielaufruf:
```
java
  -Dcom.scopevisio.openscope.verbose=verbose
  -Dcom.scopevisio.openscope.webservice.url=http://test.internal.domain:65535
  com.scopevisio.openscope.Example 2000000 user@example.com password
```
