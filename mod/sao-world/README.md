# SAO World (Forge 1.20.1)

Slice jouable de Sword Art Online pour Minecraft Forge 1.20.1 : HUD SAO minimal, armes, cristaux de soin/retour et boss de l’étage 1 (Illfang). Cette base est faite pour être étendue vers l’univers complet (étages, quêtes, IA plus poussée, assets custom).

## Lancer en dev
1. Ouvre un terminal dans `C:\Users\allon\MinecraftDev\mods\forge\sao-world` puis active Java 17 : `. ..\\..\\..\\use-java17.ps1` (PowerShell).
2. Génère/ouvre l’IDE : `./gradlew genIntellijRuns` ou `./gradlew eclipse` ou lance directement `./gradlew runClient` pour tester.
3. Build : `./gradlew build` (le jar est dans `build/libs`).

## Contenu actuel
- HUD SAO simplifié (barre HP cyan au-dessus de la hotbar).
- Armes : `Anneal Blade` (dégâts diamant rapides), `Demonic Sword` (dégâts netherite, feu).
- Cristaux : `Healing Crystal` (heal complet + absorption/regen), `Teleport Crystal` (retour au spawn).
- Boss : `Illfang the Kobold Lord` (HP 120, dégâts 10, leap agressif) + spawn egg et loot (cristaux + chance de lame).
- Onglet créatif dédié « Sword Art Online » regroupant les items.

## Map / immersion
- Place ta map Aincrad (open source) dans `saves/Aincrad` ou utilise un serveur Forge avec la map dans `world`. Je n’ai pas inclus de map tierce pour éviter les licences, mais ce dossier est prêt.

## TODO pour aller vers l’univers complet SAO
- Assets : textures dédiées (armes, UI, particules), musiques/sons d’interface.
- IA : phases de boss (parades, adds), patterns par étage, drops spécifiques, donjons.
- Monde : génération / structure Aincrad (100 étages), ports vers les boss, zones de farm, villes.
- Systèmes : niveaux de joueur, skills, menus SAO complets, crystals multiples (antiteleport, rez, etc.), HUD plus riche (party, buffs).
- Réseau : compatibilité serveur dédiée (Paper/Forge mix via proxy), équilibrage multi.
