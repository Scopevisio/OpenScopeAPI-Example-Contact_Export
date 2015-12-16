## Shell-Beispiel

### Aufruf des Code-Beispiels

`java com.scopevisio.openscope.HelloWorld 2000000 user@example.com password`

#### Zusätzliche VM-Argumente:

- com.scopevisio.openscope.verbose (valider Wert ist ausschließlich "verbose")
- com.scopevisio.openscope.webservice.url (ausschließlich für interne Zwecke)

Beispielaufruf:

```
java
  -Dcom.scopevisio.openscope.verbose=verbose
  -Dcom.scopevisio.openscope.webservice.url=http://test.internal.domain:65535
  com.scopevisio.openscope.HelloWorld 2000000 user@example.com password
```

#### Hinweise

Dieses Code-Beispiel kann ab Java 6 kompiliert werden.
