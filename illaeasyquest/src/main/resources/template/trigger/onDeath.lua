--[[
  This file is part of the Illarion project.

  Copyright © 2014 - Illarion e.V.

  Illarion is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  Illarion is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  ]]
require("questsystem.base")
require("monster.base.kills")
module("onDeath", package.seeall)

-- category: monster
-- Kill a certain amount of monsters -- Eine bestimmte Anzahl an Monstern t�ten

local QUEST_NUMBER = 0
local PRECONDITION_QUESTSTATE = 0
local POSTCONDITION_QUESTSTATE = 0

local MONSTER_AMNT = INTEGER -- Amount of monsters to be killed -- Anzahl der zu t�tenden Monster

function onDeath(MONSTER)
    if monster.base.kills.hasLastAttacker(MONSTER) then
        PLAYER = monster.base.kills.getLastAttacker(MONSTER); -- get killer
        if ADDITIONALCONDITIONS(PLAYER)
                and questsystem.base.fulfilsPrecondition(PLAYER, QUEST_NUMBER, PRECONDITION_QUESTSTATE) then -- this one is really doing our quest
            if killList == nil then
                killList = {};
            end
            if killList[PLAYER.id] == nil then
                killList[PLAYER.id] = {};
                killList[PLAYER.id][MONSTER:getMonsterType()] = 0;
            end
            killList[PLAYER.id][MONSTER:getMonsterType()] = killList[PLAYER.id][MONSTER:getMonsterType()] + 1;
            if killList[PLAYER.id][MONSTER:getMonsterType()] == MONSTER_AMNT then
                HANDLER(PLAYER)
                killList[PLAYER.id][MONSTER:getMonsterType()] = 0;
                questsystem.base.setPostcondition(PLAYER, QUEST_NUMBER, POSTCONDITION_QUESTSTATE);
            end
        end
    end
    return false;
end