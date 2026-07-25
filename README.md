## Clé

This mod tweaks Millénaire 9, provides some customization and balance options, and enhances compatibility.
Note: This mod (as well as the general Millénaire community) refers to Millénaire villages and its villagers as "millages" and "millagers" respectively to distinguish them from vanilla villagers.

Clé's options are as follows:

| Name                        | Function                                                                                                  | Default       |
|-----------------------------|-----------------------------------------------------------------------------------------------------------|---------------|
| Prevent Hopper Interactions | Prevents hopper interactions with Millenaire's Locked Chests.                                             | false         |
| Use Food Components         | Adds vanilla foods components to Millenaire's foods, enhancing compatibility                              | true          |
| Yogurt Gives Bowl           | Yogurt will give you a bowl after it's consumed                                                           | false         |
| Drink Gives Bottle          | All drinks will give the player a glass bottle after being consumed                                       | true          |
| Alternative Inuit Trident   | Automatically converts Millenaire Inuit Tridents to Clé's alternatives which behave like vanilla tridents | true          |
| Alternative Maces           | Automatically converts Millenaire Inuit Tridents to Clé's alternatives which behave like vanilla tridents | true          |
| Compost Datapack            | Adds a datapack that adds composter values to various Millenaire items                                    | true          |
| Modified Damage             | Tagged mobs will take more and deal less damage to/from millagers                                         | x2 and /2     |
| Hunts Millagers             | Tagged mobs will take hunt millagers                                                                      | true          |
| Despawns in Millage         | Tagged mobs will take despawn in millages like how creepers do                                            | true          |
| Sort Constructions Panel    | Moves less important constructions to the end of the construction panel in town halls                     | wall,tower    |
| Reputation Cap per Day      | Caps the amount of reputation a player can gain per village per Minecraft day                             | -1 (disabled) |

Integrations: 
 - Malum: Spirit reaping near millagers creeps them out and reduces your reputation
 - Ars Nouveau, Ars Elemental: Using positive spells on millagers improves reputation, negative spells reduce it

## Useful Tags
### Mob AI related
- `cle:millagers_try_hunting` : Entities in this tag WILL be hunted by Millagers, even if their original logic said they should not
- `cle:millagers_avoid_hunting` : Entities in this tag will NOT be hunted by Millagers, even if their original logic said they should
- `cle:hunts_millagers`: (if enabled) Mobs in this tag will target Millagers when they are nearby, even if that mob didn't originally
- `cle:despawns_in_millage`: (if enabled) Mobs in this tag will despawn inside of millages, in addition to the default of Creepers and Endermen
### Damage
These are useful if you have especially powerful mobs walking around the Overworld, like from Grimoire of Gaia.
- `cle:millagers_deal_modified_damage_to` : Millagers deal extra/less (determined by config) damage to entities in this tag.
- `cle:millagers_take_modified_damage_from` : Entities in this tag take extra/less (determined by config) damage from millagers


Fun fact: *"Clé" is French for "wrench"*. (I don't speak French, but I thought it would make for a good name)