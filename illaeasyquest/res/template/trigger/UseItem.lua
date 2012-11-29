require("questsystem.base")
module("UseItem", package.seeall)

-- category: item
-- Use an item -- Einen Gegenstand benutzen

local QUEST_NUMBER = 0
local PRECONDITION_QUESTSTATE = 0
local POSTCONDITION_QUESTSTATE = 0

local POSITION = POSITION -- Map position -- Position auf der Karte
local RADIUS = INTEGER -- Radius -- Radius

function UseItem( PLAYER, item, TargetItem, counter, Param, ltstate )
  if PLAYER:isInRangeToPosition(POSITION,RADIUS)
      and ADDITIONALCONDITIONS(PLAYER)
      and questsystem.base.fulfilsPrecondition(PLAYER, QUEST_NUMBER, PRECONDITION_QUESTSTATE) then
    PLAYER:inform(TEXT_DE, TEXT_EN)
    
    HANDLER(PLAYER)
    
    questsystem.base.setPostcondition(PLAYER, QUEST_NUMBER, POSTCONDITION_QUESTSTATE)
    return true
  end

  return false
end


-- local TEXT_DE = TEXT -- German Use Text -- Deutscher Text beim Benutzen
-- local TEXT_EN = TEXT -- English Use Text -- Englischer Text beim Benutzen
