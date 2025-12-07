# Architecture générale du serveur

## Type de serveur et stack recommandée
- **Base** : Paper 1.20+ (optimisations + compatibilité plugin étendue).
- **Proxy** : Velocity (plus performant que Bungee, support moderne de signatures de ressource packs). Permet de séparer lobby et shard d'étage sans déconnecter le joueur.
- **Backends** :
  - `lobby` (Paper) : accueil, sélection de personnage, chargement du pack.
  - `aincrad` (Paper) : monde principal multi-étages. Peut être split en plusieurs instances par plages d'étages si la population l'exige (ex : `aincrad-1-25`, `aincrad-26-50`).
  - `admin` (Paper) : monde créatif/test réservé au staff.
- **Base de données** : MariaDB/MySQL pour les données persistantes (joueurs, stats, quêtes, guildes). Redis optionnel pour le cache et la messagerie inter-serveurs (party/guilde via proxy).

## Plugins/systèmes recommandés
- **Quêtes** : BetonQuest ou BeautyQuests (dialogues, objectifs complexes, étapes multiples). BetonQuest est plus scriptable et interfaçable avec MythicMobs.
- **Mobs custom** : MythicMobs + ModelEngine (3D/poses) + MMOItems (loot/equipements customs). Citizens pour PNJ statiques/dialogues.
- **Stats & niveaux** : Aurelium Skills ou MMOCore (stats, points, compétences passives) + PlaceholderAPI pour l'UI.
- **Économie** : Vault (abstraction), banque/shops via EconomyShopGUI ou ChestShop; AuctionHouse pour l'hôtel des ventes.
- **Menus / GUI** : DeluxeMenus ou BossShopPro pour les interfaces SAO-like; Configurate/ItemsAdder si besoin d'UI avancée.
- **Ressource pack** : ItemsAdder ou Oraxen pour gérer modèles 3D (armes SAO, UI), sons custom et polices; distribution forcée via server.properties/resource-pack et Velocity modern forwarding.

## Organisation des mondes
- **Lobby** : monde plat stylisé salle blanche/bleue "Link Start", PNJ sélection de perso, chargement du pack. Portails vers Aincrad.
- **Aincrad** : overworld unique découpé en zones d'étages (regions WorldGuard) avec téléportation contrôlée. Les donjons peuvent être des instances séparées chargées à la volée (ex: Multiverse-Core/WorldSystem) pour éviter les blocages.
- **Admin/Test** : monde créatif inaccessible aux joueurs; flag WorldGuard; whitelisting via permissions.

# Univers SAO & Level design

## Concept d'étages verrouillés
- Progression strictement linéaire : battre le boss d'étage débloque le cristal de téléportation vers l'étage suivant.
- Les cristaux de ville permettent le retour vers les safe-zones des étages débloqués; aucun TP vers un étage non vaincu.
- Liaison verticale via tours/ascenseurs RP dans la ville après défaite du boss.

## Étages 1 à 10 (proposition)
1. **Étage 1 – Plaine d'Émeraude** :
   - Biome : plaines/forêt claire, météo douce.
   - Ville : *Arcadia* (maisons bois/pierre, place centrale, forge débutant, auberge, tableau de quêtes, portails TP). PNJ : Guide, Forgeron novice, Marchand de cristaux.
   - Farm : sangliers, loups des plaines, abeilles agressives proche des fleurs.
   - Donjon : ruines kobolds sous la colline nord, couloirs étroits, pièges simples.
   - Boss : **Illfang le Kobold** (épée à deux mains, phases cleave/dash/adds). Capacités : Cleave frontal, Charge en ligne, Enrage <50% HP (vitesse + adds kobolds). 
2. **Étage 2 – Marais de Brume** :
   - Biome : marais sombre/brume permanente.
   - Ville : *Brumécume* sur pilotis, lanternes vertes, alchimiste, pêcheur de limons.
   - Farm : limons acides, moustiques géants, fougères carnivores.
   - Donjon : caverne de racines englouties, poison et flaques ralentissantes.
   - Boss : **Reine Limonde** (aoe poison, clones de limons, pluie acide).
3. **Étage 3 – Crêtes Boréales** :
   - Biome : montagnes enneigées, vents forts.
   - Ville : *Frimasbourg* fortifiée, grande forge, quêtes de chasse.
   - Farm : loups alpha, yétis, aigles.
   - Donjon : fortin gelé avec ponts suspendus; plaques de glace cassables.
   - Boss : **Golem de Glace Runique** (bouclier de glace, piliers à briser, rayons glacés).
