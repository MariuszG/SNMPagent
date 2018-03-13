# SNMPagent
Projekt akademicki napisany w języku Java (werska SDK 1.8)
Zadaniem agenta SNMP jest odpowiedź na polecenia "snmpset/snmpget" wysyłane do na serwer UDP agenta oraz wygenerowanie odpowiedzi w protokole SNMP, która jest odsyłana do nadawcy. W tym celu program rozpoczyna od utworzenia struktury "drzewiastej" z danych sparsowanych z pliku RFC1213-MIB oraz plików, które są importowane do pliku podstawowego. Po utowrzeniu ww. struktury informacji, agent jest gotowy do odebrania pakietów SNMP poprzez serwer UDP, który po otrzymaniu ramki informacji wywołuje funkcje dekodowania danych. Po zdekodowaniu poprawnego pakietu następuje generowanie pakietu odpowiedzi, który zależy od tego czy otrzymana ramka danych była poprawna i pozwalała na zmianę danych w drzewie np. czy nowa wartość dla danego "liścia" jest w sparsowanym zakresie.

Do poprwy:
- bug w parsowaniu plików,
- bug w przeszukiwaniu drzewa.
