# Folder Wizard

## Bedienungshinweise

Folder Wizard benötigt Java 1.8u60 oder neuer.

Das Programm benötigt eine FML Datei, um daraus die Ordnerstruktur zu erzeugen.
Diese Datei muss mit `*.fml` enden.

#### FML Regeln

- Das "Keyword", damit Folder Wizard erkennt, dass die Zeile ein Ordner werden soll, ist das `+`.
- Jede Zeile, die nicht mit einem oder mehreren `+` anfängt, ist ein Kommentar und wird für die Ordnerstruktur ignoriert.
- Die einzelnen Ebenen der Ordnerstruktur werden mit weiteren `+` Zeichen gekennzeichnet, siehe Bsp. unten.
- Für jeden Ordner muss eine neue Zeile angefangen werden.
- Die Ordnernamen dürfen nur aus a-z, A-Z, 0-9, _, -, + und einem Leerzeichen bestehen.
- Alle Zeichen, die auf den ersten Block an `+` Zeichen folgen, werden als Ordnername gewertet.
- HINWEIS: Folder Wizard prüft automatisch, ob die FML Datei den Syntax- und gängigen Windows Ordner-Erstellungs-Regeln entspricht.
   
  Zusätzlich prüft Folder Wizard, ob ein "Vorwärtssprung" existiert. Ein Vorwärtsprung liegt vor, 
  wenn in der FML Datei ein Unterordner erstellt wird, der mehr als eine Ebene tiefer liegt.
  Der/die Ordner zwischen diesem und dem vorigen Ordner fehlt/fehlen dann.
  
  Ein Beispiel:
  ```
  +Ordner
  +++Illegaler Ordner
  ```

#### FML Beispiel

```
Kommentar
 
+Ordner 1
++Unterordner 1 von Ordner 1
+++Unterunterordner 1 von Unterordner 1
+Ordner 2
+Ordner 3
+Ordner 4
Auch hier ist ein Kommentar möglich
+Ordner 5
++Unterordner 1 von Ordner 5
+Ordner 6
```

## Bekannte Beschränkungen

- Man kann nicht den Laufwerksbuchstaben (Bspw. C:\\) als Root Verzeichnis auswählen.

## DEV Setup
- Java 11 installiert
- [GraalVM installieren](https://github.com/graalvm/graalvm-ce-builds/releases)
- JavaFX kommt über die `pom.xml` mit, dadurch ist keine Installation nötig.
- Projekt laufen lassen mit: `mvn clean:clean compiler:compile javafx:run -f pom.xml`
- Projekt bauen in natives Image mit: `mvn clean client:build`
    - Dafür muss die Variable `GRAALVM_HOME` gesetzt sein und `JAVA_HOME` muss auf `GRAALVM_HOME` zeigen.
      
      Beispiel:
      ```shell script
      export GRAALVM_HOME=/Library/Java/JavaVirtualMachines/graalvm-ce-java11-20.2.0/Contents/Home
      export JAVA_HOME=$GRAALVM_HOME
      ```
    - Und die Berechtigung gesetzt werden `sudo xattr -r -d com.apple.quarantine /path/to/graalvm`.