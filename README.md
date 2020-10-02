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
