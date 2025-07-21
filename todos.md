# TODOs

## Completed Tasks

- [x] Move Farm 1 (Top-Left) to the far left of the map (X=0), and make the NPC village height twice its current value (from 156 to 312 tiles). Update all related coordinate logic, transitions, and map constants accordingly.
- [x] Update all transition logic (farm <-> village) to account for the new village height and Farm 1's new position.
- [x] Add visual paths connecting farms to the village in the minimap
- [x] Create paths in the village that connect to the farms

## Current Tasks

- [ ] Test the new map layout and transitions to ensure paths work correctly from farms to the NPC village
- [ ] Verify that pressing 'M' shows the correct minimap with the new layout and paths
- [ ] Test all farm-to-village and village-to-farm transitions work properly
- [ ] Verify that paths are visible in both the game world and minimap

## Future Tasks

- [ ] Optimize the minimap rendering for better performance
- [ ] Add more detailed map labels and legend
- [ ] Add decorative elements along the paths (lampposts, signs, etc.) 