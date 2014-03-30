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
module("CharacterOnField", package.seeall)

-- category: triggerfield
-- Someone steps onto a triggerfield -- Jemand steigt auf ein Triggerfeld

local QUEST_NUMBER = 0
local PRECONDITION_QUESTSTATE = 0
local POSTCONDITION_QUESTSTATE = 0

function MoveToField(PLAYER)
    if ADDITIONALCONDITIONS(PLAYER)
            and questsystem.base.fulfilsPrecondition(PLAYER, QUEST_NUMBER, PRECONDITION_QUESTSTATE) then

        HANDLER(PLAYER)

        questsystem.base.setPostcondition(PLAYER, QUEST_NUMBER, POSTCONDITION_QUESTSTATE)
        return true
    end

    return false
end
