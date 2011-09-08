require("questsystem.base")
module("CharacterOnField", package.seeall)

-- category: triggerfield
-- Someone steps onto a triggerfield -- Jemand steigt auf ein Triggerfeld

local QUEST_NUMBER = 0
local PRECONDITION_QUESTSTATE = 0
local POSTCONDITION_QUESTSTATE = 0

local POSITION = POSITION -- Map position -- Position auf der Karte

function MoveToField( PLAYER )
    
    HANDLER()
    
    questsystem.base.setPostcondition(PLAYER, QUEST_NUMBER, POSTCONDITION_QUESTSTATE)
    return true
end
