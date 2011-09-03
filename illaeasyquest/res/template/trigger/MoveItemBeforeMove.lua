require("questsystem.base")
module("MoveItemBeforeMove", package.seeall)

-- category: item
-- An item will be moved -- Ein Gegenstand soll bewegt werden

local QUEST_NUMBER = 0
local PRECONDITION_QUESTSTATE = 0
local POSTCONDITION_QUESTSTATE = 0

local POSITION = POSITION -- Map position -- Position auf der Karte
local RADIUS = INTEGER -- Radius -- Radius


function MoveItemBeforeMove(PLAYER, item, itemAfter)
  if PLAYER:isInRangeToPosition(POSITION,RADIUS)
      and questsystem.base.fulfilsPrecondition(PLAYER, QUEST_NUMBER, PRECONDITION_QUESTSTATE) then
    -- informNLS(PLAYER, TEXT_DE, TEXT_EN)
    
    HANDLER()
    
    questsystem.base.setPostcondition(PLAYER, QUEST_NUMBER, POSTCONDITION_QUESTSTATE)
    return true
  end

  return false
end

function informNLS(player, textDe, textEn)
  if player:getPlayerLanguage() == Player.german then
    player:inform(player, item, textDe)
  else
    player:inform(player, item, textEn)
  end
end

-- local TEXT_DE = TEXT -- German Text before movement -- Deutscher Text vor Bewegung
-- local TEXT_EN = TEXT -- English Text before movement -- Englischer Text vor Bewegung