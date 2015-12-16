## Java-Beispiel


### Erforderliche Argumente

- Kundennummer (1)
- Benutzername
- Kennwort


### Aufruf des Code-Beispiels

#### Einfacher Aufruf:

java com.scopevisio.openscope.HelloWorld 2000000 user@example.com password

So werden die Webdienste auf die Standard Produktion-URL (https://appload.scopevisio.com) aufgerufen.

#### Aufruf mit zusatzlichen VM-Argumenten:

Sie können nach Wunsch zwei VM-Argumente zusätzlich nutzen, nämlich:

- com.scopevisio.openscope.verbose (Valider Wert is ausschließlich "verbose")
- com.scopevisio.openscope.webservice.url (Es wird ausschließlich für interne Zwecke benutzt)

java -Dcom.scopevisio.openscope.verbose=verbose -Dcom.scopevisio.openscope.webservice.url=http://test.internal.domain:65535 com.scopevisio.openscope.HelloWorld 2000000 user@example.com password


### Ausführungsschritte

1. Der Webdienst **accounting.GetOrganisations** wird aufgerufen, um die vorhanden Gesellschaften zu abrufen. Eine normale Gesellschaft vor der Demo Gesellschaft wird bevorzugt.

2. Der Webdienst **Contact.exportExtendedCSV** wird aufgerufen. So werden alle Kontakte abgerufen, die während den letzen 100 Tagen erstellt würden.


### API

accounting.GetOrganisations (Es besteht gegenwärtig keine entsprechende API-Seite)

[Contact.exportExtendedCSV](https://www.scopevisio.com/help/de/API/Contact_Export)


#### Fußnoten

(1) Sie können Ihre Kundennummer herausfinden, wenn Sie z.B. bei scopevisio.com sich einloggen und die Seite [Meine Downloads](https://www.scopevisio.com/lounge) besuchen.


#### Hinweis

Dieses Code-Beispiel kann ab Java 6 kompiliert werden.