4. **Étage 4 – Jungle Suspendue** :
   - Biome : jungle luxuriante avec plateformes naturelles.
   - Ville : *Canopée* sur arbres géants, tyroliennes.
   - Farm : lianes vivantes, panthères, singes lanceurs de noix étourdissantes.
   - Donjon : temple couvert de végétation, puzzles de pression, fléchettes empoisonnées.
   - Boss : **Gardien Serpentaire** (serpent plumard, attaques de fouet, anneaux constricteurs, phase aérienne sur lianes).
5. **Étage 5 – Désert des Échos** :
   - Biome : désert/mesa, tempêtes de sable aléatoires.
   - Ville : *Mirage* oasis souterraine, marché noir, caravanes.
   - Farm : scorpions, chacals, bandits PNJ hostiles.
   - Donjon : pyramide inversée avec salles tournantes, énigmes d'ombre.
   - Boss : **Sphinx de Verre** (questions/énigmes, laser solaire, invocations de scarabées).
6. **Étage 6 – Archipel d'Obsidienne** :
   - Biome : îles volcaniques, ponts de basalte.
   - Ville : *Nautilis* port fortifié, forge avancée (armes feu), docks.
   - Farm : salamandres, elementaires de lave, pirates spectres.
   - Donjon : cavernes magmatiques, geysers, plateformes mouvantes.
   - Boss : **Seigneur Magmatique Klynos** (vagues de lave, météores, piliers à refroidir avec eau lootable).
