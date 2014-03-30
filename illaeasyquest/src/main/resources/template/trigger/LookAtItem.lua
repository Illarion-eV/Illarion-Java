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
require("base.lookat")
require("base.common")
module("LookAtItem", package.seeall)

-- category: item
-- Look at an item -- Einen Gegenstand ansehen

local QUEST_NUMBER = 0
local PRECONDITION_QUESTSTATE = 0
local POSTCONDITION_QUESTSTATE = 0

local POSITION = POSITION -- Map position -- Position auf der Karte
local RADIUS = INTEGER -- Range to be within -- Wirkungsreichweite
local LOOKAT_TEXT_DE = TEXT -- German LookAt Text -- Deutscher Text beim Anschauen
local LOOKAT_TEXT_EN = TEXT -- English LookAt Text -- Englischer Text beim Anschauen

function LookAtItem(PLAYER, item)
    if PLAYER:isInRangeToPosition(POSITION, RADIUS)
            and ADDITIONALCONDITIONS(PLAYER)
            and questsystem.base.fulfilsPrecondition(PLAYER, QUEST_NUMBER, PRECONDITION_QUESTSTATE) then

        itemInformNLS(PLAYER, item, LOOKAT_TEXT_DE, LOOKAT_TEXT_EN)

        HANDLER(PLAYER)

        questsystem.base.setPostcondition(PLAYER, QUEST_NUMBER, POSTCONDITION_QUESTSTATE)
        return true
    end

    return false
end

function itemInformNLS(player, item, textDe, textEn)
    local lookAt = base.lookat.GenerateLookAt(player, item)
    lookAt.description = base.common.GetNLS(player, textDe, textEn)
    world:itemInform(player, item, lookAt)
end
