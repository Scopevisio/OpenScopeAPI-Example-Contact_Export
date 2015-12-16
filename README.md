## OpenScope API Beispiel: Export von Kontakten

### Erforderliche Argumente

- Kundennummer (1)
- Benutzername
- Kennwort

### Was macht dieses Beispiel? 

- Organisation laden
- Alle Kontakte der letzten 100 Tage laden

### Vorbereitung

- Scopevisio Account / Testinstanz anlegen 

### Wo finde ich was

- Für Java, gehe nach Java / Readme.
...

### Relevante API-Dokumentation

https://www.scopevisio.com/help/de/API/Contact_Export 

### Ausführungsschritte

1. Der Webdienst **accounting.GetOrganisations** wird aufgerufen, um die vorhanden Gesellschaften zu abrufen. Eine normale Gesellschaft vor der Demo Gesellschaft wird bevorzugt.

2. Der Webdienst **Contact.exportExtendedCSV** wird aufgerufen. So werden alle Kontakte abgerufen, die während den letzen 100 Tagen erstellt würden.


### API

accounting.GetOrganisations (Es besteht gegenwärtig keine entsprechende API-Seite)

[Contact.exportExtendedCSV](https://www.scopevisio.com/help/de/API/Contact_Export)


#### Fußnoten

(1) Sie können Ihre Kundennummer herausfinden, wenn Sie z.B. bei scopevisio.com sich einloggen und die Seite [Meine Downloads](https://www.scopevisio.com/lounge) besuchen.

 


