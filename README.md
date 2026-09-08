# qe_suit

## Struttura Maven

Il repository è organizzato con un POM root aggregatore (`pom.xml`) che raccoglie i moduli principali della suite:

- `frontend-e2e-framework` (progetto separato, con un proprio POM aggregatore)
- `common`
- `interop`
- `send`

I moduli `common`, `interop` e `send` possono essere compilati anche in modo selettivo, lasciando che Maven risolva automaticamente le dipendenze necessarie nel reactor.

### Compilare un modulo specifico

L’approccio consigliato è partire dal POM root e usare `-pl` insieme a `-am`:

```powershell
mvn -f "C:\path\to\qe_suit\pom.xml" -pl :interop-suite -am clean install -DskipTests
```

In questo esempio:

- `-f` indica a Maven quale `pom.xml` usare
- `-pl :interop-suite` seleziona il modulo `interop`
- `-am` costruisce anche i moduli da cui `interop` dipende

Se sei già nella directory che contiene il POM root, puoi omettere `-f`:

```powershell
mvn -pl :interop-suite -am clean install -DskipTests
```

Per lavorare invece dalla directory del singolo modulo, senza passare dal reactor root, puoi lanciare Maven direttamente lì; in quel caso però le dipendenze devono essere già risolte nel repository locale o remoto.

