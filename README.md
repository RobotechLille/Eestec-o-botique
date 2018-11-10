# Eestec'o'botique

Un programme Arduino pour les robots de type "Eestec" et une application Android pour les contrôler en Bluetooth

## Communication

Pour commander les moteurs, l'application Android envoie deux bytes : le premier pour la roue gauche et le second pour la roue droite.

Le codage des valeurs est le suivant :

 - de 0 à 127, le moteur tourne dans le sens inverse (recule). 127 signifie aucune puissance et 0 signifie pleine puissance.
 - de 128 à 255, le moteur tourne dans le sens normal (avance). 128 signifie aucune puissance et 255 signifie pleine puissance.

Pour représentation, on peut imaginer un slider (représentant la valeur du byte), où à la position basse le moteur tourne à pleine puissance dans le sens inverse, à la position au milieu le moteur est à l'arrêt et à la position haute le moteur tourne à pleine puissance dans le sens normal.

Ces informations doivent être envoyées toutes les 30 ms maximum. Plus rapide et le module Bluetooth risque de sauter des valeurs, décalant ainsi gauche et droite à chaque réception (en clair : ça ne marche pas). 30 ms est normalement suffisant pour que la réaction du robot paraisse instantanée.

## Programme Arduino

Le programme Arduino est localisé dans le dossier /Arduino/eestec-o-botique.

Ce programme est destiné à être exécuté par une Arduino Mega (Le module Bluetooth doit être connecté sur le Serial2 pour ne pas interférer avec le Serial1 utilisé pour transmettre le programme via USB).

Si ne pouvez pas vous permettre d'avoir une Arduino Mega :

 - Changer, dans le programme, toutes les références à "Serial2" par "Serial".
 - Veiller à débrancher le module Bluetooth des ports Rx et Tx pour télécharger le programme dans l'Arduino.
 - Rebrancher le module Bluetooth aux ports Rx et Tx.

## Programme Android

Dans le dossier /Android se trouve les sources de l'application son l'installateur (dans /Android/release).

L'application est aussi disponible sur le Google Play Store : https://play.google.com/store/apps/details?id=me.robotech.eestecobotique

Pour connecter l'Arduino et l'application par Bluetooth :

 - Jumeler le module Bluetooth via les paramètres Bluetooth du téléphone (Code PIN usuel : 1234 ou 0000).
 - Lancer l'application et sélectionner le module Bluetooth dans la liste des appareils.
 - Un texte "Status" plus bas vous informe si la connexion a été établie.
 - Si la connexion est établie, sélectionner un mode de pilotage dans le menu en haut à droite.