7. **Étage 7 – Forêt Onirique** :
   - Biome : forêt sombre/bioluminescente, particules.
   - Ville : *Lunæra* architectures elfiques, cristaux bleus, quêtes d'herboristerie.
   - Farm : faons spectraux, dryades, failles invoquant des wisp.
   - Donjon : labyrinthe d'arbres mouvants, illusions, champignons rebondissants.
   - Boss : **Avatar Sylvestre** (phase de racines immobilisantes, champignon explosif, clones d'écorce).
8. **Étage 8 – Citadelle des Cieux** :
   - Biome : îles flottantes, ciel clair, arcs-en-ciel.
   - Ville : *Aeria* ponts d'aéronefs, artisans aériens.
   - Farm : harpies, golems de vent, wyvernes.
   - Donjon : spire flottante avec ascenseurs d'air, plateformes temporisées.
   - Boss : **Chevalier Tempête** (charges aériennes, lames de vent, phase tornade qui repousse).
9. **Étage 9 – Abysses Saphirs** :
   - Biome : grottes cristallines aquatiques, lacs souterrains.
   - Ville : *Azul* bâtie sur stalagmites, tunnels vitrés.
   - Farm : anguilles électriques, crabes cristallins, chauve-souris sonar.
   - Donjon : faille aquatique, bulles d'air à gérer, leviers lumineux.
   - Boss : **Lévianthane** (vagues, éclairs, siphon; phase hors de l'eau).
10. **Étage 10 – Bastion d'Acier** :
    - Biome : plaine rocheuse/forteresse métallique.
    - Ville : *Ardentia* citadelle industrielle, rails, forges maîtres.
    - Farm : automates, chevaliers mécaniques, drones.
    - Donjon : forteresse modulaire, portes à batterie, champs de mines.
    - Boss : **Seigneur Clockwork** (phase shield mécanique, drones réparateurs à tuer, attaques de rayon rotatif).

## Lieux iconiques
- **Place de départ** : grande place circulaire en verre/obsidienne claire, hologrammes bleus (particules), statue d'épée brisée.
- **Auberges** : lits + buffs temporaires (regen/stamina), tables de craft RP.
- **Forges** : enclume custom, PNJ forgeron, interface d'amélioration.
- **Boutiques/Market** : stands bois/métal, Auction House, tableau d'offres.

# Système de combat SAO-like
- **Sans magie** au début ; tout repose sur les armes blanches et les Sword Skills.
- **Stats de combat** : PV, Défense (réduction plate/proportionnelle), Dégâts, Critique (chance + multiplicateur), Esquive (chance d'annuler), Stamina (consommée par skills/esquives), Poise (résistance au stagger).
- **Armes** : épée 1M (équilibrée), épée 2M (dégâts lourds, moins d'esquive), rapière (vitesse, critique), dagues (double frappe, saignement), lance (portée, thrust), marteau (stagger), katana (parade + dash), épée courbe (cleave).
- **Inputs Sword Skills** :
  - Clic droit pour "préparer" puis clic gauche pour déclencher.
  - Combos directionnels : sauter + attaque pour gap closer, sprint + attaque pour thrust.
  - Objet barre (plume) pour cycler les skills disponibles.
- **Exemples de skills** : Linear (dash perforant), Vorpal Strike (coup vertical critique), Arc Sweep (cleave 180°), Quadruple Pain (4 coups rapides), Sonic Leap (saut + slam), Star Splash (enchaînement aérien), Cyclone (tourbillon AOE), Parry Window (réduction dégâts + contre).
- **Barres** : actionbar pour stamina/cooldowns, bossbar pour PV boss.
- **Mobs** :
  - Normaux : patterns simples, faibles résists.
  - Élites : mécaniques (zones au sol, bouclier à briser, projectiles).
  - Boss : phases scriptées MythicMobs, AoE, invocations, vulnérabilités conditionnelles (ex: briser piliers, tuer adds).

# Progression du joueur & stats
- **Niveaux** : XP via quêtes, farm, boss. Courbe progressive; softcap par étage.
- **Points de stats** à chaque niveau : Force (dégâts), Agilité (crit/esquive/vitesse), Vitalité (PV/def), Chance (drop/crit), Esprit (stamina max/cdr).
- **Compétences** :
  - **Armes** (épee, rapière, lance, dagues, marteau, bouclier) montent par usage, débloquent skills passifs/actifs.
  - **Utilitaires** : cuisine (buffs nourriture), forge (amélioration), récolte (drop+), alchimie (potions), pêche.
- **Raretés** : Commun, Peu commun, Rare, Épique, Légendaire, Mythique. Bonus d'affixes (dégâts %, crit, vol de vie, défense élémentaire).
- **Loot** : tables par mob/boss; boss d'étage drop item unique (arme signature + cristal d'accès étage suivant).

# Équipement, forge & économie
- **Forge** : PNJ forgerons par ville; amélioration +1 à +10 avec matériaux (minerai, fragments de boss). Taux de réussite décroissant ; à l'échec haut niveau : perte d'un niveau d'amélioration, jamais destruction.
- **Objets SAO-like** :
  - Ex : *Épée du Premier Souffle* (Rare, +5% crit, skill Linear amélioré).
  - Armures : set *Vigilance* (vitalité/def), accessoires : anneau de brume (esquive+), pendentif de courage (poise+).
- **Économie** : monnaie **Col**. Gains : quêtes, ventes PNJ, drop boss/élites. Taxes en ville; buff de guilde réduit la taxe.
- **Trade sécurisé** : /trade, HDB/AuctionHouse; shops PNJ via EconomyShopGUI.
- **Consommables** : potions SAO (HP Instant/regen), cristaux de TP (vers ville étages débloqués), nourriture buff (cuisine).

# Quêtes, PNJ & narration
- **Types** :
  - Principales : déblocage d'étages, histoire d'Aincrad.
  - Secondaires : farm/collecte, chasse élite, escortes, puzzles, exploration.
  - Guildes : contrats hebdo (chasse, craft, livraison) donnant réputation.
- **Structure** : intro RP, objectifs (kill/collect/parler), récompenses (XP/Col/équipement/réputation), dialogues multi-choix.
- **PNJ** : Citizens + BetonQuest, dialogues arborescents, portraits via ressource pack.

### Exemples de quêtes (Étage 1)
1. **"Premiers Pas"** : parler au Guide, tuer 5 sangliers, rapporter 3 viandes à l'Aubergiste. Récompense : XP, 1 cristal de retour, 500 Col.
2. **"Éclats Kobolds"** : ramasser 10 éclats sur kobolds des ruines. Récompense : XP, minerai de fer, plan d'épée 1M.
3. **"La faim des loups"** : protéger un chariot PNJ jusqu'au camp de bûcherons (escorte avec 3 vagues de loups). Récompense : XP, cape légère (agilité+).
4. **"La cloche fissurée"** : enquêter dans l'église en ruine (mini-donjon), casser 3 totems d'invocation. Récompense : XP, amulette vitalité.
5. **"Illfang doit tomber"** (quête principale) : entrer dans le donjon, vaincre Illfang. Récompense : XP massive, *Épée du Veilleur* (Rare), cristal étage 2.
6. **"Cuisine de survie"** : cuisiner 5 ragoûts de champi selon recette PNJ Chef; ingrédients à récolter. Récompense : buff nourriture + recette débloquée.
7. **"Recrutement de guilde"** : parler au maître de guilde, former un groupe de 3 et vaincre 1 élite kobold. Récompense : accès à la création de guilde.
8. **"Les cristaux perdus"** : trouver 3 fragments de cristal sur abeilles agressives; assemble un cristal de retour. Récompense : cristal, XP.

# Social : groupes, guildes, PK
- **Groupes** : création via /party, partage XP/loot (roulement), affichage HP alliés dans HUD, TP de groupe vers donjon.
- **Guildes** : création avec coût + quête préalable, rangs (Maître, Officier, Membre, Recrue), coffre de guilde, hall achetable en ville, buffs (ex: +2% exp, -10% taxe, +5% drop) via contributions hebdo.
- **PK** : zones PvP limitées (certains étages 5+/zones sauvages). Indicateur de criminalité (jaune/rouge) si kill joueur : perte de réputation, accès restreint en ville, bounty possible. Temps de désaggro en zone sûre.

# Mort & difficulté
- **Mort normale** : téléportation à la ville de l'étage débloqué; perte de 5-10% XP du niveau en cours (non létal), durabilité -10%, drop d'1 objet non-équipé aléatoire protégé par cristal d'assurance optionnel.
- **Mode Hardcore opt-in** : perte majeure (reset niveau actuel à 1, inventaire vidé sauf objets liés) avec titres cosmétiques et boosts XP.
- **Zones dangereuses** : donjons élite/boss avec meilleur loot, annonce serveur en cas de mort, mais récompenses rares.

# Interface & immersion SAO
- **Menus bleus** : DeluxeMenus + Oraxen/ItemsAdder pour GUI texturée (pane cyan, police custom). Menus pour stats, inventaire, compétences, quêtes.
- **HUD** :
  - Barre de vie stylisée (bossbar personnelle), stamina en actionbar.
  - Nametag : niveau + PV visible au-dessus des joueurs et mobs (via NameTagEdit/MythicMobs).
  - Barre de skills : hotbar dédiée, items icônes 3D.
- **Audio/FX** : sons custom "Link Start", déclenchement skill, victoire boss; particules arcs bleus pour skills.
- **Connexion** : première connexion = séquence chat/tilt camera (title) rappelant immersion, avertissement RP sur la mort (perte de progrès).
- **Ressource Pack** : modèles 3D armes SAO, UI bleue; distribué forcé (server.properties). Signé pour éviter spoof.

# Détails techniques concrets

## Exemple BetonQuest (quête Illfang doit tomber)
```yaml
illfang_fall:
  name: "Illfang doit tomber"
  quester: guide_start
  requirements:
    - objective: kill illfang_boss
  objectives:
    kill_boss:
      type: mmobkill
      name: illfang_boss
      amount: 1
  rewards:
    - tag add floor2_unlocked
    - command give %player% sao:watcher_sword 1
    - point exp 5000
    - point col 2000
```

## Exemple MythicMobs (Illfang)
```yaml
Illfang:
  Type: ZOMBIE
  Display: '&cIllfang le Kobold'
  Health: 600
  Damage: 12
  Skills:
    - skill{s=Cleave} @PlayersInRadius{r=4}
    - skill{s=Dash} @NearestPlayer{r=20}
    - skill{s=SummonAdds} @Self ~onDamaged{h=<50%}
  Drops:
    - exp 500-800
    - item:Kobold_Core 1 0.3
    - item:Watcher_Sword 1 0.05
```

## Exemple d'épée légendaire (MMOItems)
```yaml
WATCHER_SWORD:
  base:
    material: DIAMOND_SWORD
    name: '&bÉpée du Veilleur'
    lore:
      - '&7"Première lame brandie contre Illfang"'
    attack-damage: 12
    attack-speed: 1.6
    critical-strike-chance: 8
    critical-strike-power: 1.6
    ability:
      right-click:
        type: LINEAR
        mode: RIGHT_CLICK
        damage: 18
        cooldown: 12
  modifiers:
    rarity: RARE
```

## Commandes principales
- Joueur : `/menu`, `/skills`, `/quests`, `/party`, `/guild`, `/trade`, `/tpcrystal`, `/report`.
- Staff : `/mm mobs spawn Illfang`, `/bq admin`, `/mi give`, `/oraxen reload`, `/rg flag`, `/lp user <name> permission set`, `/restart`.

## Permissions (LuckPerms)
- `sao.player.*` : accès base (quêtes, skills, cristaux).
- `sao.vip.*` : cosmétiques, file prioritaire.
- `sao.staff.mod` : modération (ban/kick/mute), vanish.
- `sao.staff.builder` : worldedit, creative, accès monde admin.
- `sao.staff.dev` : reload plugins, debug MythicMobs/MMOItems.

## Conseils performance
- Limiter la densité de MythicMobs par chunk; utiliser AI Tick limiter (Paper) et règles SpawnLimiter.
- Activer Folia ou Starlight/Chunky pour génération anticipée; désactiver redstone inutile.
- Partitionner Aincrad en régions pour le pathfinding; pré-génération des étages.
- Tester ressource pack en 16/32px pour limiter le poids; compresser modèles.

---
**Livrable** : Ce document sert de blueprint complet (game design + technique) pour démarrer le serveur MMO SAO sur Paper/Velocity 1.20+ avec une stack réaliste et des exemples prêts à configurer.
