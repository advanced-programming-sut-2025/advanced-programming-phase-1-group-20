# TODOs

## Completed Tasks

- [x] Move Farm 1 (Top-Left) to the far left of the map (X=0), and make the NPC village height twice its current value (from 156 to 312 tiles). Update all related coordinate logic, transitions, and map constants accordingly.
- [x] Update all transition logic (farm <-> village) to account for the new village height and Farm 1's new position.
- [x] Add visual paths connecting farms to the village in the minimap
- [x] Create paths in the village that connect to the farms
- [x] Add NPCs with their own sprites to the village
- [x] Implement lightning system with visual effects
- [x] Integrate lightning with map effects using cheatThor method

## Current Tasks

- [ ] Test the new map layout and transitions to ensure paths work correctly from farms to the NPC village
- [x] Verify that pressing 'M' shows the correct minimap with the new layout and paths
- [x] Test all farm-to-village and village-to-farm transitions work properly
- [x] Verify that paths are visible in both the game world and minimap
- [ ] Test NPC rendering and verify that all 6 NPCs (Abigail, Pierre, Sebastian, Leah, Willy, Jojo) appear in the village with their sprites
- [ ] Verify NPC animations work correctly (idle, walk, back, face, up)
- [x] Test lightning system during storm weather
- [x] Test manual lightning trigger with 'L' key
- [x] Verify lightning effects cover the entire screen with proper fade-out

## Future Tasks

- [x] Optimize the minimap rendering for better performance
- [x] Add more detailed map labels and legend
- [x] Add decorative elements along the paths (lampposts, signs, etc.)
- [ ] Add NPC movement patterns and AI behavior
- [ ] Add NPC interaction animations and dialogue bubbles
- [ ] Add thunder sound effects to accompany lightning
- [ ] Add more complex lightning patterns (forked lightning, multiple strikes) 
