## OpenScope API Beispiel: Export von Kontakten

Dieses OpenScope API Beispiel demonstriert den Abruf von Kontaktdaten aus Ihrer Scopevisio Instanz. 
Dazu wird zuerst die API-Funktion **accounting.GetOrganisations** aufgerufen, um die vorhanden Gesellschaften abzurufen. Eine Ihrer eigenen Gesellschaften wird der vorinstallierten Demo-Gesellschaft bevorzugt. Anschließend wird die API-Funktion **Contact.exportExtendedCSV** aufgerufen. Damit werden alle Kontakte abgerufen, die während den letzen 100 Tagen erstellt wurden.

Dieses Beispiel wurde in Java, Bash und für das Web bereitgestellt. Die entsprehenden Unterordner enthalten jeweils eine eigene Dokumentation für die Anwendung. 

### Erforderliche Daten

- Einen [Scopevisio-Zugang](https://www.scopevisio.com/anwendungen/registrieren)
- Ihre [Kundennummer](https://www.scopevisio.com/help/de/CRM/Welche_Kundennummer)
- Scopevisio Benutzername bzw. E-Mail-Adresse
- Scopevisio Kennwort

### Relevante API-Dokumentation

- [API-Referenz](https://www.scopevisio.com/help/de/API/API_Reference)
- [Export von Kontakten](https://www.scopevisio.com/help/de/API/Contact_Export)
